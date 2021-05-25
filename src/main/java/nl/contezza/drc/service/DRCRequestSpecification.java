package nl.contezza.drc.service;

import java.util.Calendar;
import java.util.Date;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.RestAssuredConfig;
import io.restassured.config.SSLConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import nl.contezza.drc.utils.PropertyLoader;

public class DRCRequestSpecification {

	private static final String BASE_URI = PropertyLoader.getBaseURI();
	public static final String BASE_PATH = PropertyLoader.getBasePath();
	private static final String CLIENT_ID = PropertyLoader.getClientID();
	private static final String SECRET = PropertyLoader.getSecret();
	
	public static final String CLIENT_ID_READONLY = PropertyLoader.getClientIDReadonly();
	private static final String SECRET_READONLY = PropertyLoader.getSecretReadonly();
	
	public static final String CLIENT_ID_WRONG_SCOPE = PropertyLoader.getClientIDWrongScope();
	private static final String SECRET_WRONG_SCOPE = PropertyLoader.getSecretWrongScope();

	private static final String ZTC_BASE_URI = PropertyLoader.getZTCBaseURI();
	private static final String ZTC_BASE_PATH = PropertyLoader.getZTCBasePath();
	private static final String ZTC_CLIENT_ID = PropertyLoader.getZTCClientID();
	private static final String ZTC_SECRET = PropertyLoader.getZTCSecret();

	private static final String ZRC_BASE_URI = PropertyLoader.getZRCBaseURI();
	private static final String ZRC_BASE_PATH = PropertyLoader.getZRCBasePath();
	private static final String ZRC_CLIENT_ID = PropertyLoader.getZRCClientID();
	private static final String ZRC_SECRET = PropertyLoader.getZRCSecret();
	
	private static final String AC_BASE_URI = PropertyLoader.getACBaseURI();
	private static final String AC_BASE_PATH = PropertyLoader.getACBasePath();
	private static final String AC_CLIENT_ID = PropertyLoader.getACClientID();
	private static final String AC_SECRET = PropertyLoader.getACSecret();

	/**
	 * Default request specification for interacting with documentregistratiecomponent.
	 * 
	 * @return RequestSpecification specification
	 */
	public static RequestSpecification getDefault() {
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
		// @formatter:off
		return new RequestSpecBuilder().setConfig(new RestAssuredConfig()
				.sslConfig(new SSLConfig()
				.relaxedHTTPSValidation())).setRelaxedHTTPSValidation()
				.setContentType(ContentType.JSON)
				.setAccept(ContentType.JSON)
				.setBaseUri(BASE_URI)
				.setBasePath(BASE_PATH)
				.addHeader(
			              "Authorization",
			              "Bearer " + getToken(CLIENT_ID, SECRET, 10)
			             )
				.log(LogDetail.ALL)
				.addFilter(new ResponseLoggingFilter())
				.build();
		// @formatter:on
	}
	
	public static RequestSpecification getReadonly() {
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
		// @formatter:off
		return new RequestSpecBuilder().setConfig(new RestAssuredConfig()
				.sslConfig(new SSLConfig()
				.relaxedHTTPSValidation())).setRelaxedHTTPSValidation()
				.setContentType(ContentType.JSON)
				.setAccept(ContentType.JSON)
				.setBaseUri(BASE_URI)
				.setBasePath(BASE_PATH)
				.addHeader(
			              "Authorization",
			              "Bearer " + getToken(CLIENT_ID_READONLY, SECRET_READONLY, 10)
			             )
				.log(LogDetail.ALL)
				.addFilter(new ResponseLoggingFilter())
				.build();
		// @formatter:on
	}
	
	public static RequestSpecification getWrongScope() {
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
		// @formatter:off
		return new RequestSpecBuilder().setConfig(new RestAssuredConfig()
				.sslConfig(new SSLConfig()
				.relaxedHTTPSValidation())).setRelaxedHTTPSValidation()
				.setContentType(ContentType.JSON)
				.setAccept(ContentType.JSON)
				.setBaseUri(BASE_URI)
				.setBasePath(BASE_PATH)
				.addHeader(
			              "Authorization",
			              "Bearer " + getToken(CLIENT_ID_WRONG_SCOPE, SECRET_WRONG_SCOPE, 10)
			             )
				.log(LogDetail.ALL)
				.addFilter(new ResponseLoggingFilter())
				.build();
		// @formatter:on
	}

	/**
	 * Spec for downloading content.
	 * 
	 * @return RequestSpecification specification
	 */
	public static RequestSpecification getStream(String accept) {
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

		if (accept == null) {

		// @formatter:off
		return new RequestSpecBuilder().setConfig(new RestAssuredConfig()
				.sslConfig(new SSLConfig()
				.relaxedHTTPSValidation())).setRelaxedHTTPSValidation()
				.setBaseUri(BASE_URI)
				.setBasePath(BASE_PATH)		
				.addHeader(
			              "Authorization",
			              "Bearer " + getToken(CLIENT_ID, SECRET, 10)
			             )
				.log(LogDetail.ALL)
				.addFilter(new ResponseLoggingFilter())
				.build();
		// @formatter:on
		}

		// @formatter:off
		return new RequestSpecBuilder().setConfig(new RestAssuredConfig()
				.sslConfig(new SSLConfig()
				.relaxedHTTPSValidation())).setRelaxedHTTPSValidation()
				.setBaseUri(BASE_URI)
				.setBasePath(BASE_PATH)
				.setAccept(accept)
				.addHeader(
			              "Authorization",
			              "Bearer " + getToken(CLIENT_ID, SECRET, 10)
			             )
				.log(LogDetail.ALL)
				.addFilter(new ResponseLoggingFilter())
				.build();
		// @formatter:on

	}

	/**
	 * Request specification for communication with zaaktypecatalogus.
	 * 
	 * @return RequestSpecification request specification
	 */
	public static RequestSpecification getZTC() {
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
		// @formatter:off
		return new RequestSpecBuilder().setConfig(new RestAssuredConfig()
				.sslConfig(new SSLConfig()
				.relaxedHTTPSValidation())).setRelaxedHTTPSValidation()
				.setContentType(ContentType.JSON)
				.setAccept(ContentType.JSON)
				.setBaseUri(ZTC_BASE_URI)
				.setBasePath(ZTC_BASE_PATH)
				.addHeader(
			              "Authorization",
			              "Bearer " + getToken(ZTC_CLIENT_ID, ZTC_SECRET, 10)
			             )
				.log(LogDetail.ALL)
				.addFilter(new ResponseLoggingFilter())
				.build();
		// @formatter:on
	}

	/**
	 * Request specification for communication with zaakregistratiecomponent (ZRC).
	 * 
	 * @return RequestSpecification request specification
	 */
	public static RequestSpecification getZRC() {
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
		// @formatter:off
		return new RequestSpecBuilder().setConfig(new RestAssuredConfig()
				.sslConfig(new SSLConfig()
				.relaxedHTTPSValidation())).setRelaxedHTTPSValidation()
				.setContentType(ContentType.JSON)
				.setAccept(ContentType.JSON)
				.setBaseUri(ZRC_BASE_URI)
				.setBasePath(ZRC_BASE_PATH)
				.addHeader(
			              "Authorization",
			              "Bearer " + getToken(ZRC_CLIENT_ID, ZRC_SECRET, 10)
			             )
				.addHeader("Accept-Crs", "EPSG:4326")
				.addHeader("Content-Crs", "EPSG:4326")
				.log(LogDetail.ALL)
				.addFilter(new ResponseLoggingFilter())
				.build();
		// @formatter:on
	}
	
	public static RequestSpecification getAC() {
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
		// @formatter:off
		return new RequestSpecBuilder().setConfig(new RestAssuredConfig()
				.sslConfig(new SSLConfig()
				.relaxedHTTPSValidation())).setRelaxedHTTPSValidation()
				.setContentType(ContentType.JSON)
				.setAccept(ContentType.JSON)
				.setBaseUri(AC_BASE_URI)
				.setBasePath(AC_BASE_PATH)
				.addHeader(
			              "Authorization",
			              "Bearer " + getToken(AC_CLIENT_ID, AC_SECRET, 10)
			             )
				.log(LogDetail.ALL)
				.addFilter(new ResponseLoggingFilter())
				.build();
		// @formatter:on
	}

	/**
	 * Generate token based on client id and secret.
	 * 
	 * @param clientId              String client id
	 * @param secret                String secret
	 * @param tokenExpiresInMinutes Integer expiriation in minutes
	 * @return String Baerer token
	 */
	public static String getToken(String clientId, String secret, Integer tokenExpiresInMinutes) {
		Algorithm algorithm = Algorithm.HMAC256(secret);
		Calendar now = Calendar.getInstance();
		now.add(Calendar.MINUTE, tokenExpiresInMinutes);
		Date until = now.getTime();
		return JWT.create().withIssuer(clientId).withClaim("client_id", clientId).withIssuedAt(new Date()).withExpiresAt(until).sign(algorithm);
	}
}
