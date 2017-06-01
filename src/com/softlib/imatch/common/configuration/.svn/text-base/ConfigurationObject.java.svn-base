package com.softlib.imatch.common.configuration;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.softlib.imatch.ITicket;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.common.BeanUtils;
import com.softlib.imatch.common.LogUtils;

public class ConfigurationObject implements IConfigurationObject 
{
	private Map<String, Object> underlinedObjects;
	private Object commonUnderlinedObject;
	private String firstObjectId;
	
	private static Logger log = Logger.getLogger(ConfigurationObject.class);
	private static String INTERNAL_COMMON_OBJECT_ID = "_COMMON_";
	static String COMMON_OBJECT_ID = "vaObject"; 
	
	ConfigurationObject() {
		underlinedObjects = new HashMap<String, Object>();
	}
	
	void addUnderlinedObject(String objectId, Object underlinedObject)
	{
		if(objectId.equals(COMMON_OBJECT_ID)) {
			commonUnderlinedObject = underlinedObject;
		}
		if(!objectId.equals(INTERNAL_COMMON_OBJECT_ID)) {
			underlinedObjects.put(objectId, underlinedObject);
			if(firstObjectId == null) {
				firstObjectId = objectId;
			}
		}
	}
	
	public String getFirstObjectId()
	{
		return firstObjectId;
	}
	
	/* (non-Javadoc)
	 * @see com.softlib.imatch.common.configuration.IConfigurationObject#getProperty(java.lang.String, java.lang.String)
	 */
	public Object getProperty(String objectId, String propertyName)
	{
		Object underlinedObject = underlinedObjects.get(objectId);
		Object propertyValue = null;
		if(underlinedObject == null) {
			LogUtils.debug(log, "Using common object configuration instead of object '%s' for property '%s'", objectId, propertyName);
			try {
				propertyValue = BeanUtils.getProperty(commonUnderlinedObject, propertyName);
			} catch (Exception e) {
				throw new ConfigurationException("Unable to retrieve value for property " + propertyName + " for common object ");
			}
		}
		else {
			try {
				propertyValue = BeanUtils.getProperty(underlinedObject, propertyName);
				//0 is a default value for numeric fields in XStream
				if(commonUnderlinedObject != null) {
					if(propertyValue == null || (propertyValue instanceof Number && ((Number)propertyValue).floatValue() == 0)) {
						LogUtils.debug(log, "Using default value for property '%s'", propertyName);
						propertyValue = BeanUtils.getProperty(commonUnderlinedObject, propertyName);
					}
				}
			} catch (Exception e) {
				throw new ConfigurationException("Unable to retrieve value for property " + propertyName + " for object " + objectId);
			}
		}
		if(propertyValue != null && propertyValue instanceof String) {
			String propertyValueStr = replacePlaceHolders((String)propertyValue);
			propertyValue = propertyValueStr;
		}
		return propertyValue;
	}

	/* (non-Javadoc)
	 * @see com.softlib.imatch.common.configuration.IConfigurationObject#getCommonProperty(java.lang.String)
	 */
	public Object getCommonProperty(String propertyName) {
		return getProperty(firstObjectId, propertyName);
	}

	/* (non-Javadoc)
	 * @see com.softlib.imatch.common.configuration.IConfigurationObject#getAllObjects()
	 */
	public Set<String> getAllObjects() {
		return underlinedObjects.keySet();
	}

	/* (non-Javadoc)
	 * @see com.softlib.imatch.common.configuration.IConfigurationObject#getMinValue(java.lang.String)
	 */
	public long getMinValue(String propertyName) 
	{	
		long minValue = Long.MAX_VALUE;
		for(String objectId : getAllObjects()) {
			Object tmpObj = getProperty(objectId, propertyName);
			if(!(tmpObj instanceof Number))
				throw new ConfigurationException("Unable to find minimum value for property " + propertyName);
			long objValue = ((Number)tmpObj).longValue();
			if(minValue > objValue)
				minValue = objValue;
		}
		return minValue;
	}

	/* (non-Javadoc)
	 * @see com.softlib.imatch.common.configuration.IConfigurationObject#getMaxValue(java.lang.String)
	 */
	public long getMaxValue(String propertyName) 
	{	
		long minValue = Long.MIN_VALUE;
		for(String objectId : getAllObjects()) {
			Object tmpObj = getProperty(objectId, propertyName);
			if(!(tmpObj instanceof Number))
				throw new ConfigurationException("Unable to find maxim value for property " + propertyName);
			long objValue = ((Number)tmpObj).longValue();
			if(minValue < objValue)
				minValue = objValue;
		}
		return minValue;
	}
	
	public Object getUnderlinedObject(String objectId)
	{
		Object underlinedObject = underlinedObjects.get(objectId); 	
		if(underlinedObject == null)
			underlinedObject = commonUnderlinedObject;		
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(underlinedObject.getClass());
			for(PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
				if(pd.getWriteMethod() == null)
					continue;
				Object propertyVal = getProperty(objectId, pd.getName());						
				pd.getWriteMethod().invoke(underlinedObject, propertyVal);
			}
		} catch (Exception e) {
			throw new ConfigurationException("Unable to retrieve underlined object for " + objectId, e);
		} 
		if(underlinedObject instanceof IParameterizableConfiguration)
			((IParameterizableConfiguration)underlinedObject).replacePlaceHolders();
		return underlinedObject;
	}
	
	public static String replacePlaceHolders(String str) {
		String newStr = str;

		try {
			Pattern regexPattern = Pattern.compile("(?<=\\{).*?(?=})");
			Matcher matcher = regexPattern.matcher(str);

			while (matcher.find()) {
				try {
					String matchGroup = matcher.group();
					Object field = null;
					if(matchGroup.equals("SolutionName")) 
						field = RuntimeInfo.getCurrentInfo().getSolutionName(); 
					
					if (field!=null) {
						String fieldStr = Matcher.quoteReplacement(field.toString());
						newStr = newStr.replaceAll("\\{"+matchGroup+"\\}", fieldStr);
					}
				} 
				catch (UnsupportedOperationException e) {
					LogUtils.error(log, e.getMessage());
				}
			}
		} catch (Exception e) {
			LogUtils.error(log, "Error when replacing place holders in the configuration parameter: %s\n error msg: %s", str, e.getMessage());
		}
		
		return newStr;
	}
}
