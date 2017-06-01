package com.softlib.imatch.common;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.softlib.imatch.ITicket;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.common.trackinginfo.MatchStartedTrackingInfo;

public class TicketTracker 
{
	private Logger log = Logger.getLogger(TicketTracker.class);
	private ScheduledThreadPoolExecutor executor;

	public TicketTracker()
	{
		executor = new ScheduledThreadPoolExecutor(1);
	}
	
	public void track(ITicket ticket, TrackingInfo trackingInfo)
	{
		if (ticket.getId() != null && !ticket.getId().equals("-1"))
		{
			trackingInfo.setTicketId(ticket.getId());
		}
		else
		{
			trackingInfo.setTicketId("-1");
		}
		trackingInfo.setData(ticket);
		
		try {
			trackingInfo.setUsername("anonymous");
		} catch(Exception e){}
		
		Runnable command = new TrackingThread(trackingInfo);
		executor.execute(command);
	}
	
	public void destroy() {
		executor.shutdownNow();
	}

	private class TrackingThread implements Runnable
	{
		//This object is not multithreaded but since the thread pool is of size 1, there is no need to be multithreaded
		private TrackingInfo trackingInfo;
		
		public TrackingThread(TrackingInfo trackingInfo) {
			this.trackingInfo = trackingInfo;
		}
		
		public void run() {
			Transaction tx = null;
			Session session = RuntimeInfo.getCurrentInfo().getHibernate().acquireTempSession(SessionMode.READ_WRITE);
			try {
				tx = session.beginTransaction();
				session.saveOrUpdate(trackingInfo);
				tx.commit();			
				LogUtils.debug(log, "Tracking info %s saved successfully", trackingInfo);
			}
			catch(Exception e) {
				if(tx != null)
					tx.rollback();
				//In any case, tracking should not throw exception
				LogUtils.error(log, "Unable to save tracking info, reason " + e.getMessage(), e);
			}
			finally {
				RuntimeInfo.getCurrentInfo().getHibernate().releaseSession(session);
				session.close();
			}
		}
	}
}
