package com.appcrossings.config.strategy;

import java.util.Properties;
import com.appcrossings.config.ConfigLookupStrategy;
import com.appcrossings.config.util.Environment;
import com.appcrossings.config.util.StringUtils;

public class DefaultConfigLookupStrategy implements ConfigLookupStrategy {

  @Override
  public String lookupConfigPath(Properties hostMappings, Properties envProps) {

    String startPath = hostMappings.getProperty(envProps.getProperty(Environment.PREFIX + Environment.HOST_NAME));

    // Attempt environment as a backup
    if (!StringUtils.hasText(startPath) && StringUtils.hasText(envProps.getProperty(Environment.PREFIX + Environment.ENV_NAME))) {

      startPath = hostMappings.getProperty(envProps.getProperty(Environment.PREFIX + Environment.ENV_NAME));

    }

    if (!StringUtils.hasText(startPath)) {

      startPath = hostMappings.getProperty("*");// catch all

    }

    if (startPath == null || startPath.trim().equals("")) {
      throw new IllegalArgumentException(
          "Hosts file failed to resolve a config start path evnironment settings "
              + envProps.toString());
    }
    
    return startPath;
    
  }

}
