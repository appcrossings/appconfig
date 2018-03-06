package com.appcrossings.config;

import org.junit.Assert;
import org.junit.Test;
import com.appcrossings.config.ConfigClient;
import com.appcrossings.config.ConfigClient.Method;

public class TestDirectPathPropertyLookup {
  
  @Test
  public void testDirectPathPropertyLookup() throws Exception {
    ConfigClient client = new ConfigClient("classpath:env/dev/simple", Method.DIRECT_PATH);
    client.init();
    
    Assert.assertNotNull(client.getProperty("property.3.name", String.class));
  }
  
  @Test public void testDirectFilePropertyLookup() throws Exception {
    ConfigClient client = new ConfigClient("classpath:env/dev/simple/default.properties", Method.DIRECT_PATH);
    client.init();
    
    Assert.assertNotNull(client.getProperty("property.3.name", String.class));
  }

}
