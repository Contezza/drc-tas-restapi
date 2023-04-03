package nl.contezza.drc.tests;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import nl.contezza.drc.rest.RestTest;
import nl.contezza.drc.service.EIOService;
import nl.contezza.drc.service.OIOService;
import nl.contezza.drc.service.ZRCService;
import nl.contezza.drc.service.ZTCService;

//@Log4j2
public class OioCachingTest extends RestTest {

	/**
	 * Create necessary dependencies when creating enkelvoudiginformatieobject.
	 */
	@BeforeTest(groups = "OioCaching")
	public void init() {

		// Create random catalogi
		ZTCService ztcService = new ZTCService();
		JsonPath json = new JsonPath(ztcService.createCatalogus().asString());

		// Create informatieobjecttype
		String catalogusUrl = json.getString("url").replace(ZTC_BASE_URI, ZTC_DOCKER_URI);
		json = new JsonPath(ztcService.createInformatieObjectType(catalogusUrl).asString());
		informatieobjecttypeUrl = json.getString("url").replace(ZTC_BASE_URI, ZTC_DOCKER_URI);

		// Create zaaktype
		zaakTypeTestObject = new JsonPath(ztcService.createZaaktype(catalogusUrl).asString());

		String zaaktypeUrl = zaakTypeTestObject.getString("url").replace(ZTC_BASE_URI, ZTC_DOCKER_URI);

		// Create zaaktype-informatieobjecttype
		Response res = ztcService.createZiot(zaaktypeUrl, informatieobjecttypeUrl);
		Assert.assertEquals(res.getStatusCode(), 201);

		// Publish informatieobjecttype
		String id = informatieobjecttypeUrl.substring(informatieobjecttypeUrl.lastIndexOf('/') + 1).trim();

		res = ztcService.publishInformatieObjectType(id);
		Assert.assertEquals(res.getStatusCode(), 200);

		// Publish zaaktype
		res = ztcService.publishIZaaktype(zaaktypeUrl);
		Assert.assertEquals(res.getStatusCode(), 200);

		ZRCService zrcService = new ZRCService();
		zaakTestObject = new JsonPath(zrcService.createZaak(zaaktypeUrl).asString());
	}

	/**
	 * See {@link <a href=
	 * "https://github.com/VNG-Realisatie/documenten-api/blob/1.3.0/src/drc/api/tests/test_caching.py#L55">python
	 * code</a>}.
	 */
	@Test(groups = "OioCaching")
	public void test_oio_get_cache_header() {

		ZRCService zrcService = new ZRCService();
		EIOService eioService = new EIOService();
		OIOService oioService = new OIOService();

		String zaakUrl = zaakTestObject.getString("url").replace(ZRC_BASE_URI, ZRC_DOCKER_URI);
		String eioUrl = new JsonPath(eioService.testCreate(informatieobjecttypeUrl).asString()).getString("url");

		zrcService.createZio(eioUrl.replace(DRC_BASE_URI, DRC_DOCKER_URI), zaakUrl);

		Response resOio = oioService.listOIO(zaakUrl, eioUrl);
		String oioUrl = resOio.body().path("[0].url");

		Response res = oioService.getOIO(oioUrl);
		Assert.assertNotNull(res.getHeader("ETag"));
	}

	/**
	 * See {@link <a href=
	 * "https://github.com/VNG-Realisatie/documenten-api/blob/1.3.0/src/drc/api/tests/test_caching.py#L62">python
	 * code</a>}.
	 */
	@Test(groups = "OioCaching")
	public void test_oio_head_cache_header() {
		ZRCService zrcService = new ZRCService();
		EIOService eioService = new EIOService();
		OIOService oioService = new OIOService();

		String zaakUrl = zaakTestObject.getString("url").replace(ZRC_BASE_URI, ZRC_DOCKER_URI);
		String eioUrl = new JsonPath(eioService.testCreate(informatieobjecttypeUrl).asString()).getString("url");

		zrcService.createZio(eioUrl.replace(DRC_BASE_URI, DRC_DOCKER_URI), zaakUrl);

		Response resOio = oioService.listOIO(zaakUrl, eioUrl);
		String oioUrl = resOio.body().path("[0].url");

		Response res = oioService.getHeadOIO(oioUrl);
		Assert.assertNotNull(res.getHeader("ETag"));
	}

	/**
	 * See {@link <a href=
	 * "https://github.com/VNG-Realisatie/documenten-api/blob/1.3.0/src/drc/api/tests/test_caching.py#L74">python
	 * code</a>}.
	 */
	@Test(groups = "OioCaching")
	public void test_conditional_get_304() {

		ZRCService zrcService = new ZRCService();
		EIOService eioService = new EIOService();
		OIOService oioService = new OIOService();

		String zaakUrl = zaakTestObject.getString("url").replace(ZRC_BASE_URI, ZRC_DOCKER_URI);
		String eioUrl = new JsonPath(eioService.testCreate(informatieobjecttypeUrl).asString()).getString("url");

		zrcService.createZio(eioUrl.replace(DRC_BASE_URI, DRC_DOCKER_URI), zaakUrl);

		Response resOio = oioService.listOIO(zaakUrl, eioUrl);
		String oioUrl = resOio.body().path("[0].url");

		Response res = oioService.getOIO(oioUrl);
		String eTag = "" + res.getHeader("ETag") + "";

		res = oioService.getOioIfNonMatch(oioUrl, eTag);
		Assert.assertEquals(res.getStatusCode(), 304);
	}

	/**
	 * See {@link <a href=
	 * "https://github.com/VNG-Realisatie/documenten-api/blob/1.3.0/src/drc/api/tests/test_caching.py#L118">python
	 * code</a>}.
	 */
	@Test(groups = "OioCaching")
	public void test_conditional_get_stale() {

		ZRCService zrcService = new ZRCService();
		EIOService eioService = new EIOService();
		OIOService oioService = new OIOService();

		String zaakUrl = zaakTestObject.getString("url").replace(ZRC_BASE_URI, ZRC_DOCKER_URI);
		String eioUrl = new JsonPath(eioService.testCreate(informatieobjecttypeUrl).asString()).getString("url");

		zrcService.createZio(eioUrl.replace(DRC_BASE_URI, DRC_DOCKER_URI), zaakUrl);

		Response resOio = oioService.listOIO(zaakUrl, eioUrl);
		String oioUrl = resOio.body().path("[0].url");

		Response res = oioService.getOioIfNonMatch(oioUrl, "" + "not-an-md5" + "");
		Assert.assertEquals(res.getStatusCode(), 200);
	}
}
