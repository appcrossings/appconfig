package com.appcrossings.config.file;

import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;
import com.appcrossings.config.ConfigClient;
import com.appcrossings.config.ConfigClient.Method;

public class TestDirectMethodFetch {
  
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
  
  @Test
  public void testFetchWithDirectPath() throws Exception {

    ConfigClient client = new ConfigClient("classpath:/env/dev/json/default.json", Method.DIRECT_PATH);
    client.init();

    Properties props = client.getProperties();

    Assert.assertTrue(props.containsKey("property.1.name"));
    Assert.assertEquals("simple", props.getProperty("property.1.name"));

    Assert.assertTrue(props.containsKey("property.4.name"));
    Assert.assertEquals("${property.1.name}-${property.3.name}",
        props.getProperty("property.4.name"));

    Assert.assertTrue(props.containsKey("bonus.1.property"));
    Assert.assertEquals("bonus2", props.getProperty("bonus.1.property"));
  }

}
