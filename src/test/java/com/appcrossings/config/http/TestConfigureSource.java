package com.appcrossings.config.http;

import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;
import com.appcrossings.config.Config;
import com.appcrossings.config.ConfigSourceResolver;
import com.appcrossings.config.source.ConfigSource;

public class TestConfigureSource {

  private ConfigSourceResolver resolver;
  private final String yamlFile = "classpath:/repos.yaml";

  @Test
  public void testParseHttpRepoDef() throws Exception {
    resolver = new ConfigSourceResolver(yamlFile);

    Optional<ConfigSource> def = resolver.resolveByRepoName("git-master");
    Assert.assertFalse(def.isPresent());

    def = resolver.resolveByRepoName("http-resource");
    Assert.assertTrue(def.isPresent());
    Assert.assertTrue(def.get().getSourceConfiguration() instanceof HttpRepoDef);

    HttpRepoDef hrepo = (HttpRepoDef) def.get().getSourceConfiguration();
    Assert.assertEquals("http-resource", hrepo.getName());
    Assert.assertEquals(Config.DEFAULT_PROPERTIES_FILE_NAME, hrepo.getConfigFileName());
    Assert.assertEquals(Config.DEFAULT_HOSTS_FILE_NAME, hrepo.getHostsFileName());
    Assert.assertEquals("/appx-b", hrepo.getContext());
    Assert.assertEquals("/", hrepo.getRoot());
    Assert.assertEquals(
        "https://github.com/appcrossings/appconfig-client/tree/master/src/test/resources",
        hrepo.getUri());
    Assert.assertEquals(Config.DEFAULT_MERGE_STRATEGY_CLASS, hrepo.getMergeStrategyClass());

  }


}
