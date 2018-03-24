package com.appcrossings.config.util;

import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;
import com.appcrossings.config.ConfigSourceResolver;
import com.appcrossings.config.file.DefaultFilesystemSource;
import com.appcrossings.config.source.ConfigSource;

public class TestYamlProcessor {

  YamlProcessor proc = new YamlProcessor();
  private ConfigSourceResolver factory = new ConfigSourceResolver();
  private final String yamlFile = "classpath:/env/dev/yaml/default.yaml";

  @Test
  public void testFlattenYamlToProperties() throws Exception {

    ConfigSource source = factory.resolveByUri(yamlFile);
    InputStream stream =
        ((DefaultFilesystemSource) source).stream(yamlFile, Optional.empty());
    Properties props = proc.asProperties(stream);

    Assert.assertTrue(props.containsKey("property.1.name"));
    Assert.assertEquals("simple", props.getProperty("property.1.name"));

    Assert.assertTrue(props.containsKey("property.4.name"));
    Assert.assertEquals("${property.1.name}-${property.3.name}",
        props.getProperty("property.4.name"));

    Assert.assertTrue(props.containsKey("bonus.1.property"));
    Assert.assertEquals("bonus2", props.getProperty("bonus.1.property"));

  }

  @Test
  public void testFlattenYamlArrayToProperties() throws Exception {

    ConfigSource source = factory.resolveByUri(yamlFile);
    InputStream stream =
        ((DefaultFilesystemSource) source).stream(yamlFile, Optional.empty());
    Properties props = proc.asProperties(stream);

    Assert.assertTrue(props.containsKey("array.named[0]"));
    Assert.assertEquals("value1", props.getProperty("array.named[0]"));

    Assert.assertTrue(props.containsKey("array.named[1]"));
    Assert.assertEquals("value2", props.getProperty("array.named[1]"));

    Assert.assertTrue(props.containsKey("array.named[2]"));
    Assert.assertEquals("value3", props.getProperty("array.named[2]"));

    Assert.assertTrue(props.containsKey("array.named2.value4.sub"));
    Assert.assertEquals("value", props.getProperty("array.named2.value4.sub"));

    Assert.assertTrue(props.containsKey("array.named2.value5.sub"));
    Assert.assertEquals("value", props.getProperty("array.named2.value5.sub"));

    Assert.assertTrue(props.containsKey("array.named2.value6.sub"));
    Assert.assertEquals("value", props.getProperty("array.named2.value6.sub"));
  }

}
