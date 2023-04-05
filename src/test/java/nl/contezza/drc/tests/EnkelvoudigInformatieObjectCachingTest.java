package nl.contezza.drc.tests;

import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import nl.contezza.drc.rest.RestTest;
import nl.contezza.drc.service.EIOService;
import nl.contezza.drc.service.ZTCService;

//@Log4j2
public class EnkelvoudigInformatieObjectCachingTest extends RestTest {

	/**
	 * Create necessary dependencies.
	 */
	@BeforeTest(groups = "EnkelvoudigInformatieObjectCaching")
	public void init() {
		// Create random catalogi
		ZTCService ztcService = new ZTCService();
		JsonPath json = new JsonPath(ztcService.createCatalogus().asString());

		// Create informatieobjecttype
		String catalogusUrl = json.getString("url").replace(ZTC_BASE_URI, ZTC_DOCKER_URI);
		json = new JsonPath(ztcService.createInformatieObjectType(catalogusUrl).asString());
		informatieobjecttypeUrl = json.getString("url").replace(ZTC_BASE_URI, ZTC_DOCKER_URI);

		// @formatter:off
		Response res = ztcService.publishInformatieObjectType(informatieobjecttypeUrl.substring(informatieobjecttypeUrl.lastIndexOf('/') + 1).trim());
		Assert.assertEquals(res.getStatusCode(), 200);
		// @formatter:on
	}

	/**
	 * See {@link <a href=
	 * "https://github.com/VNG-Realisatie/documenten-api/blob/1.3.0/src/drc/api/tests/test_caching.py#L19">python
	 * code</a>}.
	 */
	@Test(groups = "EnkelvoudigInformatieObjectCaching")
	public void test_eio_get_cache_header() {

		EIOService eioService = new EIOService();
		String eioUrl = new JsonPath(eioService.testCreate(informatieobjecttypeUrl).asString()).getString("url");

		Response res = eioService.getEIO(eioUrl, null);
		Assert.assertNotNull(res.getHeader("ETag"));
	}

	/**
	 * See {@link <a href=
	 * "https://github.com/VNG-Realisatie/documenten-api/blob/1.3.0/src/drc/api/tests/test_caching.py#L26">python
	 * code</a>}.
	 */
	@Test(groups = "EnkelvoudigInformatieObjectCaching")
	public void test_eio_head_cache_header() {
		EIOService eioService = new EIOService();
		String eioUrl = new JsonPath(eioService.testCreate(informatieobjecttypeUrl).asString()).getString("url");

		Response res = eioService.getHeadEIO(eioUrl);
		Assert.assertNotNull(res.getHeader("ETag"));
		Assert.assertEquals(res.getStatusCode(), 200);
	}

	/**
	 * See {@link <a href=
	 * "https://github.com/VNG-Realisatie/documenten-api/blob/1.3.0/src/drc/api/tests/test_caching.py#L38">python
	 * code</a>}.
	 */
	@Test(groups = "EnkelvoudigInformatieObjectCaching")
	public void test_conditional_get_304() {

		EIOService eioService = new EIOService();
		String eioUrl = new JsonPath(eioService.testCreate(informatieobjecttypeUrl).asString()).getString("url");

		// get ETag
		Response res = eioService.getEIO(eioUrl, null);
		String eTag = "" + res.getHeader("ETag") + "";

		// Not modified
		res = eioService.getEioIfNonMatch(eioUrl, eTag);
		Assert.assertEquals(res.getStatusCode(), 304);
	}

	/**
	 * See {@link <a href=
	 * "https://github.com/VNG-Realisatie/documenten-api/blob/1.3.0/src/drc/api/tests/test_caching.py#L44">python
	 * code</a>}.
	 */
	@Test(groups = "EnkelvoudigInformatieObjectCaching")
	public void test_conditional_get_stale() {
		EIOService eioService = new EIOService();
		String eioUrl = new JsonPath(eioService.testCreate(informatieobjecttypeUrl).asString()).getString("url");

		Response res = eioService.getEioIfNonMatch(eioUrl, "" + "not-an-md5" + "");
		Assert.assertEquals(res.getStatusCode(), 200);
	}

	/**
	 * See {@link <a href=
	 * "https://github.com/VNG-Realisatie/documenten-api/blob/1.3.0/src/drc/api/tests/test_caching.py#L142">python
	 * code</a>}.
	 */
	@Test(groups = "EnkelvoudigInformatieObjectCaching")
	public void test_invalidate_etag_after_change() {

		EIOService eioService = new EIOService();
		String eioUrl = new JsonPath(eioService.testCreate(informatieobjecttypeUrl).asString()).getString("url");

		// get ETag
		Response res = eioService.getEIO(eioUrl, null);
		String eTag = "" + res.getHeader("ETag") + "";

		updateEIO(eioUrl, "aangepast");

		res = eioService.getEioIfNonMatch(eioUrl, eTag);
		Assert.assertEquals(res.getStatusCode(), 200);
	}

	/**
	 * Update title.
	 * 
	 * @param eioUrl String url of EIO
	 * @param titel  String title
	 * @return Response response
	 */
	private Response updateEIO(String eioUrl, String titel) {
		EIOService eioService = new EIOService();
		String lock = new JsonPath(eioService.lock(eioUrl).asString()).getString("lock");

		JSONObject body = new JSONObject();
		body.put("titel", titel);
		body.put("lock", lock);

		Response res = eioService.partialUpdate(eioUrl, body);
		Assert.assertEquals(res.getStatusCode(), 200);
		res = eioService.unlock(eioUrl, lock);
		Assert.assertEquals(res.getStatusCode(), 204);
		return res;
	}
}