package nl.contezza.drc.service;

import static io.restassured.RestAssured.given;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import nl.contezza.drc.dataprovider.DRCDataProvider;

//@Log4j2
@AllArgsConstructor
public class ZRCService {

	public Response createZaak(String zaaktypeUrl) {
		// @formatter:off
		return given()
				.spec(DRCRequestSpecification.getZRC())
				.body(DRCDataProvider.createZaak(zaaktypeUrl))
				.when()
				.post("/zaken")
				.then()
				.extract()
				.response();
		// @formatter:on
	}

	public Response createZio(String informatieobjectUrl, String zaakUrl) {
		// @formatter:off
		return given()
				.spec(DRCRequestSpecification.getZRC())
				.body(DRCDataProvider.createZio(informatieobjectUrl, zaakUrl))
				.when()
				.post("/zaakinformatieobjecten")
				.then()
				.extract()
				.response();
		// @formatter:on
	}
}