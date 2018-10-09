package com.appcrossings.config.processor;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessorSelector {

  private final static Logger log = LoggerFactory.getLogger(ProcessorSelector.class);

  public static Map<String, Object> process(String uri, InputStream stream) {

    Map<String, Object> p = new HashMap<>();

    try {

      if (JsonProcessor.isJsonFile(uri)) {

        p = JsonProcessor.asProperties(stream);

      } else if (YamlProcessor.isYamlFile(uri)) {

        p = YamlProcessor.asProperties(stream);

      } else if (PropertiesProcessor.isPropertiesFile(uri)) {

        p = PropertiesProcessor.asProperties(stream);

      } else {

        log.warn("Unable to process file " + uri + ". No compatible file processor found.");

      }
    } finally {
      try {
        stream.close();
      } catch (Exception e) {
        log.error(e.getMessage());
      }
    }


    return p;

  }

  public static Map<String, Object> process(String uri, byte[] bytes) {

    Map<String, Object> p = new HashMap<>();

    if (JsonProcessor.isJsonFile(uri)) {

      p = JsonProcessor.asProperties(bytes);

    } else if (YamlProcessor.isYamlFile(uri)) {

      p = YamlProcessor.asProperties(bytes);

    } else if (PropertiesProcessor.isPropertiesFile(uri)) {

      p = PropertiesProcessor.asProperties(bytes);

    } else {

      log.warn("Unable to process file " + uri + ". No compatible file processor found.");

    }

    return p;

  }

}
