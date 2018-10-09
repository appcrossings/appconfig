package com.appcrossings.config.util;

import java.net.URI;
import org.junit.Assert;
import org.junit.Test;
import com.appcrossings.config.util.CfgrdURI;

public class TestCfgrdUri {

  @Test
  public void testCfgrdPath() {

    URI uri = URI.create("cfgrd://repoName/path1/path2/path3");
    uri = URI.create("cfgrd://username:password@repoName/path1/path2/path3");

  }
  
  @Test
  public void testBasicUri() {

    URI uri = URI.create("cfgrd://username:password@testRepo/first/second/third");

    Assert.assertNotNull(uri);
    Assert.assertEquals("username:password", uri.getUserInfo());
    Assert.assertEquals("testRepo", uri.getHost());
    Assert.assertEquals("/first/second/third", uri.getPath());
    Assert.assertEquals("cfgrd", uri.getScheme());

  }

  @Test
  public void testBasicUriWithoutPassword() {

    URI uri = URI.create("cfgrd://username@testRepo/first/second/third");

    Assert.assertNotNull(uri);
    Assert.assertEquals("username", uri.getUserInfo());
    Assert.assertEquals("testRepo", uri.getHost());
    Assert.assertEquals("/first/second/third", uri.getPath());
    Assert.assertEquals("cfgrd", uri.getScheme());

  }

  @Test
  public void testFullCfgrdURI() {

    CfgrdURI uri = new CfgrdURI(URI.create("cfgrd://username:password@testRepo/first/second/third/file.properties"));
    Assert.assertEquals("/first/second/third/file.properties", uri.getPath());
    Assert.assertEquals("testRepo", uri.getRepoName());
    Assert.assertEquals("username", uri.getUserName());
    Assert.assertEquals("password", uri.getPassword());
    Assert.assertTrue(uri.hasFile());
    Assert.assertEquals("file.properties", uri.getFileName());

  }
  
  @Test
  public void testBasicCfgrdURI() {

    CfgrdURI uri = new CfgrdURI(URI.create("cfgrd://testRepo/first/second/third"));
    Assert.assertEquals("/first/second/third", uri.getPath());
    Assert.assertEquals("testRepo", uri.getRepoName());
    Assert.assertNull(uri.getUserName());
    Assert.assertNull(uri.getPassword());
    Assert.assertFalse(uri.hasFile());
    Assert.assertNull(uri.getFileName());

  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testRelativeFileURI() {

    CfgrdURI uri = new CfgrdURI(URI.create("file:testRepo/first/second/third/file.properties"));
 

  }

}
