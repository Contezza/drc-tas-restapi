package nl.contezza.drc.service;

import static io.restassured.RestAssured.given;

import java.util.HashMap;
import java.util.Map;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.AllArgsConstructor;

//@Log4j2
@AllArgsConstructor
public class OIOService {

	public Response listOIO(String object, String informatieobject) {
		Map<String, String> params = new HashMap<String, String>();
		if (object != null) {
			params.put("object", object);
		}

		if (informatieobject != null) {
			params.put("informatieobject", informatieobject);
		}
		// @formatter:off
		return given()
				.params(params)
				.spec(DRCRequestSpecification.getDefault())
				.when()
				.get("/objectinformatieobjecten")
				.then()
				.extract()
				.response();
		// @formatter:on
	}

	public Response listOIO(RequestSpecification req, String object, String informatieobject) {
		Map<String, String> params = new HashMap<String, String>();
		if (object != null) {
			params.put("object", object);
		}

		if (informatieobject != null) {
			params.put("informatieobject", informatieobject);
		}
		// @formatter:off
		return given()
				.params(params)
				.spec(req)
				.when()
				.get("/objectinformatieobjecten")
				.then()
				.extract()
				.response();
		// @formatter:on
	}

	public Response getOIO(String url) {
		return getOIO(DRCRequestSpecification.getDefault(), url);
	}

	public Response getOIO(RequestSpecification req, String url) {
		String id = url.substring(url.lastIndexOf('/') + 1).trim();
		// @formatter:off
		return given()
				.spec(req)
				.when()
				.get("/objectinformatieobjecten/" + id)
				.then()
				.extract()
				.response();
		// @formatter:on
	}

	public Response getHeadOIO(String url) {
		String id = url.substring(url.lastIndexOf('/') + 1).trim();
		// @formatter:off
		return given()
				.spec(DRCRequestSpecification.getDefault())
				.when()
				.head("/objectinformatieobjecten/" + id)
				.then()
				.extract()
				.response();
		// @formatter:on
	}

	public Response getOioIfNonMatch(String url, String eTag) {
		String id = url.substring(url.lastIndexOf('/') + 1).trim();
		// @formatter:off
		return given()
				.spec(DRCRequestSpecification.getDefault())
				.header("If-None-Match", eTag)
				.when()
				.get("/objectinformatieobjecten/" + id)
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
}