package com.appcrossings.config.processor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.appcrossings.config.util.StringUtils;
import com.jsoniter.JsonIterator;
import com.jsoniter.ValueType;
import com.jsoniter.any.Any;

public class JsonProcessor {

  public static Map<String, Object> asProperties(InputStream stream) {

    Map<String, Object> props = new HashMap<>();

    try {

      ByteArrayOutputStream buffer = new ByteArrayOutputStream();

      int nRead;
      byte[] data = new byte[16384];

      while ((nRead = stream.read(data, 0, data.length)) != -1) {
        buffer.write(data, 0, nRead);
      }

      buffer.flush();

      Any body = JsonIterator.deserialize(buffer.toByteArray());
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

  public static Map<String, Object> asProperties(byte[] bytes) {

    Map<String, Object> props = new HashMap<>();

    Any body = JsonIterator.deserialize(bytes);
    StringBuilder builder = new StringBuilder();

    if (body.valueType().equals(ValueType.OBJECT)) {
      recurse(body.asMap(), builder, props);
    } else if (body.valueType().equals(ValueType.ARRAY)) {
      recurse(body.asList(), builder, props);
    }

    return props;
  }


  public static boolean isJsonFile(String path) {

    assert StringUtils.hasText(path) : "Path was null or empty";
    return path.toLowerCase().endsWith(".json");
  }

  private static void recurse(List<Any> list, StringBuilder builder, Map<String, Object> props) {

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

  private static void recurse(Map<String, Any> map, StringBuilder builder,
      Map<String, Object> props) {

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
