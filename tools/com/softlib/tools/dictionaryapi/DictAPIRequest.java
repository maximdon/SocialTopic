package com.softlib.tools.dictionaryapi;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("ParseQueryRequest")
public class DictAPIRequest {

	@XStreamAlias("Header")
	private DictAPIRequestHeader header;

	@XStreamAlias("Body")
	private DictAPIRequestBody body;

	
	public DictAPIRequestBody getBody() {
		return body;
	}

	public void setBody(DictAPIRequestBody body) {
		this.body = body;
	}

	public DictAPIRequestHeader getHeader() {
		return header;
	}

	public void setHeader(DictAPIRequestHeader header) {
		this.header = header;
	}
	
	static public DictAPIRequest parse(String xml) {
		XStream xstream = new XStream();
		xstream.processAnnotations(DictAPIRequest.class);
		return (DictAPIRequest)xstream.fromXML(xml);
	}
	
//	static public DictAPIRequest parse(String xml) {
//		//Hebrew support stuff:
//		//This works when hardcoded, but doesn't work from iSolve request
//		//xml = "<ParseQueryRequest><Header><RequestId>28</RequestId><LibraryId>1149</LibraryId></Header><Body><QueryStr>מילה בעברית</QueryStr></Body></ParseQueryRequest>";
//		/*			DictAPIRequestBody body = new DictAPIRequestBody();
//		int beginIndex = xml.indexOf("<QueryStr>") + "<QueryStr>".length();
//		int endIndex = xml.indexOf("</QueryStr>");
//		body.setQueryStr(xml.substring(beginIndex, endIndex).trim());
//		request.setBody(body); */
//		//End of hebrew support stuff
//		XStream xstream = new XStream();
//		xstream.processAnnotations(DictAPIRequest.class);
//		ByteArrayInputStream is = new ByteArrayInputStream(xml.getBytes());
//		try {
//			//Reader reader = new InputStreamReader(is, "UTF-8");
//			Reader reader = new InputStreamReader(is);
//			DictAPIRequest request = (DictAPIRequest)xstream.fromXML(reader);
//			return request;
//		} 
//		//catch (UnsupportedEncodingException e) {
//		catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return null;
//		}
//	}

	public String toString() {
		return header.toString() + " , " + body.toString();
	}
	
};
