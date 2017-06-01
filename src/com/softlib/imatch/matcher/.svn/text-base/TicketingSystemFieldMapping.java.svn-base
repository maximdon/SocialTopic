package com.softlib.imatch.matcher;

import java.util.HashMap;
import java.util.Map;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class TicketingSystemFieldMapping implements Converter {
	
	private Map<String, String> fieldNames;

	public static final String ID_FIELD_DEFAULT_NAME = "idField";
	public static final String TITLE_FIELD_DEFAULT_NAME = "titleField";
	public static final String STATE_FIELD_DEFAULT_NAME = "stateField";
	
	public TicketingSystemFieldMapping()
	{
		fieldNames = new HashMap<String, String>();
		fieldNames.put(ID_FIELD_DEFAULT_NAME, ID_FIELD_DEFAULT_NAME);
		fieldNames.put(TITLE_FIELD_DEFAULT_NAME, TITLE_FIELD_DEFAULT_NAME);
		fieldNames.put(STATE_FIELD_DEFAULT_NAME, STATE_FIELD_DEFAULT_NAME);
	}
	
	public String getIdFieldName() {
		return fieldNames.get(ID_FIELD_DEFAULT_NAME);
	}

	public void setIdFieldName(String idField) {
		fieldNames.put(ID_FIELD_DEFAULT_NAME, idField);
	}

	public String getTitleFieldName() {
		return fieldNames.get(TITLE_FIELD_DEFAULT_NAME);
	}

	public void setTitleFieldName(String titleField) {
		fieldNames.put(TITLE_FIELD_DEFAULT_NAME, titleField);
	}

	public String getStateFieldName() {
		return fieldNames.get(STATE_FIELD_DEFAULT_NAME);
	}

	public void setStateFieldName(String stateField) {
		fieldNames.put(STATE_FIELD_DEFAULT_NAME, stateField);
	}

	public String getFieldName(String fieldDefaultName) {
		return fieldNames.get(fieldDefaultName);
	}
	/** Converter implementation **/
	public void marshal(Object value, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		//TODO complete
	}

	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) {
		
		while(reader.hasMoreChildren())
		{
			reader.moveDown();
			String defaultFieldName = reader.getNodeName();
			String actualFieldName = reader.getValue();
			fieldNames.put(defaultFieldName, actualFieldName);
			reader.moveUp();
		}
		return this;
	}

	@SuppressWarnings("unchecked")
	public boolean canConvert(Class clazz) {
		return clazz.equals(TicketingSystemFieldMapping.class);
	}
}
