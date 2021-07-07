package nl.contezza.drc.tests.custom;

import static io.restassured.RestAssured.given;

import java.util.Date;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import nl.contezza.drc.rest.RestTest;
import nl.contezza.drc.service.DRCRequestSpecification;
import nl.contezza.drc.service.EIOService;
import nl.contezza.drc.service.ZTCService;

/**
 * Some custom unit tests which are not mapped to any python scripts.
 */

//@Log4j2
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

		Response res = ztcService.publishInformatieObjectType(informatieobjecttypeUrl.substring(informatieobjecttypeUrl.lastIndexOf('/') + 1).trim());
		Assert.assertEquals(res.getStatusCode(), 200);
	}

	@Test(groups = "CustomInputValidation")
	public void empty_body_patch() {

		EIOService eioService = new EIOService();
		// Create EIO
		JsonPath json = new JsonPath(eioService.testCreate(informatieobjecttypeUrl, "beschrijving0", "some content0", new Date()).asString());
		String eioUrl = json.getString("url");

		String id = eioUrl.substring(eioUrl.lastIndexOf('/') + 1).trim();
		Response res = given().spec(DRCRequestSpecification.getDefault()).when().patch("/enkelvoudiginformatieobjecten/" + id + "/unlock").then().extract().response();

		Assert.assertEquals(res.getStatusCode(), 405);
	}
}