package com.appcrossings.config;

import org.junit.Assert;
import org.junit.Test;

public class TestPropertyValueSubstitution {

  @Test
  public void testPropertyValueSubstitution() throws Exception {
    ConfigClient client = new ConfigClient("classpath:env/dev/michelangello-custom2");
    client.init();

    Assert.assertNotNull(client.getProperty("property.4.name", String.class));
    Assert.assertEquals("simple-michelangello",
        client.getProperty("property.4.name", String.class));
  }

  @Test
  public void testPropertyValueSubstitutionWithMissingValue() throws Exception {
    ConfigClient client = new ConfigClient("classpath:env/dev/michelangello-custom2");
    client.init();

    Assert.assertNotNull(client.getProperty("property.5.name", String.class));
    Assert.assertEquals("${property.1.notexsts}-michelangello",
        client.getProperty("property.5.name", String.class));
  }

}
