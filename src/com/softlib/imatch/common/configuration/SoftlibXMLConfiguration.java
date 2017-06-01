package com.softlib.imatch.common.configuration;

import java.io.File;
import java.io.IOException;
import java.security.CodeSource;
import java.text.MessageFormat;
import java.util.Collection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.dom4j.DocumentFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.apache.commons.configuration.ConfigurationException;

public class SoftlibXMLConfiguration 
{
	private File file;
	private Document document;
	
	public SoftlibXMLConfiguration(String fileName) throws  ConfigurationException
	{
		file = new File(fileName);
		DocumentBuilderFactory factory =
		      DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			document = builder.parse(file);
		} 
		catch (ParserConfigurationException pce) {
			throw new ConfigurationException("Unable to parse document due to " + pce.getMessage(), pce);
		} catch (SAXException se) {
			throw new ConfigurationException("Unable to parse document due to " + se.getMessage(), se);
		} catch (IOException ie) {
			throw new ConfigurationException("Unable to open file " + file + " due to " + ie.getMessage(), ie);			
		}
		catch(Exception e){
			throw new ConfigurationException("Unable to create xml config due to " + e.getMessage(), e);
		}
	}

	private static String getJaxpImplementationInfo(String componentName, Class componentClass) {
	    CodeSource source = componentClass.getProtectionDomain().getCodeSource();
	    return MessageFormat.format(
	            "{0} implementation: {1} loaded from: {2}",
	            componentName,
	            componentClass.getName(),
	            source == null ? "Java Runtime" : source.getLocation());
	}
	
	public Document getDocument() {
		return document;
	}

	public File getFile() {
		return file;
	}

	public void clearConfigurationListeners() {
	}

	public Collection getConfigurationListeners() {
		return null;
	}

	public void reload() {		
	}

}
