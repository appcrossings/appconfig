package com.appcrossings.config.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.io.IOUtils;
import com.jsoniter.JsonIterator;
import com.jsoniter.ValueType;
import com.jsoniter.any.Any;

public class JsonProcessor {

  public static Properties asProperties(InputStream stream) {

    Properties props = new Properties();

    try {

      Any body = JsonIterator.deserialize(IOUtils.toByteArray(stream));
      StringBuilder builder = new StringBuilder();

      if (body.valueType().equals(ValueType.OBJECT)) {
        recurse(body.asMap(), builder, props);
      } else if (body.valueType().equals(ValueType.ARRAY)) {
        recurse(body.asList(), builder, props);
      }


    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return props;
  }


  public static boolean isJsonFile(String path) {

    assert StringUtils.hasText(path) : "Path was null or empty";
    return path.toLowerCase().endsWith(".json");
  }

  private static void recurse(List<Any> list, StringBuilder builder, Properties props) {

    final String node = builder.toString();

    int i = 0;
    for (Any k : list) {

      if (k.valueType().equals(ValueType.STRING) || k.valueType().equals(ValueType.NUMBER)
          || k.valueType().equals(ValueType.BOOLEAN)) {

        String key = builder.toString() + "[" + i + "]";
        props.put(key, k.toString().trim());

      } else if (k.valueType().equals(ValueType.OBJECT)) {

        recurse(k.asMap(), builder, props);

      } else if (k.valueType().equals(ValueType.ARRAY)) {

        recurse(k.asList(), builder, props);

      }

      builder = new StringBuilder(node);
      i++;
    }

  }

  private static void recurse(Map<String, Any> map, StringBuilder builder, Properties props) {

    final String node = builder.toString();

    for (Object k : map.keySet()) {

      Any i = map.get(k);

      if (builder.length() > 0)
        builder.append("." + k);
      else
        builder.append(k);

      if (i.valueType().equals(ValueType.STRING) || i.valueType().equals(ValueType.NUMBER)
          || i.valueType().equals(ValueType.BOOLEAN)) {

        props.put(builder.toString(), i.toString().trim());

      } else if (i.valueType().equals(ValueType.ARRAY)) {

        recurse(i.asList(), builder, props);

      } else if (i.valueType().equals(ValueType.OBJECT)) {

        recurse(i.asMap(), builder, props);

      }

      builder = new StringBuilder(node);
    }
  }

}
