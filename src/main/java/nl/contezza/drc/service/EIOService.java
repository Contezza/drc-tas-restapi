package nl.contezza.drc.service;

import static io.restassured.RestAssured.given;

import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.AllArgsConstructor;
import nl.contezza.drc.dataprovider.DRCDataProvider;

//@Log4j2
@AllArgsConstructor
public class EIOService {

	/**
	 * Test create
	 * 
	 * @param iot String informatieobjecttype
	 * @return Response response of request
	 */
	public Response testCreate(String iot) {
		// @formatter:off
		return given()
				.spec(DRCRequestSpecification.getDefault())
				.body(DRCDataProvider.testCreate(iot))
				.when()
				.post("/enkelvoudiginformatieobjecten")
				.then()
				.extract()
				.response();
		// @formatter:on
	}

	public Response testCreate(JSONObject jsonObject) {
		// @formatter:off
		return given()
				.spec(DRCRequestSpecification.getDefault())
				.body(jsonObject.toString())
				.when()
				.post("/enkelvoudiginformatieobjecten")
				.then()
				.extract()
				.response();
		// @formatter:on
	}

	public Response testCreate(RequestSpecification requestSpecification, JSONObject jsonObject) {
		// @formatter:off
		return given()
				.spec(DRCRequestSpecification.getDefault())
				.body(jsonObject.toString())
				.when()
				.post("/enkelvoudiginformatieobjecten")
				.then()
				.extract()
				.response();
		// @formatter:on
	}

	public Response testCreate(RequestSpecification requestSpecification, String iot) {
		// @formatter:off
		return given()
				.spec(requestSpecification)
				.body(DRCDataProvider.testCreate(iot))
				.when()
				.post("/enkelvoudiginformatieobjecten")
				.then()
				.extract()
				.response();
		// @formatter:on
	}

	public Response testCreate(String iot, String identificatie) {
		// @formatter:off
		return given()
				.spec(DRCRequestSpecification.getDefault())
				.body(DRCDataProvider.testCreate(iot, identificatie))
				.when()
				.post("/enkelvoudiginformatieobjecten")
				.then()
				.extract()
				.response();
		// @formatter:on
	}

	public Response testCreate(String iot, String beschrijving, String inhoud) {
		// @formatter:off
		return given()
				.spec(DRCRequestSpecification.getDefault())
				.body(DRCDataProvider.testCreate(iot, beschrijving, inhoud))
				.when()
				.post("/enkelvoudiginformatieobjecten")
				.then()
				.extract()
				.response();
		// @formatter:on
	}

	public Response testCreate(String iot, String beschrijving, String inhoud, Date creatiedatum) {
		// @formatter:off
		return given()
				.spec(DRCRequestSpecification.getDefault())
				.body(DRCDataProvider.testCreate(iot, beschrijving, inhoud, creatiedatum))
				.when()
				.post("/enkelvoudiginformatieobjecten")
				.then()
				.extract()
				.response();
		// @formatter:on
	}

	public Response testCreate(String iot, Date creatiedatum) {
		// @formatter:off
		return given()
				.spec(DRCRequestSpecification.getDefault())
				.body(DRCDataProvider.testCreate(iot, creatiedatum))
				.when()
				.post("/enkelvoudiginformatieobjecten")
				.then()
				.extract()
				.response();
		// @formatter:on
	}

	public Response testCreateReqOnly(String iot, RequestSpecification spec) {
		// @formatter:off
		return given()
				.spec(spec)
				.body(DRCDataProvider.testCreateReqOnly(iot))
				.when()
				.post("/enkelvoudiginformatieobjecten")
				.then()
				.extract()
				.response();
		// @formatter:on
	}

	public Response testCreate(String iot, String beschrijving, String inhoud, String vertrouwelijkheidaanduiding) {
		// @formatter:off
		return given()
				.spec(DRCRequestSpecification.getDefault())
				.body(DRCDataProvider.testCreate(iot, beschrijving, inhoud, vertrouwelijkheidaanduiding))
				.when()
				.post("/enkelvoudiginformatieobjecten")
				.then()
				.extract()
				.response();
		// @formatter:on
	}

	public Response testCreate(String iot, String beschrijving, String inhoud, String vertrouwelijkheidaanduiding,
			String bronorganisatie) {
		// @formatter:off
		return given()
				.spec(DRCRequestSpecification.getDefault())
				.body(DRCDataProvider.testCreate(iot, beschrijving, inhoud, vertrouwelijkheidaanduiding, bronorganisatie))
				.when()
				.post("/enkelvoudiginformatieobjecten")
				.then()
				.extract()
				.response();
		// @formatter:on
	}

	public Response testCreate(String iot, JSONArray inhoud) {
		// @formatter:off
		return given()
				.spec(DRCRequestSpecification.getDefault())
				.body(DRCDataProvider.testCreate(iot, inhoud))
				.when()
				.post("/enkelvoudiginformatieobjecten")
				.then()
				.extract()
				.response();
		// @formatter:on
	}

	public Response lock(String eioUrl) {

		String id = eioUrl.substring(eioUrl.lastIndexOf('/') + 1).trim();

		// @formatter:off
		return given()
				.spec(DRCRequestSpecification.getDefault())
				.when()
				.post("/enkelvoudiginformatieobjecten/"+ id + "/lock")
				.then()
				.extract()
				.response();
		// @formatter:on
	}

	public Response lock(RequestSpecification spec, String eioUrl) {

		String id = eioUrl.substring(eioUrl.lastIndexOf('/') + 1).trim();

		// @formatter:off
		return given()
				.spec(spec)
				.when()
				.post("/enkelvoudiginformatieobjecten/"+id + "/lock")
				.then()
				.extract()
				.response();
		// @formatter:on
	}

	public Response unlock(String eioUrl, String lockId) {
		String id = eioUrl.substring(eioUrl.lastIndexOf('/') + 1).trim();

		// @formatter:off
		return given()
				.spec(DRCRequestSpecification.getDefault())
				.body(DRCDataProvider.unlock(lockId))
				.when()
				.post("/enkelvoudiginformatieobjecten/" +id + "/unlock")
				.then()
				.extract()
				.response();
		// @formatter:on
	}

	public Response unlock(RequestSpecification spec, String eioUrl, String lockId) {
		String id = eioUrl.substring(eioUrl.lastIndexOf('/') + 1).trim();

		// @formatter:off
		return given()
				.spec(spec)
				.body(DRCDataProvider.unlock(lockId))
				.when()
				.post("/enkelvoudiginformatieobjecten/" +id + "/unlock")
				.then()
				.extract()
				.response();
		// @formatter:on
	}

	public Response update(String eioUrl, JSONObject body) {
		String id = eioUrl.substring(eioUrl.lastIndexOf('/') + 1).trim();
		// @formatter:off
		return given()
				.spec(DRCRequestSpecification.getDefault())
				.when()
				.body(body.toString())
				.put("/enkelvoudiginformatieobjecten/" + id)
				.then()
				.extract()
				.response();
		// @formatter:on
	}

	public Response partialUpdate(String eioUrl, JSONObject body) {
		String id = eioUrl.substring(eioUrl.lastIndexOf('/') + 1).trim();
		// @formatter:off
		return given()
				.spec(DRCRequestSpecification.getDefault())
				.when()
				.body(body.toString())
				.patch("/enkelvoudiginformatieobjecten/" + id)
				.then()
				.extract()
				.response();
		// @formatter:on
	}

	public Response testIntegrityCreate(String iot, JSONObject integrity) {
		// @formatter:off
		return given()
				.spec(DRCRequestSpecification.getDefault())
				.body(DRCDataProvider.testIntegrityCreate(iot, integrity))
				.when()
				.post("/enkelvoudiginformatieobjecten")
				.then()
				.extract()
				.response();
		// @formatter:on
	}

	/**
	 * Get enkelvoudiginformatieobject (EIO)
	 * 
	 * @param id      String identificatie
	 * @param version Integer version number
	 * @return Response response of request
	 */
	public Response getEIO(String url, Integer version) {
		// @formatter:off
		return given().param("versie", version)
				.spec(DRCRequestSpecification.getDefault())
				.when()
				.get(url.split("/v1")[1])
				.then()
				.extract()
				.response();
		// @formatter:on
	}

	public Response getEIO(RequestSpecification requestSpecification, String eioUrl, Integer version) {
		// @formatter:off
		return given().param("versie", version)
				.spec(requestSpecification)
				.when()
				.get(eioUrl.split("/v1")[1])
				.then()
				.extract()
				.response();
		// @formatter:on
	}

	public Response getEIO(String eioUrl, Integer version, String registratieOp) {
		String id = eioUrl.substring(eioUrl.lastIndexOf('/') + 1).trim();
		Map<String, String> params = new HashMap<String, String>();

		if (version != null) {
			params.put("versie", String.valueOf(version));
		}

		if (registratieOp != null) {
			params.put("registratieOp", registratieOp);
		}

		// @formatter:off
		return given()
				.params(params)
				.spec(DRCRequestSpecification.getDefault())
				.when()
				.get("/enkelvoudiginformatieobjecten/" + id)
				.then()
				.extract()
				.response();
		// @formatter:on
	}

	public Response getHeadEIO(String url) {
		// @formatter:off
		return given()
				.spec(DRCRequestSpecification.getDefault())
				.when()
				.head(url.split("/v1")[1])
				.then()
				.extract()
				.response();
		// @formatter:on
	}

	public Response getEioIfNonMatch(String url, String eTag) {
		// @formatter:off
		return given()
				.spec(DRCRequestSpecification.getDefault())
				.header("If-None-Match", eTag)
				.when()
				.get(url.split("/v1")[1])
				.then()
				.extract()
				.response();
		// @formatter:on
	}

	public Response listEIO(String identificatie, String bronorganisatie, Integer page) {
		Map<String, String> params = new HashMap<String, String>();
		if (identificatie != null) {
			params.put("identificatie", identificatie);
		}

		if (bronorganisatie != null) {
			params.put("bronorganisatie", bronorganisatie);
		}

		if (page != null) {
			params.put("page", String.valueOf(page));
		}
		// @formatter:off
		return given()
				.params(params)
				.spec(DRCRequestSpecification.getDefault())
				.when()
				.get("/enkelvoudiginformatieobjecten")
				.then()
				.extract()
				.response();
		// @formatter:on
	}

	public Response listEIO(RequestSpecification requestSpecification, String identificatie, String bronorganisatie,
			Integer page) {
		Map<String, String> params = new HashMap<String, String>();
		if (identificatie != null) {
			params.put("identificatie", identificatie);
		}

		if (bronorganisatie != null) {
			params.put("bronorganisatie", bronorganisatie);
		}

		if (page != null) {
			params.put("page", String.valueOf(page));
		}
		// @formatter:off
		return given()
				.params(params)
				.spec(requestSpecification)
				.when()
				.get("/enkelvoudiginformatieobjecten")
				.then()
				.extract()
				.response();
		// @formatter:on
	}

	public Response listEIO(Map<String, String> params) {
		// @formatter:off
		return given()
				.params(params)
				.spec(DRCRequestSpecification.getDefault())
				.when()
				.get("/enkelvoudiginformatieobjecten")
				.then()
				.extract()
				.response();
		// @formatter:on
	}

	/**
	 * Downoad content as string.
	 * 
	 * @param url String inhoud URL
	 * @return String content
	 */
	public String downloadAsString(String url, String accept) {
		// @formatter:off
		return given()
		.spec(DRCRequestSpecification.getStream(accept))
		.when()
		.get(url.split("/v1")[1])
		.then()
		.extract()
		.asString();
		// @formatter:on
	}

	public String downloadAsString(String url) {
		return downloadAsString(url, null);
	}

	public Response download(String url, String accept) {
		// @formatter:off
		return given()
		.spec(DRCRequestSpecification.getStream(accept))
		.when()
		.get(url.split("/v1")[1])
		.then()
		.extract()
		.response();
		// @formatter:on
	}

	public Response delete(String url) {
		// @formatter:off
		return given()
		.spec(DRCRequestSpecification.getDefault())
		.when()
		.delete(url.split("/v1")[1])
		.then()
		.extract()
		.response();
		// @formatter:on
	}

	public Response search(JSONArray uuids) {
		// @formatter:off
		return given()
				.spec(DRCRequestSpecification.getDefault())
				.body(DRCDataProvider.search(uuids))
				.when()
				.post("/enkelvoudiginformatieobjecten/_zoek")
				.then()
				.extract()
				.response();
		// @formatter:on
	}

	public InputStream downloadEIO(String url, Integer version) {
		// @formatter:off
		return given().param("versie", version)
				.spec(DRCRequestSpecification.getStream(null))
				.when()
				.get(url.split("/v1")[1] + "/download")
				.then()
				.extract()
				.asInputStream();
		// @formatter:on
	}
}
