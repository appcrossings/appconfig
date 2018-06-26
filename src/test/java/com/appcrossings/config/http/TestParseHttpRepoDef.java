package com.appcrossings.config.http;

import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;
import com.appcrossings.config.Config;
import com.appcrossings.config.ConfigSourceResolver;
import com.appcrossings.config.source.ConfigSource;

public class TestParseHttpRepoDef {

  private ConfigSourceResolver resolver;
  private final String yamlFile = "classpath:/repos.yaml";

  @Test
  public void testParseHttpRepoDef() throws Exception {

    resolver = new ConfigSourceResolver(yamlFile);

    Optional<ConfigSource> def = resolver.findByRepoName("git-master");
    Assert.assertFalse(def.isPresent());

    def = resolver.findByRepoName("http-resource");
    Assert.assertTrue(def.isPresent());
    Assert.assertTrue(def.get().getStreamSource().getSourceConfig() instanceof HttpRepoDef);

    HttpRepoDef hrepo = (HttpRepoDef) def.get().getStreamSource().getSourceConfig();
    Assert.assertEquals("http-resource", hrepo.getName());
    Assert.assertEquals(Config.DEFAULT_PROPERTIES_FILE_NAME, hrepo.getFileName());
    Assert.assertEquals(Config.DEFAULT_HOSTS_FILE_NAME, hrepo.getHostsName());
    Assert.assertEquals(
        "https://raw.githubusercontent.com/appcrossings/appconfig-client/master/src/test/resources",
        hrepo.getUri());
    Assert.assertEquals(2, hrepo.getNamed().size());
    
  }


}
