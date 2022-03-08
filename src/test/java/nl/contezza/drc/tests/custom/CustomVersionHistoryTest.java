package nl.contezza.drc.tests.custom;

import java.util.Base64;
import java.util.Date;

import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import nl.contezza.drc.rest.RestTest;
import nl.contezza.drc.service.EIOService;
import nl.contezza.drc.service.ZTCService;
import nl.contezza.drc.utils.StringDate;

/**
 * Some custom unit tests which are not mapped to any python scripts.
 */

//@Log4j2
public class CustomVersionHistoryTest extends RestTest {

	/**
	 * Create necessary dependencies.
	 */
	@BeforeTest(groups = "CustomVersionHistory")
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

	@Test(groups = "CustomVersionHistory")
	public void test_registratie_op_filter() {

		EIOService eioService = new EIOService();
		// Create EIO
		JsonPath json = new JsonPath(eioService.testCreate(informatieobjecttypeUrl, "beschrijving0", "some content0", new Date()).asString());
		String eioUrl = json.getString("url");

		// Update 1
		createVersion(eioUrl, "beschrijving1", "some file content1");

		wait(2000);
		Date now1 = new Date();

		// Update 2
		createVersion(eioUrl, "beschrijving2", "some file content2");

		wait(2000);
		Date now2 = new Date();

		Response res = eioService.getEIO(eioUrl, null, StringDate.toISO8601(now1));

		Assert.assertEquals(res.getStatusCode(), 200);
		json = new JsonPath(res.body().asString());
		Assert.assertEquals(json.getString("beschrijving"), "beschrijving1");

		res = eioService.getEIO(eioUrl, null, StringDate.toISO8601(now2));

		Assert.assertEquals(res.getStatusCode(), 200);
		json = new JsonPath(res.body().asString());
		Assert.assertEquals(json.getString("beschrijving"), "beschrijving2");
	}

	@Test(groups = "CustomVersionHistory")
	public void test_versie_filter() {

		EIOService eioService = new EIOService();
		// Create EIO
		JsonPath json = new JsonPath(eioService.testCreate(informatieobjecttypeUrl, "beschrijving0", "some content0", new Date()).asString()); // v1
		String eioUrl = json.getString("url");

		createVersion(eioUrl, "beschrijving1", "some content1"); // v2

		// Get version 1
		json = new JsonPath(eioService.getEIO(eioUrl, 1, null).asString());

		Assert.assertEquals(json.getString("beschrijving"), "beschrijving0");
		Assert.assertEquals(json.getInt("versie"), 1);
		Assert.assertEquals(eioService.downloadAsString(json.getString("inhoud")), "some content0");

		// Get version 2
		json = new JsonPath(eioService.getEIO(eioUrl, 2, null).asString());

		Assert.assertEquals(json.getString("beschrijving"), "beschrijving1");
		Assert.assertEquals(json.getInt("versie"), 2);
	}
	
	@Test(groups = "CustomVersionHistory")
	public void test_update_bestandsnaam() {
		EIOService eioService = new EIOService();

		// Create EIO
		JsonPath json = new JsonPath(eioService.testCreate(informatieobjecttypeUrl, "beschrijving1", "some content").asString());
		String eioUrl = json.getString("url");

		// Lock EIO
		String lock = new JsonPath(eioService.lock(eioUrl).asString()).getString("lock");
		
		// Validate original name
		json = new JsonPath(eioService.getEIO(eioUrl, null, null).asString());
		Assert.assertEquals(json.getString("bestandsnaam"), "dummy.txt");

		// Change name
		JSONObject body = new JSONObject();
		body.put("bestandsnaam", "dummy2.txt");
		body.put("lock", lock);

		Response res = eioService.partialUpdate(eioUrl, body);
		Assert.assertEquals(res.getStatusCode(), 200);

		json = new JsonPath(res.asString());
		Assert.assertEquals(json.getString("bestandsnaam"), "dummy2.txt");

		// Unlock file
		res = eioService.unlock(eioUrl, lock);
		Assert.assertEquals(res.getStatusCode(), 204);

		json = new JsonPath(eioService.getEIO(eioUrl, null, null).asString());
		Assert.assertEquals(json.getString("bestandsnaam"), "dummy2.txt");
	}

	/**
	 * Create version by unllock, update and unlock EIO.
	 * 
	 * @param eioUrl       String eio url
	 * @param beschrijving String description
	 * @param inhoud       String some content
	 * @return Response response of unlock request
	 */
	private Response createVersion(String eioUrl, String beschrijving, String inhoud) {
		EIOService eioService = new EIOService();
		String lock = new JsonPath(eioService.lock(eioUrl).asString()).getString("lock");

		JSONObject body = new JSONObject();
		body.put("beschrijving", beschrijving);
		body.put("inhoud", Base64.getEncoder().encodeToString(inhoud.getBytes()));
		body.put("lock", lock);

		Response res = eioService.partialUpdate(eioUrl, body);
		Assert.assertEquals(res.getStatusCode(), 200);
		res = eioService.unlock(eioUrl, lock);
		Assert.assertEquals(res.getStatusCode(), 204);
		return res;
	}
}