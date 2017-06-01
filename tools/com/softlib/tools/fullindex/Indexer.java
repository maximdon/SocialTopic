package com.softlib.tools.fullindex;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.apache.log4j.Logger;
import org.hibernate.Session;

import com.softlib.imatch.ITicket;
import com.softlib.imatch.ITicketProvider;
import com.softlib.imatch.ITicketRetrievedCallback;
import com.softlib.imatch.MatcherException;
import com.softlib.imatch.MultitenantThreadInfo;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.ServerState;
import com.softlib.imatch.TicketProviderFactory;
import com.softlib.imatch.TicketState;
import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.common.SessionMode;
import com.softlib.imatch.common.progressnotifier.DefaultProgressNotifier;
import com.softlib.imatch.common.progressnotifier.IProgressNotificationListener;
import com.softlib.imatch.common.progressnotifier.IProgressNotifier;
import com.softlib.imatch.dictionary.TechnicalDictionary;
import com.softlib.imatch.matcher.ITicketsRepository;
import com.softlib.imatch.matcher.SearcherConfiguration;
import com.softlib.imatch.ticketprocessing.IProcessedTicket;
import com.softlib.imatch.ticketprocessing.ITicketProcessor;
import com.softlib.imatch.ticketprocessing.ProcessedTicket;
import com.softlib.imatch.ticketprocessing.TicketProcessor;
import com.softlib.imatch.ticketprocessing.IProcessedTicket.MatchMode;
import com.softlib.tools.dictionaryparsers.DictionaryBuilder.ProgressStatus;

/**
 * This class is responsible for indexing all tickets that was changed since lastRunTime.
 * Indexing mechanism supports recovery by updating recoveryPosition after each successful buffer processed.
 * Recovery mechanism requires that ticket ids are numeric otherwise it will be disabled. 
 * Indexing mechanism uses cursor mode for load changed tickets to prevent large number of tickets to be loaded into memory
 * Before running index, an existing index is copied to backup location 
 * @author Maxim Donde
 *
 */
public class Indexer implements ITicketRetrievedCallback, IProgressNotifier
{
	private static Logger log = Logger.getLogger(Indexer.class);
	
	private ITicketsRepository repository;
	private ITicketProcessor ticketProcessor;
	private SearcherConfiguration searchConfig; 
	private ScheduledThreadPoolExecutor threadExecutor;
	private int totalProcessedCount = 0;
	private double newRecoveryPosition = -1;
	private ProgressStatus progressStatus = ProgressStatus.UNKNOWN;
	private Object lock = new Object();
	private BlockingQueue<ITicket> ticketsQueue;
	private ITicket currentTicket;
	private List<IndexThread> threads = new ArrayList<IndexThread>();
	private ServerState internalState;
	private String objectId;

	private IndexContext ctx;
	private IProgressNotifier notifier = new DefaultProgressNotifier();
	
	Indexer(String objectId, SearcherConfiguration searchConfig) {
		this.objectId = objectId;
		this.searchConfig = searchConfig;
		repository = (ITicketsRepository) RuntimeInfo.getCurrentInfo().getBean("ticketsRepository");
		ticketProcessor = (ITicketProcessor) RuntimeInfo.getCurrentInfo().getBean("ticketProcessor");
		if(searchConfig.getNumThreadsInPool() < 1)
			throw new IllegalArgumentException("Should be at least 1 thread in the pool");

		ticketsQueue = new ArrayBlockingQueue<ITicket>(searchConfig.getIndexBufferSize());	
		//One for Notification thread
		threadExecutor = new ScheduledThreadPoolExecutor(searchConfig.getNumThreadsInPool() + 1);
		for(int i = 0; i < searchConfig.getNumThreadsInPool(); ++i) {
			IndexThread thread = new IndexThread(i, RuntimeInfo.getCurrentInfo().getSolutionName());
			threads.add(thread);
		}
	}
	
	public ProgressStatus index(IndexContext indexContext) {
		this.ctx = indexContext;
		if(progressStatus == ProgressStatus.IN_PROGRESS) {
			//Previous index is still running, skip this run
			LogUtils.warn(log, "Previous index is still running, skip this run");
			return ProgressStatus.IN_PROGRESS;
		}
		//TODO verify that dictionary is not empty, something like getTerms(ErrorCodeSource).size() > 1
		LogUtils.info(log, "Start indexing object id %s", objectId);
		internalState = RuntimeInfo.getCurrentInfo().getInternalState(objectId);
		long lastRunTime = internalState.getLastIndexRun();	
		initRun(internalState);
		
		ITicketProvider ticketProvider = (ITicketProvider) RuntimeInfo.getCurrentInfo().getBean(TicketProviderFactory.getProviderId(objectId));
		try {
			repository.startBatchUpdate();
		}
		catch(MatcherException me){
			LogUtils.error(log, "Unexpected error occured during index, no indexes processed. The underline error is %s", me.getMessage());
			return ProgressStatus.PROCESS_FAILED;
		}
		
		try {
			ticketProvider.getChangedTickets(lastRunTime, this);
		}
		catch(Exception e) {
			progressStatus = ProgressStatus.CONNECTOR_FAILED;
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			LogUtils.error(log, "Indexing object %s failed due to connection error %s stack trace %s", objectId, e.getMessage(), sw.getBuffer().toString());
		}
		
		while(!ticketsQueue.isEmpty() && progressStatus != ProgressStatus.PROCESS_FAILED) {
			try {				
				Thread.sleep(1000 * 10); //10 seconds
			} catch (InterruptedException e) {
				LogUtils.error(log, "The main thread was interrupted due to %s", e.getMessage());
			}
		}
		
		if(progressStatus == ProgressStatus.IN_PROGRESS) {
			int seconds = 60;  // 1 Minutes
			LogUtils.debug(log, "Give last tickets chance to finish : Started ("+seconds + " Sec)");
			try {
				Thread.sleep(seconds*1000);
			}
			catch(Exception e){
				LogUtils.debug(log, "Give last tickets chance to finish : Exception:"+e.getMessage());
			}
			LogUtils.debug(log, "Give last tickets chance to finish : Ended");
			LogUtils.info(log, "Indexing objet %s, All tickets processed successfully, committing index", objectId);
			progressStatus = ProgressStatus.COMPLETED;
		}
		else {
			LogUtils.warn(log, "Index failed after processing %d tickets, committing partial index and setting recovery location", totalProcessedCount);
		}
		
		try {
			synchronized (lock) {				
				lock.notify();
			}
		}
		catch(IllegalMonitorStateException e) {
			//Do nothing
		}
		
		try {
			repository.endBatchUpdate();
		}
		catch(MatcherException me) {
			LogUtils.error(log, "Unexpected error occured during indexing object %s, no indexes processed. The underline error is %s", objectId, me.getMessage());
			progressStatus = ProgressStatus.PROCESS_FAILED;
			newRecoveryPosition = -1;
		}
		
		if(progressStatus == ProgressStatus.COMPLETED) {
			lastRunTime = new Date().getTime();
			internalState.setLastIndexRun(lastRunTime);
			internalState.setIndexRecoveryPosition(-1);
			LogUtils.info(log, "Indexing object %s finished successfully, total %d tickets indexed", objectId, totalProcessedCount);
			if(totalProcessedCount == 0)
				return ProgressStatus.COMPLETED_NO_CHANGES;
			else
				return ProgressStatus.COMPLETED;
		}
		else {
			if(newRecoveryPosition < internalState.getIndexRecoveryPosition() || internalState.getIndexRecoveryPosition() == -1) {
				internalState.setIndexRecoveryPosition(newRecoveryPosition);
				LogUtils.warn(log, "Index object %s failed, recovery position was set to %f", objectId, newRecoveryPosition);
			}
			else
				LogUtils.warn(log, "Index object %s failed, recovery position remains %f", objectId, internalState.getIndexRecoveryPosition());
			progressStatus = ProgressStatus.COMPLETED;
			return ProgressStatus.PROCESS_FAILED;
		}
	}

	private void initRun(ServerState internalState) {
		progressStatus = ProgressStatus.IN_PROGRESS;
		newRecoveryPosition = -1;
		totalProcessedCount = 0;
		ticketsQueue.clear();
		TechnicalDictionary dictionary = (TechnicalDictionary)RuntimeInfo.getCurrentInfo().getBean("dictionary");
		dictionary.loadDictionary();
		threadExecutor.getQueue().clear();
		threadExecutor.execute(new NotificationThread(repository, RuntimeInfo.getCurrentInfo().getSolutionName()));
		for(IndexThread thread : threads) {
			thread.setRecoveryPosition(internalState.getIndexRecoveryPosition());
			threadExecutor.execute(thread);
		}
	}
	
	public void ticketRetrieved(ITicket ticket) {
		try {
			if(progressStatus == ProgressStatus.IN_PROGRESS && this.ctx.getMaxNumTickets() > totalProcessedCount) {
				//Patch, for some reason sometimes the read-only session lock is not released and if the thread get stuck on offer it doesn't releases the lock
				RuntimeInfo.getCurrentInfo().getHibernate().releaseSession(null);
				ticketsQueue.put(ticket);				
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
	private double getRecoveryPosition(ITicket ticket)
	{
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
	
	private class NotificationThread implements Runnable 
	{
		private ITicketsRepository repository;
		private String solutionName;
		
		public NotificationThread(ITicketsRepository repository, String solutionName)
		{
			this.repository = repository;
			this.solutionName = solutionName;
		}

		public void run() {
			RuntimeInfo.getCurrentInfo().startThread(new MultitenantThreadInfo(solutionName));
			while(progressStatus != ProgressStatus.COMPLETED)
			{
				synchronized (lock) {
					try {
						Date startDate = new Date();
						//30 minutes
						long waitInterval = 1000 * 60 *30;
						lock.wait(waitInterval);
						if(progressStatus == ProgressStatus.COMPLETED)
							return;
						long interval = new Date().getTime() - startDate.getTime();
						if(interval >= waitInterval && ctx.getMaxNumTickets() > totalProcessedCount) {
							//No updates from extraction threads for 30 minutes, consider process stuck
							LogUtils.error(log, "Notification thread didn't receive any updates for 30 minutes, consider process stuck");
							progressStatus = ProgressStatus.PROCESS_FAILED;
						}
					} catch (InterruptedException e) {
						//Main thread asks for shutdown
						return;
					}
				}
				if(progressStatus == ProgressStatus.PROCESS_FAILED) {
					LogUtils.error(log, "After %d tickets processed, processing failed on ticket %s", totalProcessedCount, currentTicket);
					try {
						repository.flush();
						newRecoveryPosition = getRecoveryPosition(currentTicket);
						internalState.setIndexRecoveryPosition(newRecoveryPosition);
						ticketsQueue.clear();
						return;
					} catch (MatcherException e) {
						LogUtils.error(log, "Unable to flush the index due to %s", e.getMessage());
					}
				}
				else {
					totalProcessedCount++;
					if(totalProcessedCount % searchConfig.getIndexBufferSize() == 0) {
						LogUtils.info(log, "%d more tickets processed, from the beggining %d are done", searchConfig.getIndexBufferSize(), totalProcessedCount);
						notifier.notifyProgress(1, String.format("%d more tickets processed", searchConfig.getIndexBufferSize()), totalProcessedCount, -1);
						Session session = RuntimeInfo.getCurrentInfo().getHibernate().acquireSession(SessionMode.READ_WRITE);
						try {
							repository.flush();
							newRecoveryPosition = getRecoveryPosition(currentTicket);
							internalState.setIndexRecoveryPosition(newRecoveryPosition);
							LogUtils.debug(log, "Save Internal State : Started ...");
							RuntimeInfo.getCurrentInfo().saveInternalState(objectId, session);
							LogUtils.debug(log, "Save Internal State : Ended.");
						} catch (Exception e) {
							LogUtils.error(log, "Unable to flush the index due to %s", e.getMessage());
						}
						RuntimeInfo.getCurrentInfo().getHibernate().releaseSession(session);
					}
				}
			}
			RuntimeInfo.getCurrentInfo().finishThread();
		}
	}
	
	private class IndexThread implements Runnable {
		private static final int MAX_SEQUANTIAL_ERRORS = 3;
		private double lastRecoveryPosition;
		private int idx;
		private int errorsCount = 0;
		private String solutionName;
		
		public IndexThread(int idx, String solutionName) {
			this.idx = idx;
			this.solutionName = solutionName;
		}

		public void setRecoveryPosition(double recoveryPosition)
		{
			lastRecoveryPosition = recoveryPosition;
		}
		
		public void run() {
			ITicket ticket = null;
			try {
				RuntimeInfo.getCurrentInfo().startThread(new MultitenantThreadInfo(solutionName));
				while(progressStatus == ProgressStatus.IN_PROGRESS || progressStatus == ProgressStatus.CONNECTOR_FAILED) {
					ticket = ticketsQueue.take();
					if(ticket == null)
						continue;
					try {
						if(ctx.getMaxNumTickets() <= totalProcessedCount)
							LogUtils.info(log, "Ticket %s was skipped, enough tickets already processed", ticket);
						else if(lastRecoveryPosition == -1 || getRecoveryPosition(ticket) < lastRecoveryPosition) {
							switch (ticket.getState()) {
							case New:
							case Updated:
								indexTicket(ticket);
								break;
							case Deleted:
								IProcessedTicket processedTicket = new ProcessedTicket(ticket, ProcessedTicket.getDefaultCalculator());
								repository.remove(processedTicket);
								break;
							}
						}
						else
							LogUtils.info(log, "Ticket %s was skipped as it before lastRecoveryPosition", ticket);
						synchronized (lock) {
							currentTicket = ticket;
							lock.notify();
						}			
						errorsCount = 0;
					}
					catch (Exception e) {
						e.printStackTrace();
						LogUtils.error(log, "Thread #%d, Unable to index ticket %s, reason: %s",
								idx, ticket.getId(), e.getMessage());
						errorsCount ++;
						if(errorsCount >= MAX_SEQUANTIAL_ERRORS) {
							progressStatus = ProgressStatus.PROCESS_FAILED;
							synchronized (lock) {
								currentTicket = ticket;
								lock.notify();
							}
							break;
						}
					}
					ticket = null;
				}
				RuntimeInfo.getCurrentInfo().finishThread();				
			} catch (InterruptedException e) {
				LogUtils.debug(log, "Index thread was interupted by the main thread");
			}
		}
		
		private void indexTicket(ITicket ticket) throws MatcherException {
			LogUtils.debug(log, "Thread #%d, Indexing ticket %s", idx, ticket);
			
			IProcessedTicket processedTicket = 
				ticketProcessor.processTicket(TicketProcessor.StepsMode.Index,
											  ticket,MatchMode.all,ticket.getOriginObjectId(),false);
			
			if(processedTicket.getId() == null || processedTicket.getData().isEmpty()) {
				LogUtils.info(log, "Thread #%d, Ticket %s was skipped as it doesn't contain data", idx, ticket);
				return;
			}

			if (ticket.getState() == TicketState.New)
				repository.add(processedTicket);
			else if (ticket.getState() == TicketState.Updated)
				repository.update(processedTicket);
			processedTicket = null;
			LogUtils.info(log, "Thread #%d, Ticket %s indexed successfully", idx, ticket);
		}		
	}

	//Progress notification listener
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
}
