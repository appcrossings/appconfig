package com.appcrossings.config.http;

import java.util.Optional;
import java.util.Properties;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.util.collections.Sets;
import com.appcrossings.config.Config;
import com.appcrossings.config.ConfigSourceResolver;
import com.appcrossings.config.source.ConfigSource;

public class TestLoadProperties {

  private String host = "http://config.appcrossings.net";
  private Optional<ConfigSource> source;
  private ConfigSourceResolver factory = new ConfigSourceResolver();

  @Before
  public void before() throws Exception {

    source = factory.resolveBySourceName(Sets.newSet(ConfigSource.HTTPS));
  }

  public void testPullHostFileFromAmazon() throws Exception {

    Properties p =
        source.get().fetchHostEntries(host + "/env/hosts.properties", Config.DEFAULT_HOSTS_FILE_NAME);
    Assert.assertNotNull(p);
    Assert.assertTrue(p.containsKey("kkarski-ibm"));

  }

  @Test
  public void testPullPropertiesFileFromAmazon() throws Exception {

    Properties p = source.get().fetchConfig(host + "/env/dev/", Optional.of(new HttpRepoDef()));

    Assert.assertNotNull(p);
    Assert.assertTrue(p.containsKey("property.1.name"));

  }

}
