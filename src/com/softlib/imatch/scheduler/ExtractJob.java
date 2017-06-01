package com.softlib.imatch.scheduler;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.StageMngr;
import com.softlib.imatch.common.HibernateUtils;
import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.dictionary.TechnicalDictionary;
import com.softlib.tools.dictionaryparsers.DictionaryBuilder;
import com.softlib.tools.dictionaryparsers.DictionaryBuilderFactory;
import com.softlib.tools.dictionaryparsers.ExtractionContext;
import com.softlib.tools.dictionaryparsers.DictionaryBuilder.ProgressStatus;

public class ExtractJob implements Job {
	private static Logger log = Logger.getLogger(ExtractJob.class);

	private static Map<String,Integer> statusByObjectId = 
		new HashMap<String, Integer>();
	
	//Returns true if all objects are done and at least for one object there was changes and some tickets were processed
	private boolean isAllJobDone() {
		boolean doneWithChanges = false;
		for (String objectId : statusByObjectId.keySet() ) { 
			if (statusByObjectId.get(objectId) == -1)
				return false;
			if(statusByObjectId.get(objectId) == 1)
				doneWithChanges = true;
		}
		return doneWithChanges;
	}
	
	static public void setObjectIds(Set<String> objectIds) {
		for (String objectId : objectIds)
			statusByObjectId.put(objectId, -1);
	}
	
	public ExtractJob() {
	}

	public void execute(JobExecutionContext ctx) throws JobExecutionException {
		Scheduler.lock.lock();
		try {
			String objectId = (String) ctx.getMergedJobDataMap().get("objectId");
			if(objectId == null)
				throw new JobExecutionException("ObjectId parameter not provided in context");
			
			DictionaryBuilder builder = (DictionaryBuilder)RuntimeInfo.getCurrentInfo().getBean(DictionaryBuilderFactory.getBuilderId(objectId));
			RuntimeInfo.getCurrentInfo().startThread();
			LogUtils.info(log, "Start extraction for %s", objectId);
			ExtractionContext extractionCtx = new ExtractionContext();
			StageMngr.instance().setStage(StageMngr.Stage.Extract);
			ProgressStatus status = builder.buildDictionary(extractionCtx);
			LogUtils.info(log, "Extraction for %s completed with status %s", objectId, status.toString());
			if(status == ProgressStatus.COMPLETED || status == ProgressStatus.COMPLETED_NO_CHANGES) {
				LogUtils.info(log, "Start post extraction for %s", objectId);
				StageMngr.instance().setStage(StageMngr.Stage.PostExtract);
				status = builder.buildDictionary(extractionCtx);
				LogUtils.info(log, "Post extraction for %s completed with status %s", objectId, status.toString());
				if(status == ProgressStatus.COMPLETED)
					statusByObjectId.put(objectId, 1);
				if(status == ProgressStatus.COMPLETED_NO_CHANGES)
					statusByObjectId.put(objectId, 0);
			}			
			if (isAllJobDone()) {
				TechnicalDictionary dictionary = (TechnicalDictionary) RuntimeInfo.getCurrentInfo().getBean("dictionary");
				dictionary.loadDictionary(false);
				dictionary.updateTermsAfterFreq();
				LogUtils.info(log, "Terms relations updated successfully");
				dictionary.addComplexSynonym();
				LogUtils.info(log, "Patterns synonyms added successfully");
				//TechnicalTermSource personsNames = dictionary.addSource("Persons Names");
				//TechnicalTermSource changeSource = dictionary.addSource("Contain Person Name");
				//changeSource.setMaster();
				//dictionary.changeTermsContainSource(personsNames,changeSource);
				dictionary.save();
				LogUtils.info(log, "Dictionary extraction and processing finished successfully");
			}
			
		} 
		catch(Exception e) {
			throw new JobExecutionException("Unable to run dictionary extraction, reason " + e.getMessage(), e);
		}
		finally {
			RuntimeInfo.getCurrentInfo().finishThread();
			RuntimeInfo.getCurrentInfo().getHibernate().closeSession();
			Scheduler.lock.unlock();
		}
	}
	
};
