package com.softlib.imatch.common;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.log4j.Logger;
import org.apache.log4j.NDC;

public class LogUtils {
	
	public static void startThread(String context)
	{
		NDC.push(context);
	}
	
	public static void finishThread()
	{
		NDC.remove();
	}
	
	public static void debug(Logger log, String msg, Object...args) {
		if(!log.isDebugEnabled())
			return;
		String formattedMsg ;
		if(args.length > 0)
			formattedMsg = String.format(msg, args);
		else
			formattedMsg = msg;
		log.debug(formattedMsg);
	}
	
	public static void info(Logger log, String msg, Object...args) {
		if(!log.isInfoEnabled())
			return;
		String formattedMsg ;
		if(args.length > 0)
			formattedMsg = String.format(msg, args);
		else
			formattedMsg = msg;
		log.info(formattedMsg);
	} 
	
	public static void warn(Logger log, String msg, Object...args) {
		String formattedMsg ;
		if(args.length > 0)
			formattedMsg = String.format(msg, args);
		else
			formattedMsg = msg;
		log.warn(formattedMsg);
	}

	public static void error(Logger log, String msg, Object...args) {
		String formattedMsg ;
		if(args.length > 0)
			formattedMsg = String.format(msg, args);
		else
			formattedMsg = msg;
		log.error(formattedMsg);
	}

	public static void fatal(Logger log, Throwable t, String msg, Object...args) {
		String formattedMsg ;
		if(args.length > 0)
			formattedMsg = String.format(msg, args);
		else
			formattedMsg = msg;
		log.fatal(formattedMsg, t);
	}
	
	public static void error(Logger log, Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		log.error(e.getMessage());
		log.error(sw.toString());
	}

}
