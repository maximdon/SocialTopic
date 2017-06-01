package com.softlib.imatch.common.configuration;

public class ConfigurationException extends RuntimeException
{
	private static final long serialVersionUID = -7686854081261176838L;

	private String resourceURI;
	
	public ConfigurationException(String resourceURI)
	{
		this(resourceURI, (Throwable)null);
	}
	
	public ConfigurationException(String resourceURI, String msg)
	{
		this(resourceURI, (Throwable)null);
	}
	
	public ConfigurationException(String resourceURI, Throwable e)
	{
		this(resourceURI, "Invalid configuration for resource " + resourceURI, e);
	}

	public ConfigurationException(String resourceURI, String msg, Throwable e)
	{
		super(msg, e);
		this.setResourceURI(resourceURI);
	}

	public ConfigurationException(String resourceURI, ConfigurationValidationException cve) {
		this(resourceURI, "Configuration validation failed  for " + resourceURI + " failed due to " + cve.getMessage(), cve);
	}

	public void setResourceURI(String resourceURI) {
		this.resourceURI = resourceURI;
	}

	public String getResourceURI() {
		return resourceURI;
	}
	
	public static String buildResourceURI(String filePath, String resourcePath)
	{
		return XMLConfigurationResourceLoader.XML_PROTOCOL + "://" + filePath + ";" + resourcePath;
	}
}
