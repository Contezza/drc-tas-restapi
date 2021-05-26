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
import nl.contezza.drc.service.GebruiksrechtenService;
import nl.contezza.drc.service.ZTCService;

@Log4j2
public class GebruiksrechtenReadTest extends RestTest {

	/**
	 * Create necessary dependencies.
	 */
	@BeforeTest(groups = "GebruiksrechtenRead")
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
	 * See {@link <a href="https://github.com/VNG-Realisatie/documenten-api/blob/1.0.0/src/drc/api/tests/test_auth.py#L148">python code</a>}.
	 */
	@Test(groups = "GebruiksrechtenRead")
	public void test_list_gebruiksrechten_limited_to_authorized_zaken() {

		EIOService eioService = new EIOService();

		JsonPath json1 = new JsonPath(eioService.testCreate(informatieobjecttypeUrl, "beschrijving1", "inhoud1", "openbaar").asString());
		JsonPath json2 = new JsonPath(eioService.testCreate(informatieobjecttypeUrl, "beschrijving2", "inhoud2", "vertrouwelijk").asString());

		GebruiksrechtenService gebruiksrechtenService = new GebruiksrechtenService();
		Response res1 = gebruiksrechtenService.create(json1.getString("url"));
		Response res2 = gebruiksrechtenService.create(json2.getString("url"));

		Assert.assertEquals(res1.getStatusCode(), 201);
		Assert.assertEquals(res2.getStatusCode(), 201);

		AuthService authService = new AuthService();
		Response res = authService.list(DRCRequestSpecification.CLIENT_ID_READONLY, null);

		String acUrl = res.body().path("results[0].url");

		res = authService.updatePartial(acUrl, new JSONArray().put(DRCRequestSpecification.CLIENT_ID_READONLY), new JSONArray().put("documenten.lezen"), informatieobjecttypeUrl,
				"openbaar");
		Assert.assertEquals(res.getStatusCode(), 200);

		res = gebruiksrechtenService.get(DRCRequestSpecification.getReadonly(), new JsonPath(res1.asString()).getString("url"));

		log.debug("URL1 (openbaar): " + new JsonPath(res1.asString()).getString("url"));
		log.debug("URL2 (vertrouwelijk): " + new JsonPath(res2.asString()).getString("url"));

		// FIXME: expect 200, but look like AC does not update correctly, when update via UI without any changes it works ??.
		Assert.assertEquals(res.getStatusCode(), 200);

		res = gebruiksrechtenService.get(DRCRequestSpecification.getReadonly(), new JsonPath(res2.asString()).getString("url"));

		Assert.assertEquals(res.getStatusCode(), 403);
	}

	/**
	 * See {@link <a href="https://github.com/VNG-Realisatie/documenten-api/blob/1.0.0/src/drc/api/tests/test_auth.py#L175">python code</a>}.
	 */
	@Test(groups = "GebruiksrechtenRead")
	public void test_create_gebruiksrechten_limited_to_authorized_zaken() {

		EIOService eioService = new EIOService();

		JsonPath json1 = new JsonPath(eioService.testCreate(informatieobjecttypeUrl, "beschrijving1", "inhoud1", "openbaar").asString());
		JsonPath json2 = new JsonPath(eioService.testCreate(informatieobjecttypeUrl, "beschrijving2", "inhoud2", "vertrouwelijk").asString());

		GebruiksrechtenService gebruiksrechtenService = new GebruiksrechtenService();
		Response res1 = gebruiksrechtenService.create(DRCRequestSpecification.getWrongScope(), json1.getString("url"));
		Response res2 = gebruiksrechtenService.create(DRCRequestSpecification.getWrongScope(), json2.getString("url"));

		Assert.assertEquals(res1.getStatusCode(), 403);
		Assert.assertEquals(res2.getStatusCode(), 403);
	}
}
