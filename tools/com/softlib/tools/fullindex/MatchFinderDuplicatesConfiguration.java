package com.softlib.tools.fullindex;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

public class MatchFinderDuplicatesConfiguration 
{
	@XStreamAsAttribute
	@XStreamAlias("enabled")
	private boolean isEnabled;

	@XStreamAsAttribute
	@XStreamAlias("numSamplesInReport")
	private int numSamplesInReport;

	@XStreamAsAttribute
	@XStreamAlias("minCandidateScore")
	private float minCandidateScore;
	
	@XStreamAsAttribute
	@XStreamAlias("generateCompleteGroup")
	private boolean isGenerateCompleteGroup;

	@XStreamAsAttribute
	@XStreamAlias("sortOrder")
	private SortOrder sortOrder;

	public boolean isEnabled() {
		return isEnabled;
	}

	public int getNumSamplesInReport() {
		return numSamplesInReport;
	}

	public float getMinCandidateScore() {
		return minCandidateScore;
	}

	public boolean isGenerateCompleteGroup() {
		return isGenerateCompleteGroup;
	}

	public SortOrder getSortOrder() {
		return sortOrder;
	}
}
