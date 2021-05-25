package nl.contezza.drc.tests;

import org.json.JSONArray;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import nl.contezza.drc.rest.RestTest;
import nl.contezza.drc.service.AuthService;
import nl.contezza.drc.service.DRCRequestSpecification;
import nl.contezza.drc.service.EIOService;
import nl.contezza.drc.service.ZTCService;

//@Log4j2
public class InformatieObjectReadCorrectScopeTest extends RestTest {

	/**
	 * Create necessary dependencies.
	 */
	@BeforeTest(groups = "InformatieObjectScopeForbidden")
	public void init() {
		// Create random catalogi
		ZTCService ztcService = new ZTCService();
		JsonPath json = new JsonPath(ztcService.createCatalogus().asString());

		// Create informatieobjecttype
		String catalogusUrl = json.getString("url").replace(ZTC_BASE_URI, ZTC_DOCKER_URI);
		json = new JsonPath(ztcService.createInformatieObjectType(catalogusUrl).asString());
		informatieobjecttypeUrl = json.getString("url").replace(ZTC_BASE_URI, ZTC_DOCKER_URI);

		Response res = ztcService.publishInformatieObjectType(informatieobjecttypeUrl.substring(informatieobjecttypeUrl.lastIndexOf('/') + 1).trim());
		Assert.assertEquals(res.getStatusCode(), 200);
	}

	/**
	 * See {@link <a href="https://github.com/VNG-Realisatie/documenten-api/blob/1.0.0/src/drc/api/tests/test_auth.py#L49">python code</a>}.
	 */
	@Test(groups = "InformatieObjectReadCorrectScope")
	public void test_io_list() {
		EIOService eioService = new EIOService();
		
		eioService.testCreate(informatieobjecttypeUrl, "beschrijving1", "inhoud1", "openbaar");
		eioService.testCreate(informatieobjecttypeUrl, "beschrijving2", "inhoud2", "zeer_geheim");
		eioService.testCreate("https://informatieobjecttype.nl/not_ok", "beschrijving3", "inhoud3", "openbaar");
		eioService.testCreate("https://informatieobjecttype.nl/not_ok", "beschrijving4", "inhoud4", "zeer_geheim");

		AuthService authService = new AuthService();
		Response res = authService.list(DRCRequestSpecification.CLIENT_ID_READONLY, null);

		String acUrl = res.body().path("results[0].url");
		res = authService.updatePartial(acUrl, new JSONArray().put(DRCRequestSpecification.CLIENT_ID_READONLY), new JSONArray().put("documenten.lezen"), informatieobjecttypeUrl,
				"openbaar");

		Assert.assertEquals(res.getStatusCode(), 200);

		res = eioService.listEIO(DRCRequestSpecification.getReadonly(), null, null, null);

		Assert.assertEquals(res.getStatusCode(), 200);
		Assert.assertEquals((int) res.body().path("results.size()"), 1);

		String iot = res.body().path("results[0].informatieobjecttype");
		Assert.assertEquals(iot.replace(ZTC_BASE_URI, ZTC_DOCKER_URI), informatieobjecttypeUrl);
		Assert.assertEquals(res.body().path("results[0].vertrouwelijkheidaanduiding"), "openbaar");
	}

	/**
	 * See {@link <a href="https://github.com/VNG-Realisatie/documenten-api/blob/1.0.0/src/drc/api/tests/test_auth.py#L87">python code</a>}.
	 */
	@Test(groups = "InformatieObjectReadCorrectScope")
	public void test_io_retrieve() {

		EIOService eioService = new EIOService();

		JsonPath json1 = new JsonPath(eioService.testCreate(informatieobjecttypeUrl, "beschrijving1", "inhoud1", "openbaar").asString());
		JsonPath json2 = new JsonPath(eioService.testCreate(informatieobjecttypeUrl, "beschrijving2", "inhoud2", "zeer_geheim").asString());

		AuthService authService = new AuthService();
		Response res = authService.list(DRCRequestSpecification.CLIENT_ID_READONLY, null);

		String acUrl = res.body().path("results[0].url");

		// Create new IOT
		ZTCService ztcService = new ZTCService();
		JsonPath json = new JsonPath(ztcService.createCatalogus().asString());
		String catalogusUrl = json.getString("url").replace(ZTC_BASE_URI, ZTC_DOCKER_URI);
		json = new JsonPath(ztcService.createInformatieObjectType(catalogusUrl).asString());

		informatieobjecttypeUrl = json.getString("url").replace(ZTC_BASE_URI, ZTC_DOCKER_URI);
		ztcService.publishInformatieObjectType(informatieobjecttypeUrl.substring(informatieobjecttypeUrl.lastIndexOf('/') + 1).trim());

		res = authService.updatePartial(acUrl, new JSONArray().put(DRCRequestSpecification.CLIENT_ID_READONLY), new JSONArray().put("documenten.lezen"), informatieobjecttypeUrl,
				"openbaar");

		res = eioService.getEIO(DRCRequestSpecification.getReadonly(), json1.getString("url"), null);

		Assert.assertEquals(res.getStatusCode(), 403);

		res = eioService.getEIO(DRCRequestSpecification.getReadonly(), json2.getString("url"), null);

		Assert.assertEquals(res.getStatusCode(), 403);
	}

	/**
	 * See {@link <a href="https://github.com/VNG-Realisatie/documenten-api/blob/1.0.0/src/drc/api/tests/test_auth.py#L109">python code</a>}.
	 */
	@Test(groups = "InformatieObjectReadCorrectScope")
	public void test_read_superuser() {

		EIOService eioService = new EIOService();

		String rsin = randomRsin();

		eioService.testCreate(informatieobjecttypeUrl, "beschrijving1", "inhoud1", "openbaar", rsin);
		eioService.testCreate(informatieobjecttypeUrl, "beschrijving2", "inhoud2", "zeer_geheim", rsin);
		eioService.testCreate(informatieobjecttypeUrl, "beschrijving3", "inhoud3", "openbaar", rsin);
		eioService.testCreate(informatieobjecttypeUrl, "beschrijving4", "inhoud4", "zeer_geheim", rsin);

		Response res = eioService.listEIO(null, rsin, null);

		Assert.assertEquals(res.getStatusCode(), 200);
		Assert.assertEquals((int) res.body().path("results.size()"), 4);
	}
}
