package com.softlib.imatch.ticketprocessing;

public class VersionsTechToknes extends TechTokens {
	
	static final public String FIELD_NAME = "Versions";
	
	@Override
	protected String getFileName() {
		return "versions.txt";
	}
	
	public String getStepName() {
		return FIELD_NAME;
	}
};
