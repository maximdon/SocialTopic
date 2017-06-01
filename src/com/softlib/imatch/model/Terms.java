package com.softlib.imatch.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;

public class Terms {
	private List<TechnicalDictionaryTerm> terms;
	
	public List<TechnicalDictionaryTerm> getTerms() {
		return terms;
	}

	public void setTerms(Collection<TechnicalDictionaryTerm> terms) {
		this.terms = new ArrayList<TechnicalDictionaryTerm>();
		for(TechnicalDictionaryTerm term : terms)
			if (term!=null)
				this.terms.add(term);
		
		Collections.sort(this.terms);
	}

	public List<TechnicalDictionaryTerm> select(String text, IComparator comparator)
	{
		List<TechnicalDictionaryTerm> selectedTerms = new ArrayList<TechnicalDictionaryTerm>();
		
		for (Iterator<TechnicalDictionaryTerm> iterator = this.terms.iterator(); iterator.hasNext();) {
			TechnicalDictionaryTerm term = (TechnicalDictionaryTerm) iterator.next();
			
			if (comparator.Compare(text, term)) {
				selectedTerms.add(term);
			}
		}
		
		return selectedTerms;
	}

	public void sort(Comparator<TechnicalDictionaryTerm> sorter,
			boolean ascending) 
	{
		if(ascending)
			Collections.sort(terms, sorter);
		else
			Collections.sort(terms, Collections.reverseOrder(sorter));
	}
}
