package com.appcrossings.config.http;

import org.junit.Assert;
import org.junit.Test;

public class TestFileNameResolution {

  private DefaultHttpSource source = new DefaultHttpSource();

  @Test
  public void testResolveWithRepoName() throws Exception {

    HttpRepoDef def = new HttpRepoDef();
    def.setUri("http://config.appcrossings.net/env");
    def.setRoot("/dev");
    def.setContext("/appx-d");
    def.setName("appx-d");
    def.setFileName("default.json");
    def.setHostsName("hosts.properties");
    source.repoConfig = def;

    String path = source.resolveFullFilePath("/appx-d/json", def.getFileName());

    Assert.assertEquals("http://config.appcrossings.net/env/dev/json/default.json", path);

  }

  @Test
  public void testResolveClasspathWithDefaults() throws Exception {

    HttpRepoDef def = new HttpRepoDef();
    def.setRoot("/");
    def.setName("http");
    def.setFileName("default.properties");
    def.setHostsName("hosts.properties");
    source.repoConfig = def;

    String path = source.resolveFullFilePath("http://config.appcrossings.net/env/dev/custom", def.getFileName());

    Assert.assertEquals("http://config.appcrossings.net/env/dev/custom/default.properties", path);

  }

  @Test
  public void testResolveFileWithDefaults() throws Exception {

    HttpRepoDef def = new HttpRepoDef();
    def.setRoot("/");
    def.setName("http");
    def.setFileName("default.properties");
    def.setHostsName("hosts.properties");
    source.repoConfig = def;

    String path = source.resolveFullFilePath("http://config.appcrossings.net/env/dev/custom", def.getFileName());

    Assert.assertEquals("http://config.appcrossings.net/env/dev/custom/default.properties", path);

  }

  @Test
  public void testResolveFileWithCustomRepoDef() throws Exception {

    HttpRepoDef def = new HttpRepoDef();
    def.setUri("http://config.appcrossings.net/env");
    def.setRoot("/dev");
    def.setContext("/appx-d");
    def.setName("appx-d");
    def.setFileName("default.json");
    def.setHostsName("hosts.properties");
    source.repoConfig = def;

    String path = source.resolveFullFilePath("http://config.appcrossings.net/env/dev/custom", def.getFileName());

    Assert.assertEquals("http://config.appcrossings.net/env/dev/custom/default.json", path);

  }
  
  @Test
  public void testResolveFileWithCustomRepoDefNoRoot() throws Exception {

    HttpRepoDef def = new HttpRepoDef();
    def.setUri("http://config.appcrossings.net/env");
    def.setContext("/appx-d");
    def.setName("appx-d");
    def.setFileName("default.json");
    def.setHostsName("hosts.properties");
    source.repoConfig = def;

    String path = source.resolveFullFilePath("http://config.appcrossings.net/env/custom", def.getFileName());

    Assert.assertEquals("http://config.appcrossings.net/env/custom/default.json", path);

  }

  @Test
  public void testResolveFileWithCustomFileName() throws Exception {

    HttpRepoDef def = new HttpRepoDef();
    def.setUri("http://config.appcrossings.net/env");
    def.setRoot("/dev");
    def.setContext("/appx-d");
    def.setName("appx-d");
    def.setFileName("default.json");
    def.setHostsName("hosts.properties");
    source.repoConfig = def;

    String path =
        source.resolveFullFilePath("http://config.appcrossings.net/env/dev/custom/something.yaml", def.getFileName());

    Assert.assertEquals("http://config.appcrossings.net/env/dev/custom/something.yaml", path);

  }

}
