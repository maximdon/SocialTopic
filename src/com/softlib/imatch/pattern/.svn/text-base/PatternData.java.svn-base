package com.softlib.imatch.pattern;

import java.util.ArrayList;
import java.util.List;

import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;


public class PatternData implements Cloneable {

	final private String source;
	final private List<TechnicalDictionaryTerm> terms;
		
	PatternData(String source) {
		this(source,null);
	}

	PatternData(String source, List<TechnicalDictionaryTerm> terms) {
		this.source = source;
		if (terms==null)
			this.terms = new ArrayList<TechnicalDictionaryTerm>();
		else
			this.terms = terms;
	}

	public String getSource() {
		return source;
	}
	
	public List<TechnicalDictionaryTerm> getTerms() {
		return terms;
	}

	public PatternData clone() {
		return new PatternData(source,new ArrayList<TechnicalDictionaryTerm>(terms));
	}
	
};
