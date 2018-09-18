package com.appcrossings.config.processor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;
import com.appcrossings.config.util.StringUtils;

public class YamlProcessor {

  public static Map<String, Object> asProperties(InputStream stream) {

    Yaml yaml = new Yaml();
    final Map<String, Object> properties = new HashMap<>();
    LinkedHashMap<String, Object> map = (LinkedHashMap) yaml.load(stream);

    StringBuilder builder = new StringBuilder();
    recurse(map, builder, properties);

    return properties;
  }

  public static Map<String, Object> asProperties(byte[] stream) {

    Yaml yaml = new Yaml();
    final Map<String, Object> properties = new HashMap<>();
    LinkedHashMap<String, Object> map = (LinkedHashMap) yaml.load(new ByteArrayInputStream(stream));

    StringBuilder builder = new StringBuilder();
    recurse(map, builder, properties);

    return properties;
  }

  public static boolean isYamlFile(String path) {

    assert StringUtils.hasText(path) : "Path was null or empty";
    return (path.toLowerCase().endsWith(".yaml") || path.toLowerCase().endsWith(".yml"));
  }

  private static void recurse(List<Object> list, StringBuilder builder, Map<String, Object> props) {

    final String node = builder.toString();

    int i = 0;
    for (Object k : list) {

      if (k instanceof String) {

        String key = builder.toString() + "[" + i + "]";
        props.put(key, k);

      } else if (k instanceof LinkedHashMap) {

        recurse((Map) k, builder, props);

      } else if (k instanceof ArrayList) {

        recurse((List) k, builder, props);

      }

      builder = new StringBuilder(node);
      i++;
    }

  }

  private static void recurse(Map<String, Object> map, StringBuilder builder,
      Map<String, Object> props) {

    final String node = builder.toString();


    for (Object k : map.keySet()) {

      Object i = map.get(k);

      if (builder.length() > 0)
        builder.append("." + k);
      else
        builder.append(k);

      if (i instanceof LinkedHashMap) {

        recurse((Map) i, builder, props);

      } else if (i instanceof ArrayList) {

        recurse((List) i, builder, props);

      } else if (i instanceof String) {

        props.put(builder.toString(), i);

      } else if (i instanceof Boolean) {
        
        props.put(builder.toString(), i);
        
      } else if (i instanceof Integer) {
        
        props.put(builder.toString(), i);
        
      }

      builder = new StringBuilder(node);
    }
  }
}
