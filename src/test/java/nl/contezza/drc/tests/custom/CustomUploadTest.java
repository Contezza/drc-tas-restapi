package nl.contezza.drc.tests.custom;

import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import nl.contezza.drc.dataprovider.DRCDataProvider;
import nl.contezza.drc.rest.RestTest;
import nl.contezza.drc.service.EIOService;
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
}
