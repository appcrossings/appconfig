package com.appcrossings.config.processor;

import java.io.InputStream;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessorSelector {

  private final static Logger log = LoggerFactory.getLogger(ProcessorSelector.class);

  public static Properties process(String uri, InputStream stream) {

    Properties p = new Properties();

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

}
