package com.softlib.imatch.commandline;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

class CommandLineUtils 
{
	private static int MAX_RESTART_COUNT = 6;
	
	public static void restartLongProcess(String batFile) {
		String rootDir = System.getProperty("user.dir");
		restartLongProcess(rootDir, batFile);
	}
	
	public static void restartLongProcess(String rootDir, String batFile) 
	{
		String counterFile = "restart_counter.txt";
		try {
			File f = new File("c:\\iMatch" + "\\" + counterFile);
			int restartCount = 0; 
			char[] cbuf = new char[1];
			if(f.exists()) {
				FileReader fr = new FileReader(f);
				fr.read(cbuf);
				restartCount = Character.getNumericValue(cbuf[0]);
			}
			if(restartCount < MAX_RESTART_COUNT) {
				FileWriter fw = new FileWriter(f);
				restartCount ++;
				cbuf[0] = Character.forDigit(restartCount, 10);
				fw.write(cbuf);
				fw.flush();
				fw.close();
				String fileName = rootDir + "\\" + batFile;
				String[] commands = {"cmd", "/c", "start", "\"iMatch\"",fileName};
				Runtime.getRuntime().exec(commands);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public static void restartTomcat() {
		final String CMD_START = "cmd /c net start \"";
		final String CMD_STOP = "cmd /c net stop \"";
		final String serviceName = "Tomcat6";

		try {
			System.out.println("Stopping Tomcat...");
			execCmd(CMD_STOP + serviceName + "\"");
			//Wait 5 minutes for stop
			Thread.sleep(3 * 60 * 1000);
			System.out.println("Starting Tomcat...");
			execCmd(CMD_START + serviceName + "\"");
			//Wait 1 minute for start
			Thread.sleep(60 * 1000);
			System.out.println("Tomcat restarted successfully");
		} catch (Exception e) {
			System.out.println("Unable to restart Tomcat due to " + e.getMessage());
		}
	}
	
	private static void execCmd(String cmdLine) throws Exception {
		Runtime.getRuntime().exec(cmdLine);		
	}
}
