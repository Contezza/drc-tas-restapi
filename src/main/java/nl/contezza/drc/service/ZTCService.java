package nl.contezza.drc.service;

import static io.restassured.RestAssured.given;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import nl.contezza.drc.dataprovider.DRCDataProvider;

//@Log4j2
@AllArgsConstructor
public class ZTCService {

	public Response createZaaktype(String catalogusUrl) {
		// @formatter:off
		return given()
				.spec(DRCRequestSpecification.getZTC())
				.body(DRCDataProvider.createZaakType(catalogusUrl))
				.when()
				.post("/zaaktypen")
				.then()
				.extract()
				.response();
		// @formatter:on
	}

	/**
	 * Publish zaaktype.
	 * 
	 * @param id String UUID of informatieobjecttype
	 * @return Response response of request
	 */
	public Response publishIZaaktype(String url) {
		String id = url.substring(url.lastIndexOf('/') + 1).trim();
		// @formatter:off
		return given()
				.spec(DRCRequestSpecification.getZTC())
				.when()
				.post("/zaaktypen/" + id +"/publish")
				.then()
				.extract()
				.response();
		// @formatter:on
	}

	public Response createZiot(String zaaktypeUrl, String informatieobjecttypeUrl) {
		// @formatter:off
		return given()
				.spec(DRCRequestSpecification.getZTC())
				.body(DRCDataProvider.createZiot(zaaktypeUrl, informatieobjecttypeUrl))
				.when()
				.post("/zaaktype-informatieobjecttypen")
				.then()
				.extract()
				.response();
		// @formatter:on
	}
	
	/**
	 * Create random catalogi.
	 * 
	 * @return Response response of request
	 */
	public Response createCatalogus() {
		// @formatter:off
		return given()
				.spec(DRCRequestSpecification.getZTC())
				.body(DRCDataProvider.createCatalogi())
				.when()
				.post("/catalogussen")
				.then()
				.extract()
				.response();
		// @formatter:on
	}

	/**
	 * Create informatieobjecttype.
	 * 
	 * @param catalogusUrl String url
	 * @return Response response of request
	 */
	public Response createInformatieObjectType(String catalogusUrl) {
		// @formatter:off
		return given()
				.spec(DRCRequestSpecification.getZTC())
				.body(DRCDataProvider.createInformatieObjectType(catalogusUrl))
				.when()
				.post("/informatieobjecttypen")
				.then()
				.extract()
				.response();
		// @formatter:on
	}

	/**
	 * Publish informatieobjecttype.
	 * 
	 * @param id String UUID of informatieobjecttype
	 * @return Response response of request
	 */
	public Response publishInformatieObjectType(String id) {
		// @formatter:off
		return given()
				.spec(DRCRequestSpecification.getZTC())
				.when()
				.post("/informatieobjecttypen/" + id +"/publish")
				.then()
				.extract()
				.response();
		// @formatter:on
	}
}