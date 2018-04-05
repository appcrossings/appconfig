package com.appcrossings.config.file;

import org.junit.Assert;
import org.junit.Test;

public class TestFileNameResolution {

  private DefaultFilesystemSource source = new DefaultFilesystemSource();

  @Test
  public void testResolveWithRepoName() throws Exception {

    FileRepoDef def = new FileRepoDef();
    def.setUri("classpath:/env");
    def.setRoot("/dev");
    def.setContext("/appx-d");
    def.setName("appx-d");
    def.setFileName("default.json");
    def.setHostsName("hosts.properties");
    source.repoConfig = def;

    String path = source.resolveFullFilePath("/appx-d/json", def.getFileName());

    Assert.assertEquals("classpath:/env/dev/json/default.json", path);

  }

  @Test
  public void testResolveClasspathWithDefaults() throws Exception {

    FileRepoDef def = new FileRepoDef();
    def.setRoot("/");
    def.setName("classpath");
    def.setFileName("default.properties");
    def.setHostsName("hosts.properties");
    source.repoConfig = def;

    String path = source.resolveFullFilePath("classpath:/env/dev/custom", def.getFileName());

    Assert.assertEquals("classpath:/env/dev/custom/default.properties", path);

  }

  @Test
  public void testResolveFileWithDefaults() throws Exception {

    FileRepoDef def = new FileRepoDef();
    def.setRoot("/");
    def.setName("classpath");
    def.setFileName("default.properties");
    def.setHostsName("hosts.properties");
    source.repoConfig = def;

    String path = source.resolveFullFilePath("file:/env/dev/custom", def.getFileName());

    Assert.assertEquals("file:/env/dev/custom/default.properties", path);

  }

  @Test
  public void testResolveFileWithCustomRepoDef() throws Exception {

    FileRepoDef def = new FileRepoDef();
    def.setUri("classpath:/env");
    def.setRoot("/dev");
    def.setContext("/appx-d");
    def.setName("appx-d");
    def.setFileName("default.json");
    def.setHostsName("hosts.properties");
    source.repoConfig = def;

    String path = source.resolveFullFilePath("file:/env/dev/custom", def.getFileName());

    Assert.assertEquals("file:/env/dev/custom/default.json", path);

  }
  
  @Test
  public void testResolveFileWithCustomRepoDefNoRoot() throws Exception {

    FileRepoDef def = new FileRepoDef();
    def.setUri("classpath:/env");
    def.setContext("/appx-d");
    def.setName("appx-d");
    def.setFileName("default.json");
    def.setHostsName("hosts.properties");
    source.repoConfig = def;

    String path = source.resolveFullFilePath("file:/env/custom", def.getFileName());

    Assert.assertEquals("file:/env/custom/default.json", path);

  }

  @Test
  public void testResolveFileWithCustomFileName() throws Exception {

    FileRepoDef def = new FileRepoDef();
    def.setUri("classpath:/env");
    def.setRoot("/dev");
    def.setContext("/appx-d");
    def.setName("appx-d");
    def.setFileName("default.json");
    def.setHostsName("hosts.properties");
    source.repoConfig = def;

    String path =
        source.resolveFullFilePath("file:/env/dev/custom/something.yaml", def.getFileName());

    Assert.assertEquals("file:/env/dev/custom/something.yaml", path);

  }

}
