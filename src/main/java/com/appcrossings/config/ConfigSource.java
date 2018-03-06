package com.appcrossings.config;

import java.util.Properties;

public interface ConfigSource {

	public Properties traverseConfigs(String propertiesPath, String propertiesFileName);
	public Properties resolveConfigPath(String hostsFile);

}
