package com.appcrossings.config.processor;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import com.appcrossings.config.util.StringUtils;

public class PropertiesProcessor {

  public static Map<String, Object> asProperties(InputStream stream) {

    try {

      Map<String, Object> props = new HashMap<>();
      BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
      String line = null;
      while ((line = reader.readLine()) != null) {

        if (line.trim().startsWith("#") || line.trim().startsWith("//") || !line.contains("=")) {
          continue;
        }

        String[] prop = line.split("=", 2);

        if (prop.length != 2) {
          continue;
        }

        props.put(prop[0].trim(), prop[1].trim());

      }

      return props;


    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static Map<String, Object> toMap(Properties props) {

    Map<String, Object> hash = new HashMap<>();
    props.entrySet().stream().forEach(e -> {
      hash.put((String) e.getKey(), e.getValue());
    });

    return hash;

  }

  public static Map<String, Object> asProperties(byte[] stream) {

    Map<String, Object> props = new HashMap<>();

    try (InputStream is = new ByteArrayInputStream(stream)) {

      return asProperties(is);

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static boolean isPropertiesFile(String path) {

    assert StringUtils.hasText(path) : "Path was null or empty";
    return (path.toLowerCase().endsWith(".properties"));
  }

  public static Properties asProperties(Map<String, Object> map) {

    final Properties props = new Properties();

    if (!map.isEmpty()) {
      map.forEach((k, v) -> {
        if (v != null && k != null && k != "")
          props.put(k, v);
      });
    }
    return props;

  }

  public static String toText(Map<String, Object> map) {

    StringBuilder builder = new StringBuilder();

    if (!map.isEmpty()) {
      map.forEach((k, v) -> {
        if (v != null && k != null && k != "")
          builder.append(k + "=" + v).append("\n");
      });
    }

    return builder.toString();
  }

}
