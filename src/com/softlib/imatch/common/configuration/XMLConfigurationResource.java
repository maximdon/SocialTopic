package com.softlib.imatch.common.configuration;

//import java.io.Console;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.configuration.XMLConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.thoughtworks.xstream.XStream;

public class XMLConfigurationResource implements IConfigurationResource 
{
	private String xpath;
	private SoftlibXMLConfiguration config;
	
	public XMLConfigurationResource(SoftlibXMLConfiguration configuration, String xpath)
	{
		this.xpath = xpath;
		this.config = configuration;
	}
	
	public IConfigurationObject getConfigurationObject(Class<?> configObjectClass) throws ConfigurationException 
	{
		Document doc = config.getDocument();
		ConfigurationObject configurationObject = new ConfigurationObject();
		if(xpath != null && !(xpath.equals("")) && !(xpath.equals("/"))) {
			XPathFactory xpathFactory = XPathFactory.newInstance();
			XPath xpathObj = xpathFactory.newXPath();
			XPathExpression expr;
			try {
				expr = xpathObj.compile(xpath);
				NodeList nodeList = (NodeList)expr.evaluate(doc, XPathConstants.NODESET);
				if(nodeList == null || nodeList.getLength() == 0)
					//The xpath expression returns no results.
					return null;
				for(int i = 0; i < nodeList.getLength(); ++i) {
					Node node = nodeList.item(i);
					handleSingleNode(node, configObjectClass, configurationObject);
				}
			} catch (XPathExpressionException e) {
				throw new ConfigurationException(ConfigurationException.buildResourceURI(config.getFile().getPath(), xpath), e);
			} 
		}	
		else {
			Node node = doc.getDocumentElement();
			handleSingleNode(node, configObjectClass, configurationObject);
		}
		return configurationObject;		
	}
	
	public Object getCustomConfiguration(Class<?> configObjectClass) throws ConfigurationException 
	{		
		Document doc = config.getDocument();
		Node rootNode;
		if(xpath != null && !(xpath.equals("")) && !(xpath.equals("/"))) {
			XPathFactory xpathFactory = XPathFactory.newInstance();
			XPath xpathObj = xpathFactory.newXPath();
			XPathExpression expr;
			try {
				expr = xpathObj.compile(xpath);
				rootNode = (Node)expr.evaluate(doc, XPathConstants.NODE);
				if(rootNode == null)
					//The xpath expression returns no results.
					return null;
			} catch (XPathExpressionException e) {
				throw new ConfigurationException(ConfigurationException.buildResourceURI(config.getFile().getPath(), xpath), e);
			} 
		}
		else
			rootNode = doc.getDocumentElement();
		Object configurationObject = parseNode(rootNode, configObjectClass);
		return configurationObject;
	}
	
	private void handleSingleNode(Node node, Class<?> configObjectClass, ConfigurationObject configurationObject)
	{
		Node parentNode = node.getParentNode();
		Node idAttribute = parentNode.getAttributes().getNamedItem("id");
		if(!parentNode.getNodeName().equals("object") || idAttribute == null)
			throw new ConfigurationException("Invalid configuration for node " + node.getNodeName() + " id attribute not found on parent 'object' element");
		String objectId = idAttribute.getNodeValue();
		Object underlinedObject = null;
		try {
			underlinedObject = parseNode(node, configObjectClass);
		}
		catch(Exception e) {
			throw new ConfigurationException(xpath, e);
		}
		if(!objectId.equals(ConfigurationObject.COMMON_OBJECT_ID) && underlinedObject instanceof IValidatableConfiguration) {
			try {
				((IValidatableConfiguration)underlinedObject).validate();
			}
			catch(ConfigurationValidationException cve) {
				throw new ConfigurationException(xpath, cve);
			}
		}
		configurationObject.addUnderlinedObject(objectId, underlinedObject);
	}	
	
	private Object parseNode(Node node, Class<?> configObjectClass)
	{
		XStream xstream = new XStream();
		xstream.processAnnotations(configObjectClass);
		String xml = null;
		try {
            Source source = new DOMSource(node);
            StringWriter stringWriter = new StringWriter();
            Result result = new StreamResult(stringWriter);
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();
            transformer.transform(source, result);
            xml = stringWriter.getBuffer().toString();
        } catch (TransformerConfigurationException e) {
        	throw new ConfigurationException(xpath, e);
        } catch (TransformerException e) {
        	throw new ConfigurationException(xpath, e);
        }
		Object underlinedObject = xstream.fromXML(xml);
		return underlinedObject;
	}
	
	public void saveConfigurationObject(Object configObject) throws ConfigurationException {
		Collection configListeners = config.getConfigurationListeners();
		config.clearConfigurationListeners();
		if(xpath != null && !(xpath.equals("")) && !(xpath.equals("/"))) {
			Document doc = config.getDocument();
			Node rootNode;
			XPathFactory xpathFactory = XPathFactory.newInstance();
			XPath xpathObj = xpathFactory.newXPath();
			XPathExpression expr;
			try {
				expr = xpathObj.compile(xpath);
				rootNode = (Node)expr.evaluate(doc, XPathConstants.NODE);
				if(rootNode == null)
					//The xpath expression returns no results.
					return;
				XStream xstream = new XStream();
				xstream.alias(configObject.getClass().getSimpleName(), configObject.getClass());			
				String xml = xstream.toXML(configObject);
				DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Document newDoc = db.parse(new InputSource(new StringReader(xml)));
				Node newNode = doc.importNode(newDoc.getDocumentElement(), true);
				rootNode.getParentNode().replaceChild(newNode, rootNode);
				Source source = new DOMSource(doc);
		        Result result = new StreamResult( config.getFile() );
		    
		        // Write the DOM document to the file
		        Transformer xformer = TransformerFactory.newInstance().newTransformer();
		        xformer.transform(source, result);		        
			} catch (Exception e) {
				throw new ConfigurationException(ConfigurationException.buildResourceURI(config.getFile().getPath(), xpath), e);
			} 
		}
		else
		{
			XStream xstream = new XStream();
			xstream.alias(configObject.getClass().getSimpleName(), configObject.getClass());
			FileWriter fw;
			try {
				fw = new FileWriter(config.getFile());
				xstream.toXML(configObject, fw);
				fw.flush();
				fw.close();
			} catch (IOException e) {
				throw new ConfigurationException(ConfigurationException.buildResourceURI(config.getFile().getPath(), xpath), e);
			}			
		}
		config.reload();
		//TODO set configuration listeners back.
	}
		
}
