package com.appcrossings.config.file;

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
  public void testParseFileRepoDef() throws Exception {
    resolver = new ConfigSourceResolver(yamlFile);

    Optional<ConfigSource> def = resolver.resolveByRepoName("tmp-configs");
    Assert.assertTrue(def.isPresent());
    Assert.assertTrue(def.get().getSourceConfiguration() instanceof FileRepoDef);
    
    FileRepoDef hrepo = (FileRepoDef) def.get().getSourceConfiguration();
    Assert.assertEquals("tmp-configs", hrepo.getName());
    Assert.assertEquals(Config.DEFAULT_PROPERTIES_FILE_NAME, hrepo.getConfigFileName());
    Assert.assertEquals(Config.DEFAULT_HOSTS_FILE_NAME, hrepo.getHostsFileName());
    Assert.assertEquals("/appx-c", hrepo.getContext());
    Assert.assertEquals("/", hrepo.getRoot());
    Assert.assertEquals("file:/var/tmp/configs", hrepo.getUri());
    Assert.assertEquals(Config.DEFAULT_MERGE_STRATEGY_CLASS, hrepo.getMergeStrategyClass());

  }


}
