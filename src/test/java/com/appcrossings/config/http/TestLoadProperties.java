package com.appcrossings.config.http;

import java.util.Properties;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import com.appcrossings.config.Config;
import com.appcrossings.config.ConfigSource;
import com.appcrossings.config.ConfigSourceResolver;

public class TestLoadProperties {

  private String host = "http://config.appcrossings.net";
  private ConfigSource source;
  private ConfigSourceResolver factory = new ConfigSourceResolver();

  @Before
  public void before() throws Exception {

    source = factory.getBySourceName(ConfigSourceResolver.HTTPS);
  }

  public void testPullHostFileFromAmazon() throws Exception {

    Properties p = source.resolveConfigPath(host + "/env/hosts.properties", Config.DEFAULT_HOSTS_FILE_NAME);
    Assert.assertNotNull(p);
    Assert.assertTrue(p.containsKey("kkarski-ibm"));

  }

  @Test
  public void testPullPropertiesFileFromAmazon() throws Exception {

    Properties p = source.fetchConfig(host + "/env/dev/", Config.DEFAULT_PROPERTIES_FILE_NAME);

    Assert.assertNotNull(p);
    Assert.assertTrue(p.containsKey("property.1.name"));

  }

}
