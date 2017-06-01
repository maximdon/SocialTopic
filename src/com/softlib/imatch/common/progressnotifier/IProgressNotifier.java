package com.softlib.imatch.common.progressnotifier;

public interface IProgressNotifier 
{
	void registerProgressNotificationListener(IProgressNotificationListener listener);
	void unregisterProgressNotificationListener(IProgressNotificationListener listener);
	void notifyProgress(int level, String message, int processedCount, int remainingCount);
}
