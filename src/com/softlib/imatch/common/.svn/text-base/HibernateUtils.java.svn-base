package com.softlib.imatch.common;

import java.io.File;
import java.sql.SQLException;

import org.apache.commons.collections.ArrayStack;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

import com.softlib.imatch.RuntimeInfo;

/**
 * This class provides convenient way to manage Hibernate Session in the application
 * Note: Despite the fact the class is public it should not be accessed directly but via RuntimeInfo.getHibernate()
 * @author Maxim Donde
 *
 */
public class HibernateUtils {
	private SessionFactory sessionFactory;
	private static final String SQLiteDriver = "org.sqlite.JDBC";
	private String connectionUrl = null;
	private Session session;
	private ArrayStack currentMode = new ArrayStack();
	private SessionMode lastMode;

	private static ReadWriteLock sessionLock = ReadWriteLock.createLock(ReadWriteLockMode.WITH_TIMER, "sessionLock");

	protected static Logger log = Logger.getLogger(HibernateUtils.class);

	protected HibernateUtils(String dbFilePath)
	{
		try {
        	//TODO in development environment there are 2 databases (there is no such problem in production env). Can we use only one?
        	//INFO: using driver: org.sqlite.JDBC at URL: jdbc:sqlite:C:\Documents and Settings\Administrator\Workspaces\MyEclipse\.metadata\.me_tcat\webapps\iMatch\imatch.db
        	//INFO: using driver: org.sqlite.JDBC at URL: jdbc:sqlite:C:\Documents and Settings\Administrator\Workspaces\MyEclipse\iMatch/WebRoot/imatch.db
			Class.forName(SQLiteDriver);
       		connectionUrl = "jdbc:sqlite:" + dbFilePath;
        	AnnotationConfiguration config = new AnnotationConfiguration();
        	config.setProperty("hibernate.connection.driver_class", SQLiteDriver)
        	.setProperty("hibernate.connection.url", connectionUrl)
        	.setProperty("hibernate.dialect", "com.softlib.imatch.common.SQLiteDialect")
        	//Set to true if you need to see actual sql statements running
        	.setProperty("hibernate.show_sql", "false")
        	.setProperty("hibernate.connection.pool_size", "5")
        	//These properties are set to prevent database lock on the SQLite db. Change them very carefully...
        	.setProperty("hibernate.connection.autocommit", "true")
        	.setProperty("hibernate.cache.use_query_cache", "false")
        	.setProperty("hibernate.statement_cache.size", "0");
        	//.addAnnotatedClass(ServerState.class);
        	String hibernateConfigFilePath = RuntimeInfo.getCurrentInfo().getRealPath("/{SolutionConfigFolder}/hibernate.cfg.xml");
        	File hibernateConfigFile = new File(hibernateConfigFilePath);
        	sessionFactory = config.configure(hibernateConfigFile).buildSessionFactory();        	
        } catch (Throwable ex) {
            // Log exception! 
            throw new ExceptionInInitializerError(ex);
        }
	}

    public static HibernateUtils createHibernate(String dbFilePath)
    {
    	return new HibernateUtils(dbFilePath);
    }
	
	public Session acquireSession(SessionMode mode){		
		if(mode == SessionMode.READONLY)
			sessionLock.lockRead();
		else
			sessionLock.lockWrite();
		currentMode.push(mode);
		if(session == null){
			session = sessionFactory.openSession();
		}
		return session;
	}
	
	public Session acquireTempSession(SessionMode mode) 
	{
		if(mode == SessionMode.READONLY)
			sessionLock.lockRead();
		else
			sessionLock.lockWrite();
		currentMode.push(mode);
		return sessionFactory.openSession();
	}
	
	private synchronized SessionMode getMode() {
		if (!currentMode.isEmpty())
			lastMode = (SessionMode)currentMode.pop();
		return lastMode;
	}
	
	public void releaseSession(Session session) {
		if(getMode() == SessionMode.READONLY)
			sessionLock.unlockRead();
		else
			sessionLock.unlockWrite();
	}

	public void closeSession(){
		Session s = (Session)session;//.get();
		if(s != null) {
			s.close();
		}
		sessionLock.clear();
		session = null;
	}
    
    public void shutdown() throws SQLException
    {
    	session.close();
    	session = null;
     	sessionLock.cancel();
    	sessionFactory.close();
    }
}
