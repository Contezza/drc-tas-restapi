package nl.contezza.drc.tests;

import org.json.JSONArray;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import nl.contezza.drc.rest.RestTest;
import nl.contezza.drc.service.EIOService;
import nl.contezza.drc.service.ZTCService;

//@Log4j2
public class EnkelvoudigInformatieObjectZoekTest extends RestTest {

	/**
	 * Create necessary dependencies.
	 */
	@BeforeTest(groups = "EnkelvoudigInformatieObjectZoek")
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
	 * "https://github.com/VNG-Realisatie/documenten-api/blob/1.3.0/src/drc/api/tests/test_enkelvoudiginformatieobject.py#L683">python
	 * code</a>}.
	 */
	@Test(groups = "EnkelvoudigInformatieObjectZoek")
	public void test_zoek_uuid_in() {

		EIOService eioService = new EIOService();

		// @formatter:off
		String url1 = new JsonPath(eioService.testCreate(informatieobjecttypeUrl).asString()).getString("url");
		String url2 = new JsonPath(eioService.testCreate(informatieobjecttypeUrl).asString()).getString("url");
		String url3 = new JsonPath(eioService.testCreate(informatieobjecttypeUrl).asString()).getString("url");
		// @formatter:on

		String uuid1 = url1.substring(url1.lastIndexOf('/') + 1).trim();
		String uuid2 = url2.substring(url2.lastIndexOf('/') + 1).trim();
		String uuid3 = url3.substring(url3.lastIndexOf('/') + 1).trim();

		Response res = eioService.search(new JSONArray().put(uuid1).put(uuid2));

		Assert.assertEquals(res.getStatusCode(), 200);
		Assert.assertEquals((int) res.body().path("results.size()"), 2);

		res = eioService.search(new JSONArray().put(uuid3));

		Assert.assertEquals(res.getStatusCode(), 200);
		Assert.assertEquals((int) res.body().path("results.size()"), 1);
	}

	/**
	 * See {@link <a href=
	 * "https://github.com/VNG-Realisatie/documenten-api/blob/1.3.0/src/drc/api/tests/test_enkelvoudiginformatieobject.py#L697">python
	 * code</a>}.
	 */
	@Test(groups = "EnkelvoudigInformatieObjectZoek")
	public void test_zoek_without_params() {

		EIOService eioService = new EIOService();

		Response res = eioService.search(null);

		Assert.assertEquals(res.getStatusCode(), 400);
		Assert.assertEquals(res.body().path("invalidParams[0].code"), "required");
	}
}