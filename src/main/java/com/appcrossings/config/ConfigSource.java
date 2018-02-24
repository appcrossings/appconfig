package com.appcrossings.config;

import java.util.Properties;

public interface ConfigSource {

	public Properties loadProperties(String propertiesPath, String propertiesFileName);
	public Properties loadHosts(String hostsFile);

}
