package com.appcrossings.config;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigSourceFactory {

  private final static Logger logger = LoggerFactory.getLogger(ConfigSourceFactory.class);

  public static final String FILE_SYSTEM = "file";
  public static final String AWS_S3 = "s3";
  public static final String HTTPS = "http";

  protected final Map<String, ConfigSource> sources = new HashMap<>();

  public ConfigSource buildConfigSource(final String source) {

    if (sources.containsKey(source.toLowerCase()))
      return sources.get(source.toLowerCase());

    ConfigSource cs = null;

    Class clazz = null;

    try {
      switch (source) {
        case FILE_SYSTEM:

          clazz = Class.forName("com.appcrossings.config.file.FilesystemSource");
          break;

        case HTTPS:

          clazz = Class.forName("com.appcrossings.config.http.HttpSource");
          break;

        default:
          throw new IllegalArgumentException("No config source known for " + source);

      }

      cs = (ConfigSource) clazz.newInstance();

    } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
      throw new RuntimeException(e);
    }

    if (!sources.containsKey(source.toLowerCase()))
      sources.put(source.toLowerCase(), cs);

    return cs;

  }

  public String resolveConfigSource(final String filePath) {
	  
	  assert filePath != null && filePath != "" : "filePath is null or empty.";
	  
	  final String prefix = filePath.trim().substring(0, filePath.indexOf("/"));
	  
	  if(prefix == "" || prefix.equals(File.separator) || prefix.toLowerCase().startsWith("file:")) {
	    return FILE_SYSTEM;
	  }else if(prefix.toLowerCase().startsWith("classpath")) {
	    return FILE_SYSTEM;
	  }else if(prefix.toLowerCase().startsWith("http")){
	    return HTTPS;
	  }else if(prefix.toLowerCase().startsWith("s3")) {
	    return AWS_S3;
	  }else {
	    throw new RuntimeException("unable to resolve a config source for " + filePath);
	  }
	  
	}

}
