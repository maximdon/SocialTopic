package com.softlib.imatch.matcher.filter;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("mode")
public class CandidatesFilterModeConfig {
	@XStreamAsAttribute
	private String name;
	@XStreamAsAttribute
	private boolean separateObjectsForFilter = true;	
	@XStreamAsAttribute
	private int maxNumCandidates = -1;
	@XStreamAsAttribute
	private int maxAuthCandidates = -1;
	@XStreamAsAttribute
	private float minCandidateScore = -1;
	@XStreamAsAttribute
	private float maxCandidateScore = -1;
	@XStreamAsAttribute
	private int maxScoreDistancePercentage = -1;
	@XStreamAsAttribute
	private int filterPercentageDropOff = 5;
	@XStreamAsAttribute
	private int candidatesFactor = 2;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setMaxNumCandidates(int maxNumCandidates) {
		this.maxNumCandidates = maxNumCandidates;
	}
	public int getMaxNumCandidates() {
		return maxNumCandidates;
	}
	public int getMaxAuthCandidates() {
		return maxAuthCandidates;
	}
	public void setMaxAuthCandidates(int maxAuthCandidates) {
		this.maxAuthCandidates = maxAuthCandidates;
	}
	public void setMinCandidateScore(float minCandidateScore) {
		this.minCandidateScore = minCandidateScore;
	}
	public float getMinCandidateScore() {
		return minCandidateScore;
	}
	public void setMaxCandidateScore(float maxCandidateScore) {
		this.maxCandidateScore = maxCandidateScore;
	}
	public float getMaxCandidateScore() {
		return maxCandidateScore;
	}
	public void setMaxScoreDistancePercentage(int maxScoreDistance) {
		this.maxScoreDistancePercentage = maxScoreDistance;
	}
	public int getMaxScoreDistancePercentage() {
		return maxScoreDistancePercentage;
	}
	public void setFilterPercentageDropOff(int filterPercentageDropOff) {
		this.filterPercentageDropOff = filterPercentageDropOff;
	}
	public int getFilterPercentageDropOff() {
		return filterPercentageDropOff;
	}
	public void setSeparateObjectsForFilter(boolean separateObjectsForFiltering) {
		this.separateObjectsForFilter = separateObjectsForFiltering;
	}
	public boolean isSeparateObjectsForFilter() {
		return separateObjectsForFilter;
	}
	
	public int getCandidatesFactor() {
		return candidatesFactor;
	}
	public void setCandidatesFactor(int candidatesFactor) {
		this.candidatesFactor = candidatesFactor;
	}
	
}
