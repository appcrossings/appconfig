package com.appcrossings.config;

import java.util.Optional;
import java.util.Properties;

public interface ConfigLookupStrategy {

  public Optional<String> lookupConfigPath(Properties hosts, Properties envProps);

}
