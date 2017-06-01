package com.softlib.imatch.matcher.filter;

import org.apache.log4j.Logger;

public abstract class BaseCandidatesSubfilter implements ICandidatesSubfilter {

	protected CandidatesFilterModeConfig config;
	protected static final Logger log = Logger.getLogger(BaseCandidatesSubfilter.class);
	
	public void setConfiguration(CandidatesFilterModeConfig config) {
		this.config = config;
	}
	
};
