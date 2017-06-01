package com.softlib.imatch.dictionary;

import java.util.List;

public interface ITechnicalTermsContainer {

	TechnicalDictionary getDictionary();

	void startSession(String fieldName,String ticketId, String sourceName);
	
	TechnicalDictionaryTerm addTerm(TechnicalDictionaryKey termKey);
	
	TechnicalTermSource addSource(String sourceName);

	void addDocFreq(TechnicalDictionaryKey key);
	
	void reduceTermFreq(TechnicalDictionaryTerm term);

	void endSession(float boostFactor, 
				   	List<Float> itemsBoostFactors,
				    boolean isRequired);
	
	void finish();
	
    void freezeTerms();


	TechnicalDictionaryTerm addTerm(TechnicalDictionaryKey termKey,	TechnicalTermSource termSource);

};
