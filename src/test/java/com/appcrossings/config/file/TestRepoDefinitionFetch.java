package com.appcrossings.config.file;

import java.util.Optional;
import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;
import com.appcrossings.config.Config;
import com.appcrossings.config.ConfigClient;
import com.appcrossings.config.ConfigClient.Method;
import com.appcrossings.config.ConfigSourceResolver;
import com.appcrossings.config.source.ConfigSource;
import com.appcrossings.config.util.Environment;

public class TestRepoDefinitionFetch {

  private ConfigSourceResolver resolver;
  private final String yamlFile = "classpath:/repos.yaml";

  private ConfigClient client;
    
  @Test
  public void testFetchDirectPathWithRepoDefinitions() throws Exception {

    client = new ConfigClient(yamlFile, "/appx-d/json", Method.DIRECT_PATH);
    client.init();

    Properties props = client.getProperties();

    Assert.assertTrue(props.containsKey("property.1.name"));
    Assert.assertEquals("simple", props.getProperty("property.1.name"));

    Assert.assertTrue(props.containsKey("property.4.name"));
    Assert.assertEquals("${property.1.name}-${property.3.name}",
        props.getProperty("property.4.name"));

    Assert.assertTrue(props.containsKey("bonus.1.property"));
    Assert.assertEquals("bonus2", props.getProperty("bonus.1.property"));
  }

  @Test
  public void testParseFileRepoDef() throws Exception {
    resolver = new ConfigSourceResolver(yamlFile, new Environment());

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
