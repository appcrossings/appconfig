package com.appcrossings.config;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigSourceResolver {

  private final static Logger logger = LoggerFactory.getLogger(ConfigSourceResolver.class);

  public static final String FILE_SYSTEM = "file";
  public static final String HTTPS = "http";

  protected final Map<String, ConfigSource> sources = new HashMap<>();

  public ConfigSourceResolver() {
    ServiceLoader<ConfigSource> loader = ServiceLoader.load(ConfigSource.class);

    for (ConfigSource s : loader) {
      sources.put(s.getSourceName().toLowerCase(), s);
    }
  }

  public ConfigSource getBySourceName(final String sourceName) {

    if (sources.containsKey(sourceName.toLowerCase()))
      return sources.get(sourceName.toLowerCase());


    throw new IllegalArgumentException("No config source known for name " + sourceName);

  }
  
  public ConfigSource resolveSource(String path) {
    
    for(ConfigSource s : sources.values()) {
      if(s.isCompatible(path))
        return s;
    }
    
    throw new IllegalArgumentException("Unable to resolve a config source for path " + path);
  }

}
