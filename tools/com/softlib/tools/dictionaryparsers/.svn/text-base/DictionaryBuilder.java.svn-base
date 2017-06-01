package com.softlib.tools.dictionaryparsers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.hibernate.Session;

import com.softlib.imatch.ITicket;
import com.softlib.imatch.ITicketProvider;
import com.softlib.imatch.ITicketRetrievedCallback;
import com.softlib.imatch.MatcherException;
import com.softlib.imatch.MultitenantThreadInfo;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.ServerState;
import com.softlib.imatch.StageMngr;
import com.softlib.imatch.TicketProviderFactory;
import com.softlib.imatch.StageMngr.Stage;
import com.softlib.imatch.common.FileUtils;
import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.common.ReadWriteLock;
import com.softlib.imatch.common.ReadWriteLockMode;
import com.softlib.imatch.common.ScriptRunner;
import com.softlib.imatch.common.SessionMode;
import com.softlib.imatch.common.StringUtils;
import com.softlib.imatch.common.progressnotifier.DefaultProgressNotifier;
import com.softlib.imatch.common.progressnotifier.IProgressNotificationListener;
import com.softlib.imatch.common.progressnotifier.IProgressNotifier;
import com.softlib.imatch.dictionary.TechnicalDictionary;
import com.softlib.imatch.dictionary.Vendor;
import com.softlib.imatch.dictionary.VendorList;
import com.softlib.imatch.matcher.SearcherConfiguration;

//TODO extract base class between DictionaryBuilder and Indexer
public class DictionaryBuilder implements ITicketRetrievedCallback, IProgressNotifier
{
	private SearcherConfiguration searchConfig; 
	private ScheduledThreadPoolExecutor threadExecutor;
	private int totalProcessedCount = 0;
	private double newRecoveryPosition = -1;
	private ProgressStatus progressStatus = ProgressStatus.UNKNOWN;
	private Object lock = new Object();
	private BlockingQueue<ITicket> ticketsQueue;
	private ITicket currentTicket;
	private List<ExtractThread> threads = new ArrayList<ExtractThread>();
	private DefaultProgressNotifier notifier = new DefaultProgressNotifier();
	
	private Map<StageMngr.Stage,List<IDictionaryParser>> parsersByStage =
		new HashMap<StageMngr.Stage,List<IDictionaryParser>>();
	
	private TechnicalDictionary dictionary;
	private DictionaryBuilderState builderState;
	private static Logger log = Logger.getLogger(DictionaryBuilder.class); 
	
	private ReadWriteLock __saveLock = ReadWriteLock.createLock(ReadWriteLockMode.WITH_TIMER, "saveLock");
	private String objectId;
	private ExtractionContext ctx;
	private ITicketProvider ticketProvider;	
	
	//10 Minutes inactivity, after 10 minutes, the process will stop
	private static final int MAX_INACTIVITY_INTERVAL_SECONDS = 10 * 60;

	DictionaryBuilder(String objectId, SearcherConfiguration searchConfig) {
		this.searchConfig = searchConfig;
		this.objectId = objectId;
		if(searchConfig.getNumThreadsInPool() < 1)
			throw new IllegalArgumentException("Should be at least 1 thread in the pool");

		ticketsQueue = new ArrayBlockingQueue<ITicket>(searchConfig.getExtractBufferSize());	
		//One for Notification thread
		threadExecutor = new ScheduledThreadPoolExecutor(searchConfig.getNumThreadsInPool() + 1);		
		for(int i = 0; i < searchConfig.getNumThreadsInPool(); ++i) {
			ExtractThread thread = new ExtractThread(i, RuntimeInfo.getCurrentInfo().getSolutionName());
			threads.add(thread);
		}
	}

	void setParsers(List<IDictionaryParser> parsers,StageMngr.Stage stage) {
		parsersByStage.put(stage,parsers);
	}
		
	@SuppressWarnings("deprecation")
	public ProgressStatus buildDictionary(ExtractionContext ctx) {
		this.ctx = ctx;
		
		ServerState lastInternalState = 
			RuntimeInfo.getCurrentInfo().getInternalState(objectId);
		 builderState = new DictionaryBuilderState(lastInternalState);
				
		if(progressStatus == ProgressStatus.IN_PROGRESS) {
			//Previous index is still running, skip this run
			LogUtils.warn(log, "Previous index is still running, skip this run");
			return ProgressStatus.IN_PROGRESS;
		}
		LogUtils.info(log,"Start dictionary %s for object %s", StageMngr.instance().getStage().toString(), objectId);
		long lastRunTime = builderState.getLastRun();	
		try {
			initRun();
		}
		catch(MatcherException e) {
			LogUtils.fatal(log, e, "Unexpected error during initialization of scheduled extraction process");
			return ProgressStatus.PROCESS_FAILED;
		}
		
		ticketProvider = (ITicketProvider) RuntimeInfo.getCurrentInfo().getBean(TicketProviderFactory.getProviderId(objectId));
		if(ticketProvider instanceof IProgressNotifier)
		{
			for(IProgressNotificationListener listener : notifier.getListeners())
				((IProgressNotifier)ticketProvider).registerProgressNotificationListener(listener);
		}
		try {
			ticketProvider.getChangedTickets(lastRunTime, this);
		}
		catch(Exception e)
		{
			progressStatus = ProgressStatus.CONNECTOR_FAILED;
			LogUtils.error(log, "Extraction failed due to connection error %s", e.getMessage());
			//TODO bug, release notification thread and session lock
			return ProgressStatus.PROCESS_FAILED;
		}
		while(!ticketsQueue.isEmpty() && progressStatus != ProgressStatus.PROCESS_FAILED) {
			//10 seconds
			try {				
				Thread.sleep(1000 * 10);
			} catch (InterruptedException e) {
				LogUtils.error(log, "The main thread was interrupted due to %s", e.getMessage());
			}
		}
		if(progressStatus == ProgressStatus.IN_PROGRESS)
			progressStatus = ProgressStatus.COMPLETED;
		try {
			//Give last tickets chance to finish
			Thread.sleep(60 * 1000);
		}
		catch(Exception e){}
		LogUtils.info(log, "Dictionary %s for object %s, all terms extracted, completing", StageMngr.instance().getStage().toString(), objectId);

		//Save dictionary only if at least one ticket processed
		boolean cont = totalProcessedCount > 0;
		Session session = RuntimeInfo.getCurrentInfo().getHibernate().acquireSession(SessionMode.READ_WRITE);
		if (cont) {
			try {
				__saveLock.lockWrite();
				dictionary.save(session);
				ticketProvider.save();

			}
			catch (Exception e) {
				LogUtils.error(log, "Unable to save dictionary due to %s", e.getMessage());
				progressStatus = ProgressStatus.PROCESS_FAILED;	
				cont = false;
			}
			finally {
				__saveLock.unlockWrite();
			}
		}

		if (cont && StageMngr.instance().isStage(Stage.PostExtract)) {
			try {
				//TODO check there, running sql scripts in extract step can cause Hibernate exception
				//Clean SQL scripts should be run both in extraction & post extraction phases
				ScriptRunner scriptRunner = new ScriptRunner(session.connection());
				scriptRunner.runScript(RuntimeInfo.getCurrentInfo().getRealPath("/{SolutionConfigFolder}/product.sql"));
				File f = new File(RuntimeInfo.getCurrentInfo().getRealPath("/{SolutionConfigFolder}/solution.sql"));
				if(f.exists())
					scriptRunner.runScript(RuntimeInfo.getCurrentInfo().getRealPath("/{SolutionConfigFolder}/solution.sql"));
				session.connection().commit();
				LogUtils.info(log, "Dictionary %s for object %s, Cleaning scripts executed successfully", StageMngr.instance().getStage().toString(), objectId);
			}
			catch (Exception e) {
				LogUtils.error(log, "Unable to commit dictionary due to %s", e.getMessage());
				progressStatus = ProgressStatus.PROCESS_FAILED;				
				cont=false;
			}
		}
		
		try {
			dictionary.unloadDictionary();
			RuntimeInfo.getCurrentInfo().getHibernate().releaseSession(session);
			synchronized (lock) {
				lock.notify();				
			}
		}
		catch(Exception e) {
			LogUtils.warn(log, "Unable to quit notification thread due to %s", e.getMessage());
		}		

		if(progressStatus == ProgressStatus.COMPLETED) {
			lastRunTime = new Date().getTime();
			builderState.setLastRun(lastRunTime);
			builderState.setRecoveryPosition(-1);
			//Clean relations position after extraction
			builderState.setRelationsRecoveryPosition(-1);
			RuntimeInfo.getCurrentInfo().saveInternalState(objectId, session);
			for(IDictionaryParser parser : parsersByStage.get(StageMngr.instance().getStage()))
				parser.end();
			LogUtils.info(log, "Dictionary %s for object %s finished successfully, total %d tickets processed", StageMngr.instance().getStage().toString(), objectId, totalProcessedCount);
			if(totalProcessedCount == 0)
				return ProgressStatus.COMPLETED_NO_CHANGES;
			else				
				return ProgressStatus.COMPLETED;
		}
		else {
			if(newRecoveryPosition < builderState.getRecoveryPosition() || builderState.getRecoveryPosition() == -1) {
				builderState.setRecoveryPosition(newRecoveryPosition);
				//Clean relations position after extraction
				builderState.setRelationsRecoveryPosition(-1);
				LogUtils.warn(log, "Dictionary %s for object %s failed after processing %d tickets, recovery position was set to %f", StageMngr.instance().getStage().toString(), objectId, totalProcessedCount, newRecoveryPosition);
			}
			else {
				LogUtils.warn(log, "Dictionary %s for object %s failed, recovery position remains %f", StageMngr.instance().getStage().toString(), objectId, builderState.getRecoveryPosition());
			}
			progressStatus = ProgressStatus.COMPLETED;
			return ProgressStatus.PROCESS_FAILED;
		}
	}

	private void initRun() throws MatcherException {
		progressStatus = ProgressStatus.IN_PROGRESS;
		newRecoveryPosition = -1;
		totalProcessedCount = 0;
		ticketsQueue.clear();
		//TODO check here
		threadExecutor.getQueue().clear();
		threadExecutor.execute(new NotificationThread(RuntimeInfo.getCurrentInfo().getSolutionName()));
		for(ExtractThread thread : threads) {
			thread.setRecoveryPosition(builderState.getRecoveryPosition());
			threadExecutor.execute(thread);
		}
		Session session = RuntimeInfo.getCurrentInfo().getHibernate().acquireSession(SessionMode.READONLY);
		if(!ctx.getCompanyName().equals("")) {
			VendorList vendors = VendorList.getInstance();
			vendors.addVendor(new Vendor(ctx.getCompanyName()));
		}
		dictionary = (TechnicalDictionary) RuntimeInfo.getCurrentInfo().getBean("dictionary");
		dictionary.loadDictionary(session,false);
		for(IDictionaryParser parser : parsersByStage.get(StageMngr.instance().getStage()))
			parser.init();
		RuntimeInfo.getCurrentInfo().getHibernate().releaseSession(session);
	}
	
	public void ticketRetrieved(ITicket ticket) {
		try {
			if(progressStatus == ProgressStatus.IN_PROGRESS && ctx.getMaxNumTickets() > totalProcessedCount) {
				//Patch, for some reason sometimes the read-only session lock is not released and if the thread get stuck on offer it doesn't releases the lock
				RuntimeInfo.getCurrentInfo().getHibernate().releaseSession(null);
				//TODO refactor, find general approach here
				Object tmpUrl = ticket.getField("Url");
				if(tmpUrl != null) {
					String url = tmpUrl.toString();
					if(url.endsWith(".pdf")) {
						//Skip pdf files for dictionary
						return;
					}
				}
				if(!ticketsQueue.offer(ticket, MAX_INACTIVITY_INTERVAL_SECONDS, TimeUnit.SECONDS))
					progressStatus = ProgressStatus.PROCESS_FAILED;
			}
		} catch (InterruptedException e) {
			throw new RuntimeException("Unexpected interruption"); 
		}
	}	
	/**
	 * Returns recovery position (id) for the given ticket if ticket id is numeric or -1 otherwise
	 * Important, for recovery position to work correctly the tickets should be order by ticket id in Descending order.
	 * @param ticket
	 * @return
	 */
	private double getRecoveryPosition(ITicket ticket) {
		double lastId = -1;
		try {
			String lastIdStr = ticket.getId();
			if(lastIdStr != null)
				lastId = Double.parseDouble(lastIdStr);
		}
		catch(NumberFormatException ne) {
			//Do nothing
		}
		return lastId;
	}
	
	private class NotificationThread implements Runnable {
		
		private String solutionName;
		
		public NotificationThread(String solutionName) {
			this.solutionName = solutionName;
		}

		public void run() {
			RuntimeInfo.getCurrentInfo().startThread(new MultitenantThreadInfo(solutionName));
			while(progressStatus != ProgressStatus.COMPLETED)
			{
				synchronized (lock) {
					try {
						Date startDate = new Date();
						long waitInterval = MAX_INACTIVITY_INTERVAL_SECONDS * 1000;
						lock.wait(waitInterval);
						if(progressStatus == ProgressStatus.COMPLETED)
							return;
						long interval = new Date().getTime() - startDate.getTime();
						if(interval >= waitInterval && ctx.getMaxNumTickets() > totalProcessedCount) {
							//No updates from extraction threads for 1 minute, consider process stuck
							LogUtils.error(log, "Notification thread didn't receive any updates for 10 minutes, consider process stuck");
							progressStatus = ProgressStatus.PROCESS_FAILED;
						}

					} catch (InterruptedException e) {
						//Main thread asks for shutdown
						return;
					}
				}
				if(progressStatus == ProgressStatus.PROCESS_FAILED) {
					LogUtils.error(log, "After %d tickets processed, processing failed on ticket %s", totalProcessedCount, currentTicket);
					__saveLock.lockWrite();
					Session session = RuntimeInfo.getCurrentInfo().getHibernate().acquireSession(SessionMode.READ_WRITE);
					try {
						dictionary.save(session);
						ticketProvider.save();
						newRecoveryPosition = getRecoveryPosition(currentTicket);
					} 
					catch (Exception e) {
						LogUtils.error(log, "Unable to save the dictionary due to %s", e.getMessage());
					}
					RuntimeInfo.getCurrentInfo().getHibernate().releaseSession(session);
					__saveLock.unlockWrite();
					ticketsQueue.clear();
					return;
				}
				else {  
					totalProcessedCount++;
					if(totalProcessedCount % searchConfig.getExtractBufferSize() == 0) {
						__saveLock.lockWrite();
						Session session = RuntimeInfo.getCurrentInfo().getHibernate().acquireSession(SessionMode.READ_WRITE);
						try {
							dictionary.save(session);
							ticketProvider.save();
							newRecoveryPosition = getRecoveryPosition(currentTicket);
							builderState.setRecoveryPosition(newRecoveryPosition);
							builderState.setRelationsRecoveryPosition(-1);
							RuntimeInfo.getCurrentInfo().saveInternalState(objectId, session);
							notifyProgress(1, String.format("%d more tickets processed", searchConfig.getExtractBufferSize()), totalProcessedCount, -1);
							LogUtils.info(log, "%d more tickets processed, from the beggining %d are done",searchConfig.getExtractBufferSize(), totalProcessedCount);
							System.gc();
						} 
						catch(Exception e) {
							LogUtils.error(log, "Unable to flush temp dictionary due to %s", e.getMessage());
						}
						RuntimeInfo.getCurrentInfo().getHibernate().releaseSession(session);
						__saveLock.unlockWrite();
					}
				}
			}
			RuntimeInfo.getCurrentInfo().finishThread();
		}
	}
	
	private class ExtractThread implements Runnable {
		private static final int MAX_SEQUANTIAL_ERRORS = 3;
		private double lastRecoveryPosition;
		private int idx;
		private int errorsCount = 0;
		private String solutionName;
		
		public ExtractThread(int idx, String solutionName) {
			this.idx = idx;
			this.solutionName = solutionName;
		}

		public void setRecoveryPosition(double recoveryPosition)
		{
			lastRecoveryPosition = recoveryPosition;
		}
		
		public void run() {
			ITicket ticket = null;
			boolean stop = false;
			try {
				RuntimeInfo.getCurrentInfo().startThread(new MultitenantThreadInfo(solutionName));
				while(progressStatus == ProgressStatus.IN_PROGRESS || progressStatus == ProgressStatus.CONNECTOR_FAILED) {
					ticket = ticketsQueue.poll(MAX_INACTIVITY_INTERVAL_SECONDS, TimeUnit.SECONDS);						
					if(ticket == null) {
						if(progressStatus == ProgressStatus.IN_PROGRESS && handleError(null))
							break;
						else
							continue;
					}
					Date startDate = new Date();
					__saveLock.lockRead();
					try {
						if(ctx.getMaxNumTickets() <= totalProcessedCount)
							LogUtils.debug(log, "Ticket %s was skipped, enough tickets already processed", ticket);
						else if(lastRecoveryPosition == -1 || getRecoveryPosition(ticket) < lastRecoveryPosition) {
							for(IDictionaryParser parser : parsersByStage.get(StageMngr.instance().getStage())) {
								LogUtils.debug(log, "Thread #%d Parsing ticket %s", idx, ticket);
								parser.parse(ticket);
								LogUtils.debug(log, "Thread #%d Ticket %s parsed successfully", idx, ticket);
							}			
						}
						else
							LogUtils.debug(log, "Ticket %s was skipped as it before lastRecoveryPosition", ticket);
						__saveLock.unlockRead();
						Date endDate = new Date();
						LogUtils.info(log, "Ticket %s was processed in %d milliseconds", ticket, endDate.getTime() - startDate.getTime());
						synchronized (lock) {
							currentTicket = ticket;
							lock.notify();
						}			
						errorsCount = 0;
					}
					catch (Exception e) {
						__saveLock.unlockRead();
						e.printStackTrace();
						LogUtils.error(log, "Thread #%d, Unable to extract terms from ticket %s, reason: %s",
								idx, ticket.getId(), e.getMessage());
						stop = handleError(ticket);
					}
					finally {
						ticket = null;						
					}
					if(stop)
						break;
				}
				RuntimeInfo.getCurrentInfo().finishThread();
			} 
			catch (InterruptedException e) {
				LogUtils.debug(log, "Extraction thread was interupted by the main thread");
			}
		}	
		
		private boolean handleError(ITicket ticket) {
			errorsCount ++;
			if(errorsCount >= MAX_SEQUANTIAL_ERRORS) {
				progressStatus = ProgressStatus.PROCESS_FAILED;
				synchronized (lock) {
					currentTicket = ticket;
					lock.notify();
				}
				return true;
			}
			return false;
		}
	}
	
	public enum ProgressStatus {
		UNKNOWN,
		CONNECTOR_FAILED,
		PROCESS_FAILED,
		IN_PROGRESS,
		COMPLETED, 
		COMPLETED_NO_CHANGES
	}

	//Notifier interface
	@Override
	public void notifyProgress(int level, String message, int processedCount,
			int remainingCount) {
		notifier.notifyProgress(level, message, processedCount, remainingCount);
	}

	@Override
	public void registerProgressNotificationListener(
			IProgressNotificationListener listener) {
		notifier.registerProgressNotificationListener(listener);
	}

	@Override
	public void unregisterProgressNotificationListener(
			IProgressNotificationListener listener) {
		notifier.unregisterProgressNotificationListener(listener);
	}
	
};
