package com.softlib.imatch.common;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;


abstract public class QueueThread<T> extends Thread {

	private static Logger log = Logger.getLogger(QueueThread.class);

	private String queueName = "no name";
	
	private int numEnd = 1;
	private int countEnd = 0;

	private BlockingQueue<T> exist;
	private BlockingQueue<T> allow;

	public QueueThread(BlockingQueue<T> exist, BlockingQueue<T> allow) {
		this.exist = exist;
		this.allow = allow;
	}

	public void setNumEnd(int numEnd) {
		this.numEnd = numEnd;
	}

	public boolean isDone(T d) {
		if (isEndObject(d)) {
			countEnd++;
			if (countEnd>=numEnd)
				return true;
		}
		return false;
	}
	
	protected List<T> processEnd() {
		return new ArrayList<T>();
	}

	abstract protected List<T> process(T take);

	abstract protected T getEndObject();

	abstract protected boolean isEndObject(T s);

	protected void init() {}

	public void run() {
		init();
		while (true) {
			try {
				T take = exist.take();
				if (isEndObject(take)) {
					debug(take,"===>");
					List<T> processEndList = processEnd();
					int idx = 1;
					int lastIdx = processEndList.size();
					for (T processTake : processEndList) {
						allow.put(processTake);
						debug(processTake,(idx==lastIdx?"<--+":"<---"));
						idx++;
					}
					allow.put(take);
					debug(take,"<===");
					break;
				}
				else {
					debug(take,"--->");
					List<T> processList = process(take);
					int idx = 1;
					int lastIdx = processList.size();
					for (T processTake : processList) {
						allow.put(processTake);
						debug(processTake,(idx==lastIdx?"<--+":"<---"));
						idx++;
					}
				}

			} 
			catch (InterruptedException e) {
				LogUtils.error(log, "QueueThread %s stop. Error=%s", queueName ,e.getMessage());
				break;
			}

		}
	}

	private void debug(T object,String msg) {
		//LogUtils.debug(log, "QThread %s : %s %s : ",queueName,msg,object);
	}
	
	public String getQueueName() {
		return queueName;
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}


};

