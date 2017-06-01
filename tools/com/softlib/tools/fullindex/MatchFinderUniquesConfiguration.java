package com.softlib.tools.fullindex;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

public class MatchFinderUniquesConfiguration
{
	@XStreamAsAttribute
	@XStreamAlias("enabled")
	private boolean isEnabled;

	@XStreamAsAttribute
	@XStreamAlias("numSamplesInReport")
	private int numSamplesInReport;

	@XStreamAsAttribute
	@XStreamAlias("maxCandidateScore")
	private float maxCandidateScore;

	@XStreamAsAttribute
	@XStreamAlias("sortOrder")
	private SortOrder sortOrder;
	
	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	public boolean isEnabled() {
		return isEnabled;
	}

	public void setNumSamplesInReport(int numSamplesInReport) {
		this.numSamplesInReport = numSamplesInReport;
	}

	public int getNumSamplesInReport() {
		return numSamplesInReport;
	}

	public void setMaxCandidateScore(float maxCandidateScore) {
		this.maxCandidateScore = maxCandidateScore;
	}

	public float getMaxCandidateScore() {
		return maxCandidateScore;
	}
	
	public SortOrder getSortOrder() {
		return sortOrder;
	}
}
