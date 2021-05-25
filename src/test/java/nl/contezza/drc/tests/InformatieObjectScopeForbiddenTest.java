package nl.contezza.drc.tests;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import nl.contezza.drc.rest.RestTest;
import nl.contezza.drc.service.DRCRequestSpecification;
import nl.contezza.drc.service.EIOService;
import nl.contezza.drc.service.GebruiksrechtenService;
import nl.contezza.drc.service.OIOService;
import nl.contezza.drc.service.ZTCService;

//@Log4j2
public class InformatieObjectScopeForbiddenTest extends RestTest {

	/**
	 * Create necessary dependencies.
	 */
	@BeforeTest(groups = "InformatieObjectScopeForbidden")
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
	 * See {@link <a href="https://github.com/VNG-Realisatie/documenten-api/blob/1.0.0/src/drc/api/tests/test_auth.py#L22">python code</a>}.
	 */
	@Test(groups = "InformatieObjectScopeForbidden")
	public void test_cannot_create_io_without_correct_scope() {
		EIOService eioService = new EIOService();

		Response res = eioService.testCreate(DRCRequestSpecification.getReadonly(), informatieobjecttypeUrl);

		Assert.assertEquals(res.getStatusCode(), 403);
	}

	/**
	 * See {@link <a href="https://github.com/VNG-Realisatie/documenten-api/blob/1.0.0/src/drc/api/tests/test_auth.py#L26">python code</a>}.
	 */
	@Test(groups = "InformatieObjectScopeForbidden")
	public void test_cannot_read_without_correct_scope() {

		EIOService eioService = new EIOService();
		Response res = eioService.testCreate(informatieobjecttypeUrl);

		JsonPath json = new JsonPath(res.asString());
		String eioUrl = json.getString("url").replace(DRC_BASE_URI, DRC_DOCKER_URI);

		Assert.assertEquals(res.getStatusCode(), 201);

		GebruiksrechtenService gebruiksrechtenService = new GebruiksrechtenService();
		res = gebruiksrechtenService.create(eioUrl);

		Assert.assertEquals(res.getStatusCode(), 201);

		String gebruiksrechtUrl = new JsonPath(res.asString()).getString("url");

		res = gebruiksrechtenService.get(DRCRequestSpecification.getWrongScope(), gebruiksrechtUrl);
		Assert.assertEquals(res.getStatusCode(), 403);

		res = gebruiksrechtenService.list(DRCRequestSpecification.getWrongScope(), eioUrl);
		Assert.assertEquals(res.getStatusCode(), 403);

		res = eioService.getEIO(DRCRequestSpecification.getWrongScope(), eioUrl, 1);
		Assert.assertEquals(res.getStatusCode(), 403);

		res = eioService.listEIO(DRCRequestSpecification.getWrongScope(), null, null, null);
		Assert.assertEquals(res.getStatusCode(), 403);

		OIOService oioService = new OIOService();
		res = oioService.listOIO(DRCRequestSpecification.getWrongScope(), null, eioUrl);
		Assert.assertEquals(res.getStatusCode(), 403);
	}
}
