package com.softlib.imatch.dictionary;


import java.util.Collection;

import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.common.Pair;
import com.softlib.imatch.dictionary.TechnicalDictionary.Result;
import com.softlib.imatch.matcher.MatchCandidate;

public class WriteTechnicalDictionary extends TechnicalDictionary {
	
	private TechnicalDictionary dictionary;

	public WriteTechnicalDictionary(TechnicalDictionary dictionary) {
		super(false);
		this.dictionary = dictionary;
	}

	protected Collection<MatchCandidate> add2IndexRepository(TechnicalDictionaryTerm term) {
		return null;
	}
	
	protected void debugAddTerm(String debugText,TechnicalDictionaryTerm term) {
	}
	
	public Pair<TechnicalDictionaryTerm,Result> addTermAndResult(TechnicalDictionaryKey termKey) {
		TechnicalDictionaryTerm term = dictionary.get(termKey, true);
		Result result;
		if(term == null)
			result = Result.NotFound;
		else
			result = Result.Exist;
		return new Pair<TechnicalDictionaryTerm, Result>(term, result);
	}
	
	public TechnicalTermSource addSource(String sourceName) {
		return dictionary.getSource(sourceName);
	}		
	
	public void contextInitialized() {
		
	}

};
