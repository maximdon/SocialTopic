package com.softlib.tools.dictionaryapi;

import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import com.thoughtworks.xstream.XStream;

public class DictAPIXmlUtil {

/*	static public String getXMLString(Object object) {
		String rc = null;
		XStream xstream = new XStream();
		xstream.processAnnotations(object.getClass());
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		Writer out = null;
		try {
			out = new OutputStreamWriter(os, "UTF-8");
			xstream.toXML(object, out);
			rc = new String(os.toByteArray(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rc;
	}
*/
	static public String getXMLString(Object object) {
		String rc;
		XStream xstream = new XStream();
		xstream.processAnnotations(object.getClass());
		rc = xstream.toXML(object);
		return rc;
	}
	
	static public void saveXMLString(Object object) {
		XStream xstream = new XStream();
		xstream.processAnnotations(object.getClass());
		FileWriter fw;
		try {
			fw = new FileWriter("c:\\DictAPIResponse.xml");
			xstream.toXML(object,fw);
			fw.flush();
			fw.close();
		} catch (IOException e) {
			System.out.println("IOException: "+e);
		}			
	}
	
};
