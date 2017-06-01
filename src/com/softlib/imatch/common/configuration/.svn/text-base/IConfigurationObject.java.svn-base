package com.softlib.imatch.common.configuration;

import java.util.Set;

public interface IConfigurationObject {

	public Object getProperty(String objectId, String propertyName);

	public Object getCommonProperty(String propertyName);

	public String getFirstObjectId();
	
	public Set<String> getAllObjects();
	
	public Object getUnderlinedObject(String objectId);

	/**
	 * Convenient method to find minimum value between all objects for property with numeric value
	 * @param string
	 * @return
	 */
	public long getMinValue(String propertyName);

	/**
	 * Convenient method to find maximum value between all objects for property with numeric value
	 * @param string
	 * @return
	 */
	public long getMaxValue(String propertyName);

}