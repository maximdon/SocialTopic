package com.softlib.imatch.common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;


public class TracerFile {

	public static final String defualtPath = "C:\\Temp\\TracerFile\\";
	
	private static final String strSpaces = "                    "; 
	
	private static final String intSpaces = "    ";  
	private static final String intFormat = "###0" ;  
    private static DecimalFormat intFm = 
    	new DecimalFormat(intFormat,new DecimalFormatSymbols(Locale.US));

 	private static final String floatSpaces = "      ";  
	private static final String floatFormat = "##0.00" ;  
    private static DecimalFormat floatFm = 
    	new DecimalFormat(floatFormat,new DecimalFormatSymbols(Locale.US));

	private FileWriter fstream;
	private BufferedWriter out;
	
	public TracerFile() {
	}

	public TracerFile(String dir,String name) throws IOException {
		this(defualtPath,dir,name);
	}

	private String path;
	private String dir;
	private String name;
	
	public TracerFile(String path, String dir, String name) throws IOException {
		this.path = path;
		this.dir = dir;
		this.name = name;
		init();
	}
	
	private void init() throws IOException  {
		//fstream = new FileWriter(path+"\\"+dir+"\\"+name);
		File file = new File(path+"\\"+dir+"\\"+name);
		OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file), "UTF8");
		out = new BufferedWriter(osw);	
	}
	
	public void clean() {
		try {
			init();
		} catch (IOException e) {
		}
	}
	
	public void write(String ... strings ) {
		String line = "";
		boolean first = true;
		for (String str : strings) {
			if (first) {
				line = str;
				first = false;
			}
			else
				line = line + "|" + str;
		}
		write(line);
	}
	
	public void write(String str) {
		try{
			out.write(str+"\n");
			out.flush();
		} catch (Exception e){
			System.err.println("TracerFile.write Error: " + e.getMessage());
		}
	}
	
	public void close() {
		try{
			out.close();
		} catch (Exception e){
			System.err.println("TracerFile.close Error: " + e.getMessage());
		}
	}

	public boolean isActive() {
		return true;
	}
	
	static public String getInt(int i) {
		String intStr = intFm.format(i);
		int endIdx = intSpaces.length()-intStr.length();
		if(endIdx < 0)
			return "";
		return intSpaces.substring(0, endIdx)+intStr;
	}

	static public String getFloat(float f) {
		String floatStr = floatFm.format(f);
		return floatSpaces.substring(0,floatSpaces.length()-floatStr.length())+floatStr;
	}

	
	static public String getString(String s,int length) {
		if (s.length()>=length)
			s = s.substring(0,length);
		if (s.length()<length)
			s = s + strSpaces.substring(0,length-s.length());
		return s;
	}

	
};
