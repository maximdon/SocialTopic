package com.softlib.imatch;

import org.apache.commons.configuration.ConfigurationException;

/**
 * Defines custom factory to extend Spring functionality.
 * The factory will be registered in RuntimeInfo and handle all beans with specified prefix
 * @author Maxim Donde
 *
 */
public interface ICustomFactory 
{
	/**
	 * Returns the bean with the given name which is handled by this factory
	 * @param beanName - the name of the bean to retrieve
	 * @return
	 */
	Object getBean(String beanName);
	
	/**
	 * 
	 * @return
	 */
	String getNamespace(); 
}
