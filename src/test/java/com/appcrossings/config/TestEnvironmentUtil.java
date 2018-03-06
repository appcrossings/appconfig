package com.appcrossings.config;

import org.junit.Assert;
import org.junit.Test;

public class TestEnvironmentUtil {

  private final Environment util = new Environment();



  @Test
  public void testFindHostName() throws Exception {

    String hostName = util.detectHostName();
    Assert.assertNotNull(hostName);

    util.setHostName("testName");
    Assert.assertNotNull(util.getEnvironment().get(Environment.HOST_NAME));
    Assert.assertEquals("testName", util.getEnvironment().get(Environment.HOST_NAME));

  }

  @Test
  public void testFindEnvironment() throws Exception {


    System.setProperty("ENVIRONMENT", "testName5");
    String env = util.detectEnvironment();
    Assert.assertNotNull(env);
    Assert.assertEquals("testName5", env);

    util.setEnvironmentName(null);

    System.setProperty("environment", "testName3");
    env = util.detectEnvironment();
    Assert.assertNotNull(env);
    Assert.assertEquals("testName3", env);

    util.setEnvironmentName(null);

    System.setProperty("ENV", "testName4");
    env = util.detectEnvironment();
    Assert.assertNotNull(env);
    Assert.assertEquals("testName4", env);

    util.setEnvironmentName(null);

    System.setProperty("env", "testName2");
    env = util.detectEnvironment();
    Assert.assertNotNull(env);
    Assert.assertEquals("testName2", env);

    util.setEnvironmentName(null);

    util.setEnvironmentName("testName");
    Assert.assertNotNull(util.getEnvironment().get(Environment.ENV_NAME));
    Assert.assertEquals("testName", util.getEnvironment().get(Environment.ENV_NAME));
    env = util.detectEnvironment();
    Assert.assertEquals("testName", env);


  }
  
  @Test
  public void testFindIP() {
    
    String ip = util.detectIP();
    Assert.assertNotNull(ip);
    
  }
  
  @Test
  public void testReplacePathsWithEnvironmentValues() throws Exception {
    String templatedPath = "http://config.server.com:8080/${appconfig.app.name}/${appconfig.app.env}/hosts.properties";
    
  }

}
