package com.appcrossings.config.http;

import java.util.Properties;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import com.appcrossings.config.Config;
import com.appcrossings.config.ConfigSource;
import com.appcrossings.config.ConfigSourceFactory;

public class TestLoadProperties {

  private String host = "http://config.appcrossings.net";
  private ConfigSource source;
  private ConfigSourceFactory factory = new ConfigSourceFactory();

  @Before
  public void before() throws Exception {

    source = factory.buildConfigSource(ConfigSourceFactory.HTTPS);
  }

  public void testPullHostFileFromAmazon() throws Exception {

    Properties p = source.resolveConfigPath(host + "/env/hosts.properties");
    Assert.assertNotNull(p);
    Assert.assertTrue(p.containsKey("kkarski-ibm"));

  }

  @Test
  public void testPullPropertiesFileFromAmazon() throws Exception {

    Properties p = source.traverseConfigs(host + "/env/dev/", Config.DEFAULT_PROPERTIES_FILE_NAME);

    Assert.assertNotNull(p);
    Assert.assertTrue(p.containsKey("property.1.name"));

  }

}
