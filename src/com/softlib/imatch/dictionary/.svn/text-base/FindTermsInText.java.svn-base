package com.softlib.imatch.dictionary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.softlib.imatch.common.Pair;
import com.softlib.imatch.distance.TermPosition;
import com.softlib.imatch.distance.TermsByPositions;

/**
 * Finds all technical terms in the given text along with their relations.
 * The algorithm is quite simple:
 * 1. Check if the word is present, and if not skip it
 * 2. Find all technical terms containing this word (using TechnicalDictionaryTermWord#getRealTerms)
 * 3. Starting from the longest possible term try to find the longest term matching words in the given text
 * 4. For all technical terms found, add their relations as well (if withRelations is set)
 
 * Returns all technical terms in the dictionary corresponding to the given text.
 * Some terms are multi words, in this case the longest corresponding term is returned.
 * For example, if both abstract, class and abstract class are technical terms in the dictionary, 
 * then for sentence "Use interface instead of abstract class", the returned technical term is "abstract class"
 * But for sentence "Use interfaces instead of abstract classes", the returned technical terms are "abstract" and "class".
 * This is due to the fact that multi-word terms are not stemmed.   
 * Equals to findRelations(words, false)
 */

public class FindTermsInText {
	
	private final ThreadSafeTerms terms;
	private TermsNGrams termNGrams;
	
	private Map<TechnicalDictionaryKey,TechnicalDictionaryTerm> externTermsByKey;
	private Set<TechnicalTermSource> allowSources;	
	private int minLength = 1;
	private int maxLength = 4;

	public FindTermsInText(ThreadSafeTerms terms, TermsNGrams termNGrams) {
		this.terms = terms;
		allowSources = new HashSet<TechnicalTermSource>();
		externTermsByKey = new HashMap<TechnicalDictionaryKey, TechnicalDictionaryTerm>();
		this.termNGrams = termNGrams;
	}
	
	public void addExternTerms(Set<TechnicalDictionaryTerm> externTerms) {
		for (TechnicalDictionaryTerm term : externTerms)
			externTermsByKey.put(term.getTermKey(), term);
	}
	
	public void addAllowSources(Set<TechnicalTermSource> allowSources) {
		allowSources.addAll(allowSources);
	}

	public void addAllowSource(TechnicalTermSource allowSource) {
		allowSources.add(allowSource);
	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	public void setMinLength(int minLength) {
		this.minLength = minLength;
	}

	public TermsByPositions getFoundTerms(String[] words, boolean withRelations) {
		Integer currentPos = 0;
		int endPosition = 0;
		TermsByPositions rc = new TermsByPositions();
		StringBuilder s = new StringBuilder();
		
		while(currentPos < words.length) {
			TechnicalDictionaryTerm foundTerm = null;
			for(int i = maxLength; i >= minLength; --i) {
				s.delete(0, s.length());
				for(int j = currentPos; j < Math.min(currentPos + i, words.length); ++j)
					s.append(words[j]).append(" ");
				int actualIdx = currentPos + i < words.length ? i : words.length - currentPos;
				if(!termNGrams.containsString(s.toString()))
					continue;
				String termText = TechnicalDictionaryKey.cleanText(s.toString());
				if(termText.length() == 0) 
					continue;
				Pair<TechnicalDictionaryTerm,Boolean> result = get(termText);
				foundTerm = result.getLeft();
				if (foundTerm != null && !result.getRight() && (foundTerm.isDeletedByUser() || (allowSources.size() > 0 && !allowSources.contains(foundTerm.getTermSource())))) 
					foundTerm = null;
				if(foundTerm != null && currentPos + actualIdx > endPosition) {
					rc.add(foundTerm, new TermPosition(currentPos, currentPos + foundTerm.getNumTokens() - 1));
					endPosition = currentPos + actualIdx;
					currentPos ++;
					if(withRelations) {
						Collection<TechnicalDictionaryTerm> relations = foundTerm.getRelations();
						for (TechnicalDictionaryTerm relation : relations) 
							rc.add(relation, new TermPosition(currentPos, currentPos + relation.getNumTokens() - 1));
					}
					break;
				}
				else {
					foundTerm = null;
				}
			}			
			if(foundTerm == null) {
				currentPos ++;
			}
		}
		return rc;
	}
	
	public List<TechnicalDictionaryTerm> findShortestTerms(String[] words) {
		Integer currentPos = 0;
		int endPosition = 0;
		List<TechnicalDictionaryTerm> rc = new ArrayList<TechnicalDictionaryTerm>();
		StringBuilder s = new StringBuilder();
		
		while(currentPos < words.length) {
			TechnicalDictionaryTerm foundTerm = null;
			for(int i = minLength; i < maxLength; ++i) {
				s.delete(0, s.length());
				for(int j = currentPos; j < Math.min(currentPos + i, words.length); ++j)
					s.append(words[j]).append(" ");
				int actualIdx = currentPos + i < words.length ? i : words.length - currentPos;
				if(!termNGrams.containsString(s.toString()))
					continue;
				String termText = TechnicalDictionaryKey.cleanText(s.toString());
				if(termText.length() == 0) 
					continue;
				Pair<TechnicalDictionaryTerm,Boolean> result = get(termText);
				foundTerm = result.getLeft();
				if (foundTerm != null && foundTerm.isDeletedByUser()) 
					foundTerm = null;
				if(foundTerm != null && currentPos + actualIdx > endPosition) {
					rc.add(foundTerm);
					endPosition = currentPos + actualIdx;
					currentPos ++;
					break;
				}
				else {
					foundTerm = null;
				}
			}			
			if(foundTerm == null) {
				currentPos ++;
			}
		}
		return rc;
	}

		
	private Pair<TechnicalDictionaryTerm,Boolean> get(String text) {
		TechnicalDictionaryKey termKey = new TechnicalDictionaryKey(text);
		TechnicalDictionaryTerm term = getByKey(termKey);
		if (term != null && !term.isDeletedByUser() && term.getFrequency() >= 2)
			return new Pair<TechnicalDictionaryTerm, Boolean>(term,false);
		term = getExternByKey(termKey);
		if (term != null)
			return new Pair<TechnicalDictionaryTerm, Boolean>(term, true);
		return  new Pair<TechnicalDictionaryTerm, Boolean>(null,false);
	}

	private TechnicalDictionaryTerm getByKey(TechnicalDictionaryKey termKey) {		
		TechnicalDictionaryTerm term = terms.get(termKey);
		if (term!=null)
			return term;
		termKey.clean();
		return terms.get(termKey);
	}

	private TechnicalDictionaryTerm getExternByKey(TechnicalDictionaryKey termKey) {		
		TechnicalDictionaryTerm term = externTermsByKey.get(termKey);
		if (term!=null)
			return term;
		return externTermsByKey.get(termKey);
	}

};
