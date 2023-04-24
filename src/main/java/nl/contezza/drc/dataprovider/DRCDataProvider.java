package nl.contezza.drc.dataprovider;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.DataProvider;

import nl.contezza.drc.utils.StringDate;

public class DRCDataProvider {

	static final String AB = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	static SecureRandom rnd = new SecureRandom();

	@DataProvider(name = "create_informatieobjecttype")
	public static String createInformatieObjectType(String catalogusUrl) {
		JSONObject json = new JSONObject();
		json.put("catalogus", catalogusUrl);
		json.put("omschrijving", "Informatieobjectype " + randomString(5));
		json.put("vertrouwelijkheidaanduiding", "openbaar");
		json.put("beginGeldigheid", StringDate.toDateString(2019, 1, 1));
		return json.toString();
	}

	@DataProvider(name = "create_catalogi")
	public static String createCatalogi() {
		JSONObject json = new JSONObject();
		json.put("domein", randomString(5));
		json.put("rsin", "000000000");
		json.put("contactpersoonBeheerNaam", "Test Persoon");
		return json.toString();
	}

	@DataProvider(name = "test_create")
	public static String testCreate(String iot) {
		JSONObject json = new JSONObject();
		json.put("identificatie", UUID.randomUUID().toString());
		json.put("bronorganisatie", "159351741");
		json.put("creatiedatum", StringDate.toDateString(2018, 6, 27));
		json.put("titel", "detailed summary");
		json.put("auteur", "test_auteur");
		json.put("formaat", "txt");
		json.put("taal", "eng");
		json.put("bestandsnaam", "dummy.txt");
		json.put("inhoud", Base64.getEncoder().encodeToString("some file content".getBytes()));
		json.put("bestandsomvang", "some file content".getBytes().length);
		json.put("link", "http://een.link");
		json.put("beschrijving", "test_beschrijving");
		json.put("informatieobjecttype", iot);
		json.put("vertrouwelijkheidaanduiding", "openbaar");
		return json.toString();
	}

	@DataProvider(name = "test_create")
	public static String testCreateReqOnly(String iot) {
		JSONObject json = new JSONObject();
		json.put("bronorganisatie", "159351741");
		json.put("creatiedatum", StringDate.toDateString(2020, 8, 1));
		json.put("titel", "detailed summary");
		json.put("auteur", "test_auteur");
		json.put("taal", "eng");
		json.put("inhoud", Base64.getEncoder().encodeToString("some file content".getBytes()));
		json.put("bestandsomvang", "some file content".getBytes().length);
		json.put("informatieobjecttype", iot);
		return json.toString();
	}

	@DataProvider(name = "test_create")
	public static String testCreate(String iot, Date creatiedatum) {
		JSONObject json = new JSONObject();
		json.put("identificatie", UUID.randomUUID().toString());
		json.put("bronorganisatie", "159351741");
		json.put("creatiedatum", StringDate.formatDate(creatiedatum));
		json.put("titel", "detailed summary");
		json.put("auteur", "test_auteur");
		json.put("formaat", "txt");
		json.put("taal", "eng");
		json.put("bestandsnaam", "dummy.txt");
		json.put("inhoud", Base64.getEncoder().encodeToString("some file content".getBytes()));
		json.put("bestandsomvang", "some file content".getBytes().length);
		json.put("link", "http://een.link");
		json.put("beschrijving", "test_beschrijving");
		json.put("informatieobjecttype", iot);
		json.put("vertrouwelijkheidaanduiding", "openbaar");
		return json.toString();
	}

	@DataProvider(name = "test_create")
	public static String testCreate(String iot, String beschrijving, String inhoud) {
		JSONObject json = new JSONObject();
		json.put("identificatie", UUID.randomUUID().toString());
		json.put("bronorganisatie", "159351741");
		json.put("creatiedatum", StringDate.toDateString(2018, 6, 27));
		json.put("titel", "detailed summary");
		json.put("auteur", "test_auteur");
		json.put("formaat", "txt");
		json.put("taal", "eng");
		json.put("bestandsnaam", "dummy.txt");
		json.put("inhoud", Base64.getEncoder().encodeToString(inhoud.getBytes()));
		json.put("bestandsomvang", inhoud.getBytes().length);
		json.put("link", "http://een.link");
		json.put("beschrijving", beschrijving);
		json.put("informatieobjecttype", iot);
		json.put("vertrouwelijkheidaanduiding", "openbaar");
		return json.toString();
	}

	@DataProvider(name = "test_create")
	public static String testCreate(String iot, String beschrijving, String inhoud, Date creatiedatum) {
		JSONObject json = new JSONObject();
		json.put("identificatie", UUID.randomUUID().toString());
		json.put("bronorganisatie", "159351741");
		json.put("creatiedatum", StringDate.formatDate(creatiedatum));
		json.put("titel", "detailed summary");
		json.put("auteur", "test_auteur");
		json.put("formaat", "txt");
		json.put("taal", "eng");
		json.put("bestandsnaam", "dummy.txt");
		json.put("inhoud", Base64.getEncoder().encodeToString(inhoud.getBytes()));
		json.put("bestandsomvang", inhoud.getBytes().length);
		json.put("link", "http://een.link");
		json.put("beschrijving", beschrijving);
		json.put("informatieobjecttype", iot);
		json.put("vertrouwelijkheidaanduiding", "openbaar");
		return json.toString();
	}

	@DataProvider(name = "test_create")
	public static String testCreate(String iot, String beschrijving, String inhoud,
			String vertrouwelijkheidaanduiding) {
		JSONObject json = new JSONObject();
		json.put("identificatie", UUID.randomUUID().toString());
		json.put("bronorganisatie", "159351741");
		json.put("creatiedatum", StringDate.toDateString(2018, 6, 27));
		json.put("titel", "detailed summary");
		json.put("auteur", "test_auteur");
		json.put("formaat", "txt");
		json.put("taal", "eng");
		json.put("bestandsnaam", "dummy.txt");
		json.put("inhoud", Base64.getEncoder().encodeToString(inhoud.getBytes()));
		json.put("bestandsomvang", inhoud.getBytes().length);
		json.put("link", "http://een.link");
		json.put("beschrijving", beschrijving);
		json.put("informatieobjecttype", iot);
		json.put("vertrouwelijkheidaanduiding", vertrouwelijkheidaanduiding);
		return json.toString();
	}

	@DataProvider(name = "test_create")
	public static String testCreate(String iot, String beschrijving, String inhoud, String vertrouwelijkheidaanduiding,
			String bronorganisatie) {
		JSONObject json = new JSONObject();
		json.put("identificatie", UUID.randomUUID().toString());
		json.put("bronorganisatie", bronorganisatie);
		json.put("creatiedatum", StringDate.toDateString(2018, 6, 27));
		json.put("titel", "detailed summary");
		json.put("auteur", "test_auteur");
		json.put("formaat", "txt");
		json.put("taal", "eng");
		json.put("bestandsnaam", "dummy.txt");
		json.put("inhoud", Base64.getEncoder().encodeToString(inhoud.getBytes()));
		json.put("bestandsomvang", inhoud.getBytes().length);
		json.put("link", "http://een.link");
		json.put("beschrijving", beschrijving);
		json.put("informatieobjecttype", iot);
		json.put("vertrouwelijkheidaanduiding", vertrouwelijkheidaanduiding);
		return json.toString();
	}

	@DataProvider(name = "test_create")
	public static String testCreate(String iot, String indentificatie) {
		JSONObject json = new JSONObject();
		json.put("identificatie", indentificatie);
		json.put("bronorganisatie", "159351741");
		json.put("creatiedatum", StringDate.toDateString(2018, 6, 27));
		json.put("titel", "detailed summary");
		json.put("auteur", "test_auteur");
		json.put("formaat", "txt");
		json.put("taal", "eng");
		json.put("bestandsnaam", "dummy.txt");
		json.put("inhoud", Base64.getEncoder().encodeToString("some file content".getBytes()));
		json.put("bestandsomvang", "some file content".getBytes().length);
		json.put("link", "http://een.link");
		json.put("beschrijving", "test_beschrijving");
		json.put("informatieobjecttype", iot);
		json.put("vertrouwelijkheidaanduiding", "openbaar");
		return json.toString();
	}

	@DataProvider(name = "test_create")
	public static String testCreate(String iot, JSONArray inhoud) {
		JSONObject json = new JSONObject();
		json.put("identificatie", UUID.randomUUID().toString());
		json.put("bronorganisatie", "159351741");
		json.put("creatiedatum", StringDate.toDateString(2018, 6, 27));
		json.put("titel", "detailed summary");
		json.put("auteur", "test_auteur");
		json.put("formaat", "txt");
		json.put("taal", "eng");
		json.put("bestandsnaam", "dummy.txt");
		json.put("inhoud", inhoud);
		json.put("bestandsomvang", inhoud.toString().getBytes().length);
		json.put("link", "http://een.link");
		json.put("beschrijving", "test_beschrijving");
		json.put("informatieobjecttype", iot);
		json.put("vertrouwelijkheidaanduiding", "openbaar");
		return json.toString();
	}

	@DataProvider(name = "test_integrity_create")
	public static String testIntegrityCreate(String iot, JSONObject integriteit) {
		JSONObject json = new JSONObject();
		json.put("identificatie", UUID.randomUUID().toString());
		json.put("bronorganisatie", "159351741");
		json.put("creatiedatum", StringDate.toDateString(2018, 12, 13));
		json.put("titel", "Voorbeelddocument");
		json.put("auteur", "test_auteur");
		json.put("formaat", "text/plain");
		json.put("taal", "eng");
		json.put("bestandsnaam", "dummy.txt");
		json.put("inhoud", Base64.getEncoder().encodeToString("some file content".getBytes()));
		json.put("bestandsomvang", "some file content".getBytes().length);
		json.put("informatieobjecttype", iot);
		if (integriteit == null) {
			json.put("integriteit", JSONObject.NULL);
		} else {
			json.put("integriteit", integriteit);
		}

		return json.toString();
	}

	public static String randomString(int len) {
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++)
			sb.append(AB.charAt(rnd.nextInt(AB.length())));
		return sb.toString();
	}

	@DataProvider(name = "create_zaaktype")
	public static String createZaakType(String catalogusUrl) {
		JSONObject json = new JSONObject();
		json.put("identificatie", UUID.randomUUID().toString());
		json.put("omschrijving", "Zaaktype " + DRCDataProvider.randomString(5));
		json.put("vertrouwelijkheidaanduiding", "openbaar");
		json.put("doel", "test_doel");
		json.put("aanleiding", "test_aanleiding");
		json.put("indicatieInternOfExtern", "extern");
		json.put("handelingInitiator", "indienen");
		json.put("onderwerp", "test_onderwerp");
		json.put("handelingBehandelaar", "behandelen");
		json.put("doorlooptijd", "P10D");
		json.put("opschortingEnAanhoudingMogelijk", false);
		json.put("verlengingMogelijk", false);
		json.put("publicatieIndicatie", false);
		json.put("referentieproces", new JSONObject("{\"naam\":\"test_naam\", \"link\" : \"\"}"));
		json.put("catalogus", catalogusUrl);
		json.put("besluittypen", new JSONArray());
		// json.put("selectielijstProcestype",
		// "https://referentielijsten-api.vng.cloud/api/v1/procestypen/941de99f-b702-4b3e-9df4-db370f457bea");
		json.put("gerelateerdeZaaktypen", new JSONArray());
		json.put("beginGeldigheid", StringDate.toDateString(2019, 1, 1));
		json.put("versiedatum", StringDate.toDateString(2019, 1, 1));
		json.put("productenOfDiensten", new JSONArray());
		return json.toString();
	}

	@DataProvider(name = "create_zaak")
	public static String createZaak(String zaaktypeUrl) {
		JSONObject json = new JSONObject();
		json.put("identificatie", "ZAAK-" + DRCDataProvider.randomString(5));
		json.put("bronorganisatie", "159351741");
		json.put("omschrijving", "Zaak " + DRCDataProvider.randomString(5));
		json.put("zaaktype", zaaktypeUrl);
		json.put("verantwoordelijkeOrganisatie", "000000000");
		json.put("startdatum", StringDate.toDateString(2020, 1, 1));
		return json.toString();
	}

	@DataProvider(name = "create_ziot")
	public static String createZiot(String zaaktypeUrl, String informatieobjecttypeUrl) {
		JSONObject json = new JSONObject();
		json.put("zaaktype", zaaktypeUrl);
		json.put("informatieobjecttype", informatieobjecttypeUrl);
		json.put("volgnummer", 1);
		json.put("richting", "intern");
		return json.toString();
	}

	@DataProvider(name = "create_zio")
	public static String createZio(String informatieobjectUrl, String zaakUrl) {
		JSONObject json = new JSONObject();
		json.put("informatieobject", informatieobjectUrl);
		json.put("zaak", zaakUrl);
		json.put("titel", "test_titel");
		json.put("omschrijving", "test_omschrijving");
		return json.toString();
	}

	@DataProvider(name = "update_partial_ac")
	public static String updatePartialAC(JSONArray clientIds, JSONArray scopes, String iotUrl,
			String maxVertrouwelijkheid) {

		JSONObject comp = new JSONObject();
		comp.put("component", "drc");
		comp.put("scopes", scopes);
		comp.put("informatieobjecttype", iotUrl);
		comp.put("maxVertrouwelijkheidaanduiding", maxVertrouwelijkheid);

		JSONArray arr = new JSONArray().put(comp);

		JSONObject json = new JSONObject();
		json.put("clientIds", clientIds);
		// json.put("label", "Test");
		json.put("autorisaties", arr);
		json.put("heeftAlleAutorisaties", false);
		return json.toString();
	}

	@DataProvider(name = "update_partial_ac")
	public static String updatePartialAC(JSONArray clientIds, JSONArray autorisaties, Boolean heeftAlleAutorisaties) {
		JSONObject json = new JSONObject();
		json.put("clientIds", clientIds);
		json.put("autorisaties", autorisaties);
		json.put("heeftAlleAutorisaties", heeftAlleAutorisaties);
		return json.toString();
	}

	@DataProvider(name = "create_gebruiksrecht")
	public static String createGebruiksrecht(String eioUrl) {
		JSONObject json = new JSONObject();
		json.put("informatieobject", eioUrl);
		json.put("startdatum", StringDate.toDatetimeString(new Date()));
		json.put("omschrijvingVoorwaarden", "Test");
		return json.toString();
	}

	@DataProvider(name = "unlock")
	public static String unlock(String lockId) {
		JSONObject json = new JSONObject();
		json.put("lock", lockId);
		return json.toString();
	}

	@DataProvider(name = "search")
	public static String search(JSONArray uuids) {
		JSONObject json = new JSONObject();
		if (uuids != null) {
			json.put("uuid_In", uuids);
		}
		return json.toString();
	}
}
