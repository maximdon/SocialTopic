package com.softlib.imatch.common;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.softlib.imatch.matcher.MatchCandidate;

public class BeanUtils {
	private static Map<String, Method> methodCache = new HashMap<String, Method>();
	
	public static Object getProperty(Object bean, String propertyName) throws IntrospectionException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		
		String key = bean.getClass().getSimpleName()+"#"+propertyName;
		Object propertyValue = null;
		Method readMethod = methodCache.get(key);
		if (readMethod == null) {
			BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
			PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
			for (PropertyDescriptor tmpDesc : descriptors) {
				if (tmpDesc.getName().equals(propertyName)) {
					readMethod = tmpDesc.getReadMethod();
					methodCache.put(key,readMethod);
					break;
				}
			}
		}
		propertyValue = readMethod.invoke(bean);

		return propertyValue;
	}
	
	public static Object getCandidateProperty(MatchCandidate candidate, String propertyName)
	{
		Object propertyValue = null;
		try {
			// Try to access the property with the given name
			propertyValue = getProperty(candidate, propertyName);
		} catch (Exception e) {
			// Not found, try to get it as ticket field
			propertyValue = candidate.getCandidateData().getField(propertyName);
		}
		return propertyValue;
	}
	
}
