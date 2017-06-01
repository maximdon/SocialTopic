package com.softlib.imatch.model;

import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;

public class TermContainsComparator implements IComparator {

	public boolean Compare(Object test, Object obj) {
		return ((TechnicalDictionaryTerm)obj).getTermText().contains((String)test);
	}

}
