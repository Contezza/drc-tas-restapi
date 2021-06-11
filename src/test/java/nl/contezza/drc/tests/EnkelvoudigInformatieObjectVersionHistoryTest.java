package nl.contezza.drc.tests;

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

//@Log4j2
public class EnkelvoudigInformatieObjectVersionHistoryTest extends RestTest {

	/**
	 * Create necessary dependencies.
	 */
	@BeforeTest(groups = "EnkelvoudigInformatieObjectVersionHistory")
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
	 * See {@link <a href="https://github.com/VNG-Realisatie/documenten-api/blob/1.0.0/src/drc/api/tests/test_enkelvoudiginformatieobject.py#L336">python code</a>}.
	 */
	@Test(groups = "EnkelvoudigInformatieObjectVersionHistory")
	public void test_eio_update() {
		EIOService eioService = new EIOService();
		JsonPath json = new JsonPath(eioService.testCreate(informatieobjecttypeUrl, "beschrijving1", "some content").asString());

		JSONObject createdEio = new JSONObject(json.prettify());
		String eioUrl = json.getString("url");

		// Do lock
		json = new JsonPath(eioService.lock(eioUrl).asString());

		JSONObject body = new JSONObject();
		body.put("beschrijving", "beschrijving2");
		body.put("inhoud", Base64.getEncoder().encodeToString("some file content".getBytes()));
		body.put("lock", json.getString("lock"));

		JSONObject mergedJson = mergeJSONObjects(createdEio, body);
		mergedJson.remove("ondertekening");
		mergedJson.remove("integriteit");

		// Update EIO
		Response res = eioService.update(eioUrl, mergedJson);
		Assert.assertEquals(res.getStatusCode(), 200);

		json = new JsonPath(res.body().asString());

		Assert.assertEquals(json.getString("beschrijving"), "beschrijving2");
		Assert.assertEquals(json.getInt("versie"), 2);

		// Get older version
		json = new JsonPath(eioService.getEIO(eioUrl, 1).asString());

		Assert.assertEquals(json.getString("beschrijving"), "beschrijving1");
	}

	/**
	 * See {@link <a href="https://github.com/VNG-Realisatie/documenten-api/blob/1.0.0/src/drc/api/tests/test_enkelvoudiginformatieobject.py#L380">python code</a>}.
	 */
	@Test(groups = "EnkelvoudigInformatieObjectVersionHistory")
	public void test_eio_partial_update() {
		EIOService eioService = new EIOService();
		JsonPath json = new JsonPath(eioService.testCreate(informatieobjecttypeUrl, "beschrijving1", "some content").asString());

		String eioUrl = json.getString("url");

		// Do lock
		json = new JsonPath(eioService.lock(eioUrl).asString());

		JSONObject body = new JSONObject();
		body.put("beschrijving", "beschrijving2");
		body.put("inhoud", Base64.getEncoder().encodeToString("some file content".getBytes()));
		body.put("lock", json.getString("lock"));

		// Update EIO
		Response res = eioService.partialUpdate(eioUrl, body);
		Assert.assertEquals(res.getStatusCode(), 200);

		json = new JsonPath(res.body().asString());

		Assert.assertEquals(json.getString("beschrijving"), "beschrijving2");
		Assert.assertEquals(json.getInt("versie"), 2);

		// Get older version
		json = new JsonPath(eioService.getEIO(eioUrl, 1).asString());

		Assert.assertEquals(json.getString("beschrijving"), "beschrijving1");
	}

	/**
	 * See {@link <a href="https://github.com/VNG-Realisatie/documenten-api/blob/1.0.0/src/drc/api/tests/test_enkelvoudiginformatieobject.py#L409">python code</a>}.
	 */
	@Test(groups = "EnkelvoudigInformatieObjectVersionHistory")
	public void test_eio_delete() {
		EIOService eioService = new EIOService();
		JsonPath json = new JsonPath(eioService.testCreate(informatieobjecttypeUrl, "beschrijving1", "some content").asString());

		String eioUrl = json.getString("url");

		// Do lock
		json = new JsonPath(eioService.lock(eioUrl).asString());

		JSONObject body = new JSONObject();
		body.put("beschrijving", "beschrijving2");
		body.put("inhoud", Base64.getEncoder().encodeToString("some file content".getBytes()));
		body.put("lock", json.getString("lock"));

		eioService.partialUpdate(eioUrl, body);

		// Delete EIO
		Response res = eioService.delete(eioUrl);

		Assert.assertEquals(res.getStatusCode(), 204);

		// Validate EIO not exists
		res = eioService.getEIO(eioUrl, 1);
		Assert.assertEquals(res.getStatusCode(), 404);

		json = new JsonPath(res.asString());
		Assert.assertEquals(json.getString("code"), "not_found");
	}

	/**
	 * See {@link <a href="https://github.com/VNG-Realisatie/documenten-api/blob/1.0.0/src/drc/api/tests/test_enkelvoudiginformatieobject.py#L423">python code</a>}.
	 */
	@Test(groups = "EnkelvoudigInformatieObjectVersionHistory")
	public void test_eio_detail_retrieves_latest_version() {
		EIOService eioService = new EIOService();
		JsonPath json = new JsonPath(eioService.testCreate(informatieobjecttypeUrl, "beschrijving1", "some content").asString());

		String eioUrl = json.getString("url");

		json = new JsonPath(eioService.lock(eioUrl).asString());

		JSONObject body = new JSONObject();
		body.put("beschrijving", "beschrijving2");
		body.put("inhoud", Base64.getEncoder().encodeToString("some file content".getBytes()));
		body.put("lock", json.getString("lock"));

		Response res = eioService.partialUpdate(eioUrl, body);

		Assert.assertEquals(res.getStatusCode(), 200);

		json = new JsonPath(res.asString());

		Assert.assertEquals(json.getString("beschrijving"), "beschrijving2");
	}

	/**
	 * See {@link <a href="https://github.com/VNG-Realisatie/documenten-api/blob/1.0.0/src/drc/api/tests/test_enkelvoudiginformatieobject.py#L436">python code</a>}.
	 */
	@Test(groups = "EnkelvoudigInformatieObjectVersionHistory")
	public void test_eio_list_shows_latest_versions() {
		EIOService eioService = new EIOService();
		JsonPath json = new JsonPath(eioService.testCreate(informatieobjecttypeUrl, "object1", "some content").asString());

		String eioUrl = json.getString("url");

		json = new JsonPath(eioService.lock(eioUrl).asString());

		JSONObject body = new JSONObject();
		body.put("beschrijving", "object1 versie2");
		body.put("inhoud", Base64.getEncoder().encodeToString("some file content".getBytes()));
		body.put("lock", json.getString("lock"));

		Response res1 = eioService.partialUpdate(eioUrl, body);
		JsonPath json1 = new JsonPath(res1.body().asString());

		json = new JsonPath(eioService.testCreate(informatieobjecttypeUrl, "object2", "some content").asString());

		eioUrl = json.getString("url");

		json = new JsonPath(eioService.lock(eioUrl).asString());

		body = new JSONObject();
		body.put("beschrijving", "object2 versie2");
		body.put("inhoud", Base64.getEncoder().encodeToString("some file content".getBytes()));
		body.put("lock", json.getString("lock"));

		Response res2 = eioService.partialUpdate(eioUrl, body);
		JsonPath json2 = new JsonPath(res2.body().asString());

		Assert.assertEquals(res1.getStatusCode(), 200);
		Assert.assertEquals(json1.getString("beschrijving"), "object1 versie2");

		Assert.assertEquals(res2.getStatusCode(), 200);
		Assert.assertEquals(json2.getString("beschrijving"), "object2 versie2");
	}

	/**
	 * See {@link <a href="https://github.com/VNG-Realisatie/documenten-api/blob/1.0.0/src/drc/api/tests/test_enkelvoudiginformatieobject.py#L462">python code</a>}.
	 */
	@Test(groups = "EnkelvoudigInformatieObjectVersionHistory")
	public void test_eio_detail_filter_by_version() {
		EIOService eioService = new EIOService();
		JsonPath json = new JsonPath(eioService.testCreate(informatieobjecttypeUrl, "beschrijving1", "some content").asString());

		String eioUrl = json.getString("url");

		json = new JsonPath(eioService.lock(eioUrl).asString());

		JSONObject body = new JSONObject();
		body.put("beschrijving", "beschrijving2");
		body.put("inhoud", Base64.getEncoder().encodeToString("some file content".getBytes()));
		body.put("lock", json.getString("lock"));

		eioService.partialUpdate(eioUrl, body);

		Response res = eioService.getEIO(eioUrl, 1);
		json = new JsonPath(res.asString());

		Assert.assertEquals(res.getStatusCode(), 200);
		Assert.assertEquals(json.getString("beschrijving"), "beschrijving1");

	}

	/**
	 * See {@link <a href="https://github.com/VNG-Realisatie/documenten-api/blob/1.0.0/src/drc/api/tests/test_enkelvoudiginformatieobject.py#L475">python code</a>}.
	 */
	@Test(groups = "EnkelvoudigInformatieObjectVersionHistory")
	public void test_eio_detail_filter_by_wrong_version_gives_404() {
		EIOService eioService = new EIOService();
		JsonPath json = new JsonPath(eioService.testCreate(informatieobjecttypeUrl, "beschrijving1", "some content").asString());

		String eioUrl = json.getString("url");

		json = new JsonPath(eioService.lock(eioUrl).asString());

		JSONObject body = new JSONObject();
		body.put("beschrijving", "beschrijving2");
		body.put("inhoud", Base64.getEncoder().encodeToString("some file content".getBytes()));
		body.put("lock", json.getString("lock"));

		eioService.partialUpdate(eioUrl, body);

		Response res = eioService.getEIO(eioUrl, 100);
		json = new JsonPath(res.asString());

		Assert.assertEquals(res.getStatusCode(), 404);
	}

	@Test(groups = "EnkelvoudigInformatieObjectVersionHistory")
	public void test_eio_detail_filter_by_registratie_op() {

		EIOService eioService = new EIOService();
		JsonPath json = new JsonPath(eioService.testCreate(informatieobjecttypeUrl, "beschrijving1", "some content").asString());

		wait(2000);

		Date date = new Date();

		String eioUrl = json.getString("url");

		json = new JsonPath(eioService.lock(eioUrl).asString());

		JSONObject body = new JSONObject();
		body.put("beschrijving", "beschrijving2");
		body.put("inhoud", Base64.getEncoder().encodeToString("some file content".getBytes()));
		body.put("lock", json.getString("lock"));

		eioService.partialUpdate(eioUrl, body);

		Response res = eioService.getEIO(eioUrl, null, StringDate.toISO8601(date));

		Assert.assertEquals(res.getStatusCode(), 200);

		json = new JsonPath(res.body().asString());

		// FIXME: diff with seconds works not as expected
		Assert.assertEquals(json.getString("beschrijving"), "beschrijving1");
	}

	/**
	 * See {@link <a href="https://github.com/VNG-Realisatie/documenten-api/blob/1.0.0/src/drc/api/tests/test_enkelvoudiginformatieobject.py#L505">python code</a>}.
	 */
	@Test(groups = "EnkelvoudigInformatieObjectVersionHistory")
	public void test_eio_detail_filter_by_wrong_registratie_op_gives_404() {
		EIOService eioService = new EIOService();
		JsonPath json = new JsonPath(eioService.testCreate(informatieobjecttypeUrl, new Date()).asString());

		String eioUrl = json.getString("url");

		Response res = eioService.getEIO(eioUrl, null, StringDate.toDatetimeString(2019, 1, 1));

		Assert.assertEquals(res.getStatusCode(), 404);
	}

	/**
	 * See {@link <a href="https://github.com/VNG-Realisatie/documenten-api/blob/1.0.0/src/drc/api/tests/test_enkelvoudiginformatieobject.py#L517">python code</a>}.
	 */
	@Test(groups = "EnkelvoudigInformatieObjectVersionHistory")
	public void test_eio_download_content_filter_by_version() {
		EIOService eioService = new EIOService();
		JsonPath json = new JsonPath(eioService.testCreate(informatieobjecttypeUrl, "beschrijving1", "inhoud1").asString());

		String eioUrl = json.getString("url");

		json = new JsonPath(eioService.lock(eioUrl).asString());

		JSONObject body = new JSONObject();
		body.put("beschrijving", "beschrijving2");
		body.put("inhoud", Base64.getEncoder().encodeToString("inhoud2".getBytes()));
		body.put("lock", json.getString("lock"));

		eioService.partialUpdate(eioUrl, body);

		json = new JsonPath(eioService.getEIO(eioUrl, 1, null).asString());

		String data = eioService.downloadAsString(json.getString("inhoud"));
		Assert.assertEquals(data, "inhoud1");
	}

	/**
	 * See {@link <a href="https://github.com/VNG-Realisatie/documenten-api/blob/1.0.0/src/drc/api/tests/test_enkelvoudiginformatieobject.py#L540">python code</a>}.
	 */
	@Test(groups = "EnkelvoudigInformatieObjectVersionHistory")
	public void test_eio_download_content_filter_by_registratie() {
		EIOService eioService = new EIOService();
		JsonPath json = new JsonPath(eioService.testCreate(informatieobjecttypeUrl, "beschrijving1", "inhoud1").asString());

		wait(2000);

		Date date = new Date();

		String eioUrl = json.getString("url");

		json = new JsonPath(eioService.lock(eioUrl).asString());

		JSONObject body = new JSONObject();
		body.put("beschrijving", "beschrijving2");
		body.put("inhoud", Base64.getEncoder().encodeToString("inhoud2".getBytes()));
		body.put("lock", json.getString("lock"));

		wait(2000);

		eioService.partialUpdate(eioUrl, body);

		json = new JsonPath(eioService.getEIO(eioUrl, null, StringDate.toISO8601(date)).asString());

		String data = eioService.downloadAsString(json.getString("inhoud"));
		Assert.assertEquals(data, "inhoud1");
	}
}