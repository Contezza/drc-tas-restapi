package nl.contezza.drc.tests.custom;

import org.json.JSONArray;
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
public class CustomScopeTest extends RestTest {

	/**
	 * Create necessary dependencies.
	 */
	@BeforeTest(groups = "CustomScope")
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

	@Test(groups = "CustomScope")
	public void document_create_with_correct_scope() {

		AuthService authService = new AuthService();

		// can create document
		authService.updateReadOnlyClientScope(new JSONArray().put("documenten.lezen").put("documenten.aanmaken"), "zeer_geheim", informatieobjecttypeUrl);
		wait(2000);

		EIOService eioService = new EIOService();

		Response res = eioService.testCreate(DRCRequestSpecification.getReadonly(), informatieobjecttypeUrl);

		Assert.assertEquals(res.getStatusCode(), 201);
	}

	@Test(groups = "CustomScope")
	public void document_create_with_wrong_scope() {

		AuthService authService = new AuthService();

		// cannot create document
		authService.updateReadOnlyClientScope(new JSONArray().put("documenten.lezen"), "zeer_geheim", informatieobjecttypeUrl);
		wait(2000);

		EIOService eioService = new EIOService();

		Response res = eioService.testCreate(DRCRequestSpecification.getReadonly(), informatieobjecttypeUrl);

		Assert.assertEquals(res.getStatusCode(), 403);
	}

	@Test(groups = "CustomScope")
	public void lock_document_with_wrong_scope() {

		AuthService authService = new AuthService();

		authService.updateReadOnlyClientScope(new JSONArray().put("documenten.lezen").put("documenten.aanmaken"), "zeer_geheim", informatieobjecttypeUrl);
		wait(2000);

		EIOService eioService = new EIOService();

		JsonPath json = new JsonPath(eioService.testCreate(DRCRequestSpecification.getReadonly(), informatieobjecttypeUrl).asString());

		String eioUrl = json.getString("url");

		// cannot lock document
		Response res = eioService.lock(DRCRequestSpecification.getReadonly(), eioUrl);

		Assert.assertEquals(res.getStatusCode(), 403);
	}

	@Test(groups = "CustomScope")
	public void lock_document_with_correct_scope() {

		AuthService authService = new AuthService();

		authService.updateReadOnlyClientScope(new JSONArray().put("documenten.lezen").put("documenten.aanmaken").put("documenten.lock"), "zeer_geheim", informatieobjecttypeUrl);
		wait(2000);

		EIOService eioService = new EIOService();

		JsonPath json = new JsonPath(eioService.testCreate(DRCRequestSpecification.getReadonly(), informatieobjecttypeUrl).asString());

		String eioUrl = json.getString("url");

		Response res = eioService.lock(DRCRequestSpecification.getReadonly(), eioUrl);

		Assert.assertEquals(res.getStatusCode(), 200);
	}

	@Test(groups = "CustomScope")
	public void cannot_read_with_create_document_scope() {

		AuthService authService = new AuthService();

		authService.updateReadOnlyClientScope(new JSONArray().put("documenten.aanmaken"), "zeer_geheim", informatieobjecttypeUrl);
		wait(2000);

		EIOService eioService = new EIOService();

		Response res = eioService.listEIO(DRCRequestSpecification.getReadonly(), null, null, null);

		Assert.assertEquals(res.getStatusCode(), 403);
	}
}