package com.softlib.imatch.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ReadWriteLockWithTimer extends ReadWriteLock {

	private static Timer timer = new Timer();
	private Map<Long,RemoveReaderTask> readersTimers = 
		new HashMap<Long,RemoveReaderTask>();
	private int readTimeout;

	protected ReadWriteLockWithTimer(int readTimeout) {
		super();
		this.readTimeout = readTimeout;
	}

	protected ReadWriteLockWithTimer() {
		this(1000*60*3);
	}

	public synchronized void lockRead() {
		super.lockRead();
		addReader(Thread.currentThread().getId());
	}

	public synchronized void unlockRead() {
		Long id = Thread.currentThread().getId();
		removeReader(id);
		super.unlockRead();
	}

	public synchronized void clear() {
		super.clear();
		Collection<Long> readersIds = new ArrayList<Long>(readersTimers.keySet());
		for (Long id : readersIds) 
			removeReader(id);
	}

	private class RemoveReaderTask extends TimerTask {
		private final Long id;
		private final ReadWriteLockWithTimer own;
		public RemoveReaderTask(Long id,ReadWriteLockWithTimer own) {
			this.id = id;
			this.own = own;
		}
		public void run() {
			LogUtils.error(log, "unlockRead timeout by lock " + lockName + " - remove Reader " + id);
			removeReader(id);
			synchronized(own) {
				own.notifyAll();
			}
		}
	};

	synchronized private void addReader(Long id) {
		if (readersTimers.keySet().contains(id)) {
			readersTimers.get(id).cancel();
			timer.purge();
		}
		RemoveReaderTask task = new RemoveReaderTask(id,this);
		readersTimers.put(id,task);
		timer.schedule(task,readTimeout);
	}

	synchronized private void removeReader(Long id) {
		if (readersTimers.keySet().contains(id)) {
			readersTimers.get(id).cancel();
			readersTimers.remove(id);
		}
	}

	protected synchronized boolean isReadersEmpty() {
		return super.isReadersEmpty() || readersTimers.isEmpty(); 
	}

	public void cancel() {
		timer.cancel();
		timer = null;
		timer = new Timer();
	}
};

