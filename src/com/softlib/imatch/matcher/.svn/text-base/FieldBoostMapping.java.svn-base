package com.softlib.imatch.matcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.softlib.imatch.ticketprocessing.IProcessedTicket.MatchMode;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class FieldBoostMapping implements Converter {
	
	private Map<String,Float> fieldBoostMap = new HashMap<String,Float>();

	public Float GetFieldBoost(String fieldName)
	{
		Float ret = 1.0f;
		try
		{
			Float f = fieldBoostMap.get(fieldName);
			if (f == null)
				return ret;
			return f;
		}
		catch(Exception e)
		{
			
		}
		return ret;
	}
	
	private void put(String fieldName,Float value) {
		Float currValue = fieldBoostMap.get(fieldName);
		fieldBoostMap.put(fieldName,value);
	}

	/** Converter implementation **/
	public void marshal(Object value, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		//TODO complete
	}

	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) {
		

		while(reader.hasMoreChildren()) {
			reader.moveDown();
			String fieldName = reader.getAttribute("name");
			String boost = reader.getAttribute("boost");

			put(fieldName,Float.valueOf(boost));
			
			reader.moveUp();
		}
		return this;
	}

	@SuppressWarnings("unchecked")
	public boolean canConvert(Class clazz) {
		return clazz.equals(FieldBoostMapping.class);
	}
}
