package com.softlib.imatch.common;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

import cli.VirtualAgent.JavaHelper.JavaLogUtils;

public class AzureLogAppender extends AppenderSkeleton {

	@Override
	protected void append(LoggingEvent event) {
		switch(event.getLevel().toInt())
		{
		case Level.DEBUG_INT:
			JavaLogUtils.Debug(event.getRenderedMessage());
			break;
		case Level.INFO_INT:
			JavaLogUtils.Info(event.getRenderedMessage());
			break;
		case Level.ERROR_INT:
			JavaLogUtils.Error(event.getRenderedMessage());
			break;
		case Level.FATAL_INT:
			JavaLogUtils.Error(event.getRenderedMessage());
			break;			
		}
	}

	@Override
	public void close() {	
	}

	@Override
	public boolean requiresLayout() {
		return false;
	}

}
