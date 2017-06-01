package com.softlib.imatch.matcher.filter;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("candidatesFilter")
public class CandidatesFilterConfig {

	@XStreamImplicit(itemFieldName="mode")
	private List<CandidatesFilterModeConfig> modes;

	public void setModes(List<CandidatesFilterModeConfig> modes) {
		this.modes = modes;
	}

	public List<CandidatesFilterModeConfig> getModes() {
		return modes;
	}
};
