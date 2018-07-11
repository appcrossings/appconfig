package com.appcrossings.config.http;

import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import com.appcrossings.config.source.ConfigSource;
import com.appcrossings.config.source.ConfigSourceFactory;
import com.appcrossings.config.source.FileBasedRepo;
import com.beust.jcommander.internal.Maps;

public class TestGetRawPropertiesFile {

  private String host = "http://config.appcrossings.net";
  private ConfigSource source;
  private ConfigSourceFactory csf = new HttpConfigSourceFactory();
  private Map<String, Object> defaults = new HashMap<>();

  @Before
  public void before() throws Exception {

    defaults.put(FileBasedRepo.FILE_NAME_FIELD, "default.properties");
    defaults.put(FileBasedRepo.HOSTS_FILE_NAME_FIELD, "hosts.properties");

    source = csf.newConfigSource("TestGetRawPropertiesFile", (Map) Maps.newHashMap("uri", host),
        defaults);

  }

  @Test
  public void testPullWithDefaultFileName() throws Exception {

    Map<String, Object> p = source.getRaw("/env/dev/");

    Assert.assertNotNull(p);
    Assert.assertTrue(p.containsKey("property.1.name"));

  }

  @Test
  public void testPullWithSpecificFileName() throws Exception {

    Map<String, Object> p = source.getRaw("/env/dev/default.properties");

    Assert.assertNotNull(p);
    Assert.assertTrue(p.containsKey("property.1.name"));

  }

  @Test
  public void testTraverseHttp() throws Exception {

    Map<String, Object> p = source.get("/env/dev/default.properties");

    Assert.assertNotNull(p);
    Assert.assertTrue(p.containsKey("property.1.name"));

  }

}
