package com.appcrossings.config.discovery;

import java.net.URI;
import java.util.Optional;
import java.util.Properties;

public interface ConfigDiscoveryStrategy {

  public Optional<URI> lookupConfigPath(Properties hosts, Properties envProps);

}
