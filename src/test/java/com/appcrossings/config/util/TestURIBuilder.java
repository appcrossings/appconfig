package com.appcrossings.config.util;

import java.net.URI;
import org.junit.Assert;
import org.junit.Test;

public class TestURIBuilder {

  @Test
  public void testExtendPathAtBuild() {

    URIBuilder builder = URIBuilder.create("classpath:root/first/second/third/file.properties");

    Assert.assertEquals(URI.create("classpath:root/first/second/third/file.properties"),
        builder.build());
    Assert.assertEquals(URI.create("classpath:root/first/second/third/file.properties"),
        builder.build(""));
    Assert.assertEquals(URI.create("classpath:root/first/second/third/fourth/file.properties"),
        builder.build("/fourth"));
    Assert.assertEquals(URI.create("classpath:root/first/second/third/fourth/file2.properties"),
        builder.build("/fourth/file2.properties"));

  }

  @Test
  public void testURIBuilder() {

    Assert.assertEquals(URI.create("file:first/second/third/file.properties"), URIBuilder.create()
        .setScheme("file").setPath("first/second/third").setFileName("file.properties").build());

    Assert.assertEquals(URI.create("file:/first/second/third/file.properties"), URIBuilder.create()
        .setScheme("file").setPath("/first/second/third").setFileName("file.properties").build());

    Assert.assertEquals(URI.create("file:/first/second/third"),
        URIBuilder.create().setScheme("file").setPath("/first/second/third").build());

    Assert.assertEquals(
        URI.create("file://user:pass@localhost:1234/first/second/third/file.properties"),
        URIBuilder.create().setScheme("file").setPath("/first/second/third").setHost("localhost")
            .setPort(1234).setUsername("user").setPassword("pass").setFileName("file.properties")
            .build());

    Assert.assertEquals(URI.create("file://user:pass@/first/second/third/file.properties"),
        URIBuilder.create().setScheme("file").setPath("/first/second/third").setUsername("user")
            .setPassword("pass").setFileName("file.properties").build());

    Assert.assertEquals(URI.create("file:file.properties"),
        URIBuilder.create().setScheme("file").setFileName("file.properties").build());

    Assert.assertEquals(URI.create("file:/file.properties"),
        URIBuilder.create().setScheme("file").setFileName("file.properties").setPath("/").build());

    Assert.assertEquals(URI.create("http://host:12345/first/second/third/file.properties#custom,simple"),
        URIBuilder.create().setScheme("http").setHost("host").setPort(12345)
            .setPath("first/second/third").setFileName("file.properties")
            .setFragment("custom", "simple").build());

  }

  @Test
  public void testURIBuilderWithBaseURI() {

    URI uri = URI.create("file://user:pass@host:1234/first/second/third/file.properties");

    Assert.assertEquals(uri, URIBuilder.create(uri).build());

    Assert.assertEquals(
        URI.create("file://user:pass@host:1234/first/second/third/replaced.properties"),
        URIBuilder.create(uri).setFileName("replaced.properties").build());

    Assert.assertEquals(URI.create("file://user:pass@host:1234/first/second/replaced.properties"),
        URIBuilder.create(uri).setPath("/first/second/").setFileName("replaced.properties")
            .build());

    Assert.assertEquals(URI.create("file://user:pass@host:1234/first/second/"),
        URIBuilder.create(uri).setPath("/first/second/").setFileName("").build());

    Assert.assertEquals(URI.create("file://user:pass@host:9999/first/second/"),
        URIBuilder.create(uri).setPath("/first/second/").setFileName("").setPort(9999).build());

    Assert.assertEquals(
        URI.create("classpath://user2:pass2@host:1234/first/second/third/file.properties"),
        URIBuilder.create(uri).setScheme("classpath").setPath("/first/second/third")
            .setUsername("user2").setPassword("pass2").setFileName("file.properties").build());

    Assert.assertEquals(
        URI.create("classpath://user:pass@host:1234/first/second/third/file2.properties"),
        URIBuilder.create(uri).setScheme("classpath").setFileName("file2.properties").build());

    Assert.assertEquals(URI.create("file://user:pass@host:1234/file2.properties"),
        URIBuilder.create(uri).setPath("").setFileName("file2.properties").setPath("/").build());

  }

}
