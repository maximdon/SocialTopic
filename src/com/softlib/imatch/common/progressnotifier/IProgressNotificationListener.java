package com.softlib.imatch.common.progressnotifier;

public interface IProgressNotificationListener 
{
	void progress(int level, String message, int processedCount, int remainingCount);
}
