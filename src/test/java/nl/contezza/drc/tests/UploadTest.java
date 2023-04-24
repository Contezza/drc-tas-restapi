package nl.contezza.drc.tests;

import java.io.File;
import java.util.Base64;

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
import nl.contezza.drc.service.UploadService;
import nl.contezza.drc.service.ZTCService;

//@Log4j2
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
	@Test(groups = "Upload")
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
	@Test(groups = "Upload")
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
	@Test(groups = "Upload")
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
	@Test(groups = "Upload")
	public void test_create_without_size() {
		EIOService eioService = new EIOService();

		JSONObject jsonObject = new JSONObject(DRCDataProvider.testCreate(informatieobjecttypeUrl));
		jsonObject.remove("bestandsomvang");

		Response res = eioService.testCreate(jsonObject);
		Assert.assertEquals(res.getStatusCode(), 400);
		Assert.assertEquals(res.body().path("invalidParams[0].code"), "file-size");
	}

	/**
	 * See {@link <a href=
	 * "https://github.com/VNG-Realisatie/documenten-api/blob/1.3.0/src/drc/tests/test_upload.py#L217">python
	 * code</a>}.
	 */
	@Test(groups = "Upload")
	public void test_update_eio_metadata() {
		EIOService eioService = new EIOService();

		String eioUrl = new JsonPath(eioService.testCreate(informatieobjecttypeUrl).asString()).getString("url");
		String lock = new JsonPath(eioService.lock(eioUrl).asString()).getString("lock");

		JSONObject body = new JSONObject();
		body.put("titel", "another summary");
		body.put("lock", lock);

		Response res = eioService.partialUpdate(eioUrl, body);
		JsonPath json = new JsonPath(res.asString());

		Assert.assertEquals(res.getStatusCode(), 200);
		Assert.assertEquals(json.getString("titel"), "another summary");
		Assert.assertEquals(json.getString("versie"), "2");
		Assert.assertEquals(json.getList("bestandsdelen").size(), 0);
	}

	/**
	 * See {@link <a href=
	 * "https://github.com/VNG-Realisatie/documenten-api/blob/1.3.0/src/drc/tests/test_upload.py#L258">python
	 * code</a>}.
	 */
	@Test(groups = "Upload")
	public void test_update_eio_file() {
		EIOService eioService = new EIOService();

		String eioUrl = new JsonPath(eioService.testCreate(informatieobjecttypeUrl).asString()).getString("url");
		String lock = new JsonPath(eioService.lock(eioUrl).asString()).getString("lock");

		JSONObject body = new JSONObject();
		body.put("inhoud", Base64.getEncoder().encodeToString("some other file content".getBytes()));
		body.put("bestandsomvang", "some other file content".getBytes().length);
		body.put("lock", lock);

		Response res = eioService.partialUpdate(eioUrl, body);
		JsonPath json = new JsonPath(res.asString());

		String data = eioService.downloadAsString(json.getString("inhoud"));

		Assert.assertEquals(res.getStatusCode(), 200);
		Assert.assertEquals(data, "some other file content");
	}

	/**
	 * See {@link <a href=
	 * "https://github.com/VNG-Realisatie/documenten-api/blob/1.3.0/src/drc/tests/test_upload.py#L304">python
	 * code</a>}.
	 */
	@Test(groups = "Upload")
	public void test_update_eio_file_set_empty() {
		EIOService eioService = new EIOService();

		String eioUrl = new JsonPath(eioService.testCreate(informatieobjecttypeUrl).asString()).getString("url");
		String lock = new JsonPath(eioService.lock(eioUrl).asString()).getString("lock");

		JSONObject body = new JSONObject();
		body.put("inhoud", JSONObject.NULL);
		body.put("bestandsomvang", JSONObject.NULL);
		body.put("lock", lock);

		Response res = eioService.partialUpdate(eioUrl, body);
		JsonPath json = new JsonPath(res.asString());

		Assert.assertEquals(res.getStatusCode(), 200);
		Assert.assertNull(json.getString("inhoud"));
		Assert.assertNull(json.getString("bestandsomvang"));
		Assert.assertEquals(json.getList("bestandsdelen").size(), 0);
	}

	/**
	 * See {@link <a href=
	 * "https://github.com/VNG-Realisatie/documenten-api/blob/1.3.0/src/drc/tests/test_upload.py#L341">python
	 * code</a>}.
	 */
	@Test(groups = "Upload")
	public void test_update_eio_only_size() {
		EIOService eioService = new EIOService();

		String eioUrl = new JsonPath(eioService.testCreate(informatieobjecttypeUrl).asString()).getString("url");
		String lock = new JsonPath(eioService.lock(eioUrl).asString()).getString("lock");

		JSONObject body = new JSONObject();
		body.put("bestandsomvang", 20);
		body.put("lock", lock);

		Response res = eioService.partialUpdate(eioUrl, body);

		Assert.assertEquals(res.getStatusCode(), 400);
		Assert.assertEquals(res.body().path("invalidParams[0].name"), "nonFieldErrors");
		Assert.assertEquals(res.body().path("invalidParams[0].code"), "file-size");
	}

	/**
	 * See {@link <a href=
	 * "https://github.com/VNG-Realisatie/documenten-api/blob/1.3.0/src/drc/tests/test_upload.py#L371">python
	 * code</a>}.
	 */
	@Test(groups = "Upload")
	public void test_update_eio_only_file_without_size() {
		EIOService eioService = new EIOService();

		String eioUrl = new JsonPath(eioService.testCreate(informatieobjecttypeUrl).asString()).getString("url");
		String lock = new JsonPath(eioService.lock(eioUrl).asString()).getString("lock");

		JSONObject body = new JSONObject();
		body.put("inhoud", Base64.getEncoder().encodeToString("some other file content".getBytes()));
		body.put("lock", lock);

		Response res = eioService.partialUpdate(eioUrl, body);

		Assert.assertEquals(res.getStatusCode(), 400);
		Assert.assertEquals(res.body().path("invalidParams[0].name"), "nonFieldErrors");
		Assert.assertEquals(res.body().path("invalidParams[0].code"), "file-size");
	}

	/**
	 * See {@link <a href=
	 * "https://github.com/VNG-Realisatie/documenten-api/blob/1.3.0/src/drc/tests/test_upload.py#L606">python
	 * code</a>}.
	 */
	@Test(groups = "Upload")
	public void test_upload_part_wrong_size() {
		UploadService uploadService = new UploadService();
		EIOService eioService = new EIOService();

		JSONObject jsonObject = new JSONObject(DRCDataProvider.testCreate(informatieobjecttypeUrl));
		jsonObject.put("inhoud", JSONObject.NULL);
		jsonObject.put("bestandsomvang", "some content for file".getBytes().length + 1);

		Response res = eioService.testCreate(jsonObject);

		String uploadUrl = res.body().path("bestandsdelen[0].url");
		String lock = res.body().path("lock");

		File file = uploadService.createTextFile("some content for file");
		res = uploadService.uploadFile(uploadUrl, lock, file);

		Assert.assertEquals(res.getStatusCode(), 400);
		Assert.assertEquals(res.body().path("invalidParams[0].name"), "nonFieldErrors");
		Assert.assertEquals(res.body().path("invalidParams[0].code"), "file-size");
	}

	/**
	 * See {@link <a href=
	 * "https://github.com/VNG-Realisatie/documenten-api/blob/1.3.0/src/drc/tests/test_upload.py#L672">python
	 * code</a>}.
	 */
	@Test(groups = "Upload")
	public void test_unlock_without_uploading() {
		AuthService authService = new AuthService();

		JSONArray scopes = new JSONArray().put("documenten.lezen").put("documenten.aanmaken")
				.put("documenten.bijwerken").put("documenten.lock");
		String maxVertrouwelijkheid = "zeer_geheim";

		JSONObject comp = new JSONObject();
		comp.put("component", "drc");
		comp.put("scopes", scopes);
		comp.put("informatieobjecttype", informatieobjecttypeUrl);
		comp.put("maxVertrouwelijkheidaanduiding", maxVertrouwelijkheid);

		authService.updateReadOnlyClientScope(scopes, new JSONArray().put(comp), false);

		wait(2000);

		EIOService eioService = new EIOService();

		JSONObject jsonObject = new JSONObject(DRCDataProvider.testCreate(informatieobjecttypeUrl));
		jsonObject.put("inhoud", JSONObject.NULL);
		jsonObject.put("bestandsomvang", "some content for file".getBytes().length);

		Response res = eioService.testCreate(DRCRequestSpecification.getReadonly(), jsonObject);

		String eioUrl = res.body().path("url");
		String uploadUrl = res.body().path("bestandsdelen[0].url");
		String lock = res.body().path("lock");

		Assert.assertNotNull(uploadUrl);
		Assert.assertNotNull(lock);

		res = eioService.unlock(DRCRequestSpecification.getReadonly(), eioUrl, lock);

		Assert.assertEquals(res.getStatusCode(), 400);
		Assert.assertEquals(res.body().path("invalidParams[0].name"), "nonFieldErrors");
		Assert.assertEquals(res.body().path("invalidParams[0].code"), "file-size");
	}

	/**
	 * See {@link <a href=
	 * "https://github.com/VNG-Realisatie/documenten-api/blob/1.3.0/src/drc/tests/test_upload.py#L735">python
	 * code</a>}.
	 */
	@Test(groups = "Upload")
	public void test_unlock_not_finish_upload_force() {
		AuthService authService = new AuthService();

		JSONArray scopes = new JSONArray().put("documenten.lezen").put("documenten.aanmaken")
				.put("documenten.bijwerken").put("documenten.lock").put("documenten.geforceerd-unlock");
		String maxVertrouwelijkheid = "zeer_geheim";

		JSONObject comp = new JSONObject();
		comp.put("component", "drc");
		comp.put("scopes", scopes);
		comp.put("informatieobjecttype", informatieobjecttypeUrl);
		comp.put("maxVertrouwelijkheidaanduiding", maxVertrouwelijkheid);

		authService.updateReadOnlyClientScope(scopes, new JSONArray().put(comp), false);

		wait(2000);

		EIOService eioService = new EIOService();

		JSONObject jsonObject = new JSONObject(DRCDataProvider.testCreate(informatieobjecttypeUrl));
		jsonObject.put("inhoud", JSONObject.NULL);
		jsonObject.put("bestandsomvang", "some content for file".getBytes().length);

		Response res = eioService.testCreate(DRCRequestSpecification.getReadonly(), jsonObject);

		String eioUrl = res.body().path("url");
		String uploadUrl = res.body().path("bestandsdelen[0].url");
		String lock = res.body().path("lock");

		Assert.assertNotNull(uploadUrl);
		Assert.assertNotNull(lock);

		res = eioService.unlock(DRCRequestSpecification.getReadonly(), eioUrl, lock);

		JsonPath json = new JsonPath(eioService.getEIO(DRCRequestSpecification.getReadonly(), eioUrl, null).asString());

		// When no parts are uploaded, apparently bestandsdelen will be present
		Assert.assertEquals(res.getStatusCode(), 204);
		Assert.assertEquals(json.getList("bestandsdelen").size(), 1);
		Assert.assertNull(json.get("inhoud"));
	}

	/**
	 * See {@link <a href=
	 * "https://github.com/VNG-Realisatie/documenten-api/blob/1.3.0/src/drc/tests/test_upload.py#L782">python
	 * code</a>}.
	 */
	@Test(groups = "Upload")
	public void test_update_metadata_without_upload() {

		EIOService eioService = new EIOService();

		JSONObject jsonObject = new JSONObject(DRCDataProvider.testCreate(informatieobjecttypeUrl));
		jsonObject.put("inhoud", JSONObject.NULL);
		jsonObject.put("bestandsomvang", "some content for file".getBytes().length + 1);

		Response res = eioService.testCreate(jsonObject);

		JSONObject body = new JSONObject();
		body.put("beschrijving", "beschrijving2");
		body.put("lock", res.body().path("lock").toString());

		res = eioService.partialUpdate(res.body().path("url"), body);

		JsonPath json = new JsonPath(res.asString());

		Assert.assertEquals(res.getStatusCode(), 200);
		Assert.assertEquals(json.getList("bestandsdelen").size(), 1);
		Assert.assertEquals(json.getString("beschrijving"), "beschrijving2");
		Assert.assertNull(res.getBody().path("inhoud"));
	}

	/**
	 * See {@link <a href=
	 * "https://github.com/VNG-Realisatie/documenten-api/blob/1.3.0/src/drc/tests/test_upload.py#L889">python
	 * code</a>}.
	 */
	@Test(groups = "Upload")
	public void test_update_metadata_set_size_zero() {
		EIOService eioService = new EIOService();

		JSONObject jsonObject = new JSONObject(DRCDataProvider.testCreate(informatieobjecttypeUrl));
		jsonObject.put("inhoud", JSONObject.NULL);
		jsonObject.put("bestandsomvang", "some content for file".getBytes().length);

		Response res = eioService.testCreate(jsonObject);

		String lock = res.body().path("lock");

		JSONObject body = new JSONObject();
		body.put("bestandsomvang", 0);
		body.put("lock", lock);

		res = eioService.partialUpdate(res.body().path("url"), body);
		JsonPath json = new JsonPath(res.asString());

		Assert.assertEquals(res.getStatusCode(), 200);
		Assert.assertEquals(json.getList("bestandsdelen").size(), 0);
	}

	/**
	 * See {@link <a href=
	 * "https://github.com/VNG-Realisatie/documenten-api/blob/1.3.0/src/drc/tests/test_upload.py#L924">python
	 * code</a>}.
	 */
	@Test(groups = "Upload")
	public void test_update_metadata_set_size_null() {
		EIOService eioService = new EIOService();

		JSONObject jsonObject = new JSONObject(DRCDataProvider.testCreate(informatieobjecttypeUrl));
		jsonObject.put("inhoud", JSONObject.NULL);
		jsonObject.put("bestandsomvang", "some content for file".getBytes().length);

		Response res = eioService.testCreate(jsonObject);

		String lock = res.body().path("lock");

		JSONObject body = new JSONObject();
		body.put("bestandsomvang", JSONObject.NULL);
		body.put("lock", lock);

		res = eioService.partialUpdate(res.body().path("url"), body);
		JsonPath json = new JsonPath(res.asString());

		Assert.assertEquals(res.getStatusCode(), 200);
		Assert.assertEquals(json.getList("bestandsdelen").size(), 0);
		Assert.assertNull(json.get("bestandsomvang"));
		Assert.assertNull(json.get("inhoud"));
	}
}
