package com.softlib.imatch.common;

import org.apache.log4j.Logger;

import com.softlib.imatch.RuntimeInfo;

public class ReadWriteLock {

	protected int readers       = 0;
	protected int writers       = 0;
	protected int writeRequests = 0;
	protected long writeLockOwner = -1;
	protected String lockName = "";
	
	protected static Logger log = Logger.getLogger(ReadWriteLockWithTimer.class);

	public static ReadWriteLock createLock() {
		return createLock(ReadWriteLockMode.DEFAULT);
	}
	
	public static ReadWriteLock createLock(ReadWriteLockMode mode) {
		if(mode == ReadWriteLockMode.DEFAULT ||	mode == ReadWriteLockMode.NO_TIMER)
			return new ReadWriteLock();
		else
			return new ReadWriteLockWithTimer();
	}
	
	public static ReadWriteLock createLock(ReadWriteLockMode mode, String lockName) {
		ReadWriteLock lock = createLock(mode);
		lock.lockName = lockName;
		return lock;
	}
	
	protected ReadWriteLock() {		
	}
	
	public synchronized void lockRead() {
		if(writeLockOwner != Thread.currentThread().getId()) {
			while(writers > 0 || writeRequests > 0)
				try {
					wait();
				} catch (InterruptedException e) {
					LogUtils.error(log, "lockWrite Error , Message="+e.getMessage());
				}
		}
		readers++;
	}

	public synchronized void unlockRead() {
		if (readers>0) 
			readers--;
		if(writeLockOwner != Thread.currentThread().getId())
			notifyAll();
	}

	public synchronized void lockWrite() {
		try {
			writeRequests++;
			if(writeLockOwner!= Thread.currentThread().getId()) {
				while(!isReadersEmpty() || writers > 0)
					wait();
			}
			writeLockOwner = Thread.currentThread().getId();
			writeRequests--;
			writers++;
		} 
		catch (InterruptedException e) {
			LogUtils.error(log, "lockWrite Error , Message="+e.getMessage());
		}
	}

	public synchronized void unlockWrite() {
		if (writers>0) {
			writers--;
			if(writers == 0) {
				writeLockOwner = -1;
				notifyAll();
			}
		}
	}
	
	public synchronized void clear() {
		writeLockOwner = -1;
		writers = 0;
		readers = 0;
		writeRequests = 0;
	}

	protected synchronized boolean isReadersEmpty() {
		return readers == 0;
	}

	public void cancel() {		
	}
};
