package com.appcrossings.config;

import java.util.Properties;

public interface ConfigLookupStrategy {
  
  public String lookupConfigPath(Properties hosts, Properties envProps);

}
