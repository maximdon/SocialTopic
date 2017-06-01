package com.softlib.imatch.common.configuration;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.XMLConfiguration;

import com.softlib.imatch.RuntimeInfo;

public class XMLConfigurationResourceLoader implements IConfigurationResourceLoader 
{
	static final String XML_PROTOCOL = "xml";
	private Map<String, SoftlibXMLConfiguration> loadedConfigurations;
	
	public XMLConfigurationResourceLoader()
	{
		loadedConfigurations = new HashMap<String, SoftlibXMLConfiguration>();
	}
	
	public String getProtocol()
	{
		return XML_PROTOCOL;
	}
	
	public IConfigurationResource loadResource(String resourcePath) throws ConfigurationException {
		//TODO introduce base class which implements resource path validation and split
		String actualResourcePath = resourcePath.substring((XML_PROTOCOL + "://").length());
		String[] resourcePathParts = actualResourcePath.split(";");
		String fileName = RuntimeInfo.getCurrentInfo().getRealPath(resourcePathParts[0]);
		String xpath = resourcePathParts.length > 1 ? resourcePathParts[1] : null;
		SoftlibXMLConfiguration underlinedConfiguration = loadedConfigurations.get(fileName);
		if(underlinedConfiguration == null){
			try {
				underlinedConfiguration = new SoftlibXMLConfiguration(fileName);
			}
			catch(org.apache.commons.configuration.ConfigurationException ce) {
				throw new ConfigurationException(resourcePath, ce);
			}
			loadedConfigurations.put(fileName, underlinedConfiguration);
		}
		XMLConfigurationResource configurationResource = new XMLConfigurationResource(underlinedConfiguration, xpath);
		return configurationResource;
	}
}
