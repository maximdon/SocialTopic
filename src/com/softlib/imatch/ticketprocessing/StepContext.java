package com.softlib.imatch.ticketprocessing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.softlib.imatch.common.Pair;
import com.softlib.imatch.nlp.NLP.Chunk;

public class StepContext  {
	private Map<String, Object> stepsData = new HashMap<String, Object>();
	private String tempTicketId;
	private List<Chunk> chunks;
	private String[] sentences;
	private String nlpData;
	private Map<String,String[]> tokensBySentence;
	private Map<String,String[]> tagsBySentence;
	private boolean splitEnabled = true;
	private Pair<String,String> fieldTerms;
	
	public Pair<String, String> getFieldTerms() {
		return fieldTerms;
	}

	public void setFieldTerms(Pair<String, String> fieldTerms) {
		this.fieldTerms = fieldTerms;
	}

	public StepContext(String tempTicketId) {		
		this.tempTicketId = tempTicketId;
	}
	
	public Map<String, String[]> getTokensBySentence() {
		return tokensBySentence;
	}
	public void setTokensBySentence(Map<String, String[]> tokensBySentence) {
		this.tokensBySentence = tokensBySentence;
	}

	public Map<String, String[]> getTagsBySentence() {
		return tagsBySentence;
	}
	public void setTagsBySentence(Map<String, String[]> tagsBySentence) {
		this.tagsBySentence = tagsBySentence;
	}

	public String getNlpData() {
		return nlpData;
	}
	public void setNlpData(String nlpData) {
		this.nlpData = nlpData;
	}

	public void setStepData(String step, Object stepData) {
		this.stepsData.put(step, stepData);
	}
	public Object getStepData(String step) {
		return stepsData.get(step);
	}
	
	public List<Chunk> getChunks() {
		return chunks;
	}
	public void setChunks(List<Chunk> chunks) {
		this.chunks = chunks;
	}

	public String[] getSentences() {
		return sentences;
	}
	public void setSentences(String[] sentences) {
		this.sentences = sentences;
	}

	public String getCleanText() {
		return (String)getStepData("cleanStep");
	}
	public void setCleanText(String text) {
		setStepData("cleanStep", text);
	}

	public String getTempTicketId() {
		return tempTicketId;
	}

	public boolean isSplitEnabled() {
		return splitEnabled;
	}
	
	public void setSplitEnabled(boolean enabled) {
		splitEnabled = enabled;
	}
}
