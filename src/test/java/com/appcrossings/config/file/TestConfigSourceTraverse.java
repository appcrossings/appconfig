package com.appcrossings.config.file;

import java.util.Optional;
import java.util.Properties;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.internal.util.collections.Sets;
import com.appcrossings.config.Config;
import com.appcrossings.config.ConfigSourceResolver;
import com.appcrossings.config.source.ConfigSource;

public class TestConfigSourceTraverse {

  private Optional<ConfigSource> source;
  private ConfigSourceResolver factory = new ConfigSourceResolver();

  @Rule
  public TemporaryFolder folder = new TemporaryFolder();

  @Before
  public void before() throws Exception {
    folder.create();
    System.out.println("from: " + FileUtils.toFile(this.getClass().getResource("/env")));
    System.out.println("to: " + folder.getRoot().toPath());
    FileUtils.copyDirectory(FileUtils.toFile(this.getClass().getResource("/")), folder.getRoot());
    source = factory.resolveBySourceName(Sets.newSet(ConfigSource.FILE_SYSTEM));
  }

  @After
  public void cleanup() throws Exception {
    FileUtils.forceDelete(folder.getRoot());
  }

  @Test
  public void loadClasspathProperties() throws Exception {

    Properties p = source.get().traverseConfigs("classpath:/env/dev");
    Assert.assertNotNull(p);
    Assert.assertTrue(p.containsKey("property.1.name"));
    Assert.assertEquals(p.getProperty("property.1.name"), "value1");

  }

  @Test
  public void loadFileProperties() throws Exception {

    Properties p = source.get().traverseConfigs("file:/" + folder.getRoot() + "/env/dev/");
    Assert.assertNotNull(p);
    Assert.assertTrue(p.containsKey("property.1.name"));
    Assert.assertEquals(p.getProperty("property.1.name"), "value1");

  }

  @Test
  public void loadClasspathRelativePath() throws Exception {

    Properties p = source.get().traverseConfigs("/env/dev/");
    Assert.assertNotNull(p);
    Assert.assertTrue(p.containsKey("property.1.name"));
    Assert.assertEquals(p.getProperty("property.1.name"), "value1");

  }

  @Test
  public void loadAbsoluteFile() throws Exception {

    Properties p =
        source.get().traverseConfigs(folder.getRoot() + "/env/dev/");
    Assert.assertNotNull(p);
    Assert.assertTrue(p.containsKey("property.1.name"));
    Assert.assertEquals(p.getProperty("property.1.name"), "value1");

  }



  @Test
  public void testLoadHosts() throws Exception {

    Properties p = source.get().fetchHostEntries("classpath:/env/hosts.properties",
        Config.DEFAULT_HOSTS_FILE_NAME);
    Assert.assertNotNull(p);
    Assert.assertTrue(p.containsKey("michelangello"));
    Assert.assertEquals(p.getProperty("michelangello"), "classpath:/env/dev/");

  }



}
