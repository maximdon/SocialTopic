package com.softlib.imatch.model;

import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;

public class FrequencyGreaterThanComparator implements IComparator {

	public boolean Compare(Object test, Object obj) {
		return ((TechnicalDictionaryTerm)obj).getFrequency() > Integer.parseInt((String) test);
	}
}
