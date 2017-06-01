package com.softlib.imatch.ticketprocessing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.softlib.imatch.common.Pair;
import com.softlib.imatch.common.SourceMngr;
import com.softlib.imatch.common.SourceMngr.Type;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.dictionary.TechnicalTermSource;

public class DictionaryTermsMerge {
	
	static private class Checked {
		private String str;
		
		public Checked(TechnicalDictionaryTerm term) {
			str = term.getTermStemmedText();
		}
		public boolean replace(String subStr) {
			if (str.contains(subStr)) {
				str = str.replace(subStr, "");
				return true;
			}
			return false;
		}
	};
		
	static public List<TechnicalDictionaryTerm> mergeSubTerms(Collection<TechnicalDictionaryTerm> terms) {
		List<TechnicalDictionaryTerm> rc = new ArrayList<TechnicalDictionaryTerm>();

		List<Pair<TechnicalDictionaryTerm,Checked>> termsChecked = new ArrayList<Pair<TechnicalDictionaryTerm,Checked>>();
		for (TechnicalDictionaryTerm term : terms)
			termsChecked.add(new Pair<TechnicalDictionaryTerm,Checked>(term,new Checked(term)));
		
		for (TechnicalDictionaryTerm term : terms) {
			String termText = term.getTermStemmedText();
			boolean exist = false;
			for (Pair<TechnicalDictionaryTerm,Checked> pair : termsChecked) {
				if (pair.getLeft()==term)
					continue;
				if (pair.getRight().replace(termText)) {
					exist=true;
					break;
				}
			}
			if (!exist)
				rc.add(term);
		}
		return rc;
	}

	static public boolean isTermInSubTerms(Collection<TechnicalDictionaryTerm> terms,
									       TechnicalDictionaryTerm term) {
		String termText = term.getTermStemmedText();
		for (TechnicalDictionaryTerm existTerm : terms) {
			String existTermText = existTerm.getTermStemmedText();
			if (existTermText.contains(termText))
				return true;
		}
		return false;
	}
};
