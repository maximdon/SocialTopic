package com.softlib.imatch.scheduler;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.StageMngr;
import com.softlib.imatch.common.HibernateUtils;
import com.softlib.imatch.dictionary.Wordnet;
import com.softlib.tools.fullindex.IndexContext;
import com.softlib.tools.fullindex.Indexer;
import com.softlib.tools.fullindex.IndexerFactory;

public class IndexJob implements Job {

	public IndexJob()
	{
	}
	
	public void execute(JobExecutionContext ctx) throws JobExecutionException
	{
		Scheduler.lock.lock();
		try {
			String objectId = (String) ctx.getMergedJobDataMap().get("objectId");
			if(objectId == null)
				throw new JobExecutionException("ObjectId parameter not provided in context");
			StageMngr.instance().setStage(StageMngr.Stage.Index);
			Indexer indexer = (Indexer)RuntimeInfo.getCurrentInfo().getBean(IndexerFactory.getIndexerId(objectId));
			RuntimeInfo.getCurrentInfo().startThread();
			Wordnet.getInstance().disableCheck();
			indexer.index(new IndexContext());
		} 
		catch(Exception e)
		{
			throw new JobExecutionException("Unable to run indexer, reason " + e.getMessage(), e);
		}
		finally {
			RuntimeInfo.getCurrentInfo().finishThread();
			RuntimeInfo.getCurrentInfo().getHibernate().closeSession();
			Scheduler.lock.unlock();
		}
	}
}
