package com.softlib.imatch.common;

import com.thoughtworks.xstream.XStream;

public class XMLUtils {

	static public String getXMLString(Object object) {
		XStream xstream = new XStream();
		xstream.processAnnotations(object.getClass());
		return xstream.toXML(object);
	}

	static public Object parse(String xml,Class<?> aClass) {
		XStream xstream = new XStream();
		xstream.processAnnotations(aClass);
		return xstream.fromXML(xml);
	}

};
