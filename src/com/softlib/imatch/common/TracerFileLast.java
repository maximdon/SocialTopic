package com.softlib.imatch.common;

import java.util.HashMap;
import java.util.Map;

public class TracerFileLast  {

	final static public String MostTerms = "MostTerms";
	final static public String Proximity = "Proximity";
	final static public String Density = "Density";
	final static public String Pattern = "Pattern";
	final static public String PP = "PP";
	final static public String Sessions = "Sessions";
	final static public String Distance = "Distance";
	final static public String Index = "Index";
	final static public String Relations = "Relations";
	public static final String NTerms = "NTerms";	
	public static final String WordnetTerms = "WordnetTerms";
	public static final String SubstitutedTerms = "SubstitutedTerms";
	
	static private Map<String,TracerFile> lastFileByDir =
		new HashMap<String,TracerFile>();
	
	synchronized static public TracerFile create(String dir,String fileName) {
		return create(dir,fileName,false);
	}
	
	synchronized static public TracerFile create(String dir,String fileName,boolean closeLast) {
		TracerFile file;
		try {
			 file = new TracerFile(dir,fileName);
		}
		catch (Exception e) {
			file = getDefaultFile();
		}
		if (closeLast)
			closeLast(dir);
		lastFileByDir.put(dir,file);
		
		return file;
	}

	private static TracerFile getDefaultFile() {
		return new TracerFile() {
			public void write(String ... strings ) {}
			public void write(String str) {}
			public void close() {}
			public boolean isActive() {return false;}
		};

	}
	
	static public TracerFile getLast(String dir) {
		TracerFile lastTracerFile = lastFileByDir.get(dir);
		if (lastTracerFile==null)
			lastTracerFile = getDefaultFile();
		return lastTracerFile;
	}
	
	static public void closeLast(String dir) {
		TracerFile lastTracerFile = lastFileByDir.get(dir);
		if (lastTracerFile!=null)
			lastTracerFile.close();		
	}
};
