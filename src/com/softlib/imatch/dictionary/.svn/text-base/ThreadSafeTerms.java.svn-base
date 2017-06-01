package com.softlib.imatch.dictionary;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.softlib.imatch.common.ReadWriteLock;

public class ThreadSafeTerms
{
	private ConcurrentMap<TechnicalDictionaryKey,TechnicalDictionaryTerm> terms;

	public ThreadSafeTerms() {
		terms = new ConcurrentHashMap<TechnicalDictionaryKey, TechnicalDictionaryTerm>();
	}

	public int size() {
		return terms.size();
	}

	public boolean containsKey(TechnicalDictionaryKey termKey) {
		return terms.containsKey(termKey);
	}

	public TechnicalDictionaryTerm get(TechnicalDictionaryKey termKey) {
		return terms.get(termKey);
	}

	public void put(TechnicalDictionaryKey termKey,TechnicalDictionaryTerm term) {
		terms.put(termKey,term);
	}

	public TechnicalDictionaryTerm remove(TechnicalDictionaryKey termKey) {
		return terms.remove(termKey);
	}

	public Collection<TechnicalDictionaryTerm> values() {
		return terms.values();
	}

	public void clear() {		
		terms.clear();
	}
	
	public String toString() {
		String rc = "";
		int idx = 0;
		for (TechnicalDictionaryTerm term : terms.values()) {
			if (!rc.isEmpty())
				rc+=",";
			rc += term.getTermText();
			if (idx>10)
				break;
		}
		return rc;
	}

};
