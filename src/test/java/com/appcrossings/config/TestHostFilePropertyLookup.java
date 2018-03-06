package com.appcrossings.config;

import org.junit.Assert;
import org.junit.Test;
import com.appcrossings.config.ConfigClient;

public class TestHostFilePropertyLookup {

  @Test(expected = IllegalArgumentException.class)
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
  
  @Test
  public void testLookupConfigByEnvironment() throws Exception {
    ConfigClient config = new ConfigClient("classpath:/env/hosts.properties");
    config.getEnvironment().setEnvironmentName("QA");
    config.init();
    
    Assert.assertEquals("custom", config.getProperty("property.3.name", String.class));
  }
  
  @Test
  public void testLookupConfigByHostname() throws Exception {
    ConfigClient config = new ConfigClient("classpath:/env/hosts.properties");
    config.setHostName("michelangello-custom2");
    config.init();
    
    Assert.assertEquals("michelangello", config.getProperty("property.3.name", String.class));
  }

}
