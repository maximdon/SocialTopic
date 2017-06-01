package com.softlib.imatch.model;

import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;

public class StemmedTermContainsComparator implements IComparator {

	public boolean Compare(Object test, Object obj) {
		if (((TechnicalDictionaryTerm)obj).getTermStemmedText() != null)
			return ((TechnicalDictionaryTerm)obj).getTermStemmedText().contains((String)test);
		
		return false;
	}

}
