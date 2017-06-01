package com.softlib.imatch;

import javax.servlet.ServletContext;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.softlib.tools.fullindex.FictiveServletCtxt;

/**
 * This class represents a server runtime environment based on web application context the server runs on.
 * Provides convenient access to Spring and custom beans as well as server configuration parameters. 
 * @author Maxim Donde
 *
 */
public class WebAppRuntimeInfo extends RuntimeInfo
{	
	private ServletContext context;
	
	public WebAppRuntimeInfo(ServletContext context) {
		this.context = context;
	}
	  
	@Override
	public String getRootDirInternal()
	{
		return context.getRealPath("/");
	}
	/**
	 * Converts web application relative path to physical absolute path
	 * @param path - web application relative path to be converted
	 * @return
	 */
	protected String getRealPathInternal(String path)
	{
		return context.getRealPath(resolveSolutionRelativePath(path));
	}

	public String getServerUrl()
	{
		//TODO complete using current request info.
		return super.getServerUrl();
	}
	
	@Override
	protected ApplicationContext createSpringContext() {		
		//return WebApplicationContextUtils.getRequiredWebApplicationContext(context);
		String[] configLocations = new String[] {
				getRealPath("/{SolutionConfigFolder}/applicationContext.xml"),
				getRealPath("/{SolutionConfigFolder}/webApplicationContext.xml") };
		return new FileSystemXmlApplicationContext(configLocations);
	}

	public boolean isFictive() {
		return context instanceof FictiveServletCtxt;
	}

	@Override
	protected String getLog4JConfigFile() {
		return "log4j.xml";
	}	
}
