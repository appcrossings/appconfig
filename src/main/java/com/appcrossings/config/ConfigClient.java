package com.appcrossings.config;

import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.beanutils.ConvertUtilsBean;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.EnvironmentStringPBEConfig;
import org.jasypt.properties.EncryptableProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.PropertyPlaceholderHelper;
import org.springframework.util.StringUtils;

/**
 * 
 * @author Krzysztof Karski
 *
 */
public class ConfigClient {

	/** Default placeholder prefix: {@value} */
	public static final String DEFAULT_PLACEHOLDER_PREFIX = "${";

	/** Default placeholder suffix: {@value} */
	public static final String DEFAULT_PLACEHOLDER_SUFFIX = "}";

	/** Defaults to {@value #DEFAULT_PLACEHOLDER_PREFIX} */
	protected String placeholderPrefix = DEFAULT_PLACEHOLDER_PREFIX;

	/** Defaults to {@value #DEFAULT_PLACEHOLDER_SUFFIX} */
	protected String placeholderSuffix = DEFAULT_PLACEHOLDER_SUFFIX;

	/** Default value separator: {@value} */
	public static final String DEFAULT_VALUE_SEPARATOR = ":";

	/** Defaults to {@value #DEFAULT_VALUE_SEPARATOR} */
	protected String valueSeparator = DEFAULT_VALUE_SEPARATOR;

	protected boolean ignoreUnresolvablePlaceholders = false;

	protected ConfigSource configSource;

	private class ReloadTask extends TimerTask {

		@Override
		public void run() {
			try {

				reload();

			} catch (Exception e) {
				logger.error("Error refreshing configs", e);
			}
		}
	}

	public final static String DEFAULT_PROPERTIES_FILE_NAME = "default.properties";
	private final static Logger logger = LoggerFactory.getLogger(ConfigClient.class);

	public final static boolean SEARCH_CLASSPATH = true;

	private final ConvertUtilsBean bean = new ConvertUtilsBean();

	private StandardPBEStringEncryptor encryptor = null;

	protected EnvironmentUtil envUtil = new EnvironmentUtil();

	private PropertyPlaceholderHelper helper;

	protected final String hostsFilePath;

	private final AtomicReference<Properties> loadedProperties = new AtomicReference<>();

	protected String propertiesFileName = DEFAULT_PROPERTIES_FILE_NAME;

	protected boolean searchClasspath = SEARCH_CLASSPATH;

	private Timer timer = new Timer(true);

	/**
	 * 
	 * @param path
	 *            The path of the hosts.properties file
	 * @throws Exception
	 */
	public ConfigClient(String path) throws Exception {
		helper = new PropertyPlaceholderHelper(this.placeholderPrefix, this.placeholderSuffix, this.valueSeparator,
				this.ignoreUnresolvablePlaceholders);
		this.hostsFilePath = path;
	}

	/**
	 * 
	 * @param path
	 *            - The path of the hosts.properties file
	 * @param refresh
	 *            - The period in seconds at which the config properties should be
	 *            refreshed. 0 indicates no automated timer
	 * @throws Exception
	 */
	public ConfigClient(String path, int refresh) throws Exception {
		this(path);
		setRefreshRate(refresh);
	}

	public <T> T getProperty(String key, Class<T> clazz) {

		String property;
		try {
			property = helper.replacePlaceholders(placeholderPrefix + key + placeholderSuffix, loadedProperties.get());

			if (property.equals(key))
				return null;

		} catch (IllegalArgumentException e) {
			return null;
		}

		if (clazz.equals(String.class))
			return (T) property;
		else if (property != null)
			return (T) bean.convert(property, clazz);
		else
			return null;

	}

	public <T> T getProperty(String key, Class<T> clazz, T value) {

		T val = getProperty(key, clazz);

		if (val != null)
			return val;

		return value;

	}

	protected void init() {

		String environmentName = envUtil.detectEnvironment();
		String hostName = envUtil.detectHostName();

		logger.info("Loading property files...");

		Properties hosts = configSource.loadHosts(hostsFilePath);

		if (encryptor != null)
			hosts = new EncryptableProperties(hosts, encryptor);

		String startPath = hosts.getProperty(hostName);

		// Attempt environment as a backup
		if (!StringUtils.hasText(startPath) && StringUtils.hasText(environmentName)) {

			startPath = hosts.getProperty(environmentName);

		} else if (!StringUtils.hasText(startPath)) {

			startPath = hosts.getProperty("*");// catch all

		}

		if (StringUtils.hasText(startPath)) {

			logger.debug("Searching for properties beginning at: " + startPath);

			Properties ps = configSource.loadProperties(startPath, propertiesFileName);

			if (encryptor != null)
				ps = new EncryptableProperties(ps, encryptor);

			if (ps.isEmpty()) {
				logger.warn(
						"Counldn't find any properties for host " + hostName + " or environment " + environmentName);
			}

			// Finally, propagate properties to PropertyPlaceholderConfigurer
			loadedProperties.set(ps);

		} else {
			logger.warn("Counldn't find any properties for host " + hostName + " or environment " + environmentName);
		}
	}

	public void reload() {

		init();

	}

	public void setEnvironment(String environmentName) {
		envUtil.setEnvironmentName(environmentName);
	}

	public void setFileName(String fileName) {
		this.propertiesFileName = fileName;
	}

	public void setHostName(String hostName) {
		envUtil.setHostName(hostName);
	}

	/**
	 * Set password on the encryptor. If an encryptor isn't configured, a
	 * BasicTextEncryptor will be initialized and the password set on it. The basic
	 * assumed encryption algorithm is PBEWithMD5AndDES. This can be changed by
	 * setting the StandardPBEStringEncryptor.
	 * 
	 * @param password
	 */
	public void setPassword(String password) {

		this.encryptor = new StandardPBEStringEncryptor();
		EnvironmentStringPBEConfig configurationEncryptor = new EnvironmentStringPBEConfig();
		configurationEncryptor.setAlgorithm("PBEWithMD5AndDES");
		configurationEncryptor.setPassword(password);
		encryptor.setConfig(configurationEncryptor);

	};

	/**
	 * Override default text encryptor (StandardPBEStringEncryptor). Enables
	 * overriding both password and algorithm.
	 * 
	 * @param encryptor
	 */
	public void setTextEncryptor(StandardPBEStringEncryptor config) {
		this.encryptor = config;
	}

	public void setRefreshRate(Integer refresh) {

		if (refresh == 0L || refresh == null) {
			timer.cancel();
			return;
		}

		synchronized (timer) {
			timer.cancel();
			timer = new Timer(true);
			timer.schedule(new ReloadTask(), refresh * 1000, refresh * 1000);
		}
	}

	public void setSearchClasspath(boolean searchClasspath) {
		this.searchClasspath = searchClasspath;
	}
}
