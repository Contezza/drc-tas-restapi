package nl.contezza.drc.tests.custom;

import java.io.File;

import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import nl.contezza.drc.dataprovider.DRCDataProvider;
import nl.contezza.drc.rest.RestTest;
import nl.contezza.drc.service.EIOService;
import nl.contezza.drc.service.UploadService;
import nl.contezza.drc.service.ZTCService;

public class CustomUploadTest extends RestTest {

    /**
     * Create necessary dependencies when creating enkelvoudiginformatieobject.
     */
    @BeforeTest(groups = "CustomUpload")
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

    @Test(groups = "CustomUpload")
    public void test_bestandsdelen_when_create() {

        EIOService eioService = new EIOService();

        JSONObject jsonObject = new JSONObject(DRCDataProvider.testCreate(informatieobjecttypeUrl));
        // jsonObject.put("inhoud", JSONObject.NULL);
        jsonObject.remove("inhoud");
        jsonObject.put("bestandsomvang", "some content for file".getBytes().length);

        Response res = eioService.testCreate(jsonObject);
        JsonPath json = new JsonPath(res.asString());

        Assert.assertEquals(res.getStatusCode(), 201);
        Assert.assertEquals(json.getList("bestandsdelen").size(), 1);
        Assert.assertEquals(json.getBoolean("locked"), true);
        Assert.assertEquals(json.getInt("bestandsomvang"), "some content for file".getBytes().length);
        Assert.assertNull(res.getBody().path("inhoud"));
    }

    @Test(groups = "CustomUpload")
    public void test_upload_flow_bestandsdelen() {
        UploadService uploadService = new UploadService();
        EIOService eioService = new EIOService();

        // Create file with empty inhoud
        JSONObject jsonObject = new JSONObject(DRCDataProvider.testCreate(informatieobjecttypeUrl));
        jsonObject.put("inhoud", JSONObject.NULL);
        jsonObject.put("bestandsomvang", "some content for file".getBytes().length);

        Response res = eioService.testCreate(jsonObject);
        String eioUrl = res.body().path("url");

        String uploadUrl = res.body().path("bestandsdelen[0].url");
        String lock = res.body().path("lock");

        // Upload file part
        File file = uploadService.createTextFile("some content for file");
        res = uploadService.uploadFile(uploadUrl, lock, file);

        Assert.assertEquals(res.getStatusCode(), 200);

        // Validate if still is locked
        res = eioService.getEIO(eioUrl, null);

        JsonPath json = new JsonPath(res.asString());
        Assert.assertEquals(res.getStatusCode(), 200);
        Assert.assertEquals(json.getList("bestandsdelen").size(), 1);
        Assert.assertEquals(json.getBoolean("locked"), true);

        // Unlock
        res = eioService.unlock(eioUrl, lock);

        Assert.assertEquals(res.getStatusCode(), 204);

        // Validate if all is normal
        res = eioService.getEIO(eioUrl, null);

        json = new JsonPath(res.asString());
        Assert.assertEquals(res.getStatusCode(), 200);
        Assert.assertNotNull(res.getBody().path("inhoud"));
        Assert.assertEquals(json.getList("bestandsdelen").size(), 0);
        Assert.assertEquals(json.getBoolean("locked"), false);
    }

    @Test(groups = "CustomUpload")
    public void test_create_empty_string_inhoud() {
        EIOService eioService = new EIOService();

        JSONObject jsonObject = new JSONObject(DRCDataProvider.testCreate(informatieobjecttypeUrl));
        jsonObject.put("inhoud", "");
        jsonObject.put("bestandsomvang", 0);

        Response res = eioService.testCreate(jsonObject);

        Assert.assertEquals(res.getStatusCode(), 201);
    }
}
