package nl.contezza.drc.service;

import static io.restassured.RestAssured.given;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.testng.Assert;

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
	
	public Response updatePartial(String acUrl, JSONArray clientIds,  JSONArray auths, Boolean heeftAlleAutorisaties) {
		String id = acUrl.substring(acUrl.lastIndexOf('/') + 1).trim();
		// @formatter:off
		return given()
				.spec(DRCRequestSpecification.getAC())
				.body(DRCDataProvider.updatePartialAC(clientIds, auths, heeftAlleAutorisaties))
				.when()
				.patch("/applicaties/" + id)
				.then()
				.extract()
				.response();
		// @formatter:on
	}

	/**
	 * Update scopes of read only client
	 * @param scopes JSONArray scopes
	 * @param auths JSONArray auths
	 * @param heeftAlleAutorisaties
	 */
	public void updateReadOnlyClientScope(JSONArray scopes, JSONArray auths, Boolean heeftAlleAutorisaties) {
		AuthService authService = new AuthService();

		// get url of client_id
		Response res = authService.list(DRCRequestSpecification.CLIENT_ID_READONLY, null);
		String acUrl = res.body().path("results[0].url");

		// update client
		res = authService.updatePartial(acUrl, new JSONArray().put(DRCRequestSpecification.CLIENT_ID_READONLY), auths, heeftAlleAutorisaties);
		Assert.assertEquals(res.getStatusCode(), 200);
	}
	
	/**
	 * Update scopes of read only client
	 * 
	 * @param scopes                  JSONArray array of scopes
	 * @param confidentiality         String confidentiality level
	 * @param informatieobjecttypeUrl String iot
	 */
	public void updateReadOnlyClientScope(JSONArray scopes, String confidentiality, String informatieobjecttypeUrl) {
		AuthService authService = new AuthService();

		// get url of client_id
		Response res = authService.list(DRCRequestSpecification.CLIENT_ID_READONLY, null);
		String acUrl = res.body().path("results[0].url");

		// update client
		res = authService.updatePartial(acUrl, new JSONArray().put(DRCRequestSpecification.CLIENT_ID_READONLY), scopes, informatieobjecttypeUrl, confidentiality);
		Assert.assertEquals(res.getStatusCode(), 200);
	}
}
