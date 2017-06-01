package com.softlib.imatch.matcher;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("candidatesRanking")
public class CandidatesRankingConfig {
	@XStreamImplicit(itemFieldName="mode")
	private List<CandidatesRankingModeConfig> modes;

	public void setModes(List<CandidatesRankingModeConfig> modes) {
		this.modes = modes;
	}

	public List<CandidatesRankingModeConfig> getModes() {
		return modes;
	}
}
