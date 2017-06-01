package com.softlib.imatch.common;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public class FileUtils {
	
	static public void emptyDirectory(File dir) throws IOException {
		if(!dir.exists() || !dir.canWrite() || !dir.isDirectory())
			return;
		File[] files = dir.listFiles();
		if(files != null)
			for(File file : files)
				file.delete();
	}
	
	static public void copyDirectory(String srcPath, String dstPath, boolean cleanDst)
			throws IOException {
		copyDirectory(new File(srcPath), new File(dstPath), cleanDst);
	}
	
	static public void copyDirectory(File srcFile, File dstFile, boolean cleanDst) throws IOException {

		if (srcFile.isDirectory()) {
			if (!dstFile.exists()) {
				dstFile.mkdir();
			}
			else if(cleanDst) {
				emptyDirectory(dstFile);
			}
			String files[] = srcFile.list();
			for (int i = 0; i < files.length; i++) {
				File newSrcFile = new File(srcFile, files[i]);
				File newDstFile = new File(dstFile, files[i]);
				if(!newSrcFile.equals(dstFile))
					copyDirectory(newSrcFile, newDstFile, cleanDst);
			}
		}
		else {
			if (!srcFile.exists()) {
				throw new IOException("Invalid path " + srcFile);
			}

			InputStream in = null;
			OutputStream out = null;
			try {
				in = new FileInputStream(srcFile);
				out = new FileOutputStream(dstFile);
				// Transfer bytes from in to out
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) 
					out.write(buf, 0, len);
			}
			finally {
				if(in != null)
					in.close();
				if(out != null)
					out.close();
			}
		}
	}

	static public void copyFile(String src, String dst) throws FileNotFoundException, IOException {
		copyFile(new File(src), new File(dst));
	}

	static public void copyFile(File src, File dst) throws FileNotFoundException, IOException  {
		InputStream in = null;
		OutputStream out = null;
		try {
			in = new FileInputStream(src);
			out = new FileOutputStream(dst);
			// Transfer bytes from in to out
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) 
				out.write(buf, 0, len);
		}
		finally {
			if(in != null)
				in.close();
			if(out != null)
				out.close();
		}
	}

	static public File findFile(String fileName, File rootDir) {
		for(File file : rootDir.listFiles()) {
			if(file.getName().equals(fileName))
				return file;
			if(file.isDirectory()) {
				File foundFile = findFile(fileName, file);
				if(foundFile != null)
					return foundFile;
			}			
		}
		return null;
	}

	static public void copyURL(String src, String dst) throws FileNotFoundException, IOException {
		copyURL(new URL(src), new File(dst));
	}

	static public void copyURL(URL src, File dst) throws FileNotFoundException, IOException  {
		InputStream in = null;
		OutputStream out = null;
		try {
		    in = src.openStream();
			out = new FileOutputStream(dst);
			// Transfer bytes from in to out
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) 
				out.write(buf, 0, len);
		}
		finally {
			if(in != null)
				in.close();
			if(out != null)
				out.close();
		}

	}
	
	static public String getDir(File file) {
		if (file==null)
			return null;
		String name = file.getName();
		String path = file.getAbsolutePath();
		return path.substring(0,path.length()-name.length());
	}

	static public String getContents(File aFile) {
		StringBuilder contents = new StringBuilder();	    
		try {
			//FileReader always assumes default encoding is OK!
			BufferedReader input =  new BufferedReader(new FileReader(aFile));
			try {
				String line = null; //not declared within while loop
				while (( line = input.readLine()) != null){
					contents.append(line);
					contents.append(System.getProperty("line.separator"));
				}
			}
			finally {
				input.close();
			}
		}
		catch (IOException ex){
			ex.printStackTrace();
		}
		return contents.toString();
	}

	static public void write(byte byteBuf[],String fileName) throws IOException {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream(); 
		OutputStream fileStream = new FileOutputStream(fileName); 
		byteStream.write(byteBuf);
		byteStream.writeTo(fileStream);
		fileStream.close();
		byteStream.close();
	}

	static public void write(char charBuf[],String fileName) throws IOException {
		FileWriter fileStream = new FileWriter(fileName);
		BufferedWriter out = new BufferedWriter(fileStream);
		out.write(charBuf);
		out.close();
	}

	
};
