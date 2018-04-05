package com.appcrossings.config.http;

import org.junit.Assert;
import org.junit.Test;
import com.appcrossings.config.ConfigClient;
import com.appcrossings.config.ConfigClient.Method;

public class TestDirectMethodFetch {
  
  @Test
  public void testDirectPathPropertyLookup() throws Exception {
    ConfigClient client = new ConfigClient("http://config.appcrossings.net/env/dev/", Method.DIRECT_PATH);
    client.init();
    
    Assert.assertNotNull(client.getProperty("property.2.name", String.class));
  }
  
  @Test public void testDirectFilePropertyLookup() throws Exception {
    ConfigClient client = new ConfigClient("http://config.appcrossings.net/env/dev/default.properties", Method.DIRECT_PATH);
    client.init();
    
    Assert.assertNotNull(client.getProperty("property.2.name", String.class));
  }

}
