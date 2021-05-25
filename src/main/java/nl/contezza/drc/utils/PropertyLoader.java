package nl.contezza.drc.utils;

import static nl.contezza.drc.utils.DefaultValues.ENVIRONMENTS_PATH;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Properties;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class PropertyLoader {

	private static final String PROPERTY_NOT_FOUND = "Property '{0}' or directory 'environments/{1}/config.properties' wasn't found in config archives";

	private static String getProperties(final String property) {
		Properties properties = new Properties();
		String env = getEnvironment();

		try (InputStream propFileInpStream = new FileInputStream(ENVIRONMENTS_PATH + env + "/config.properties")) {
			properties.load(propFileInpStream);
			return properties.getProperty(property);

		} catch (IOException | NullPointerException e) {
			log.error(MessageFormat.format(PROPERTY_NOT_FOUND, property, env, e));
		}
		return MessageFormat.format(PROPERTY_NOT_FOUND, property, env);
	}

	public static String getBaseURI() {
		return getProperties("service.baseuri");
	}

	public static String getBasePath() {
		return getProperties("service.basepath");
	}

	public static String getClientID() {
		return getProperties("service.clientId");
	}

	public static String getSecret() {
		return getProperties("service.secret");
	}
	
	public static String getClientIDReadonly() {
		return getProperties("service.readonly.clientId");
	}

	public static String getSecretReadonly() {
		return getProperties("service.readonly.secret");
	}
	
	public static String getClientIDWrongScope() {
		return getProperties("service.wrong-scope.clientId");
	}

	public static String getSecretWrongScope() {
		return getProperties("service.wrong-scope.secret");
	}
	
	public static String getDockerURI() {
		return getProperties("service.dockeruri");
	}

	public static String getZTCBaseURI() {
		return getProperties("service.ztc.baseuri");
	}

	public static String getZTCBasePath() {
		return getProperties("service.ztc.basepath");
	}

	public static String getZTCClientID() {
		return getProperties("service.ztc.clientId");
	}

	public static String getZTCSecret() {
		return getProperties("service.ztc.secret");
	}

	public static String getZTCDockerURI() {
		return getProperties("service.ztc.dockeruri");
	}
	
	public static String getZRCBaseURI() {
		return getProperties("service.zrc.baseuri");
	}

	public static String getZRCBasePath() {
		return getProperties("service.zrc.basepath");
	}

	public static String getZRCClientID() {
		return getProperties("service.zrc.clientId");
	}

	public static String getZRCSecret() {
		return getProperties("service.zrc.secret");
	}

	public static String getZRCDockerURI() {
		return getProperties("service.zrc.dockeruri");
	}
	
	public static String getACBaseURI() {
		return getProperties("service.ac.baseuri");
	}

	public static String getACBasePath() {
		return getProperties("service.ac.basepath");
	}

	public static String getACClientID() {
		return getProperties("service.ac.clientId");
	}

	public static String getACSecret() {
		return getProperties("service.ac.secret");
	}

	public static String getACDockerURI() {
		return getProperties("service.ac.dockeruri");
	}

	public static String getEnvironment() {
		return System.getProperty("env") == null ? "dev" : System.getProperty("env");
	}
}