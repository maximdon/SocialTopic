package com.softlib.imatch.matcher;

import java.util.HashMap;
import java.util.Map;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class TicketingSystemConnectionSettings implements Converter
{
	private String connectionString;
	private Map<String, String> connectionParams = new HashMap<String, String>();
	
	public void setConnectionString(String connectionString) {
		this.connectionString = connectionString;
	}
	public String getConnectionString() {
		return connectionString;
	}
	public void setConnectionParams(Map<String, String> connectionParams) {
		this.connectionParams = connectionParams;
	}
	public Map<String, String> getConnectionParams() {
		return connectionParams;
	}
	
	public void marshal(Object source, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		// TODO complete
		
	}
	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) {
		reader.moveDown();
		connectionString = reader.getValue();
		reader.moveUp();
		while(reader.hasMoreChildren())
		{
			reader.moveDown();
			String paramName = reader.getAttribute("key");
			String paramValue = reader.getAttribute("value");
			connectionParams.put(paramName, paramValue);
			reader.moveUp();
		}
		return this;
	}
	@SuppressWarnings("unchecked")
	public boolean canConvert(Class type) {
		return type.equals(TicketingSystemConnectionSettings.class);
	}
}
