package com.appcrossings.config.processor;

import java.io.InputStream;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

public class TestJsonProcessor {

  JsonProcessor proc = new JsonProcessor();

  private final String jsonFile = "/env/dev/json/default.json";

  @Test
  public void testFlattenYamlToProperties() throws Exception {

    InputStream stream = this.getClass().getResourceAsStream(jsonFile);

    Assert.assertNotNull(stream);
    Map<String, Object> props = proc.asProperties(stream);

    Assert.assertTrue(props.containsKey("property.1.name"));
    Assert.assertEquals("simple", props.get("property.1.name"));

    Assert.assertTrue(props.containsKey("property.4.name"));
    Assert.assertEquals("${property.1.name}-${property.3.name}", props.get("property.4.name"));

    Assert.assertTrue(props.containsKey("bonus.1.property"));
    Assert.assertEquals("bonus2", props.get("bonus.1.property"));

    Assert.assertTrue(props.containsKey("array.named[0]"));
    Assert.assertEquals("value1", props.get("array.named[0]"));

    Assert.assertTrue(props.containsKey("array.named[1]"));
    Assert.assertEquals("value2", props.get("array.named[1]"));

    Assert.assertTrue(props.containsKey("array.named[2]"));
    Assert.assertEquals("value3", props.get("array.named[2]"));

    Assert.assertTrue(props.containsKey("array.named2.value4.sub"));
    Assert.assertEquals("true", props.get("array.named2.value4.sub"));

    Assert.assertTrue(props.containsKey("array.named2.value5.sub"));
    Assert.assertEquals("5", props.get("array.named2.value5.sub"));

    Assert.assertTrue(props.containsKey("array.named2.value6.sub"));
    Assert.assertEquals("value", props.get("array.named2.value6.sub"));

  }
}
