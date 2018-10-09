package com.appcrossings.config.file;

import java.util.HashMap;
import java.util.Map;
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
import com.google.common.base.Splitter;
import com.google.common.collect.Sets;


public class TestFileConfigSource {

  private ConfigSource source;
  private ConfigSourceFactory factory = new FileConfigSourceFactory();

  Map<String, Object> defaults = null;

  @Rule
  public TemporaryFolder folder = new TemporaryFolder();

  @Before
  public void before() throws Exception {
    folder.create();
    System.out.println("from: " + FileUtils.toFile(this.getClass().getResource("/")));
    System.out.println("to: " + folder.getRoot().toPath());
    FileUtils.copyDirectory(FileUtils.toFile(getClass().getResource("/")), folder.getRoot());

    defaults = new HashMap<>();
    defaults.put(FileBasedRepo.FILE_NAME_FIELD, "default.properties");
    defaults.put(FileBasedRepo.HOSTS_FILE_NAME_FIELD, "hosts.properties");

  }

  @After
  public void cleanup() throws Exception {
    FileUtils.forceDelete(folder.getRoot());
  }

  @Test
  public void loadClasspathProperties() throws Exception {

    source = source =
        factory.newConfigSource("TestFileConfigSource", (Map) Splitter.on(",").omitEmptyStrings()
            .trimResults().withKeyValueSeparator("=").split("uri=classpath:/"), defaults);

    Map<String, Object> p = source.get("env/dev/default.properties", Sets.newHashSet());
    Assert.assertNotNull(p);
    Assert.assertTrue(p.containsKey("property.1.name"));
    Assert.assertEquals(p.get("property.1.name"), "value1");

  }

  @Test
  public void loadAppendDefaultFileName() throws Exception {

    source = source =
        factory.newConfigSource("TestFileConfigSource", (Map) Splitter.on(",").omitEmptyStrings()
            .trimResults().withKeyValueSeparator("=").split("uri=classpath:/"), defaults);

    Map<String, Object> p = source.get("env/dev/", Sets.newHashSet());
    Assert.assertNotNull(p);
    Assert.assertTrue(p.containsKey("property.1.name"));
    Assert.assertEquals(p.get("property.1.name"), "value1");

  }

  @Test
  public void loadFileProperties() throws Exception {

    source =
        source =
            factory
                .newConfigSource("TestFileConfigSource",
                    (Map) Splitter.on(",").omitEmptyStrings().trimResults()
                        .withKeyValueSeparator("=").split("uri=file:" + folder.getRoot()),
                    defaults);

    Map<String, Object> p = source.get("/env/dev/default.properties", Sets.newHashSet());
    Assert.assertNotNull(p);
    Assert.assertTrue(p.containsKey("property.1.name"));
    Assert.assertEquals(p.get("property.1.name"), "value1");

  }

  @Test(expected = IllegalArgumentException.class)
  public void loadClasspathRelativePath() throws Exception {

    source = source =
        factory.newConfigSource("TestFileConfigSource", (Map) Splitter.on(",").omitEmptyStrings()
            .trimResults().withKeyValueSeparator("=").split("uri=/env/dev/"), defaults);

    Map<String, Object> p = source.get("/", Sets.newHashSet());
    Assert.assertNotNull(p);
    Assert.assertTrue(p.containsKey("property.1.name"));
    Assert.assertEquals(p.get("property.1.name"), "value1");

  }

  @Test
  public void testLoadHosts() throws Exception {

    source = source =
        factory.newConfigSource("TestFileConfigSource", (Map) Splitter.on(",").omitEmptyStrings()
            .trimResults().withKeyValueSeparator("=").split("uri=classpath:/"), defaults);

    Map<String, Object> p = source.getRaw("env/hosts.properties");
    Assert.assertNotNull(p);
    Assert.assertTrue(p.containsKey("michelangello"));
    Assert.assertEquals(p.get("michelangello"), "classpath:/env/dev/");

  }



}
