package com.softlib.imatch;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.dictionary.TechnicalDictionary;

/**
 * This class is used to initialize RuntimeInfo before the application is loaded (and before the Spring is initialized)
 * @author Maxim Donde
 *
 */
public class WebAppInitializer implements ServletContextListener 
{
	private static Logger log = Logger.getLogger(WebAppInitializer.class);
	
	public void contextDestroyed(ServletContextEvent sce) {
		try {
			RuntimeInfo.getCurrentInfo().destroy();
			LogUtils.info(log, "iMatch was stopped");
			LogManager.shutdown();
		}
		catch(MatcherException e) {
			LogUtils.error(log, e);
			LogManager.shutdown();
		}
	}

	public void contextInitialized(ServletContextEvent sce) {
		RuntimeInfo runtimeInfo = null;
		try {
			MultitenantRuntimeInfo.init(sce.getServletContext());
			runtimeInfo = RuntimeInfo.getCurrentInfo();
			runtimeInfo.startThread();
		}
		finally {
			if(runtimeInfo != null)
				runtimeInfo.finishThread();
		}
	}

}
