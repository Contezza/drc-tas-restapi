package nl.contezza.drc.tests.custom;

import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import nl.contezza.drc.rest.RestTest;
import nl.contezza.drc.service.AuthService;
import nl.contezza.drc.service.DRCRequestSpecification;
import nl.contezza.drc.service.EIOService;
import nl.contezza.drc.service.ZTCService;

/**
 * Some custom unit tests which are not mapped to any python scripts.
 */

//@Log4j2
public class CustomFilterParamsTest extends RestTest {

	private String informatieobjecttypeUrl2 = null;

	/**
	 * Create necessary dependencies.
	 */
	@BeforeTest(groups = "CustomFilterParams")
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

		Response res = ztcService.publishInformatieObjectType(informatieobjecttypeUrl.substring(informatieobjecttypeUrl.lastIndexOf('/') + 1).trim());
		Assert.assertEquals(res.getStatusCode(), 200);

		res = ztcService.publishInformatieObjectType(informatieobjecttypeUrl2.substring(informatieobjecttypeUrl2.lastIndexOf('/') + 1).trim());
		Assert.assertEquals(res.getStatusCode(), 200);
	}

	@Test(groups = "CustomFilterParams")
	public void filter_with_multiple_iot_without_allAuth() {

		AuthService authService = new AuthService();

		JSONArray scopes = new JSONArray().put("documenten.lezen").put("documenten.aanmaken").put("documenten.bijwerken").put("documenten.lock");
		String maxVertrouwelijkheid = "zeer_geheim";

		JSONObject comp = new JSONObject();
		comp.put("component", "drc");
		comp.put("scopes", scopes);
		comp.put("informatieobjecttype", informatieobjecttypeUrl);
		comp.put("maxVertrouwelijkheidaanduiding", maxVertrouwelijkheid);

		JSONObject comp2 = new JSONObject();
		comp2.put("component", "drc");
		comp2.put("scopes", scopes);
		comp2.put("informatieobjecttype", informatieobjecttypeUrl2);
		comp2.put("maxVertrouwelijkheidaanduiding", maxVertrouwelijkheid);

		JSONArray auths = new JSONArray().put(comp).put(comp2);

		authService.updateReadOnlyClientScope(scopes, auths, false);

		wait(2000);

		EIOService eioService = new EIOService();

		String bronorganisatie = "159351741";
		String foo = "foo" + randomString(10);
		String bar = "bar" + randomString(10);

		eioService.testCreate(informatieobjecttypeUrl, foo);
		eioService.testCreate(informatieobjecttypeUrl2, foo);
		eioService.testCreate(informatieobjecttypeUrl, bar);

		// Including filter
		Response res = eioService.listEIO(DRCRequestSpecification.getReadonly(), foo, bronorganisatie, null);

		Assert.assertEquals(res.getStatusCode(), 200);
		Assert.assertEquals((int) res.body().path("results.size()"), 2);
		Assert.assertEquals((String) res.body().path("results[0].identificatie"), foo);
		
		// Without any filter
		res = eioService.listEIO(DRCRequestSpecification.getReadonly(), null, null, null);

		Assert.assertEquals(res.getStatusCode(), 200);
		Assert.assertEquals((int) res.body().path("results.size()"), 3);
	}
}