package com.softlib.imatch.matcher;

import java.util.ArrayList;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class SearcherConfigurationConverter implements Converter {

	public void marshal(Object object, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		// TODO complete

	}

	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) {
		ArrayList<String> fields = new ArrayList<String>();
		while(reader.hasMoreChildren())
		{
			reader.moveDown();
			String fieldName = reader.getValue();
			fields.add(fieldName);
			reader.moveUp();
		}
		return fields;
	}

	public boolean canConvert(Class clazz) {
		return clazz.equals(ArrayList.class);			
	}

}
