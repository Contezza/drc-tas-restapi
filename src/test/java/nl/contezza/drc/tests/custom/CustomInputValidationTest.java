package nl.contezza.drc.tests.custom;

import static io.restassured.RestAssured.given;

import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import nl.contezza.drc.dataprovider.DRCDataProvider;
import nl.contezza.drc.rest.RestTest;
import nl.contezza.drc.service.AuthService;
import nl.contezza.drc.service.DRCRequestSpecification;
import nl.contezza.drc.service.EIOService;
import nl.contezza.drc.service.ZTCService;
import nl.contezza.drc.utils.StringDate;

/**
 * Some custom unit tests which are not mapped to any python scripts.
 */

// @Log4j2
public class CustomInputValidationTest extends RestTest {

	/**
	 * Create necessary dependencies.
	 */
	@BeforeTest(groups = "CustomInputValidation")
	public void init() {
		// Create random catalogi
		ZTCService ztcService = new ZTCService();
		JsonPath json = new JsonPath(ztcService.createCatalogus().asString());

		// Create informatieobjecttype
		String catalogusUrl = json.getString("url").replace(ZTC_BASE_URI, ZTC_DOCKER_URI);
		json = new JsonPath(ztcService.createInformatieObjectType(catalogusUrl).asString());
		informatieobjecttypeUrl = json.getString("url").replace(ZTC_BASE_URI, ZTC_DOCKER_URI);

		Response res = ztcService.publishInformatieObjectType(
				informatieobjecttypeUrl.substring(informatieobjecttypeUrl.lastIndexOf('/') + 1).trim());
		Assert.assertEquals(res.getStatusCode(), 200);
	}

	@Test(groups = "CustomInputValidation")
	public void empty_body_patch() {

		EIOService eioService = new EIOService();
		// Create EIO
		JsonPath json = new JsonPath(eioService
				.testCreate(informatieobjecttypeUrl, "beschrijving0", "some content0", new Date()).asString());
		String eioUrl = json.getString("url");

		String id = eioUrl.substring(eioUrl.lastIndexOf('/') + 1).trim();
		Response res = given().spec(DRCRequestSpecification.getDefault()).when()
				.patch("/enkelvoudiginformatieobjecten/" + id +
						"/unlock")
				.then().extract().response();

		Assert.assertEquals(res.getStatusCode(), 405);
	}

	@Test(groups = "CustomInputValidation")
	public void create_eio_with_only_required_items() {

		AuthService authService = new AuthService();
		authService.updateReadOnlyClientScope(new JSONArray().put("documenten.aanmaken"), "zeer_geheim",
				informatieobjecttypeUrl);
		wait(2000);

		EIOService eioService = new EIOService();

		Response res = eioService.testCreateReqOnly(informatieobjecttypeUrl, DRCRequestSpecification.getReadonly());

		Assert.assertEquals(res.getStatusCode(), 201);
	}

	@Test(groups = "CustomInputValidation")
	public void create_eio_with_empty_strings() {
		EIOService eioService = new EIOService();

		JSONObject json = new JSONObject();
		json.put("identificatie", UUID.randomUUID().toString());
		json.put("bronorganisatie", "159351741");
		json.put("creatiedatum", StringDate.formatDate(new Date()));
		// json.put("ontvangstdatum", "");
		// json.put("verzenddatum", "");
		json.put("titel", "detailed summary");
		json.put("auteur", "test_auteur");
		json.put("formaat", "");
		json.put("taal", "eng");
		json.put("bestandsnaam", "");
		json.put("inhoud", Base64.getEncoder().encodeToString("some file content".getBytes()));
		json.put("bestandsomvang", "some file content".getBytes().length);
		json.put("link", "");
		json.put("beschrijving", "");
		json.put("informatieobjecttype", informatieobjecttypeUrl);
		json.put("vertrouwelijkheidaanduiding", "");
		json.put("indicatieGebruiksrecht", "");
		// json.put("ondertekening", "");
		// json.put("integriteit", "");
		json.put("status", "");

		Response res = eioService.testCreate(json);

		Assert.assertEquals(res.getStatusCode(), 201);
	}

	@Test(groups = "CustomInputValidation")
	public void create_with_path_in_title() {
		EIOService eioService = new EIOService();

		JSONObject jsonObject = new JSONObject(DRCDataProvider.testCreate(informatieobjecttypeUrl));

		String foo = "foo" + randomString(10);

		jsonObject.put("identificatie", foo);
		jsonObject.put("titel",
				"L:\\Tekeningen W\\Projecten\\Pro\\Data\\2013\\13113 Renovatie sporthal te Utrecht\\Revisie\\PS-00 PS01 (2) (1)");

		Response res = eioService.testCreate(jsonObject);
		Assert.assertEquals(res.getStatusCode(), 201);

		res = eioService.listEIO(foo, null, null);
		Assert.assertEquals(res.getStatusCode(), 200);
		Assert.assertEquals((int) res.body().path("results.size()"), 1);
		Assert.assertEquals((String) res.body().path("results[0].identificatie"), foo);
	}

	@Test(groups = "CustomInputValidation")
	public void validate_count_with_102_eios() {
		EIOService eioService = new EIOService();

		JSONObject jsonObject = new JSONObject(DRCDataProvider.testCreate(informatieobjecttypeUrl));

		String foo = "search" + randomString(10);
		jsonObject.put("identificatie", foo);

		int createdItems = 102;
		int i = 0;
		Response res = null;
		while (i < createdItems) {
			res = eioService.testCreate(jsonObject);
			Assert.assertEquals(res.getStatusCode(), 201);
			i++;
		}

		wait(20000);

		// first page
		res = eioService.listEIO(foo, null, null);
		Assert.assertEquals(res.getStatusCode(), 200);
		Assert.assertEquals((int) res.body().path("results.size()"), 100);

		JsonPath json = new JsonPath(res.asString());
		Assert.assertEquals(json.getInt("count"), createdItems);
		Assert.assertNotNull(json.get("next"));

		// second page
		res = eioService.listEIO(foo, null, 2);
		Assert.assertEquals(res.getStatusCode(), 200);
		Assert.assertEquals((int) res.body().path("results.size()"), 2);

		json = new JsonPath(res.asString());
		Assert.assertEquals(json.getInt("count"), createdItems);
		Assert.assertNull(json.get("next"));
	}

	@Test(groups = "CustomInputValidation")
	public void wrong_page_number() {
		EIOService eioService = new EIOService();

		JSONObject jsonObject = new JSONObject(DRCDataProvider.testCreate(informatieobjecttypeUrl));

		String foo = "doc" + randomString(10);
		jsonObject.put("identificatie", foo);

		int createdItems = 3;
		int i = 0;
		Response res = null;
		while (i < createdItems) {
			res = eioService.testCreate(jsonObject);
			Assert.assertEquals(res.getStatusCode(), 201);
			i++;
		}

		wait(20000);

		// first page
		res = eioService.listEIO(foo, null, null);
		Assert.assertEquals(res.getStatusCode(), 200);
		Assert.assertEquals((int) res.body().path("results.size()"), createdItems);

		// wrong page
		res = eioService.listEIO(foo, null, 2);
		JsonPath json = new JsonPath(res.asString());

		Assert.assertEquals(res.getStatusCode(), 404);
		Assert.assertEquals(json.getString("code"), "not_found");
	}
}