package com.softlib.imatch.matcher;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;

@XStreamAlias("viewLayout")
public class ViewLayoutConfig 
{
	@XStreamConverter(ViewLayoutSettings.class)
	private ViewLayoutSettings resultColumns;
	
	@XStreamConverter(ViewLayoutOriginObjects.class)
	private ViewLayoutOriginObjects originObjects;
	
	private String detailsURL;	
	private boolean highlightZeroTerms;
	
	public void setResultColumns(ViewLayoutSettings resultColumns) {
		this.resultColumns = resultColumns;
	}

	public ViewLayoutSettings getResultColumns() {
		return resultColumns;
	}

	public void setOriginObjects(ViewLayoutOriginObjects originObjects) {
		this.originObjects = originObjects;
	}

	public ViewLayoutOriginObjects getOriginObjects() {
		return originObjects;
	}

	public void setDetailsURL(String detailsURL) {
		this.detailsURL = detailsURL;
	}

	public String getDetailsURL() {
		return detailsURL;
	}

	public void setHighlightZeroTerms(boolean highlightZeroTerms) {
		this.highlightZeroTerms = highlightZeroTerms;
	}

	public boolean isHighlightZeroTerms() {
		return highlightZeroTerms;
	}	
}
