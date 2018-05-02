package com.appcrossings.config.discovery;

import java.net.URI;
import java.util.Optional;
import java.util.Properties;
import org.slf4j.Logger;
import com.appcrossings.config.Environment;
import com.appcrossings.config.util.StringUtils;

public class HostsFileDiscoveryStrategy implements ConfigDiscoveryStrategy {

  private static final Logger logger =
      org.slf4j.LoggerFactory.getLogger(HostsFileDiscoveryStrategy.class);

  @Override
  public Optional<URI> lookupConfigPath(Properties hostMappings, Properties envProps) {

    String envName = envProps.getProperty(Environment.ENV_NAME);
    String hostName = envProps.getProperty(Environment.HOST_NAME);

    Optional<URI> uri = Optional.empty();

    String startPath = hostMappings.getProperty(hostName);

    // Attempt environment as a backup
    if (!StringUtils.hasText(startPath) && StringUtils.hasText(envName)) {

      startPath = hostMappings.getProperty(envName);

    } 
    
    if (!StringUtils.hasText(startPath)) {

      logger.warn("Didn't locate any config path for host " + hostName + " or env " + envName
          + ". Falling back to '*' environment.");

      startPath = hostMappings.getProperty("*");// catch all

    }

    if (StringUtils.hasText(startPath)) {
      uri = Optional.ofNullable(URI.create(startPath));
    } else {
      logger.warn("Unable to resolve a config path from hosts lookup");
    }

    return uri;

  }

}
