package com.appcrossings.config;


import org.testng.annotations.Test;

public class TestPropertyRetry {

  @Test(expectedExceptions={IllegalArgumentException.class})
  public void createConfigWithWrongPath() throws Exception {
    ConfigClient config = new ConfigClient("classpath:/env/wrong.properties");
    config.init();
  }
  
  @Test
  public void createConfigWithNonExistingHostOrEnvironment() throws Exception {
    ConfigClient config = new ConfigClient("classpath:/env/hosts.properties");
    config.setHostName("doesntexist");
    config.init();
  }
  
  @Test
  public void createConfigWithNonExistingProperties() throws Exception {
    ConfigClient config = new ConfigClient("classpath:/env/hosts.properties");
    config.setHostName("xyz");
    config.init();
  }

}
