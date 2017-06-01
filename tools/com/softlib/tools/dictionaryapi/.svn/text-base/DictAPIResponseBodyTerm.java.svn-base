package com.softlib.tools.dictionaryapi;


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;

import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("term")
public class DictAPIResponseBodyTerm {

	@XStreamAsAttribute
	private String text;

	@XStreamAsAttribute
	private String stemmedText;

	@XStreamAsAttribute
	private String origText;

	@XStreamAsAttribute
	private String boost;

	@XStreamImplicit(itemFieldName="Term")
	private List<DictAPIResponseBodyTermRelation> relations;

	public List<DictAPIResponseBodyTermRelation> getRelations() {
		return relations;
	}

	public void setRelations(List<DictAPIResponseBodyTermRelation> relations) {
		this.relations = relations;
	}

	public String getBoost() {
		return boost;
	}

	public void setBoost(String boost) {
		this.boost = boost;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	public String getOrigText() {
		return origText;
	}

	public void setOrigText(String orgText) {
		this.origText = orgText;
	}

	public String getStemmedText() {
		return stemmedText;
	}

	public void setStemmedText(String stemmedText) {
		this.stemmedText = stemmedText;
	}

	public DictAPIResponseBodyTerm(DictAPIResponseDataTerm termData,String originalText) {
		TechnicalDictionaryTerm term = termData.getTerm();
		text = StringEscapeUtils.escapeHtml(term.getTermText());		
		origText = StringEscapeUtils.escapeHtml(originalText);
		stemmedText = StringEscapeUtils.escapeHtml(term.getTermStemmedText());
		boost = Double.toString(term.getTermSource().getSourceBoost());
		relations = new ArrayList<DictAPIResponseBodyTermRelation>();
		for (TechnicalDictionaryTerm relation : termData.getRelations()) {
			relations.add(new DictAPIResponseBodyTermRelation(term,relation));
		}
	}

	
}
