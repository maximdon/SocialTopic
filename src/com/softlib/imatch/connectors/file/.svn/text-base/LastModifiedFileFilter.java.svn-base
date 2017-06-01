package com.softlib.imatch.connectors.file;

import java.io.File;
import java.io.FileFilter;

public class LastModifiedFileFilter implements FileFilter {

	private final long startDate;
	
	public LastModifiedFileFilter(long startDate) {
		this.startDate = startDate;
	}
	
	public boolean accept(File file) {
		return file.lastModified() > startDate;
	}

	public long getStartDate() {
		return startDate;
	}
	
	
};
