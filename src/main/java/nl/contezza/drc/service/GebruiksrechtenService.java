package nl.contezza.drc.service;

import static io.restassured.RestAssured.given;

import java.util.HashMap;
import java.util.Map;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.AllArgsConstructor;
import nl.contezza.drc.dataprovider.DRCDataProvider;

//@Log4j2
@AllArgsConstructor
public class GebruiksrechtenService {

	public Response create(String eioUrl) {
		// @formatter:off
		return given()
				.spec(DRCRequestSpecification.getDefault())
				.body(DRCDataProvider.createGebruiksrecht(eioUrl))
				.when()
				.post("/gebruiksrechten")
				.then()
				.extract()
				.response();
		// @formatter:on
	}
	
	public Response create(RequestSpecification req, String eioUrl) {
		// @formatter:off
		return given()
				.spec(req)
				.body(DRCDataProvider.createGebruiksrecht(eioUrl))
				.when()
				.post("/gebruiksrechten")
				.then()
				.extract()
				.response();
		// @formatter:on
	}

	public Response list(RequestSpecification req, String informatieobject) {
		Map<String, String> params = new HashMap<String, String>();
		if (informatieobject != null) {
			params.put("informatieobject", informatieobject);
		}
		// @formatter:off
		return given()
				.params(params)
				.spec(req)
				.when()
				.get("/gebruiksrechten")
				.then()
				.extract()
				.response();
		// @formatter:on
	}
	
	public Response get(RequestSpecification req, String url) {
		String id = url.substring(url.lastIndexOf('/') + 1).trim();
		// @formatter:off
		return given()
				.spec(req)
				.when()
				.get("/gebruiksrechten/" + id)
				.then()
				.extract()
				.response();
		// @formatter:on
	}
}