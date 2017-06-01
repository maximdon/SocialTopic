package com.softlib.imatch.connectors.file.convert;

import java.io.File;
import java.util.Map;

import org.apache.log4j.Logger;

import com.softlib.imatch.common.CreateTextFileProcess;
import com.softlib.imatch.common.FileUtils;
import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.connectors.file.TicketProviderMode;
import com.softlib.imatch.matcher.TicketingSystemSettings;


public class ConvertFileMngr {
	
	private final static Logger log = Logger.getLogger(ConvertFileMngr.class);

	private final static String DIR_SEP = "\\";
	private final static String SEP = "_";
	private final static String CONVERT_SUFFIX = "CONVERT";

	private String rootDirName = "";
	private String convertRootDirName = "";
	
	final private String convertCmd;
	final private int convertTimeoutSec;
	final private boolean active;
		
	
	public ConvertFileMngr(TicketingSystemSettings settings) {
		
		Map<String, String> params = settings.getParams();
		
		String activeStr = params.get("convertActive");
		if (activeStr==null || 
			TicketProviderMode.instance().getMode()==TicketProviderMode.Mode.iSolve)
			active = false;
		else
			active = Boolean.valueOf(activeStr);

		String cnvCmd = params.get("convertCmd");
		if (cnvCmd==null)
			convertCmd = "c:\\temp\\bin-32\\GetFileText";
		else
			convertCmd = cnvCmd;
		
		String convertTimeoutSecStr = params.get("convertTimeoutSec");
		if (convertTimeoutSecStr==null)
			convertTimeoutSec = 300;
		else
			convertTimeoutSec = Integer.valueOf(convertTimeoutSecStr);
		
	}
	
	public void set(File rootFile) {
		rootDirName = rootFile.getAbsolutePath();
		convertRootDirName = rootFile.getAbsolutePath() + SEP + CONVERT_SUFFIX;
	}
	
	private String getConvertFileName(File file,String fileExtension) {
		String fileDir = FileUtils.getDir(file);
		String fileName = file.getName();
		long lastModified = file.lastModified();
		
		String convertDirName = fileDir.substring(rootDirName.length()+1,fileDir.length());
		convertDirName =  convertRootDirName + DIR_SEP + convertDirName;
		
		File convertDir = new File(convertDirName);
		try {
			convertDir.mkdirs();
		}	
		catch (Exception e) {
			LogUtils.error(log, "mkdirs(%s) error =  %s", convertDir.getAbsolutePath() , e.getMessage());
			e.printStackTrace();
			return null;
		}
		return convertDirName + SEP + lastModified + SEP + fileName + "."+ fileExtension;
	}
	
	public File getConvert(File file,String fileExtension) {
		if (!active) 
			return file;
		
		String absolutePath = file.getAbsolutePath();
		if (absolutePath.endsWith("."+fileExtension))
			return file;
		
		String filePath = file.getAbsolutePath();
		String convertFileName = getConvertFileName(file,fileExtension);
		
		File convertFile = new File(convertFileName);
		if (convertFile.exists())
			return convertFile;

		if (CreateTextFileProcess.createTextFile(convertCmd,convertTimeoutSec,filePath,convertFileName)) {
			convertFile.setLastModified(file.lastModified());
			return convertFile;
		}
		
		return null;
	}

	public boolean isActive() {
		return active;
	}
	
};
