package com.softlib.imatch.matcher;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class ViewLayoutSettings implements Converter
{
	private Map<String, String> columns = new LinkedHashMap<String, String>();
	
	public void setColumns(Map<String, String> columns) {
		this.columns = columns;
	}
	//TODO by using map we're losing column order as defined in matcher.xml
	public Map<String, String> getColumns() {
		return columns;
	}
	
	public void marshal(Object source, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		// TODO complete
		
	}
	
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		while(reader.hasMoreChildren())
		{
			reader.moveDown();
			String paramName = reader.getAttribute("name");
			String paramValue = reader.getAttribute("title");
			columns.put(paramName, paramValue);
			reader.moveUp();
		}
		return this;
	}
	@SuppressWarnings("unchecked")
	public boolean canConvert(Class type) {
		return type.equals(ViewLayoutSettings.class);
	}
	
}
