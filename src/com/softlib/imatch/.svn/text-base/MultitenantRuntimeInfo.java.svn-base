package com.softlib.imatch;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.xml.transform.TransformerFactory;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;

import com.softlib.imatch.common.HibernateUtils;
import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.dictionary.TechnicalDictionary;

/**
 * This class implements server RuntimeInfo for multitenant solutions where multiple solutions are loaded simultaneously
 * Manages a list of all loaded solutions and the current solution (for the current thread).
 * Overrides most of RuntimeInfo methods to provide correct implementation based on the current solution.
 * In multitenant scenario, there is one base solution which is loaded on startup and many actual solutions
 * which can be loaded on startup (using loadSolution) or dynamically on startThread.
 * Some beans are actually statefull beans and can't be share across multiple solutions. 
 * For example, TechhnicalDictionary contains all solution terms and can't be share. 
 * For such beans, MultitenantRuntimeInfo creates new instance for each solution and manages these instances on top of spring. 
 * @author Maxim Donde
 *
 */
public class MultitenantRuntimeInfo extends RuntimeInfo 
{
	//Contains list of all stateful beans that can't be share across solutions.
	//Currently only dictionary bean is such.
	private List<String> solutionBeansNames = new ArrayList<String>();
	//Track all loaded solutions
	private Map<String, SolutionObject> loadedSolutions;
	private ThreadLocal<SolutionObject> currentSolution;
	//Initial RuntimeInfo for the default solution created on the server startup
	private RuntimeInfo defaultRuntimeInfo;
	private static ServletContext initialContext;
	//Internal object to create stateful beans
	private MultitenantSolutionBeanCreator solutionBeanCreator; 
	
	protected MultitenantRuntimeInfo(RuntimeInfo defaultRuntimeInfo)
	{
		super();
		solutionBeansNames.add("dictionary");
		
		this.defaultRuntimeInfo = defaultRuntimeInfo;
		loadedSolutions = new HashMap<String, SolutionObject>();
		currentSolution = new ThreadLocal<SolutionObject>();
	}
	
	public static void init(ServletContext context)
	{
		TransformerFactory tFactory = TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl",null);
		RuntimeInfo.init(context);
		RuntimeInfo.runtimeInfo = new MultitenantRuntimeInfo(RuntimeInfo.getCurrentInfo());
		initialContext = context;
		//Just to load the spring 
		runtimeInfo.getBean("xmlConfigurationResourceLoader");
	}
			
	public void loadSoluion(String solutionName)
	{
		if(!loadedSolutions.containsKey(solutionName)) {
            synchronized (loadedSolutions) {
				if(!loadedSolutions.containsKey(solutionName)) {	
					RuntimeInfo solutionRuntimeInfo;
					if(initialContext != null)
						solutionRuntimeInfo = new WebAppRuntimeInfo(initialContext);
					else
						solutionRuntimeInfo = new ConsoleAppRuntimeInfo();
					solutionRuntimeInfo.serverConfig = new ServerConfiguration();
					solutionRuntimeInfo.serverConfig.setSolution(solutionName);
					solutionRuntimeInfo.serverConfig.setServerUrl(defaultRuntimeInfo.getServerUrl());
					SolutionObject solutionObject = new SolutionObject(solutionRuntimeInfo);
					loadedSolutions.put(solutionName, solutionObject);
					currentSolution.set(solutionObject);
					//Load dictionary for this solution
					if(isWebAppMode()) {
						TechnicalDictionary dict = (TechnicalDictionary) getBean("dictionary");
						dict.loadDictionary();
						LogUtils.info(log, "Soluion %s loaded successfully", solutionName);
					}
				}
			} 
		}
	}
	@Override
	public HibernateUtils getHibernate() {
		SolutionObject currentSolutionObj = currentSolution.get();
		if(currentSolutionObj == null)
			return defaultRuntimeInfo.getHibernate();
		return currentSolutionObj.getRuntimeInfo().getHibernate();
	}

	
	@Override
	public void destroy() throws MatcherException {
		for(SolutionObject solutionObject : loadedSolutions.values()) {
			try {
				solutionObject.getRuntimeInfo().getHibernate().shutdown();
			} catch (SQLException e) {				
			}
		}
		defaultRuntimeInfo.destroy();		
	}

	@Override
	protected ApplicationContext createSpringContext() {
		return defaultRuntimeInfo.createSpringContext();
	}

	@Override
	protected String getLog4JConfigFile() {
		return defaultRuntimeInfo.getLog4JConfigFile();
	}

	@Override
	protected String getRealPathInternal(String path) {
		SolutionObject currentSolutionObj = currentSolution.get();
		if(currentSolutionObj == null)
			throw new RuntimeException("Current solution should be set at the beggining");
		return currentSolutionObj.getRuntimeInfo().getRealPathInternal(path);
	}

	
	@Override
	public String getRealPath(String path) {
		String realPath = defaultRuntimeInfo.getRealPath(path);
		File f = new File(realPath);
		if(!f.exists()) {
			SolutionObject currentSolutionObj = currentSolution.get();
			if(currentSolutionObj == null)
				throw new RuntimeException("Current solution should be set at the beggining");
			realPath = currentSolutionObj.getRuntimeInfo().getRealPath(path);
		}
		return realPath;
	}

	@Override
	public String getRootDir() {
		return defaultRuntimeInfo.getRootDir();
	}

	@Override
	protected String getRootDirInternal()
	{
		return defaultRuntimeInfo.getRootDirInternal();
	}
	@Override
	public String getServerUrl() {
		return defaultRuntimeInfo.getServerUrl();
	}

	@Override
	/**
	 * Starts new execution thread.
	 * Loads solution pointed by the given ThreadInfo if needed.
	 */
	public void startThread(ThreadInfo info) {
		super.startThread(info);
		if(info instanceof MultitenantThreadInfo) {
			MultitenantThreadInfo multitenantThreadInfo = (MultitenantThreadInfo)info;
			setCurrentSolution(multitenantThreadInfo.getSolution());
		}
		if(currentSolution.get() != null)
			LogUtils.startThread(currentSolution.get().getRuntimeInfo().getSolutionName());
		defaultRuntimeInfo.startThread();
	}

	@Override
	public void finishThread() {
		defaultRuntimeInfo.finishThread();
		if(currentSolution.get() != null)
			LogUtils.finishThread();
	}

	@Override
	public ServerState getInternalState(String objectId) {
		SolutionObject currentSolutionObj = currentSolution.get();
		if(currentSolutionObj == null)
			throw new RuntimeException("Current solution should be set at the beggining");
		return currentSolutionObj.getRuntimeInfo().getInternalState(objectId);
	}
	
	@Override
	public void saveInternalState(String objectId, Session session)
			throws HibernateException {
		SolutionObject currentSolutionObj = currentSolution.get();
		if(currentSolutionObj == null)
			throw new RuntimeException("Current solution should be set at the beggining");
		currentSolutionObj.getRuntimeInfo().saveInternalState(objectId, session);
	}

	@Override
	public String getISolveRootFolder() {
		return defaultRuntimeInfo.getISolveRootFolder();
	}

	@Override
	protected String getSolutionFolder() {
		SolutionObject currentSolutionObj = currentSolution.get();
		if(currentSolutionObj == null)
			throw new RuntimeException("Current solution should be set at the beggining");
		return currentSolutionObj.getRuntimeInfo().getSolutionFolder();
	}
	
	@Override
	public String getSolutionName() {
		SolutionObject currentSolutionObj = currentSolution.get();
		if(currentSolutionObj == null)
			return defaultRuntimeInfo.getSolutionName();
		return currentSolutionObj.getRuntimeInfo().getSolutionName();
	}

	@Override
	public String getSolutionsBaseFolder() {
		return defaultRuntimeInfo.getSolutionsBaseFolder();
	}

	@Override
	public boolean isDebugMode() {
		return defaultRuntimeInfo.isDebugMode();
	}

	@Override
	public boolean isWebAppMode() {
		return defaultRuntimeInfo.isWebAppMode();
	}

	@Override
	public void registerContextInitializationListener(
			IContextInitializationListener listener) {
		defaultRuntimeInfo.registerContextInitializationListener(listener);
	}

	@Override
	public void registerCustomFactory(ICustomFactory factory) {
		defaultRuntimeInfo.registerCustomFactory(factory);
	}

	@Override
	protected String resolveSolutionRelativePath(String relativePath) {
		SolutionObject currentSolutionObj = currentSolution.get();
		if(currentSolutionObj == null)
			throw new RuntimeException("Current solution should be set at the beggining");
		return currentSolutionObj.getRuntimeInfo().resolveSolutionRelativePath(relativePath);
	}

	@Override
	public Object getBean(String beanName) {
		if(solutionBeansNames.contains(beanName)) {
			SolutionObject currentSolutionObj = currentSolution.get();
			if(currentSolutionObj == null)
				throw new RuntimeException("Current solution should be set at the beggining");
			Object bean = currentSolutionObj.getSolutionBean(beanName);
			if(bean == null) {
				if(solutionBeanCreator == null)
					solutionBeanCreator = (MultitenantSolutionBeanCreator) defaultRuntimeInfo.getBean("multitenantObjectCreator");
				bean = solutionBeanCreator.getBean(beanName);
				currentSolutionObj.addSolutionBean(beanName, bean);
			}
			return bean;
		}
		else
			return defaultRuntimeInfo.getBean(beanName);
	}
	
	private void setCurrentSolution(String solutionName)
	{
		if(!loadedSolutions.containsKey(solutionName))
			loadSoluion(solutionName);
		else
			currentSolution.set(loadedSolutions.get(solutionName));
	}

	/**
	 * Contains all required information about loaded solution.
	 * @author Maxim Donde
	 *
	 */
	private class SolutionObject
	{
		private RuntimeInfo solutionRuntimeInfo;
		private Map<String, Object> solutionBeans;
		
		public SolutionObject(RuntimeInfo runtimeInfo)
		{
			this.solutionRuntimeInfo = runtimeInfo;
			solutionBeans = new HashMap<String, Object>();
		}
		
		public void addSolutionBean(String beanName, Object beanObj) 
		{
			solutionBeans.put(beanName, beanObj);
		}
		
		public Object getSolutionBean(String beanName)
		{
			return solutionBeans.get(beanName);
		}
		
		public RuntimeInfo getRuntimeInfo()
		{
			return solutionRuntimeInfo;
		}
	}
}

/**
 * Internal object to create stateful beans
 * @author Maxim Donde
 *
 */
class MultitenantSolutionBeanCreator implements BeanFactoryPostProcessor {

	private ConfigurableListableBeanFactory factory;

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory factory)
			throws BeansException {
		this.factory = factory;
	}

	public Object getBean(String beanName) {
		return factory.createBean(factory.getType(beanName));
	}
}
