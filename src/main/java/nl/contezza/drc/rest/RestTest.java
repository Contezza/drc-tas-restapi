package nl.contezza.drc.rest;

import java.security.SecureRandom;
import java.util.concurrent.ThreadLocalRandom;

import org.json.JSONException;
import org.json.JSONObject;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;

import com.aventstack.extentreports.testng.listener.ExtentITestListenerClassAdapter;

import io.restassured.path.json.JsonPath;
import lombok.extern.log4j.Log4j2;
import nl.contezza.drc.utils.PropertyLoader;

@Log4j2
@Listeners({ ExtentITestListenerClassAdapter.class })
public abstract class RestTest {

	protected static final String DRC_DOCKER_URI = PropertyLoader.getDockerURI();
	protected static final String DRC_BASE_URI = PropertyLoader.getBaseURI();

	protected static final String ZTC_DOCKER_URI = PropertyLoader.getZTCDockerURI();
	protected static final String ZTC_BASE_URI = PropertyLoader.getZRCBaseURI();

	protected static final String ZRC_DOCKER_URI = PropertyLoader.getZRCDockerURI();
	protected static final String ZRC_BASE_URI = PropertyLoader.getZRCBaseURI();

	public static final String UUID_REGEX = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[34][0-9a-fA-F]{3}-[89ab][0-9a-fA-F]{3}-[0-9a-fA-F]{12}";

	static final String AB = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	static SecureRandom rnd = new SecureRandom();

	protected String informatieobjecttypeUrl = null;

	protected JsonPath eioTestObject = null;
	protected JsonPath zaakTestObject = null;
	protected JsonPath zaakTypeTestObject = null;

	@BeforeSuite(alwaysRun = true)
	public void init() throws Exception {

	}

	protected static String randomString(int len) {
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++)
			sb.append(AB.charAt(rnd.nextInt(AB.length())));
		return sb.toString();
	}

	protected static JSONObject mergeJSONObjects(JSONObject json1, JSONObject json2) {
		JSONObject mergedJSON = new JSONObject();
		try {
			mergedJSON = new JSONObject(json1, JSONObject.getNames(json1));
			for (String crunchifyKey : JSONObject.getNames(json2)) {
				mergedJSON.put(crunchifyKey, json2.get(crunchifyKey));
			}

		} catch (JSONException e) {
			throw new RuntimeException("JSON Exception" + e);
		}
		return mergedJSON;
	}

	protected String randomRsin() {
		int rest;
		String rsin;
		do {
			rsin = "";
			int total = 0;
			for (int i = 0; i < 8; i++) {
				int rndDigit = ThreadLocalRandom.current().nextInt(0, i == 0 ? 2 : 9);
				total += rndDigit * (9 - i);
				rsin += rndDigit;
			}
			rest = total % 11;
		} while (rest > 9);
		return rsin + rest;
	}
	
	protected void wait(int mill) {
		try {
			Thread.sleep(mill);
		} catch (InterruptedException e) {
			log.error(e);
		}

	}
}