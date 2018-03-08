package com.appcrossings.config.file;

import java.util.Properties;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import com.appcrossings.config.Config;
import com.appcrossings.config.ConfigSource;
import com.appcrossings.config.ConfigSourceResolver;

public class TestLoadProperties {

  private ConfigSource source;
  private ConfigSourceResolver factory = new ConfigSourceResolver();

  @Rule
  public TemporaryFolder folder = new TemporaryFolder();

  @Before
  public void before() throws Exception {
    folder.create();
    System.out.println("from: " + FileUtils.toFile(this.getClass().getResource("/env")));
    System.out.println("to: " + folder.getRoot().toPath());
    FileUtils.copyDirectory(FileUtils.toFile(this.getClass().getResource("/")), folder.getRoot());
    source = factory.getBySourceName(ConfigSourceResolver.FILE_SYSTEM);
  }

  @After
  public void cleanup() throws Exception {
    FileUtils.forceDelete(folder.getRoot());
  }

  @Test
  public void loadClasspathProperties() throws Exception {

    Properties p = source.traverseConfigs("classpath:/env/dev", Config.DEFAULT_PROPERTIES_FILE_NAME);
    Assert.assertNotNull(p);
    Assert.assertTrue(p.containsKey("property.1.name"));
    Assert.assertEquals(p.getProperty("property.1.name"), "value1");

  }

  @Test
  public void loadFileProperties() throws Exception {

    Properties p = source.traverseConfigs("file:/" + folder.getRoot() + "/env/dev/",
        Config.DEFAULT_PROPERTIES_FILE_NAME);
    Assert.assertNotNull(p);
    Assert.assertTrue(p.containsKey("property.1.name"));
    Assert.assertEquals(p.getProperty("property.1.name"), "value1");

  }

  @Test
  public void loadClasspathRelativePath() throws Exception {

    Properties p = source.traverseConfigs("/env/dev/", Config.DEFAULT_PROPERTIES_FILE_NAME);
    Assert.assertNotNull(p);
    Assert.assertTrue(p.containsKey("property.1.name"));
    Assert.assertEquals(p.getProperty("property.1.name"), "value1");

  }

  @Test
  public void loadAbsoluteFile() throws Exception {

    Properties p = source.traverseConfigs(folder.getRoot() + "/env/dev/",
        Config.DEFAULT_PROPERTIES_FILE_NAME);
    Assert.assertNotNull(p);
    Assert.assertTrue(p.containsKey("property.1.name"));
    Assert.assertEquals(p.getProperty("property.1.name"), "value1");

  }



  @Test
  public void testLoadHosts() throws Exception {

    Properties p = source.resolveConfigPath("classpath:/env/hosts.properties", Config.DEFAULT_HOSTS_FILE_NAME);
    Assert.assertNotNull(p);
    Assert.assertTrue(p.containsKey("michelangello"));
    Assert.assertEquals(p.getProperty("michelangello"), "classpath:/env/dev/");

  }



}
