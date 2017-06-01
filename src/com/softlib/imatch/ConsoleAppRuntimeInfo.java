package com.softlib.imatch;

import java.io.File;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * This class represents a server runtime environment for console application
 * (scheduler) Provides convenient access to Spring and custom beans as well as
 * server configuration parameters.
 * 
 * @author Maxim Donde
 * 
 */
public class ConsoleAppRuntimeInfo extends RuntimeInfo 
{
	@Override	
	public String getRootDirInternal()
	{
		String baseFolder = System.getProperty("user.dir");
		File f = new File(baseFolder, "WebRoot");
		if(f.exists())
			baseFolder = baseFolder + "\\WebRoot";
		return baseFolder;
	}

	/**
	 * Converts web application relative path to physical absolute path
	 * 
	 * @param path
	 *            - web application relative path to be converted
	 * @return
	 */
	protected String getRealPathInternal(String path)
	{
		String rootDir = getRootDir();
		return rootDir + resolveSolutionRelativePath(path);
	}

	@Override
	protected ApplicationContext createSpringContext() {
		String[] configLocations = new String[] {
				getRealPath("/{SolutionConfigFolder}/applicationContext.xml"),
				getRealPath("/{SolutionConfigFolder}/consoleApplicationContext.xml") };
		return new FileSystemXmlApplicationContext(configLocations);
	}

	@Override
	protected String getLog4JConfigFile() {
		return "log4j-console.xml";
	}
}
