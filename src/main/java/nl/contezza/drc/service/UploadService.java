package nl.contezza.drc.service;

import static io.restassured.RestAssured.given;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;

import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@AllArgsConstructor
public class UploadService {

	private static final String TARGET_LOCATION = "target/";

	/**
	 * Create plain text file.
	 * 
	 * @param contents String the contents of file
	 * @return File the file
	 */
	public File createTextFile(String contents) {
		try {
			File file = new File(TARGET_LOCATION + RandomStringUtils.random(10, true, false) + ".txt");
			FileUtils.writeStringToFile(file, contents, "UTF-8");
			return file;
		} catch (IOException e) {
			log.error(e);
		}
		return null;
	}

	/**
	 * Upload file
	 * 
	 * @param url  String bestandsdelen url
	 * @param lock String lock id
	 * @param file File the file to upload
	 * @return Response response
	 */
	public Response uploadFile(String url, String lock, File file) {
		String id = url.substring(url.lastIndexOf('/') + 1).trim();
		// @formatter:off
		return given()
				.spec(DRCRequestSpecification.getStream(null))
				.when()
				.multiPart("inhoud", file)
				.multiPart("lock", lock)
				.when()
				.put("/bestandsdelen/" + id)				
				.then()
				.extract()
				.response();
		// @formatter:on
	}
}
