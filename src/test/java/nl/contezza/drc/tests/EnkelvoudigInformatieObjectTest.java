package nl.contezza.drc.tests;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.time.DateUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import nl.contezza.drc.rest.RestTest;
import nl.contezza.drc.service.EIOService;
import nl.contezza.drc.service.ZRCService;
import nl.contezza.drc.service.ZTCService;
import nl.contezza.drc.utils.StringDate;

//@Log4j2
public class EnkelvoudigInformatieObjectTest extends RestTest {

	/**
	 * Create necessary dependencies when creating enkelvoudiginformatieobject.
	 */
	@BeforeTest(groups = "EnkelvoudigInformatieObject")
	public void init() {
		// Create random catalogi
		ZTCService ztcService = new ZTCService();
		JsonPath json = new JsonPath(ztcService.createCatalogus().asString());

		// Create informatieobjecttype
		String catalogusUrl = json.getString("url").replace(ZTC_BASE_URI, ZTC_DOCKER_URI);
		json = new JsonPath(ztcService.createInformatieObjectType(catalogusUrl).asString());
		informatieobjecttypeUrl = json.getString("url").replace(ZTC_BASE_URI, ZTC_DOCKER_URI);

		// Create zaaktype
		json = new JsonPath(ztcService.createZaaktype(catalogusUrl).asString());

		String zaaktypeUrl = json.getString("url").replace(ZTC_BASE_URI, ZTC_DOCKER_URI);

		// Create zaaktype-informatieobjecttype
		Response res = ztcService.createZiot(zaaktypeUrl, informatieobjecttypeUrl);
		Assert.assertEquals(res.getStatusCode(), 201);

		// Publish informatieobjecttype
		String id = informatieobjecttypeUrl.substring(informatieobjecttypeUrl.lastIndexOf('/') + 1).trim();

		res = ztcService.publishInformatieObjectType(id);
		Assert.assertEquals(res.getStatusCode(), 200);

		// Publish zaaktype
		res = ztcService.publishIZaaktype(zaaktypeUrl);
		Assert.assertEquals(res.getStatusCode(), 200);

		ZRCService zrcService = new ZRCService();
		zaakTestObject = new JsonPath(zrcService.createZaak(zaaktypeUrl).asString());
	}

	/**
	 * See {@link <a href="https://github.com/VNG-Realisatie/documenten-api/blob/28c082e806843def864f6be1184fbae295a1c7f2/src/drc/api/tests/test_enkelvoudiginformatieobject.py#L44">python code</a>}.
	 */
	@Test(groups = "EnkelvoudigInformatieObject")
	public void test_create() {
		EIOService eioService = new EIOService();

		Response res = eioService.testCreate(informatieobjecttypeUrl);
		JsonPath json = new JsonPath(res.asString());

		Assert.assertEquals(res.getStatusCode(), 201);

		Assert.assertTrue(json.getString("identificatie").matches(UUID_REGEX));
		Assert.assertEquals(json.getString("bronorganisatie"), "159351741");
		Assert.assertEquals(json.getString("creatiedatum"), StringDate.toDateString(2018, 6, 27));
		Assert.assertEquals(json.getString("titel"), "detailed summary");
		Assert.assertEquals(json.getString("auteur"), "test_auteur");
		Assert.assertEquals(json.getString("formaat"), "txt");
		Assert.assertEquals(json.getString("taal"), "eng");
		Assert.assertEquals(json.getInt("versie"), 1);
		Assert.assertEquals(DateUtils.round(StringDate.getDateTime(json.get("begin_registratie")), Calendar.SECOND), DateUtils.round(new Date(), Calendar.SECOND));
		Assert.assertEquals(json.getString("bestandsnaam"), "dummy.txt");
		Assert.assertEquals(eioService.downloadAsString(json.getString("inhoud")), "some file content");
		Assert.assertEquals(json.getString("link"), "http://een.link");
		Assert.assertEquals(json.getString("beschrijving"), "test_beschrijving");
		Assert.assertEquals(json.getString("informatieobjecttype").replace(ZTC_BASE_URI, ZTC_DOCKER_URI), informatieobjecttypeUrl);
		Assert.assertEquals(json.getString("vertrouwelijkheidaanduiding"), "openbaar");
		Assert.assertEquals(json.getInt("bestandsomvang"), 17);
		Assert.assertEquals(json.getString("integriteit.algoritme"), "");
		Assert.assertEquals(json.getString("integriteit.waarde"), "");
		Assert.assertNull(json.getString("integriteit.datum"));
		Assert.assertNull(json.getString("ontvangstdatum"));
		Assert.assertNull(json.getString("verzenddatum"));
		Assert.assertEquals(json.getString("ondertekening.soort"), "");
		Assert.assertNull(json.getString("ondertekening.datum"));
		Assert.assertNull(json.getString("indicatieGebruiksrecht"));
		Assert.assertEquals(json.getString("status"), "");
		Assert.assertFalse(json.getBoolean("locked"));

		eioTestObject = json;
	}

	/**
	 * See {@link <a href="https://github.com/VNG-Realisatie/documenten-api/blob/28c082e806843def864f6be1184fbae295a1c7f2/src/drc/api/tests/test_enkelvoudiginformatieobject.py#L123">python code</a>}.
	 */
	@Test(groups = "EnkelvoudigInformatieObject")
	public void test_read() {
		EIOService eioService = new EIOService();

		Response res = eioService.getEIO(eioTestObject.getString("url"), 1);
		JsonPath json = new JsonPath(res.asString());

		Assert.assertEquals(res.getStatusCode(), 200);

		Assert.assertEquals(json.getString("url"), eioTestObject.getString("url"));
		Assert.assertEquals(json.getString("identificatie"), eioTestObject.getString("identificatie"));
		Assert.assertEquals(json.getString("bronorganisatie"), eioTestObject.getString("bronorganisatie"));
		Assert.assertEquals(json.getString("creatiedatum"), eioTestObject.getString("creatiedatum"));
		Assert.assertEquals(json.getString("titel"), eioTestObject.getString("titel"));
		Assert.assertEquals(json.getString("auteur"), eioTestObject.getString("auteur"));
		Assert.assertEquals(json.getString("status"), eioTestObject.getString("status"));
		Assert.assertEquals(json.getString("formaat"), eioTestObject.getString("formaat"));
		Assert.assertEquals(json.getString("taal"), eioTestObject.getString("taal"));
		Assert.assertEquals(json.getString("beginRegistratie"), eioTestObject.getString("beginRegistratie"));
		Assert.assertEquals(json.getInt("versie"), eioTestObject.getInt("versie"));
		Assert.assertEquals(json.getString("bestandsnaam"), eioTestObject.getString("bestandsnaam"));
		Assert.assertEquals(json.getString("inhoud"), eioTestObject.getString("inhoud"));
		Assert.assertEquals(json.getInt("bestandsomvang"), eioTestObject.getInt("bestandsomvang"));
		Assert.assertEquals(json.getString("link"), eioTestObject.getString("link"));
		Assert.assertEquals(json.getString("beschrijving"), eioTestObject.getString("beschrijving"));
		Assert.assertEquals(json.getString("ontvangstdatum"), eioTestObject.getString("ontvangstdatum"));
		Assert.assertEquals(json.getString("verzenddatum"), eioTestObject.getString("verzenddatum"));
		Assert.assertEquals(json.getString("ondertekening.soort"), eioTestObject.getString("ondertekening.soort"));
		Assert.assertEquals(json.getString("ondertekening.datum"), eioTestObject.getString("ondertekening.datum"));
		Assert.assertEquals(json.getString("indicatieGebruiksrecht"), eioTestObject.getString("indicatieGebruiksrecht"));
		Assert.assertEquals(json.getString("vertrouwelijkheidaanduiding"), eioTestObject.getString("vertrouwelijkheidaanduiding"));
		Assert.assertEquals(json.getString("integriteit.algoritme"), eioTestObject.getString("integriteit.algoritme"));
		Assert.assertEquals(json.getString("integriteit.waarde"), eioTestObject.getString("integriteit.waarde"));
		Assert.assertEquals(json.getString("integriteit.datum"), eioTestObject.getString("integriteit.datum"));
		Assert.assertEquals(json.getString("informatieobjecttype"), eioTestObject.getString("informatieobjecttype"));
		Assert.assertEquals(json.getBoolean("locked"), eioTestObject.getBoolean("locked"));
	}

	/**
	 * See {@link <a href="https://github.com/VNG-Realisatie/documenten-api/blob/28c082e806843def864f6be1184fbae295a1c7f2/src/drc/api/tests/test_enkelvoudiginformatieobject.py#L177">python code</a>}.
	 */
	@Test(groups = "EnkelvoudigInformatieObject")
	public void test_eio_download_with_accept_application_octet_stream_header() {
		EIOService eioService = new EIOService();

		JsonPath json = new JsonPath(eioService.testCreate(informatieobjecttypeUrl, "beschrijving1", "inhoud1").asString());
		Response res = eioService.download(json.getString("inhoud"), "application/octet-stream");

		Assert.assertEquals(res.getStatusCode(), 200);
	}

	/**
	 * See {@link <a href="https://github.com/VNG-Realisatie/documenten-api/blob/stable/1.0.x/src/drc/api/tests/test_enkelvoudiginformatieobject.py#L189">python code</a>}.
	 */
	@Test(groups = "EnkelvoudigInformatieObject")
	public void test_download_non_existing_eio() {
		EIOService eioService = new EIOService();

		JsonPath json = new JsonPath(eioService.testCreate(informatieobjecttypeUrl, "beschrijving1", "inhoud1").asString());

		// Delete eio
		Response res = eioService.delete(json.getString("url"));
		Assert.assertEquals(res.getStatusCode(), 204);

		// FIXME: returns 500 error??
		res = eioService.download(json.getString("inhoud"), "application/octet-stream");
		Assert.assertEquals(res.getStatusCode(), 404);
	}

	/**
	 * See {@link <a href="https://github.com/VNG-Realisatie/documenten-api/blob/28c082e806843def864f6be1184fbae295a1c7f2/src/drc/api/tests/test_enkelvoudiginformatieobject.py#L204">python code</a>}.
	 */
	@Test(groups = "EnkelvoudigInformatieObject")
	public void test_bestandsomvang() {
		EIOService eioService = new EIOService();

		JsonPath json = new JsonPath(eioService.testCreate(informatieobjecttypeUrl, null, "some content").asString());

		Response res = eioService.getEIO(json.getString("url"), 1);
		Assert.assertEquals(res.getStatusCode(), 200);

		json = new JsonPath(res.asString());
		Assert.assertEquals(json.getInt("bestandsomvang"), 12);
	}

	/**
	 * See {@link <a href="https://github.com/VNG-Realisatie/documenten-api/blob/28c082e806843def864f6be1184fbae295a1c7f2/src/drc/api/tests/test_enkelvoudiginformatieobject.py#L227">python code</a>}.
	 */
	@Test(groups = "EnkelvoudigInformatieObject")
	public void test_integrity_empty() {

		EIOService eioService = new EIOService();
		Response res = eioService.testIntegrityCreate(informatieobjecttypeUrl, null);

		Assert.assertEquals(res.getStatusCode(), 201);

		JsonPath json = new JsonPath(res.asString());

		Assert.assertEquals(json.getString("integriteit.algoritme"), "");
		Assert.assertEquals(json.getString("integriteit.waarde"), "");
		Assert.assertNull(json.get("integriteit.datum"));
	}

	/**
	 * See {@link <a href="https://github.com/VNG-Realisatie/documenten-api/blob/28c082e806843def864f6be1184fbae295a1c7f2/src/drc/api/tests/test_enkelvoudiginformatieobject.py#L258">python code</a>}.
	 */
	@Test(groups = "EnkelvoudigInformatieObject")
	public void test_integrity_provided() {
		EIOService eioService = new EIOService();

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("algoritme", "md5");
		jsonObject.put("waarde", "27c3a009a3cbba674d0b3e836f2d4685");
		jsonObject.put("datum", StringDate.toDateString(2018, 12, 13));

		Response res = eioService.testIntegrityCreate(informatieobjecttypeUrl, jsonObject);

		Assert.assertEquals(res.getStatusCode(), 201);

		JsonPath json = new JsonPath(res.asString());

		Assert.assertEquals(json.getString("integriteit.algoritme"), "md5");
		Assert.assertEquals(json.getString("integriteit.waarde"), "27c3a009a3cbba674d0b3e836f2d4685");
		Assert.assertEquals(json.getString("integriteit.datum"), StringDate.toDateString(2018, 12, 13));
	}

	/**
	 * See {@link <a href="https://github.com/VNG-Realisatie/documenten-api/blob/28c082e806843def864f6be1184fbae295a1c7f2/src/drc/api/tests/test_enkelvoudiginformatieobject.py#L295">python code</a>}.
	 */
	@Test(groups = "EnkelvoudigInformatieObject")
	public void test_filter_by_identification() {
		EIOService eioService = new EIOService();

		String foo = "foo" + randomString(10);
		String bar = "bar" + randomString(10);

		eioService.testCreate(informatieobjecttypeUrl, foo);
		eioService.testCreate(informatieobjecttypeUrl, bar);

		Response res = eioService.listEIO(foo, null, null);

		Assert.assertEquals(res.getStatusCode(), 200);
		Assert.assertEquals((int) res.body().path("results.size()"), 1);
		Assert.assertEquals((String) res.body().path("results[0].identificatie"), foo);
	}

	/**
	 * See {@link <a href="https://github.com/VNG-Realisatie/documenten-api/blob/28c082e806843def864f6be1184fbae295a1c7f2/src/drc/api/tests/test_enkelvoudiginformatieobject.py#L307">python code</a>}.
	 */
	@Test(groups = "EnkelvoudigInformatieObject")
	public void test_destroy_no_relations_allowed() {
		EIOService eioService = new EIOService();

		JsonPath json = new JsonPath(eioService.testCreate(informatieobjecttypeUrl).asString());

		Response res = eioService.delete(json.getString("url"));
		Assert.assertEquals(res.getStatusCode(), 204);
	}

	/**
	 * See {@link <a href="https://github.com/VNG-Realisatie/documenten-api/blob/stable/1.0.x/src/drc/api/tests/test_enkelvoudiginformatieobject.py#L318">python code</a>}.
	 */
	@Test(groups = "EnkelvoudigInformatieObject")
	public void test_destroy_with_relations_not_allowed() {

		EIOService eioService = new EIOService();
		eioTestObject = new JsonPath(eioService.testCreate(informatieobjecttypeUrl).asString());

		String eioUrl = eioTestObject.getString("url").replace(DRC_BASE_URI, DRC_DOCKER_URI);
		String zaakUrl = zaakTestObject.getString("url").replace(ZRC_BASE_URI, ZRC_DOCKER_URI);

		// Create relation
		ZRCService zrcService = new ZRCService();
		Response res = zrcService.createZio(eioUrl, zaakUrl);

		Assert.assertEquals(res.getStatusCode(), 201);

		res = eioService.delete(eioTestObject.getString("url"));

		Assert.assertEquals(res.getStatusCode(), 400);
		Assert.assertEquals(res.body().path("invalidParams[0].code"), "pending-relations");
	}

	/**
	 * See {@link <a href="https://github.com/VNG-Realisatie/documenten-api/blob/28c082e806843def864f6be1184fbae295a1c7f2/src/drc/api/tests/test_enkelvoudiginformatieobject.py#L332">python code</a>}.
	 */
	@Test(groups = "EnkelvoudigInformatieObject")
	public void test_validate_unknown_query_params() {
		EIOService eioService = new EIOService();

		Map<String, String> params = new HashMap<String, String>();
		params.put("someparam", "somevalue");

		Response res = eioService.listEIO(params);
		Assert.assertEquals(res.getStatusCode(), 400);
		Assert.assertEquals(res.body().path("invalidParams[0].code"), "unknown-parameters");
	}

	/**
	 * See {@link <a href="https://github.com/VNG-Realisatie/documenten-api/blob/stable/1.0.x/src/drc/api/tests/test_enkelvoudiginformatieobject.py#L346">python code</a>}.
	 */
	@Test(groups = "EnkelvoudigInformatieObject")
	public void test_invalid_inhoud() {
		EIOService eioService = new EIOService();

		Response res = eioService.testCreate(informatieobjecttypeUrl, new JSONArray().put(1).put(2).put(3));

		// FIXME: not responding 400 but returns 500?
		Assert.assertEquals(res.getStatusCode(), 400);
	}
}
