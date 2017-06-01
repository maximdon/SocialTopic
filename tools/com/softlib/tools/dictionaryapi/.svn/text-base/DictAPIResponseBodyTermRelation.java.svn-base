package com.softlib.tools.dictionaryapi;

import org.apache.commons.lang.StringEscapeUtils;

import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.dictionary.TechnicalTermSource;
import com.softlib.imatch.dictionary.TermToTermRelationAttributes;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

public class DictAPIResponseBodyTermRelation {

	@XStreamAsAttribute
	private String text;

	@XStreamAsAttribute
	private String stemmedText;
	
	@XStreamAsAttribute
	private String boost;

	@XStreamAsAttribute
	private String reliable;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getStemmedText() {
		return stemmedText;
	}

	public void setStemmedText(String stemmedText) {
		this.stemmedText = stemmedText;
	}

	public String getBoost() {
		return boost;
	}

	public void setBoost(String boost) {
		this.boost = boost;
	}

	public String getReliable() {
		return reliable;
	}

	public void setReliable(String reliable) {
		this.reliable = reliable;
	}

	private String getReliable(TechnicalDictionaryTerm term,TechnicalDictionaryTerm relation) {
		return String.valueOf(TermToTermRelationAttributes.isReliable(term, relation)); 
	}
	
	public DictAPIResponseBodyTermRelation(TechnicalDictionaryTerm term,TechnicalDictionaryTerm relation) {
		reliable = getReliable(term,relation);
		text = StringEscapeUtils.escapeHtml(relation.getTermText());
		stemmedText = StringEscapeUtils.escapeHtml(relation.getTermStemmedText());
		boost = Double.toString(relation.getTermSource().getSourceBoost());
	}

}
