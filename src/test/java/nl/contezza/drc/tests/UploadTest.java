package nl.contezza.drc.tests;

import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.extern.log4j.Log4j2;
import nl.contezza.drc.dataprovider.DRCDataProvider;
import nl.contezza.drc.rest.RestTest;
import nl.contezza.drc.service.EIOService;
import nl.contezza.drc.service.ZTCService;

@Log4j2
public class UploadTest extends RestTest {

	/**
	 * Create necessary dependencies when creating enkelvoudiginformatieobject.
	 */
	@BeforeTest(groups = "Upload")
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
	 * "https://github.com/VNG-Realisatie/documenten-api/blob/1.3.0/src/drc/tests/test_upload.py#L43">python
	 * code</a>}.
	 */
	// @Test(groups = "Upload")
	public void test_create_eio() {
		EIOService eioService = new EIOService();

		Response res = eioService.testCreate(informatieobjecttypeUrl);
		Assert.assertEquals(res.getStatusCode(), 201);

		JsonPath json = new JsonPath(res.asString());

		String data = eioService.downloadAsString(json.getString("inhoud"));

		Assert.assertEquals(json.getList("bestandsdelen").size(), 0);
		Assert.assertEquals(data, "some file content");
		Assert.assertEquals(json.getBoolean("locked"), false);
		Assert.assertEquals(json.getString("titel"), "detailed summary");
	}

	/**
	 * See {@link <a href=
	 * "https://github.com/VNG-Realisatie/documenten-api/blob/1.3.0/src/drc/tests/test_upload.py#L92">python
	 * code</a>}.
	 */
	// @Test(groups = "Upload")
	public void test_create_without_file() {
		EIOService eioService = new EIOService();

		JSONObject jsonObject = new JSONObject(DRCDataProvider.testCreate(informatieobjecttypeUrl));

		jsonObject.put("inhoud", JSONObject.NULL);
		jsonObject.put("bestandsomvang", JSONObject.NULL);

		Response res = eioService.testCreate(jsonObject);
		Assert.assertEquals(res.getStatusCode(), 201);

		JsonPath json = new JsonPath(res.asString());

		Assert.assertNull(json.getString("inhoud"));
		Assert.assertNull(json.getString("bestandsomvang"));
	}

	/**
	 * See {@link <a href=
	 * "https://github.com/VNG-Realisatie/documenten-api/blob/1.3.0/src/drc/tests/test_upload.py#L132">python
	 * code</a>}.
	 */
	// @Test(groups = "Upload")
	public void test_create_empty_file() {
		EIOService eioService = new EIOService();

		JSONObject jsonObject = new JSONObject(DRCDataProvider.testCreate(informatieobjecttypeUrl));

		jsonObject.put("inhoud", JSONObject.NULL);
		jsonObject.put("bestandsomvang", 0);

		Response res = eioService.testCreate(jsonObject);
		Assert.assertEquals(res.getStatusCode(), 201);

		JsonPath json = new JsonPath(res.asString());
		String data = eioService.downloadAsString(json.getString("inhoud"));

		Assert.assertEquals(data, "");
	}

	/**
	 * See {@link <a href=
	 * "https://github.com/VNG-Realisatie/documenten-api/blob/1.3.0/src/drc/tests/test_upload.py#L180">python
	 * code</a>}.
	 */
	// @Test(groups = "Upload")
	public void test_create_without_size() {
		EIOService eioService = new EIOService();

		JSONObject jsonObject = new JSONObject(DRCDataProvider.testCreate(informatieobjecttypeUrl));
		jsonObject.remove("bestandsomvang");

		Response res = eioService.testCreate(jsonObject);
		Assert.assertEquals(res.getStatusCode(), 400);
		Assert.assertEquals(res.body().path("invalidParams[0].code"), "file-size");
	}

	// TODO: create test
	@Test(groups = "Upload")
	public void test_update_eio_metadata() {

	}

	// TODO: create test
	// @Test(groups = "Upload")
	public void test_update_eio_file() {

	}

	// TODO: create test
	// @Test(groups = "Upload")
	public void test_update_eio_file_set_empty() {

	}

	// TODO: create test
	// @Test(groups = "Upload")
	public void test_update_eio_only_size() {

	}

	// TODO: create test
	// @Test(groups = "Upload")
	public void test_update_eio_only_file_without_size() {

	}

	// TODO: create test
	// @Test(groups = "Upload")
	public void test_update_eio_put() {

	}

	// TODO: create test
	// @Test(groups = "Upload")
	public void test_create_eio_full_process() {

	}

	// TODO: create test
	// @Test(groups = "Upload")
	public void test_upload_part_wrong_size() {

	}

	// TODO: create test
	// @Test(groups = "Upload")
	public void test_upload_part_twice_correct() {

	}

	// TODO: create test
	// @Test(groups = "Upload")
	public void test_unlock_without_uploading() {

	}

	// TODO: create test
	// @Test(groups = "Upload")
	public void test_unlock_not_finish_upload() {

	}

	// TODO: create test
	// @Test(groups = "Upload")
	public void test_update_metadata_without_upload() {

	}

	// TODO: create test
	// @Test(groups = "Upload")
	public void test_update_metadata_after_unfinished_upload() {

	}

	// TODO: create test
	// @Test(groups = "Upload")
	public void test_update_metadata_set_size() {

	}

	// TODO: create test
	// @Test(groups = "Upload")
	public void test_update_metadata_set_size_zero() {

	}

	// TODO: create test
	// @Test(groups = "Upload")
	public void test_update_metadata_set_size_null() {

	}
}
