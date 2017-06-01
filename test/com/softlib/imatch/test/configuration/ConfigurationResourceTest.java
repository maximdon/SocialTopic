package com.softlib.imatch.test.configuration;

import java.io.Reader;
import java.io.StringReader;

import junit.framework.Assert;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.junit.Test;

import com.softlib.imatch.common.configuration.IConfigurationObject;
import com.softlib.imatch.common.configuration.XMLConfigurationResource;
import com.softlib.imatch.matcher.SearcherConfiguration;
import com.softlib.imatch.matcher.ViewLayoutConfig;
import com.softlib.imatch.matcher.ViewLayoutSettings;

public class ConfigurationResourceTest {
	@Test
	public void testStringValue()
	{
		XMLConfiguration config = new XMLConfiguration();
		Reader in = new StringReader("<matchConfig><object id=\"OldCases\"><searcher type=\"lucene\"><indexFilesLocation>c:\\Temp\\Liam\\indexes</indexFilesLocation></searcher></object></matchConfig>");
		try {
			config.load(in);
		} catch (ConfigurationException e) {
			Assert.fail(e.getMessage());
		}
		XMLConfigurationResource resource = new XMLConfigurationResource(config, "//searcher");
		IConfigurationObject configObject = resource.getConfigurationObject(SearcherConfiguration.class);
		Assert.assertNotNull(configObject);
		Object configProperty = configObject.getProperty("OldCases", "indexFilesLocation");
		Assert.assertNotNull(configProperty);
		Assert.assertEquals("c:\\Temp\\Liam\\indexes", configProperty.toString());
	}

	@Test
	public void testIntValue()
	{
		XMLConfiguration config = new XMLConfiguration();
		Reader in = new StringReader("<matchConfig><object id=\"OldCases\"><searcher type=\"lucene\"><indexIntervalSeconds>100</indexIntervalSeconds></searcher></object></matchConfig>");
		try {
			config.load(in);
		} catch (ConfigurationException e) {
			Assert.fail(e.getMessage());
		}
		XMLConfigurationResource resource = new XMLConfigurationResource(config, "//searcher");
		IConfigurationObject configObject = resource.getConfigurationObject(SearcherConfiguration.class);
		Assert.assertNotNull(configObject);
		Object configProperty = configObject.getProperty("OldCases", "indexIntervalSeconds");
		Assert.assertNotNull(configProperty);
		Assert.assertEquals(100, ((Integer)configProperty).intValue());
	}
	
	@Test
	public void testCommonStringValue()
	{
		XMLConfiguration config = new XMLConfiguration();
		Reader in = new StringReader("<matchConfig><object id=\"_COMMON_\"><searcher type=\"lucene\"><indexFilesLocation>c:\\Temp\\Liam\\indexes</indexFilesLocation></searcher></object><object id=\"OldCases\"><searcher type=\"lucene\"><indexIntervalSeconds>100</indexIntervalSeconds></searcher></object></matchConfig>");
		try {
			config.load(in);
		} catch (ConfigurationException e) {
			Assert.fail(e.getMessage());
		}
		XMLConfigurationResource resource = new XMLConfigurationResource(config, "//searcher");
		IConfigurationObject configObject = resource.getConfigurationObject(SearcherConfiguration.class);
		Assert.assertNotNull(configObject);
		Object configProperty = configObject.getProperty("OldCases", "indexIntervalSeconds");
		Assert.assertNotNull(configProperty);
		Assert.assertEquals(100, ((Integer)configProperty).intValue());
		configProperty = configObject.getProperty("OldCases", "indexFilesLocation");
		Assert.assertNotNull(configProperty);
		Assert.assertEquals("c:\\Temp\\Liam\\indexes", configProperty.toString());
	}

	@Test
	public void testCommonIntValue()
	{
		XMLConfiguration config = new XMLConfiguration();
		Reader in = new StringReader("<matchConfig><object id=\"_COMMON_\"><searcher type=\"lucene\"><indexIntervalSeconds>100</indexIntervalSeconds></searcher></object><object id=\"OldCases\"><searcher type=\"lucene\"><indexFilesLocation>c:\\Temp\\Liam\\indexes</indexFilesLocation></searcher></object></matchConfig>");
		try {
			config.load(in);
		} catch (ConfigurationException e) {
			Assert.fail(e.getMessage());
		}
		XMLConfigurationResource resource = new XMLConfigurationResource(config, "//searcher");
		IConfigurationObject configObject = resource.getConfigurationObject(SearcherConfiguration.class);
		Assert.assertNotNull(configObject);
		Object configProperty = configObject.getProperty("OldCases", "indexIntervalSeconds");
		Assert.assertNotNull(configProperty);
		Assert.assertEquals(100, ((Integer)configProperty).intValue());
		configProperty = configObject.getProperty("OldCases", "indexFilesLocation");
		Assert.assertNotNull(configProperty);
		Assert.assertEquals("c:\\Temp\\Liam\\indexes", configProperty.toString());
	}

	@Test
	public void testOverrideCommonValue()
	{
		XMLConfiguration config = new XMLConfiguration();
		Reader in = new StringReader("<matchConfig><object id=\"_COMMON_\"><searcher type=\"lucene\"><indexIntervalSeconds>100</indexIntervalSeconds></searcher></object><object id=\"OldCases\"><searcher type=\"lucene\"><indexIntervalSeconds>200</indexIntervalSeconds></searcher></object></matchConfig>");
		try {
			config.load(in);
		} catch (ConfigurationException e) {
			Assert.fail(e.getMessage());
		}
		XMLConfigurationResource resource = new XMLConfigurationResource(config, "//searcher");
		IConfigurationObject configObject = resource.getConfigurationObject(SearcherConfiguration.class);
		Assert.assertNotNull(configObject);
		Object configProperty = configObject.getProperty("OldCases", "indexIntervalSeconds");
		Assert.assertNotNull(configProperty);
		Assert.assertEquals(200, ((Integer)configProperty).intValue());
	}

	@Test
	public void testCommonConverterValue()
	{
		XMLConfiguration config = new XMLConfiguration();
		Reader in = new StringReader("<matchConfig><object id=\"_COMMON_\"><viewLayout><resultColumns><column name=\"CLOSE_DATE\" title=\"Close Date\"/><column name=\"OPEN_DATE\" title=\"Open Date\"/></resultColumns><detailsURL>detailsView.jsf?id={TICKET_ID}</detailsURL></viewLayout></object><object id=\"OldCases\"><searcher type=\"lucene\"><indexIntervalSeconds>200</indexIntervalSeconds></searcher></object></matchConfig>");
		try {
			config.load(in);
		} catch (ConfigurationException e) {
			Assert.fail(e.getMessage());
		}
		XMLConfigurationResource resource = new XMLConfigurationResource(config, "//viewLayout");
		IConfigurationObject configObject = resource.getConfigurationObject(ViewLayoutConfig.class);
		Assert.assertNotNull(configObject);
		Object configProperty = configObject.getProperty("OldCases", "resultColumns");
		Assert.assertNotNull(configProperty);
		Assert.assertEquals(2, ((ViewLayoutSettings)configProperty).getColumns().size());
	}

}
