package com.softlib.imatch.model;

import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;

public class SourceComparator implements IComparator {

	public boolean Compare(Object test, Object obj) {
		return ((TechnicalDictionaryTerm)obj).getTermSource().getSourceId() == Integer.valueOf(test.toString());
	}

}
