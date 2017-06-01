package com.softlib.imatch.distance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;

public class PositionsByTerm {

	private final Map<TechnicalDictionaryTerm,List<TermPosition>> map;

	public PositionsByTerm() {
		this.map = new HashMap<TechnicalDictionaryTerm, List<TermPosition>>();
	}
	
	public Map<TechnicalDictionaryTerm,List<TermPosition>> getMap() {
		return map;
	}
	
	public void add(TechnicalDictionaryTerm term,TermPosition position) {
		List<TermPosition> termPositions = map.get(term);
		if (termPositions==null)
			termPositions = new ArrayList<TermPosition>();
		termPositions.add(position);
		map.put(term, termPositions);	
	}

	public List<TechnicalDictionaryTerm> getTerms() {
		List<TechnicalDictionaryTerm> rc = new ArrayList<TechnicalDictionaryTerm>();
		
		List<TechnicalDictionaryTerm> terms = new ArrayList<TechnicalDictionaryTerm>();
		for (TechnicalDictionaryTerm term : map.keySet() ) 
			terms.add(term);
		
		for (TechnicalDictionaryTerm term : terms) {
			for (int i=0;i<map.get(term).size();i++) 
				rc.add(term);
		}
		
		return rc;
	}
	
	public String toString() {
		return map.toString();
	}
};
