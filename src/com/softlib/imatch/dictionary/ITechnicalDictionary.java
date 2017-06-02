package com.softlib.imatch.dictionary;

import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * This interface represents read-only technical dictionary.
 * Dictionary contains technical terms and abbreviations and their relations (synonyms, definitions)
 * Note, dictionary terms can be disabled, in this case they are kept in the repository, but not in the memory
 * @author Maxim Donde
 *
 */
public interface ITechnicalDictionary {

	/**
	 * @return number of enabled terms in this dictionary 
	 */
	public abstract int getTermsCount();

	/**
	 * Check if the term with given text exists in the dictionary 
	 * @param text
	 * @return
	 */
	
	public abstract boolean contains(TechnicalDictionaryKey termKey);

	/**
	 * Returns the term with the given text
	 * @param text
	 * @return
	 */
	public abstract TechnicalDictionaryTerm get(String text);

	/**
	 * Returns the term with the given key
	 * @param text
	 * @return
	 */
	public abstract TechnicalDictionaryTerm get(TechnicalDictionaryKey key);

	/**
	 * Returns iterator over terms in the dictionary 
	 * @return
	 */
	public Iterator<TechnicalDictionaryTerm> termsIterator();
		
	public TermsInTextFinder getFinder();

	public TechnicalTermSource getSource(String sourceName);

	public List<TechnicalDictionaryTerm> findContainingTerms(TechnicalDictionaryTerm term);
}