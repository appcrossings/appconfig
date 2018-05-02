package com.appcrossings.config.util;

import java.net.URI;
import org.junit.Assert;
import org.junit.Test;
import com.appcrossings.config.util.DirectoryTraverse;
import com.appcrossings.config.util.URITraverse;

public class TestDirectoryTraversalStrategy {

  @Test
  public void testDescendingURITraverseWithFile() {
    URITraverse strat = new URITraverse(URI.create("file:root/first/second/third/file.properties"));

    Assert.assertEquals(4, strat.available());
    Assert.assertEquals("file:root/first/second/third/file.properties", strat.decend());
    Assert.assertTrue(strat.hasNextDown());
    Assert.assertEquals("file:root/first/second/file.properties", strat.decend());
    Assert.assertTrue(strat.hasNextDown());
    Assert.assertEquals("file:root/first/file.properties", strat.decend());
    Assert.assertTrue(strat.hasNextDown());
    Assert.assertEquals("file:root/file.properties", strat.decend());
    Assert.assertTrue(strat.hasNextDown());
    Assert.assertEquals("file:file.properties", strat.decend());
    Assert.assertFalse(strat.hasNextDown());

    Assert.assertTrue(strat.hasNextUp());
    Assert.assertEquals("file:file.properties", strat.ascend());
    Assert.assertTrue(strat.hasNextUp());
    Assert.assertEquals("file:root/file.properties", strat.ascend());
    Assert.assertTrue(strat.hasNextUp());
    Assert.assertEquals("file:root/first/file.properties", strat.ascend());
    Assert.assertTrue(strat.hasNextUp());
    Assert.assertEquals("file:root/first/second/file.properties", strat.ascend());
    Assert.assertTrue(strat.hasNextUp());
    Assert.assertEquals("file:root/first/second/third/file.properties", strat.ascend());
    Assert.assertFalse(strat.hasNextUp());
  }

  @Test
  public void testDescendingDirTraverseWithFile() {
    DirectoryTraverse strat = new DirectoryTraverse("/root/first/second/third/file.properties");

    Assert.assertEquals(4, strat.available());
    Assert.assertEquals("/root/first/second/third/file.properties", strat.decend());
    Assert.assertTrue(strat.hasNextDown());
    Assert.assertEquals("/root/first/second/file.properties", strat.decend());
    Assert.assertTrue(strat.hasNextDown());
    Assert.assertEquals("/root/first/file.properties", strat.decend());
    Assert.assertTrue(strat.hasNextDown());
    Assert.assertEquals("/root/file.properties", strat.decend());
    Assert.assertTrue(strat.hasNextDown());
    Assert.assertEquals("/file.properties", strat.decend());
    Assert.assertFalse(strat.hasNextDown());

    
    Assert.assertTrue(strat.hasNextUp());
    Assert.assertEquals("/file.properties", strat.ascend());
    Assert.assertTrue(strat.hasNextUp());
    Assert.assertEquals("/root/file.properties", strat.ascend());
    Assert.assertTrue(strat.hasNextUp());
    Assert.assertEquals("/root/first/file.properties", strat.ascend());
    Assert.assertTrue(strat.hasNextUp());
    Assert.assertEquals("/root/first/second/file.properties", strat.ascend());
    Assert.assertTrue(strat.hasNextUp());
    Assert.assertEquals("/root/first/second/third/file.properties", strat.ascend());
    Assert.assertFalse(strat.hasNextUp());
  }

}
