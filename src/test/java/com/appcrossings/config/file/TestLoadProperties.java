package com.appcrossings.config.file;

import java.io.File;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.appcrossings.config.ConfigClient;
import com.appcrossings.config.ConfigSource;
import com.appcrossings.config.ConfigSourceFactory;

public class TestLoadProperties {

	private String host = "http://config.appcrossings.net";
	private ConfigSource source;

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Before
	public void before() throws Exception {
		folder.create();
		System.out.println(folder.getRoot().toPath());
		FileUtils.copyDirectory(FileUtils.toFile(this.getClass().getResource("/")), folder.getRoot());
		source = ConfigSourceFactory.buildConfigSource(ConfigSourceFactory.FILE_SYSTEM);
	}
	
	@After
	public void cleanup() throws Exception {
		folder.delete();
	}

	@Test
	public void loadClasspathProperties() throws Exception {

		Properties p = source.loadProperties("classpath:/env/dev", ConfigClient.DEFAULT_PROPERTIES_FILE_NAME);
		Assert.assertNotNull(p);
		Assert.assertTrue(p.containsKey("property.1.name"));
		Assert.assertEquals(p.getProperty("property.1.name"), "value1");

	}

	@Test
	public void loadFileProperties() throws Exception {

		Properties p = source.loadProperties("file:" + folder.getRoot().toPath() + "/env/dev/", ConfigClient.DEFAULT_PROPERTIES_FILE_NAME);
		Assert.assertNotNull(p);
		Assert.assertTrue(p.containsKey("property.1.name"));
		Assert.assertEquals(p.getProperty("property.1.name"), "value1");

	}

	@Test
	public void loadClasspathRelativePath() throws Exception {

		Properties p = source.loadProperties("/env/dev/", ConfigClient.DEFAULT_PROPERTIES_FILE_NAME);
		Assert.assertNotNull(p);
		Assert.assertTrue(p.containsKey("property.1.name"));
		Assert.assertEquals(p.getProperty("property.1.name"), "value1");

	}

	@Test
	public void loadAbsoluteFile() throws Exception {		
		
		Properties p = source.loadProperties("/env/dev/", ConfigClient.DEFAULT_PROPERTIES_FILE_NAME);
		Assert.assertNotNull(p);
		Assert.assertTrue(p.containsKey("property.1.name"));
		Assert.assertEquals(p.getProperty("property.1.name"), "value1");

	}

	public void testPullHostFileFromAmazon() throws Exception {

		Properties p = source.loadHosts(host + "/env/hosts.properties");
		Assert.assertNotNull(p);
		Assert.assertTrue(p.containsKey("kkarski-ibm"));

	}

	@Test
	public void testLoadHosts() throws Exception {

		Properties p = source.loadHosts("classpath:/env/hosts.properties");
		Assert.assertNotNull(p);
		Assert.assertTrue(p.containsKey("michelangello"));
		Assert.assertEquals(p.getProperty("michelangello"), "classpath:/env/dev/");

	}

	@Test
	public void testPullPropertiesFileFromAmazon() throws Exception {

		Properties p = source.loadProperties(host + "/env/dev/", ConfigClient.DEFAULT_PROPERTIES_FILE_NAME);

		Assert.assertNotNull(p);
		Assert.assertTrue(p.containsKey("property.1.name"));

	}

}
