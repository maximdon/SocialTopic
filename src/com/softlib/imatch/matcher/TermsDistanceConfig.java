package com.softlib.imatch.matcher;


import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("termsDistance")
public class TermsDistanceConfig {
	
	@XStreamAsAttribute
	private int distanceByLetters;
	@XStreamAsAttribute
	private int maxImportantTerms;
	@XStreamAsAttribute
	private int reduceScorePercent;
	@XStreamAsAttribute
	private Boolean activeInMatch;
	@XStreamAsAttribute
	private Boolean activeInRematch;
	
	public void init() {
		if (activeInMatch==null)
			activeInMatch=false;
		if (activeInRematch==null)
			activeInRematch=false;
		if (distanceByLetters==0)
			distanceByLetters=300;
		if (maxImportantTerms==0)
			maxImportantTerms=4;
		if (reduceScorePercent==0)
			reduceScorePercent=20;
	}
	
	public int getDistanceByLetters() {
		return distanceByLetters;
	}
	
	public void setDistanceByLetters(int distanceByLetters) {
		this.distanceByLetters = distanceByLetters;
	}
	
	public int getMaxImportantTerms() {
		return maxImportantTerms;
	}
	
	public void setMaxImportantTerms(int maxImportantTerms) {
		this.maxImportantTerms = maxImportantTerms;
	}
	
	public int getReduceScorePercent() {
		return reduceScorePercent;
	}
	
	public void setReduceScorePercent(int reduceScorePercent) {
		this.reduceScorePercent = reduceScorePercent;
	}
	
	public Boolean getActiveInMatch() {
		return activeInMatch;
	}
	
	public void setActiveInMatch(Boolean activeInMatch) {
		this.activeInMatch = activeInMatch;
	}
	
	public Boolean getActiveInRematch() {
		return activeInRematch;
	}
	
	public void setActiveInRematch(Boolean activeInRematch) {
		this.activeInRematch = activeInRematch;
	}

	
};
