package com.softlib.imatch.model;

import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;

public class TermPredicates 
{
	public static IPredicate<TechnicalDictionaryTerm> startsWithPredicate = new IPredicate<TechnicalDictionaryTerm>() {
	    public boolean apply(TechnicalDictionaryTerm term, String test) {
	        return term.getTermText().startsWith(test);
	    }
	};
	
	public static IPredicate<TechnicalDictionaryTerm> containsPredicate = new IPredicate<TechnicalDictionaryTerm>() {
	    public boolean apply(TechnicalDictionaryTerm term, String test) {
	        return term.getTermText().contains(test);
	    }
	};
	
	public static IPredicate<TechnicalDictionaryTerm> equalsPredicate = new IPredicate<TechnicalDictionaryTerm>() {
	    public boolean apply(TechnicalDictionaryTerm term, String test) {
	        return term.getTermText().equals(test);
	    }
	};
	
	public static IPredicate<TechnicalDictionaryTerm> stemStartsWithPredicate = new IPredicate<TechnicalDictionaryTerm>() {
	    public boolean apply(TechnicalDictionaryTerm term, String test) {
	        return term.getTermStemmedText().startsWith(test);
	    }
	};
	
	public static IPredicate<TechnicalDictionaryTerm> stemContainsPredicate = new IPredicate<TechnicalDictionaryTerm>() {
	    public boolean apply(TechnicalDictionaryTerm term, String test) {
	        return term.getTermStemmedText().contains(test);
	    }
	};
	
	public static IPredicate<TechnicalDictionaryTerm> stemEqualsPredicate = new IPredicate<TechnicalDictionaryTerm>() {
	    public boolean apply(TechnicalDictionaryTerm term, String test) {
	        return term.getTermStemmedText().equals(test);
	    }
	};
	
	public static IPredicate<TechnicalDictionaryTerm> sourcePredicate = new IPredicate<TechnicalDictionaryTerm>() {
	    public boolean apply(TechnicalDictionaryTerm term, String test) {
	        return term.getTermSource().getSourceId() == Integer.valueOf(test.toString());
	    }
	};		
	
	public static IPredicate<TechnicalDictionaryTerm> idPredicate = new IPredicate<TechnicalDictionaryTerm>() {
	    public boolean apply(TechnicalDictionaryTerm term, String test) {
	        return term.getTermId() == Integer.parseInt(test.toString());
	    }
	};		
	
	public static IPredicate<TechnicalDictionaryTerm> freqGtPredicate = new IPredicate<TechnicalDictionaryTerm>() {
	    public boolean apply(TechnicalDictionaryTerm term, String test) {
	        return term.getFrequency() >= Integer.parseInt(test);
	    }
	};		
	
	public static IPredicate<TechnicalDictionaryTerm> freqEqPredicate = new IPredicate<TechnicalDictionaryTerm>() {
	    public boolean apply(TechnicalDictionaryTerm term, String test) {
	        return term.getFrequency() == Integer.parseInt(test);
	    }
	};		
	
	public static IPredicate<TechnicalDictionaryTerm> extractionMethodsPredicate = new IPredicate<TechnicalDictionaryTerm>() {
	    public boolean apply(TechnicalDictionaryTerm term, String test) {
	        return term.getTermExtractionMethods().contains(test);
	    }
	};		
	
}
