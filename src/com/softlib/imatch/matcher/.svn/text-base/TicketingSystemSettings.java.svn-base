package com.softlib.imatch.matcher;

import java.util.HashMap;
import java.util.Map;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class TicketingSystemSettings implements Converter {

	private Map<String, String> params = new HashMap<String, String>();
	private boolean initialized = false;
	
	boolean isInitialized()
	{
		return initialized;
	}
	
	public Map<String, String> getParams() {
		return params;
	}
	
	public void setParams(Map<String, String> params) {
		this.params = params;
	}
	
	public void marshal(Object source, 
						HierarchicalStreamWriter writer,
						MarshallingContext context) {
		// TODO complete
	}
	
	public Object unmarshal(HierarchicalStreamReader reader,
							UnmarshallingContext context) {
		
		TicketingSystemSettings rc = new TicketingSystemSettings();
		
		while(reader.hasMoreChildren()) {
			reader.moveDown();
			String paramName = reader.getAttribute("key");
			String paramValue = reader.getAttribute("value");
			rc.params.put(paramName,paramValue);
			rc.initialized  = true;
			reader.moveUp();
		}
		return rc;
	}
	
	@SuppressWarnings("unchecked")
	public boolean canConvert(Class type) {
		return type.equals(TicketingSystemSettings.class);
	}
	
	public String getConnectionString() {
		return params.get("connectionString"); 
	}
	
	void setConnectionString(String connectionString)
	{
		params.put("connectionString", connectionString);
	}
};
