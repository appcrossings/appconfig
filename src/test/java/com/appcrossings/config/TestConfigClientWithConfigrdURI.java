package com.appcrossings.config;

import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;
import com.appcrossings.config.ConfigClient;
import com.appcrossings.config.ConfigClient.Method;

public class TestConfigClientWithConfigrdURI {

  private final String yamlFile = "classpath:/repos.yaml";

  private ConfigClient client;

  @Test
  public void testGetPropsFromRepoByNameAndPathWihoutFileName() throws Exception {

    client = new ConfigClient(yamlFile, "cfgrd://appx-d/json", Method.CONFIGRD_URI);
    client.init();

    Properties props = client.getProperties();

    Assert.assertTrue(props.containsKey("property.1.name"));
    Assert.assertEquals("simple", props.getProperty("property.1.name"));

    Assert.assertTrue(props.containsKey("property.4.name"));
    Assert.assertEquals("simple-${property.3.name}",
        props.getProperty("property.4.name"));

    Assert.assertTrue(props.containsKey("bonus.1.property"));
    Assert.assertEquals("bonus2", props.getProperty("bonus.1.property"));
    
    Assert.assertFalse(props.containsKey("log.root.level"));
  }

  @Test
  public void testGetPropsFromDefaultRepoByNameAndPathWihoutFileName() throws Exception {

    client = new ConfigClient(yamlFile, "cfgrd://default/env/dev/custom", Method.CONFIGRD_URI);
    client.init();

    Properties props = client.getProperties();

    Assert.assertTrue(props.containsKey("property.1.name"));
    Assert.assertEquals("custom", props.getProperty("property.1.name"));

    Assert.assertTrue(props.containsKey("property.4.name"));
    Assert.assertEquals("custom-custom",
        props.getProperty("property.4.name"));

    Assert.assertFalse(props.containsKey("bonus.1.property"));
    
    Assert.assertTrue(props.containsKey("log.root.level"));
  }

  
}
