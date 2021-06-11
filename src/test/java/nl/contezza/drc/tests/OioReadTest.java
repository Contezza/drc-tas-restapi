package nl.contezza.drc.tests;

import org.json.JSONArray;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.extern.log4j.Log4j2;
import nl.contezza.drc.rest.RestTest;
import nl.contezza.drc.service.AuthService;
import nl.contezza.drc.service.DRCRequestSpecification;
import nl.contezza.drc.service.EIOService;
import nl.contezza.drc.service.OIOService;
import nl.contezza.drc.service.ZRCService;
import nl.contezza.drc.service.ZTCService;

@Log4j2
public class OioReadTest extends RestTest {

	/**
	 * Create necessary dependencies when creating enkelvoudiginformatieobject.
	 */
	@BeforeTest(groups = "OioRead")
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
	 * See {@link <a href="https://github.com/VNG-Realisatie/documenten-api/blob/1.0.0/src/drc/api/tests/test_auth.py#L212">python code</a>}.
	 */
	@Test(groups = "OioRead")
	public void test_list_oio_limited_to_authorized_zaken() {

		String zaakUrl = zaakTestObject.getString("url").replace(ZRC_BASE_URI, ZRC_DOCKER_URI);

		// apparently we need to use localhost only for internal DRC??
		if (DRCRequestSpecification.BASE_PATH.equals("/documenten/api/v1")) {
			zaakUrl = zaakTestObject.getString("url");
		}

		EIOService eioService = new EIOService();

		JsonPath json1 = new JsonPath(eioService.testCreate(informatieobjecttypeUrl, "beschrijving1", "inhoud1", "openbaar").asString());
		JsonPath json2 = new JsonPath(eioService.testCreate(informatieobjecttypeUrl, "beschrijving2", "inhoud2", "vertrouwelijk").asString());
		JsonPath json3 = new JsonPath(eioService.testCreate(informatieobjecttypeUrl, "beschrijving3", "inhoud3", "zeer_geheim").asString());

		log.debug(zaakTestObject.getString("identificatie"));

		ZRCService zrcService = new ZRCService();
		Response res = zrcService.createZio(json1.getString("url").replace(DRC_BASE_URI, DRC_DOCKER_URI), zaakUrl);

		Assert.assertEquals(res.getStatusCode(), 201);

		res = zrcService.createZio(json2.getString("url").replace(DRC_BASE_URI, DRC_DOCKER_URI), zaakUrl);

		Assert.assertEquals(res.getStatusCode(), 201);

		res = zrcService.createZio(json3.getString("url").replace(DRC_BASE_URI, DRC_DOCKER_URI), zaakUrl);

		Assert.assertEquals(res.getStatusCode(), 201);

		AuthService authService = new AuthService();
		res = authService.list(DRCRequestSpecification.CLIENT_ID_READONLY, null);

		String acUrl = res.body().path("results[0].url");
		res = authService.updatePartial(acUrl, new JSONArray().put(DRCRequestSpecification.CLIENT_ID_READONLY), new JSONArray().put("documenten.lezen"), informatieobjecttypeUrl,
				"openbaar");

		Assert.assertEquals(res.getStatusCode(), 200);

		OIOService oioService = new OIOService();
		res = oioService.listOIO(zaakUrl, null);

		Assert.assertEquals((int) res.body().path("results.size()"), 3);

		res = oioService.listOIO(DRCRequestSpecification.getReadonly(), zaakUrl, null);

		Assert.assertEquals((int) res.body().path("results.size()"), 1);
	}

	/**
	 * See {@link <a href="https://github.com/VNG-Realisatie/documenten-api/blob/1.0.0/src/drc/api/tests/test_auth.py#L242">python code</a>}.
	 */
	@Test(groups = "OioRead")
	public void test_detail_oio_limited_to_authorized_zaken() {

		ZRCService zrcService = new ZRCService();
		JsonPath zaakTestObject = new JsonPath(zrcService.createZaak(zaakTypeTestObject.getString("url").replace(ZTC_BASE_URI, ZTC_DOCKER_URI)).asString());

		String zaakUrl = zaakTestObject.getString("url").replace(ZRC_BASE_URI, ZRC_DOCKER_URI);

		// apparently we need to use localhost only for internal DRC??
		if (DRCRequestSpecification.BASE_PATH.equals("/documenten/api/v1")) {
			zaakUrl = zaakTestObject.getString("url");
		}

		EIOService eioService = new EIOService();
		OIOService oioService = new OIOService();

		JsonPath json1 = new JsonPath(eioService.testCreate(informatieobjecttypeUrl, "beschrijving1", "inhoud1", "openbaar").asString());
		JsonPath json2 = new JsonPath(eioService.testCreate(informatieobjecttypeUrl, "beschrijving2", "inhoud2", "vertrouwelijk").asString());
		JsonPath json3 = new JsonPath(eioService.testCreate(informatieobjecttypeUrl, "beschrijving3", "inhoud3", "zeer_geheim").asString());

		// EIO 1
		String eioUrl1 = json1.getString("url").replace(DRC_BASE_URI, DRC_DOCKER_URI);
		Response res = zrcService.createZio(eioUrl1, zaakUrl);

		Assert.assertEquals(res.getStatusCode(), 201);

		Response resOio1 = oioService.listOIO(zaakUrl, eioUrl1);
		String oioUrl1 = resOio1.body().path("[0].url");

		// EIO 2
		String eioUrl2 = json2.getString("url").replace(DRC_BASE_URI, DRC_DOCKER_URI);
		res = zrcService.createZio(eioUrl2, zaakUrl);

		Assert.assertEquals(res.getStatusCode(), 201);

		Response resOio2 = oioService.listOIO(zaakUrl, eioUrl2);
		String oioUrl2 = resOio2.body().path("[0].url");

		// EIO 3
		String eioUrl3 = json3.getString("url").replace(DRC_BASE_URI, DRC_DOCKER_URI);

		res = zrcService.createZio(eioUrl3, zaakUrl);

		Assert.assertEquals(res.getStatusCode(), 201);

		Response resOio3 = oioService.listOIO(zaakUrl, eioUrl3);
		String oioUrl3 = resOio3.body().path("[0].url");

		// Update auth
		AuthService authService = new AuthService();
		Response resAuth = authService.list(DRCRequestSpecification.CLIENT_ID_READONLY, null);

		String acUrl = resAuth.body().path("results[0].url");
		resAuth = authService.updatePartial(acUrl, new JSONArray().put(DRCRequestSpecification.CLIENT_ID_READONLY), new JSONArray().put("documenten.lezen"), informatieobjecttypeUrl,
				"confidentieel");
		
		Assert.assertEquals(resAuth.getStatusCode(), 200);
		
		// Waiting for AC cache is up to date.
		wait(2000);

		Response res1 = oioService.getOIO(DRCRequestSpecification.getReadonly(), oioUrl1);

		Assert.assertEquals(res1.getStatusCode(), 200);

		Response res2 = oioService.getOIO(DRCRequestSpecification.getReadonly(), oioUrl2);

		Assert.assertEquals(res2.getStatusCode(), 200);

		Response res3 = oioService.getOIO(DRCRequestSpecification.getReadonly(), oioUrl3);
		
		// FIXME: Result is random: sometimes 200 
		Assert.assertEquals(res3.getStatusCode(), 403);
	}
}
