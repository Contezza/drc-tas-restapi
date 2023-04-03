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
import nl.contezza.drc.service.ZTCService;

//@Log4j2
public class GebruiksrechtenCachingTest extends RestTest {

	/**
	 * Create necessary dependencies.
	 */
	@BeforeTest(groups = "GebruiksrechtenCaching")
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
	 * "https://github.com/VNG-Realisatie/documenten-api/blob/1.3.0/src/drc/api/tests/test_caching.py#L91">python
	 * code</a>}.
	 */
	@Test(groups = "GebruiksrechtenCaching")
	public void test_gebruiksrecht_get_cache_header() {
		EIOService eioService = new EIOService();
		JsonPath json = new JsonPath(eioService.testCreate(informatieobjecttypeUrl).asString());

		GebruiksrechtenService gebruiksrechtenService = new GebruiksrechtenService();
		json = new JsonPath(gebruiksrechtenService.create(
				json.getString("url").replace(DRC_BASE_URI, DRC_DOCKER_URI)).asString());

		Response res = gebruiksrechtenService.get(DRCRequestSpecification.getDefault(),
				json.getString("url").replace(DRC_BASE_URI, DRC_DOCKER_URI));
		Assert.assertNotNull(res.getHeader("ETag"));
	}

	/**
	 * See {@link <a href=
	 * "https://github.com/VNG-Realisatie/documenten-api/blob/1.3.0/src/drc/api/tests/test_caching.py#L98">python
	 * code</a>}.
	 */
	@Test(groups = "GebruiksrechtenCaching")
	public void test_gebruiksrechthead_cache_header() {
		EIOService eioService = new EIOService();
		JsonPath json = new JsonPath(eioService.testCreate(informatieobjecttypeUrl).asString());

		GebruiksrechtenService gebruiksrechtenService = new GebruiksrechtenService();
		json = new JsonPath(gebruiksrechtenService.create(
				json.getString("url").replace(DRC_BASE_URI, DRC_DOCKER_URI)).asString());

		Response res = gebruiksrechtenService.getHead(json.getString("url").replace(DRC_BASE_URI, DRC_DOCKER_URI));
		Assert.assertNotNull(res.getHeader("ETag"));
	}

	/**
	 * See {@link <a href=
	 * "https://github.com/VNG-Realisatie/documenten-api/blob/1.3.0/src/drc/api/tests/test_caching.py#L110">python
	 * code</a>}.
	 */
	@Test(groups = "GebruiksrechtenCaching")
	public void test_conditional_get_304() {

		EIOService eioService = new EIOService();
		JsonPath json = new JsonPath(eioService.testCreate(informatieobjecttypeUrl).asString());

		GebruiksrechtenService gebruiksrechtenService = new GebruiksrechtenService();
		json = new JsonPath(gebruiksrechtenService.create(
				json.getString("url").replace(DRC_BASE_URI, DRC_DOCKER_URI)).asString());

		Response res = gebruiksrechtenService.get(DRCRequestSpecification.getDefault(),
				json.getString("url").replace(DRC_BASE_URI, DRC_DOCKER_URI));

		String eTag = "" + res.getHeader("ETag") + "";

		// Not modified
		res = gebruiksrechtenService.getIfNonMatch(json.getString("url"), eTag);
		Assert.assertEquals(res.getStatusCode(), 304);
	}

	/**
	 * See {@link <a href=
	 * "https://github.com/VNG-Realisatie/documenten-api/blob/1.3.0/src/drc/api/tests/test_caching.py#L118">python
	 * code</a>}.
	 */
	@Test(groups = "GebruiksrechtenCaching")
	public void test_conditional_get_stale() {
		EIOService eioService = new EIOService();
		JsonPath json = new JsonPath(eioService.testCreate(informatieobjecttypeUrl).asString());

		GebruiksrechtenService gebruiksrechtenService = new GebruiksrechtenService();
		json = new JsonPath(gebruiksrechtenService.create(
				json.getString("url").replace(DRC_BASE_URI, DRC_DOCKER_URI)).asString());

		Response res = gebruiksrechtenService.getIfNonMatch(json.getString("url"), "" + "not-an-md5" + "");
		Assert.assertEquals(res.getStatusCode(), 200);
	}
}
