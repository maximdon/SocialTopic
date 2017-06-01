package com.softlib.imatch.common;

import org.apache.log4j.Logger;

import com.softlib.imatch.ticketprocessing.ProcessedTicketDBase;

public class CreateTextFileProcess {

	private final static Logger log = Logger.getLogger(ProcessedTicketDBase.class);
	
	static private String param(String fileName) {
		return " " + "\"" + fileName + "\"";
	}
	
	static public boolean createTextFile(String cmd,int convertTimeoutSec,String file,String textFile) {
		try {
			String execCmd = cmd + param(file) + param(textFile);
			Process process = Runtime.getRuntime().exec(execCmd);
			int count = 0;
			
			while (true) {
				try {
					int exitValue = process.exitValue();
					if(exitValue != 0)
						LogUtils.error(log, "Unexpected Error converting file %s. Convert process returns value %d", file, exitValue);
					return exitValue == 0;
				}
				catch (IllegalThreadStateException e) {
					if (count++>convertTimeoutSec) {
						process.destroy();
						LogUtils.error(log, "Unexpected Error converting file %s. Convert process stuck", file);
						return false;
					}
					Thread.sleep(1000);
				}
			}
		} catch (Exception e) {
			LogUtils.error(log, "Error converting file %s. The underlined error is %s", file, e.getMessage());
			return false;
		}
			
	}

};


