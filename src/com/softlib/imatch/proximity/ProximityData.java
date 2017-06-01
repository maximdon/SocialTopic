package com.softlib.imatch.proximity;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.dictionary.TechnicalTermSource;

public class ProximityData {

	final static public String SEPERATOR = " 0 ";
	
	private List<TechnicalDictionaryTerm> terms;
	private String conjunction;
	private ProximityConfigRule rule;
	
	public ProximityData(TechnicalDictionaryTerm term1,
			 			 TechnicalDictionaryTerm term2, 
			 			ProximityConfigRule rule) {
		this(term1, "", term2, rule);
	}
		
	public ProximityData(TechnicalDictionaryTerm term1,
					     String conjunction,
						 TechnicalDictionaryTerm term2, 
						 ProximityConfigRule rule) {
		terms = new ArrayList<TechnicalDictionaryTerm>();
		terms.add(term1);
		terms.add(term2);
		this.rule = rule;
		this.conjunction = conjunction;
	}

	public List<TechnicalDictionaryTerm> getTerms() {
		return terms;
	}

	public TechnicalTermSource getProximitySource() {
		return rule.getProxSrc();
	}
	
	public String getRuleName() {
		return rule.getRuleName();
	}
	
	public String getText() {
		return getText(false);
	}
	
	public String getStemmedText() {
		return getText(true);
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
