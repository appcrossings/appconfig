package com.appcrossings.config.file;

import java.util.Map;
import java.util.Properties;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import com.appcrossings.config.source.ConfigSource;
import com.appcrossings.config.source.ConfigSourceFactory;
import com.appcrossings.config.source.FileBasedRepo;
import com.beust.jcommander.internal.Maps;

public class TestFileConfigSource {

  private ConfigSource source;
  private ConfigSourceFactory factory = new FileConfigSourceFactory();

  Map<String, Object> defaults = null;

  @Rule
  public TemporaryFolder folder = new TemporaryFolder();

  @Before
  public void before() throws Exception {
    folder.create();
    System.out.println("from: " + FileUtils.toFile(this.getClass().getResource("/env")));
    System.out.println("to: " + folder.getRoot().toPath());
    FileUtils.copyDirectory(FileUtils.toFile(this.getClass().getResource("/")), folder.getRoot());

    defaults = new HashedMap();
    defaults.put(FileBasedRepo.FILE_NAME_FIELD, "default.properties");
    defaults.put(FileBasedRepo.HOSTS_FILE_NAME_FIELD, "hosts.properties");

  }

  @After
  public void cleanup() throws Exception {
    FileUtils.forceDelete(folder.getRoot());
  }

  @Test
  public void loadClasspathProperties() throws Exception {

    source = source = factory.newConfigSource("TestFileConfigSource",
        (Map) Maps.newHashMap("uri", "classpath:/"), defaults);

    Properties p = source.get("env/dev/default.properties");
    Assert.assertNotNull(p);
    Assert.assertTrue(p.containsKey("property.1.name"));
    Assert.assertEquals(p.getProperty("property.1.name"), "value1");

  }
  
  @Test
  public void loadAppendDefaultFileName() throws Exception {

    source = source = factory.newConfigSource("TestFileConfigSource",
        (Map) Maps.newHashMap("uri", "classpath:/"), defaults);

    Properties p = source.get("env/dev/");
    Assert.assertNotNull(p);
    Assert.assertTrue(p.containsKey("property.1.name"));
    Assert.assertEquals(p.getProperty("property.1.name"), "value1");

  }

  @Test
  public void loadFileProperties() throws Exception {

    source = source = factory.newConfigSource("TestFileConfigSource",
        (Map) Maps.newHashMap("uri", "file:" + folder.getRoot()), defaults);

    Properties p = source.get("/env/dev/default.properties");
    Assert.assertNotNull(p);
    Assert.assertTrue(p.containsKey("property.1.name"));
    Assert.assertEquals(p.getProperty("property.1.name"), "value1");

  }

  @Test(expected = IllegalArgumentException.class)
  public void loadClasspathRelativePath() throws Exception {

    source = source = factory.newConfigSource("TestFileConfigSource",
        (Map) Maps.newHashMap("uri", "/env/dev/"), defaults);

    Properties p = source.get("/");
    Assert.assertNotNull(p);
    Assert.assertTrue(p.containsKey("property.1.name"));
    Assert.assertEquals(p.getProperty("property.1.name"), "value1");

  }

  @Test
  public void testLoadHosts() throws Exception {

    source = source = factory.newConfigSource("TestFileConfigSource",
        (Map) Maps.newHashMap("uri", "classpath:/"), defaults);

    Properties p = source.getRaw("env/hosts.properties");
    Assert.assertNotNull(p);
    Assert.assertTrue(p.containsKey("michelangello"));
    Assert.assertEquals(p.getProperty("michelangello"), "classpath:/env/dev/");

  }



}
