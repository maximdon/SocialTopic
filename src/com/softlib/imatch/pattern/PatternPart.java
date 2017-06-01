package com.softlib.imatch.pattern;

import java.util.ArrayList;
import java.util.List;

import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;

public class PatternPart {

	final private List<TechnicalDictionaryTerm> terms;
		
	PatternPart(List<TechnicalDictionaryTerm> terms) {
		this.terms = terms;
	}

	PatternPart(TechnicalDictionaryTerm anchor, TechnicalDictionaryTerm term) {
		terms = new ArrayList<TechnicalDictionaryTerm>();
		terms.add(anchor);
		if (term!=null)
			terms.add(term);
	}

	PatternPart(TechnicalDictionaryTerm anchor) {
		this(anchor,null);
	}

	public List<TechnicalDictionaryTerm> getTerms() {
		return terms;
	}

	public boolean isEmpty() {
		return terms.isEmpty();
	}

	public List<PatternPart> getList() {
		List<PatternPart> rc = new ArrayList<PatternPart>();
		rc.add(this);
		return rc;
	}
	
	public String toString() {
		return terms.toString();
	}
	
};
