package com.softlib.imatch.test.candidatefilter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.softlib.imatch.common.configuration.IConfigurationObject;

public class TestConfigurationObject implements IConfigurationObject {

	public Map<String, Object> values = new HashMap<String, Object>();
	
	public Set<String> getAllObjects() {
		return values.keySet();
	}

	public Object getCommonProperty(String propertyName) {
		return values.get(propertyName);
	}

	public long getMaxValue(String propertyName) {
		return (Long)getCommonProperty(propertyName);
	}

	public long getMinValue(String propertyName) {
		return (Long)getCommonProperty(propertyName);
	}

	public Object getProperty(String objectId, String propertyName) {
		return getCommonProperty(propertyName);
	}
	
	public void addProperty(String propertyName, Object propertyValue) {
		values.put(propertyName, propertyValue);
	}

	public Object getUnderlinedObject(String objectId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFirstObjectId() {
		// TODO Auto-generated method stub
		return null;
	}

}
