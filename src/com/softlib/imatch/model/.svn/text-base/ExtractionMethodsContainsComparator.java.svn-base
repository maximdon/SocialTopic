package com.softlib.imatch.model;

import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;

public class ExtractionMethodsContainsComparator implements IComparator {

	public boolean Compare(Object test, Object obj) {
		if (((TechnicalDictionaryTerm)obj).getTermExtractionMethods() != null)
			//Make comparison case insensitive
			return ((TechnicalDictionaryTerm)obj).getTermExtractionMethods().toLowerCase().contains(((String)test).toLowerCase());
		
		return false;
	}

}
