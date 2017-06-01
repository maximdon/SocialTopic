package com.softlib.imatch.logging;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.softlib.imatch.exceptions.SoftlibError;

public class Audit {
	public static void debug(String message, Class<?> clazz)
	{
		Logger logger = Logger.getLogger(clazz);
		logger.debug(message);
	}
	
	public static void info(String message, Class<?> clazz)
	{
		Logger logger = Logger.getLogger(clazz);
		logger.info(message);
	}
	
	public static void error(String message, Class<?> clazz)
	{
		Logger logger = Logger.getLogger(clazz);
		logger.error(message);
	}
	
	public static void debug(String message, Exception ex, Class<?> clazz)
	{
		Logger logger = Logger.getLogger(clazz);
		logger.debug(message);
	}
	
	public static void info(String message, Exception ex, Class<?> clazz)
	{
		Logger logger = Logger.getLogger(clazz);
		logger.info(message);
	}
	
	public static void error(String message, Exception ex, boolean sendErrorToClient, Class<?> clazz)
	{
		Logger logger = Logger.getLogger(clazz);
		logger.error(message);
		printStack(ex, clazz);
	}
	
	static private void printStack(Exception e, Class<?> clazz)
	{
		Logger logger = Logger.getLogger(clazz);
		StackTraceElement[] s;

		
		//if (e.getMessage() != null)
		//{
		logger.error("=====================================ERROR START=====================================");
		if (e.getMessage() != null)
			logger.error("Messege: " + e.getMessage().toString());
		if (e.getLocalizedMessage() != null)
			logger.error("LocalizedMessage: " + e.getLocalizedMessage().toString());
		if (e.getCause() != null)
			logger.error("Cause: " + e.getCause().toString());
		if (e.fillInStackTrace() != null)
			logger.error("fillInStackTrace: " + e.fillInStackTrace().toString());
		if (e.getStackTrace() != null)
			logger.error("getStackTrace: " + e.getStackTrace().toString());
		
		s = e.getStackTrace();
		
		for (int i = 0; i < s.length; i++) {
		      StackTraceElement si = s[i];
		      logger.error("Stack Trace Element: " + i + " " + si.toString());
		    }  
		logger.error("=====================================ERROR END=====================================");
			
		//context.getApplication().getNavigationHandler().handleNavigation(context, null, "authFailureToError");
		//context.responseComplete();
		
		
		
		//}
		/*
		else
		{
			logger.error("===========================ERROR - NO CAUSE TO DISPLAY ============================");
		}
		*/
		
		//removeSessionID(request);
	}
}
