package com.softlib.imatch.dictionary;

public interface ITechnicalTermBooster  {
	
	float getBoost(TechnicalDictionaryTerm term);
	
	void init(TechnicalDictionary dictionary);

};
