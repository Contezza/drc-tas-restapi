package nl.contezza.drc.tests;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import nl.contezza.drc.rest.RestTest;
import nl.contezza.drc.service.EIOService;
import nl.contezza.drc.service.ZTCService;

public class EnkelvoudigInformatieObjectPaginationTest extends RestTest {

	/**
	 * Create necessary dependencies.
	 */
	@BeforeTest(groups = "EnkelvoudigInformatieObjectPagination")
	public void init() {
		// Create random catalogi
		ZTCService ztcService = new ZTCService();
		JsonPath json = new JsonPath(ztcService.createCatalogus().asString());

		// Create informatieobjecttype
		String catalogusUrl = json.getString("url").replace(ZTC_BASE_URI, ZTC_DOCKER_URI);
		json = new JsonPath(ztcService.createInformatieObjectType(catalogusUrl).asString());
		informatieobjecttypeUrl = json.getString("url").replace(ZTC_BASE_URI, ZTC_DOCKER_URI);

		Response res = ztcService.publishInformatieObjectType(
				informatieobjecttypeUrl.substring(informatieobjecttypeUrl.lastIndexOf('/') + 1).trim());
		Assert.assertEquals(res.getStatusCode(), 200);
	}

	/**
	 * See {@link <a href=
	 * "https://github.com/VNG-Realisatie/documenten-api/blob/28c082e806843def864f6be1184fbae295a1c7f2/src/drc/api/tests/test_enkelvoudiginformatieobject.py#L617">python
	 * code</a>}.
	 */
	@Test(groups = "EnkelvoudigInformatieObjectPagination")
	public void test_pagination_default() {
		EIOService eioService = new EIOService();

		String rsin = randomRsin();

		eioService.testCreate(informatieobjecttypeUrl, "beschrijving", "inhoud", "openbaar", rsin);
		eioService.testCreate(informatieobjecttypeUrl, "beschrijving", "inhoud", "openbaar", rsin);

		wait(30000);

		Response res = eioService.listEIO(null, rsin, null);

		Assert.assertEquals(res.getStatusCode(), 200);
		Assert.assertEquals((int) res.body().path("results.size()"), 2);
		Assert.assertEquals((int) res.body().path("count"), 2);
		Assert.assertNull(res.body().path("previous"));
		Assert.assertNull(res.body().path("next"));
	}

	/**
	 * See {@link <a href=
	 * "https://github.com/VNG-Realisatie/documenten-api/blob/28c082e806843def864f6be1184fbae295a1c7f2/src/drc/api/tests/test_enkelvoudiginformatieobject.py#L632">python
	 * code</a>}.
	 */
	@Test(groups = "EnkelvoudigInformatieObjectPagination")
	public void test_pagination_page_param() {

		EIOService eioService = new EIOService();

		String rsin = randomRsin();

		eioService.testCreate(informatieobjecttypeUrl, "beschrijving", "inhoud", "openbaar", rsin);
		eioService.testCreate(informatieobjecttypeUrl, "beschrijving", "inhoud", "openbaar", rsin);

		wait(30000);

		Response res = eioService.listEIO(null, rsin, 1);

		Assert.assertEquals(res.getStatusCode(), 200);
		Assert.assertEquals((int) res.body().path("results.size()"), 2);
		Assert.assertEquals((int) res.body().path("count"), 2);
		Assert.assertNull(res.body().path("previous"));
		Assert.assertNull(res.body().path("next"));
	}
}
