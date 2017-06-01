package com.softlib.tools.dictionaryparsers;

import com.softlib.imatch.dictionary.ITechnicalDictionary;
import com.softlib.imatch.dictionary.SynonymsRelation;
import com.softlib.imatch.dictionary.TechnicalDictionaryKey;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;

public class KnownSynonymRelation {

	private String text;
	private TechnicalDictionaryTerm term;
	private ITechnicalDictionary dictionary;
	private static SynonymsRelation relation = new SynonymsRelation();
	
	public KnownSynonymRelation(String text, TechnicalDictionaryTerm term, ITechnicalDictionary dictionary) {
		this.term = term;
		this.text = text;
		this.dictionary = dictionary;
	}

	public TechnicalDictionaryTerm getTerm() {
		return term;
	}

	public String getText() {
		return text;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		KnownSynonymRelation other = (KnownSynonymRelation) obj;
		if (text == null) {
			if (other.text == null)
				return true;
		} 
		else if (text.trim().equals(other.text.trim()))
			return true;
		else {
			//Check if underlined terms are related.
			//Note, the text already stemmed
			TechnicalDictionaryTerm term = dictionary.get(new TechnicalDictionaryKey(text, false));
			TechnicalDictionaryTerm otherTerm = dictionary.get(new TechnicalDictionaryKey(other.text, false));
			if(term != null && otherTerm != null && relation.isRelated(term, otherTerm))
				return true;
		}
		return false;
	}
	
	
}
