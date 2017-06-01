package com.softlib.imatch.ticketprocessing;

public class TechTokens extends RegexFileStep {
	private String regexFileName;
	
	public TechTokens() {
		this("TechTokens");
	}
	public TechTokens(String regexFileName) {
		this.regexFileName = regexFileName;
	}
	
	@Override
	protected String getFileName() {
		return regexFileName + ".txt";
	}
	
	@Override
	protected boolean isSingleOperationMode() {
		return true;
	}
};
