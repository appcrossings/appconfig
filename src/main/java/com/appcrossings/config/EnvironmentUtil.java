package com.appcrossings.config;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnvironmentUtil {

	private final static Logger log = LoggerFactory.getLogger(EnvironmentUtil.class);
	protected String environmentName;
	protected String hostName;

	/**
	 * Attempt to detect environment of the application.
	 * 
	 * @return environment string
	 */
	public String detectEnvironment() {

		String env = this.environmentName; // initialize to configured environment

		if (StringUtils.hasText(env)) {

			env = StringUtils.hasText(env) ? env : System.getProperty("env");
			env = StringUtils.hasText(env) ? env : System.getProperty("ENV");
			env = StringUtils.hasText(env) ? env : System.getProperty("environment");
			env = StringUtils.hasText(env) ? env : System.getProperty("ENVIRONMENT");

			if (!StringUtils.hasText(env)) {
				log.info(
						"No environment variable detected under 'spring.profiles' or system properties 'env', 'ENV', 'environment', 'ENVIRONMENT'");
			} else {
				log.info("Detected environment: " + env);
			}
		}

		return env;
	}

	/**
	 * Reads the underlying host's name. This is used to match this host against its
	 * configuration. You can programmatically override the hostname value by
	 * setting System.setProperty("hostname", "value").
	 * 
	 * @return hostname
	 */
	public String detectHostName() {

		String hostName = this.hostName; // initialize to configured host name

		if (StringUtils.hasText(hostName)) {

			try {

				hostName = StringUtils.hasText(System.getProperty("hostname")) ? System.getProperty("hostname")
						: InetAddress.getLocalHost().getHostName();

				hostName = StringUtils.hasText(hostName) ? hostName : System.getProperty("HOSTNAME");

				if (!StringUtils.hasText(hostName)) {
					throw new UnknownHostException(
							"Unable to resolve host in order to resolve hosts file config. Searched system property 'hostname', 'HOSTNAME' and localhost.hostName");
				}

				if (hostName.contains(".")) {
					hostName = hostName.substring(0, hostName.indexOf("."));
				}

				log.info("Resolved hostname to: " + hostName);

			} catch (UnknownHostException ex) {
				log.error("Can't resolve hostname", ex);
				throw new RuntimeException(ex);
			}
		}

		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public void setEnvironmentName(String environmentName) {
		this.environmentName = environmentName;
	}

}
