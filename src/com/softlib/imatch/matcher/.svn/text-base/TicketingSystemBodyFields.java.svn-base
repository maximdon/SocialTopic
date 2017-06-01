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

public class TicketingSystemBodyFields implements Converter {
	
	private Map<MatchMode,List<String>> bodyFieldsByMode = new HashMap<MatchMode,List<String>>();
	
	public List<String> getBodyFields(MatchMode mode) {
		return bodyFieldsByMode.get(mode);
	}
	
	private void put(MatchMode mode,String name) {
		List<String> fields = bodyFieldsByMode.get(mode);
		if (fields==null)
			fields = new ArrayList<String>();
		fields.add(name);
		bodyFieldsByMode.put(mode,fields);
	}
	
	public void marshal(Object source, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		// TODO complete	
	}
	
	public Object unmarshal(HierarchicalStreamReader reader,
							UnmarshallingContext context) {
		while(reader.hasMoreChildren()) {
			reader.moveDown();
			String fieldName = reader.getAttribute("name");
			String fieldMode = reader.getAttribute("mode");
			MatchMode mode;
			if (fieldMode==null || fieldMode.isEmpty())
				mode = MatchMode.all;
			else
				mode = MatchMode.valueOf(fieldMode);
			
			if (mode.equals(MatchMode.all)) {
				put(MatchMode.all,fieldName);
				put(MatchMode.match,fieldName);
				put(MatchMode.rematch,fieldName);
			}
			else
				put(mode,fieldName);
			
			reader.moveUp();
		}
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public boolean canConvert(Class type) {
		return type.equals(TicketingSystemBodyFields.class);
	}

	public boolean isEmpty() {
		return bodyFieldsByMode.isEmpty();
	}

};
