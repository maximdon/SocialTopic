package com.softlib.imatch.model;

import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;

public class TermComparator implements IComparator {

	public boolean Compare(Object test, Object obj) {
		return ((TechnicalDictionaryTerm)obj).getTermText().equals((String)test);
	}

}
