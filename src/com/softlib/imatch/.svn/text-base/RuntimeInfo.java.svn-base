package com.softlib.imatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;

import com.softlib.imatch.common.HibernateUtils;
import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.common.SessionMode;
import com.softlib.imatch.common.StringUtils;
import com.softlib.imatch.common.TicketTracker;
import com.softlib.imatch.common.cache.ICacheManager;
import com.softlib.imatch.common.configuration.IConfigurationResource;
import com.softlib.imatch.common.configuration.IConfigurationResourceLoader;
import com.softlib.imatch.common.configuration.XMLConfigurationResourceLoader;
import com.softlib.imatch.matcher.lucene.LuceneSearcher;

/**
 * This class represents a server runtime environment based on web application context the server runs on.
 * Provides convenient access to Spring and custom beans as well as server configuration parameters. 
 * @author Maxim Donde
 *
 */
public abstract class RuntimeInfo implements IContextInitializationListener
{
	protected static RuntimeInfo runtimeInfo;
	private BeanFactory beanFactory;
	private HashMap<String, ICustomFactory> registeredFactories;
	protected ServerConfiguration serverConfig;
	protected Map<String, ServerState> internalStates;
	private List<IContextInitializationListener> listeners = new ArrayList<IContextInitializationListener>();
	private String solutionFolder;
	private HibernateUtils hibernate;
	protected static String rootDir;
	
	protected final static Logger log = Logger.getLogger(RuntimeInfo.class);
	
	protected RuntimeInfo()
	{
		registeredFactories = new HashMap<String, ICustomFactory>();
		internalStates = new HashMap<String, ServerState>();
		//listeners.add(this);
	}
	
	/**
	 * Initializes runtime info for the web application the server runs on.
	 * The initialization step consists of the following steps:
	 * 1. Initialize RuntimeInfo without Spring using RuntimeInfoConfigListener
	 * 2. Initialize Spring context using SpringConfigListener
	 * 3. Load all beans as part of the spring initialization
	 * 4. Load server config (lazy loading)
	 * 5. Bean factory is initialized (lazy) 
	 * Note, during the spring initialization it is impossible to retrieve other Spring beans (use constructor args instead)
	 * @param ctx - the servlet context of the web application the server runs on
	 */
	public static void init(ServletContext ctx)
	{
		if(runtimeInfo != null)
			//The info already initialized
			return;
		if(ctx == null)
			runtimeInfo = new ConsoleAppRuntimeInfo();
		else
			runtimeInfo = new WebAppRuntimeInfo(ctx);
		runtimeInfo.contextInitialized();
		DOMConfigurator.configure(runtimeInfo.getRealPath("/{SolutionConfigFolder}/" + runtimeInfo.getLog4JConfigFile()));		
		LogUtils.info(log, "Server config loaded successfully, server version is %s", runtimeInfo.serverConfig.getVersion());
	}
	
	/**
	 * Closes all open connections and frees resources before server shutdown
	 * @throws MatcherException
	 */
	@SuppressWarnings("unchecked")
	public void destroy() throws MatcherException
	{
		try {
			finishThread();
			ICacheManager<ITicket> cache = (ICacheManager<ITicket>) runtimeInfo.getBean("cacheManager");
			cache.destroy();
			TicketTracker tracker = (TicketTracker) runtimeInfo.getBean("ticketTracker");
			tracker.destroy();
			hibernate.shutdown();
			LuceneSearcher searcher = (LuceneSearcher) RuntimeInfo.getCurrentInfo().getBean("lucene.searcher");
			searcher.shutdown();
			runtimeInfo = null;
		}
		catch(Exception e) {
			throw new MatcherException("Error occured during shutdown, the error is " + e.getMessage());
		}
	}
	
	public static RuntimeInfo getCurrentInfo()
	{
		if(runtimeInfo == null)
			throw new RuntimeException("RuntimeInfo not initialized yet");
		return runtimeInfo;
	}
	
	public static boolean isInitialized() {
		return runtimeInfo != null;
	}

	/**
	 * Registers given object as a listener to context initialization event
	 * Note: this event fires only once after the Spring context is initialized
	 * Since we used lazzy initialization for the Spring context, it will be initialized first time getBean is called
	 * For this reason, all listeners should be registered before first getBean is called i.e. in constructor during Spring context initialization.
	 * Don't call this function from non-spring objects, neither call it from methods other than constructor!
	 * @param listener
	 */
	public void registerContextInitializationListener(IContextInitializationListener listener)
	{
		listeners.add(listener);
	}
	
	public ServerState getInternalState(String objectId)
	{
		ServerState internalState = null;
		synchronized (this) {
			internalState = internalStates.get(objectId);
			if(internalState == null) {
				//Try to load from the repository
				Session session = getHibernate().acquireSession(SessionMode.READONLY);
				try {
					Query query = session.createQuery("from ServerState where objectId=:objectId");
					query.setString("objectId", objectId);
					internalState = (ServerState)query.uniqueResult();
					internalStates.put(objectId, internalState);
				}
				finally {
					getHibernate().releaseSession(session);
				}				
			}
			if(internalState == null) {
				//No internal state found, create an empty state to be populated later
				internalState = new ServerState(objectId);
				internalStates.put(objectId, internalState);
			}
		}
		return internalState;			
	}
	/**
	 * Returns a full path to a file given solution relative path.
	 * Solution relative path is in form /{SolutionFolder}/file.txt
	 * @param path - web application relative path to be converted
	 * @return
	 */
	public String getRealPath(String path)
	{
		if(path.contains("{") && serverConfig != null && serverConfig.getSolutionsBaseFolder() != null) {
			String solutionsBaseFolder = serverConfig.getSolutionsBaseFolder();
			return solutionsBaseFolder + resolveSolutionRelativePath(path);
		}
		else
			return getRealPathInternal(path);		
	}

	public String getRootDir()
	{
		if(StringUtils.isEmpty(rootDir))
			return getRootDirInternal();
		return rootDir;
	}
	protected abstract String getRootDirInternal();
	
	public static void setRootDir(String rootDir)
	{
		RuntimeInfo.rootDir = rootDir;
	}

	public String getSolutionsBaseFolder()
	{
		String baseFolder = serverConfig.getSolutionsBaseFolder();
		if(baseFolder == null)
			baseFolder = getRootDir();
		baseFolder = baseFolder + "\\Solutions";
		return baseFolder;
	}
	
	public String getSolutionName()
	{
		return serverConfig.getSolution();
	}
	
	public String getISolveRootFolder()
	{
		return serverConfig.getiSolveRootFolder();
	}

	protected abstract String getRealPathInternal(String path);
	
	protected String getSolutionFolder()
	{
		synchronized (this) {
			if(solutionFolder == null) {
				solutionFolder = "/Solutions/";
				if(serverConfig.getSolution() != null)
					solutionFolder += serverConfig.getSolution() + "/";
				else
					solutionFolder += "Softlib/";
			}
		}
		return solutionFolder;
	}
	
	/**
	 * Converts web application relative path to physical absolute path
	 * @param relativePath
	 * @return
	 */
	protected String resolveSolutionRelativePath(String relativePath)
	{
		if(!relativePath.contains("{"))
			return relativePath;
		StringBuffer sb = new StringBuffer();
		for(SolutionFolder solutionFolder : SolutionFolder.solutionFolders) {
			if(solutionFolder.resolveSolutionFolderPath(relativePath, getSolutionFolder(), sb))
				return sb.toString();
		}
		return relativePath;
	}
	
	/**
	 * Returns the url of the server as specified in server.xml
	 * @return
	 */
	public String getServerUrl()
	{
		String serverUrl = serverConfig.getServerUrl();
		if(serverUrl == null)
			return "http://localhost:8080/iMatch";
		else
			return serverUrl;
	}
	
	/**
	 * Return true if the server is running in web (online) mode, false otherwise (the server is in offline mode - extraction / index) 
	 * @return
	 */
	public boolean isWebAppMode() {
		return this instanceof WebAppRuntimeInfo;
	}

	/**
	 * Return true if the server is in debug mode for jsp debugging
	 * @return
	 */
	public boolean isDebugMode() {
		return serverConfig.isDebugMode();
	}
	
	public void registerCustomFactory(ICustomFactory factory)
	{
		registeredFactories.put(factory.getNamespace(), factory);
	}
	
	/**
	 * Performs lookup for a bean with a given name.
	 * The lookup algorithm is as follows:
	 * 1. check if the bean name contains . Dot is used as a special delimiter character to identify namespace
	 * 2. if . is present the first part is taken as a bean namespace and the bean is retrieved from the custom factory
	 * 3. all other beans are retrieved from Spring   
	 * @param beanName
	 * @return
	 * @throws RuntimeException if the bean not found 
	 */
	public Object getBean(String beanName)
	{
		Object result = null;
		int dotIdx = beanName.indexOf('.'); 
		if(dotIdx > -1)
		{
			String factoryName = beanName.substring(0, dotIdx);
			ICustomFactory factory = registeredFactories.get(factoryName);
			if(factory != null && beanName.length() > dotIdx + 1)
			{
				//Try to retrieve the bean from custom factory. The name of the bean doesn't contain the factory name
				String customBeanName = beanName.substring(dotIdx + 1);
				result = factory.getBean(customBeanName);
			}
		}
		if(result == null)
		{
			try {
				synchronized (this) {
					//The bean factory couldn't be retrieved during the init as a Spring is not initialized yet
					//Do it first time it is required
					if(beanFactory == null)
					{
						beanFactory = createSpringContext();
						for(IContextInitializationListener listener : listeners) {
							System.out.println("Initializing listener " + listener.getClass().getSimpleName());
							listener.contextInitialized();
						}
					}
				}
			}
			catch(Exception be) {
				LogUtils.warn(log, "Spring initialization failed due to: %s", be.getMessage());
				throw new RuntimeException("Spring initialization failed " + be.getMessage());
			}
			result = beanFactory.getBean(beanName);
		}
		return result;
	}

	public HibernateUtils getHibernate()
	{
		synchronized (this) {
			if(hibernate == null)
				hibernate = HibernateUtils.createHibernate(getRealPath("/{SolutionDBFolder}/imatch.db"));
			return hibernate;
		}
	}
	/**
	 * Initializes new execution thread
	 */
	public void startThread() 
	{
		startThread(null);
	}	

	/**
	 * Initializes new execution thread
	 */
	public void startThread(ThreadInfo info) 
	{
	}	

	/**
	 * Finishes execution thread and saves all pending state
	 */
	public void finishThread() {
		for(String objectId : internalStates.keySet()) {			
			Session session = getHibernate().acquireSession(SessionMode.READ_WRITE);
			try {
				saveInternalState(objectId, session);
			}
			catch(Exception e) {
				LogUtils.fatal(log, e, "Unable to save internal state, reason: %s", e.getMessage());
			}
			finally {
				getHibernate().releaseSession(session);
			}
		}
	}
	
	public void saveInternalState(String objectId, Session session) throws HibernateException {
		ServerState internalState = internalStates.get(objectId);
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			session.saveOrUpdate(internalState);
			tx.commit();
		}
		catch(HibernateException e) {
			if(tx != null)
				tx.rollback();
			throw e;
		}
	}

	public void contextInitialized() {
		//Load server config
		IConfigurationResourceLoader loader = new XMLConfigurationResourceLoader(); //(IConfigurationResourceLoader) getBean("xmlConfigurationResourceLoader");
		IConfigurationResource resource = loader.loadResource("xml:///WEB-INF/server.xml;//config");
		serverConfig = (ServerConfiguration) resource.getCustomConfiguration(ServerConfiguration.class);
	}
	
	protected abstract ApplicationContext createSpringContext();
	
	protected abstract String getLog4JConfigFile();	
}
