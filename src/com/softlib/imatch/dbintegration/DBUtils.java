package com.softlib.imatch.dbintegration;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.softlib.imatch.ITicket;

public class DBUtils {
	
	public static String concatBodyFields(String[] fields) {
		String result = "";
		String delimiterStr;
		final String delimiterStringWithDot = ". ";
		final String delimiterStringWithoutDot = " ";
		for (String bodyField : fields) {
			if (bodyField != null && !bodyField.equals("")) {
				// . is used as a sentence delimiter and is important for regex expressions
				if (bodyField.endsWith("."))
					delimiterStr = delimiterStringWithoutDot;
				else
					delimiterStr = delimiterStringWithDot;
				bodyField += delimiterStr;
				result += bodyField;
			}
		}
		return result;
	}
	
	static public String fieldToString(Object field) {
		String rc = null;
		if (field instanceof String)
			rc = (String)field;
		if (field instanceof Double)
			rc = ((Double)field).toString();
		return rc;
	}
	
	static public String concatFields(Set<String> fieldsNames,ITicket ticket) {
		List<String> fields = new ArrayList<String>();
		for (String fieldName : fieldsNames) {
			String fieldText = fieldToString(ticket.getField(fieldName)); 
			if (fieldText!=null)
				fields.add(fieldText);
		}
		return DBUtils.concatBodyFields(fields.toArray(new String[0]));
	}

};
