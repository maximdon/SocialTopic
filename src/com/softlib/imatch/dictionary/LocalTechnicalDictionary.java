package com.softlib.imatch.dictionary;

import java.util.Collection;

import com.softlib.imatch.common.Pair;
import com.softlib.imatch.matcher.MatchCandidate;


public class LocalTechnicalDictionary extends TechnicalDictionary {

	final private TechnicalDictionary dictionary;
	final private boolean isSupportReduceTermFreq;
	final private boolean isSourceTicket;
	
	public LocalTechnicalDictionary(TechnicalDictionary dictionary,
									boolean isSupportReduceTermFreq, 
									boolean isSourceTicket) {
		super(false);
		this.dictionary = dictionary;
		this.isSupportReduceTermFreq = isSupportReduceTermFreq;
		this.isSourceTicket = isSourceTicket;
	}

	protected Collection<MatchCandidate> add2IndexRepository(TechnicalDictionaryTerm term) {
		return null;
	}
	
	protected void debugAddTerm(String debugText,TechnicalDictionaryTerm term) {
	}
	
	public Pair<TechnicalDictionaryTerm,Result> addTermAndResult(TechnicalDictionaryKey termKey) {		
		TechnicalDictionaryTerm term = dictionary.get(termKey);
		
		if (term!=null) {
			if (term.isDeletedByUser())
				return new Pair<TechnicalDictionaryTerm,Result>(null,Result.Deleted);
			return importTermAndResult(term);
		}

		if (isSourceTicket) {
			term = dictionary.getLowFreqTerm(termKey);
			if (term!=null) {
				if (term.isDeletedByUser())
					return new Pair<TechnicalDictionaryTerm,Result>(null,Result.Deleted);
				importTermAndResult(term);
				return new Pair<TechnicalDictionaryTerm,Result>(term,Result.LowFreq);
			}
			term = new TechnicalDictionaryTerm(termKey);
			setDefaultSource(term);
 			if (term!=null) {
				importTermAndResult(term);
				return new Pair<TechnicalDictionaryTerm,Result>(term,Result.ZeroFreq);
			}
		}

		return new Pair<TechnicalDictionaryTerm,Result>(term,Result.NotFound);
	}
	
	public TechnicalTermSource addSource(String sourceName) {
		return dictionary.getSource(sourceName);
	}		
	
	public void contextInitialized() {
		
	}
	
	protected boolean supportReduceTermFreq() {
		return isSupportReduceTermFreq;
	}

	
};
