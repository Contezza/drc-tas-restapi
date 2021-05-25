package nl.contezza.drc.service;

import static io.restassured.RestAssured.given;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import nl.contezza.drc.dataprovider.DRCDataProvider;

@AllArgsConstructor
public class AuthService {

	public Response list(String clientIds, Integer page) {
		Map<String, String> params = new HashMap<String, String>();
		if (clientIds != null) {
			params.put("clientIds", clientIds);
		}
		if (page != null) {
			params.put("page", String.valueOf(page));
		}
		// @formatter:off
		return given()
				.params(params)
				.spec(DRCRequestSpecification.getAC())
				.when()
				.get("/applicaties")
				.then()
				.extract()
				.response();
		// @formatter:on
	}

	public Response updatePartial(String acUrl, JSONArray clientIds, JSONArray scopes, String iotUrl, String maxVertrouwelijkheid) {
		String id = acUrl.substring(acUrl.lastIndexOf('/') + 1).trim();
		// @formatter:off
		return given()
				.spec(DRCRequestSpecification.getAC())
				.body(DRCDataProvider.updatePartialAC(clientIds, scopes, iotUrl, maxVertrouwelijkheid))
				.when()
				.patch("/applicaties/" + id)
				.then()
				.extract()
				.response();
		// @formatter:on
	}
}
