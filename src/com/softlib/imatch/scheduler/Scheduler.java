package com.softlib.imatch.scheduler;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.xml.DOMConfigurator;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import com.softlib.imatch.ConsoleAppRuntimeInfo;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.common.configuration.IConfigurationObject;
import com.softlib.imatch.common.configuration.IConfigurationResource;
import com.softlib.imatch.common.configuration.IConfigurationResourceLoader;
import com.softlib.imatch.matcher.SearcherConfiguration;

public class Scheduler
{
	private IConfigurationObject config;
	static Lock lock = new ReentrantLock();
	public Scheduler(boolean consoleApp)
	{
		if(consoleApp)
			ConsoleAppRuntimeInfo.init(null);
		DOMConfigurator.configure(RuntimeInfo.getCurrentInfo().getRealPath("/{SolutionConfigFolder}/log4j-console.xml"));
		IConfigurationResourceLoader loader = (IConfigurationResourceLoader) RuntimeInfo.getCurrentInfo().getBean("xmlConfigurationResourceLoader");
		IConfigurationResource resource = loader.loadResource("xml:///{SolutionConfigFolder}/matcher.xml;//searcher");
		config = resource.getConfigurationObject(SearcherConfiguration.class);
		//TODO Quartz Shutdown
	}
	
	public void start()
	{
        org.quartz.Scheduler scheduler;
		try {
			//TODO move quartz.properties under web-inf directory
			scheduler = StdSchedulerFactory.getDefaultScheduler();
			scheduler.start();
			
			Set<String> allObjectIds = config.getAllObjects();
			ExtractJob.setObjectIds(allObjectIds);
			
			for(String objectId : allObjectIds) {
				JobDetail indexJob = new JobDetail("indexJob" + objectId, "indexGroup", IndexJob.class);
				indexJob.getJobDataMap().put("objectId", objectId);
				JobDetail extractJob = new JobDetail("extractJob" + objectId, "extractGroup", ExtractJob.class);
				extractJob.getJobDataMap().put("objectId", objectId);
				//Create a trigger for this job according to configuration 
				//Note, the repeat interval is in milliseconds while our configuration is in seconds
				GregorianCalendar indexCalendar = new GregorianCalendar();
				indexCalendar.add(Calendar.SECOND, (Integer)config.getProperty(objectId, "indexInitialInterval"));
				Date indexStartDate = indexCalendar.getTime();
				Date indexEndDate = new GregorianCalendar(2040, 0, 0).getTime();
				Trigger indexTrigger = new SimpleTrigger("indexTrigger" + objectId, "indexGroup", indexStartDate, indexEndDate, SimpleTrigger.REPEAT_INDEFINITELY, (Integer)config.getProperty(objectId, "indexIntervalSeconds") * 1000);
	
				GregorianCalendar extractCalendar = new GregorianCalendar();
				extractCalendar.add(Calendar.SECOND, (Integer)config.getProperty(objectId, "extractInitialInterval"));
				Date extractStartDate = extractCalendar.getTime();
				Date extractEndDate = new GregorianCalendar(2040, 0, 0).getTime();
				Trigger extractTrigger = new SimpleTrigger("extractTrigger" + objectId, "extractGroup", extractStartDate, extractEndDate, SimpleTrigger.REPEAT_INDEFINITELY, (Integer)config.getProperty(objectId, "extractIntervalSeconds") * 1000);

				// Schedule the job with the trigger
				scheduler.scheduleJob(extractJob, extractTrigger);
				scheduler.scheduleJob(indexJob, indexTrigger);
			}
		} catch (SchedulerException e) {
			 System.out.println("Error running the indexer " + e.getMessage());
		}
	}
}
