package com.softlib.imatch.density;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;

public class DensityData {

	final static public String SEPERATOR = " 0 ";

	private List<TechnicalDictionaryTerm> terms;
	
	public DensityData(List<TechnicalDictionaryTerm> terms) {
		this.terms = terms;
	}

	public List<TechnicalDictionaryTerm> getTerms() {
		return terms;
	}

	public String getStemmedText() {
		return getText(true);
	}
	
	public String getText() {
		return getText(false);
	}
	
	private String getText(boolean stem) {
		SortedSet<TechnicalDictionaryTerm> sortedTerms = 
			new TreeSet<TechnicalDictionaryTerm>(terms);
		String rc = "";
		for (TechnicalDictionaryTerm term : sortedTerms) {
			String text = (stem ? term.getTermStemmedText() : term.getTermText());
			if (!rc.isEmpty())
				rc += SEPERATOR; 
			rc += text;
		}
		return rc;
	}
	
	
};
