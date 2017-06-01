package com.softlib.imatch.common.progressnotifier;

import java.util.ArrayList;
import java.util.List;

public class DefaultProgressNotifier implements IProgressNotifier {
	private List<IProgressNotificationListener> listeners = new ArrayList<IProgressNotificationListener>();
	
	@Override
	public void notifyProgress(int level, String message, int processedCount,
			int remainingCount) 
	{
		for(IProgressNotificationListener listener : listeners)
			listener.progress(level, message, processedCount, remainingCount);
	}

	@Override
	public void registerProgressNotificationListener(
			IProgressNotificationListener listener) 
	{
		listeners.add(listener);
	}

	@Override
	public void unregisterProgressNotificationListener(
			IProgressNotificationListener listener) 
	{
		listeners.remove(listener);
	}

	public List<IProgressNotificationListener> getListeners() {
		return listeners;
	}
}
