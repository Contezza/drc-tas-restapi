package nl.contezza.drc.tests.custom;

import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import nl.contezza.drc.rest.RestTest;
import nl.contezza.drc.service.EIOService;
import nl.contezza.drc.service.ZTCService;

/**
 * Some custom unit tests which are not mapped to any python scripts.
 */

// @Log4j2
public class CustomUpdateIOTTest extends RestTest {

	private String informatieobjecttypeUrl2 = null;

	/**
	 * Create necessary dependencies.
	 */
	@BeforeTest(groups = "CustomUpdateIOT")
	public void init() {
		// Create random catalogi
		ZTCService ztcService = new ZTCService();
		JsonPath json = new JsonPath(ztcService.createCatalogus().asString());

		// Create informatieobjecttype
		String catalogusUrl = json.getString("url").replace(ZTC_BASE_URI, ZTC_DOCKER_URI);
		json = new JsonPath(ztcService.createInformatieObjectType(catalogusUrl).asString());
		informatieobjecttypeUrl = json.getString("url").replace(ZTC_BASE_URI, ZTC_DOCKER_URI);

		// Create informatieobjecttype 2
		json = new JsonPath(ztcService.createInformatieObjectType(catalogusUrl).asString());
		informatieobjecttypeUrl2 = json.getString("url").replace(ZTC_BASE_URI, ZTC_DOCKER_URI);

		Response res = ztcService.publishInformatieObjectType(
				informatieobjecttypeUrl.substring(informatieobjecttypeUrl.lastIndexOf('/') + 1).trim());
		Assert.assertEquals(res.getStatusCode(), 200);

		res = ztcService.publishInformatieObjectType(
				informatieobjecttypeUrl2.substring(informatieobjecttypeUrl2.lastIndexOf('/') + 1).trim());
		Assert.assertEquals(res.getStatusCode(), 200);
	}

	@Test(groups = "CustomUpdateIOT")
	public void test_update_IOT() {
		EIOService eioService = new EIOService();

		// Create EIO
		String eioUrl = new JsonPath(eioService.testCreate(informatieobjecttypeUrl).asString()).getString("url");

		// Do lock
		JsonPath json = new JsonPath(eioService.lock(eioUrl).asString());

		JSONObject body = new JSONObject();
		body.put("informatieobjecttype", informatieobjecttypeUrl2);
		body.put("lock", json.getString("lock"));

		Response res = eioService.partialUpdate(eioUrl, body);
		Assert.assertEquals(res.getStatusCode(), 200);
	}
}