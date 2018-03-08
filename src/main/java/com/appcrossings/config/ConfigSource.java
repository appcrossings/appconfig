package com.appcrossings.config;

import java.util.Properties;

public interface ConfigSource {

    /**
     * Retrieves a single node of the config given by the properties path. No traversal.
     * @param propertiesPath
     * @param propertiesFileName
     * @return
     */
    public Properties fetchConfig(String propertiesPath, String propertiesFileName);
    
    /**
     * Traverses a config tree per the underlying implementation's mechanism. 
     * 
     * @param propertiesPath
     * @param propertiesFileName
     * @return
     */
	public Properties traverseConfigs(String propertiesPath, String propertiesFileName);
	
	/**
	 * Resolves the config node to start traversal at via hosts entry. 
	 * 
	 * @param hostsFile
	 * @param hostsFileName
	 * @return
	 */
	public Properties resolveConfigPath(String hostsFile, String hostsFileName);
	
	/**
	 * A unique name for this config source type
	 * 
	 * @return
	 */
	public String getSourceName();
	
	/**
	 * Method resolving to true or false depending if the given paths are compatible with this source's capabilities. I.e. the 
	 * method could look at the path prefix (file://, classpath:) and determine if it can handle this path or not.
	 * 
	 * @param paths
	 * @return
	 */
	public boolean isCompatible(String path);

}
