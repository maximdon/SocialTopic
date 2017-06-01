package com.softlib.imatch.model;

import java.util.Comparator;

import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;

public class TermSorters 
{
	public static Comparator<TechnicalDictionaryTerm> idSorter = new Comparator<TechnicalDictionaryTerm>() {		
		@Override
		public int compare(TechnicalDictionaryTerm arg0, TechnicalDictionaryTerm arg1) {
			if(arg0.getTermId() > arg1.getTermId())
				return 1;
			else if(arg0.getTermId() == arg1.getTermId())
				return 0;
			else
				return -1;
		}
	};
	
	public static Comparator<TechnicalDictionaryTerm> descriptionSorter = new Comparator<TechnicalDictionaryTerm>() {		
		@Override
		public int compare(TechnicalDictionaryTerm arg0, TechnicalDictionaryTerm arg1) {
			return arg0.getTermText().compareTo(arg1.getTermText());
		}
	};

	public static Comparator<TechnicalDictionaryTerm> stemmedSorter = new Comparator<TechnicalDictionaryTerm>() {		
		@Override
		public int compare(TechnicalDictionaryTerm arg0, TechnicalDictionaryTerm arg1) {
			return arg0.getTermStemmedText().compareTo(arg1.getTermStemmedText());
		}
	};
	
	public static Comparator<TechnicalDictionaryTerm> frequencySorter = new Comparator<TechnicalDictionaryTerm>() {		
		@Override
		public int compare(TechnicalDictionaryTerm arg0, TechnicalDictionaryTerm arg1) {
			 return new Integer(arg0.getFrequency()).compareTo(arg1.getFrequency());
		}
	};

	public static Comparator<TechnicalDictionaryTerm> sourceSorter = new Comparator<TechnicalDictionaryTerm>() {		
		@Override
		public int compare(TechnicalDictionaryTerm arg0, TechnicalDictionaryTerm arg1) {
			 return new Float(arg0.getTermSource().getSourceBoost()).compareTo(arg1.getTermSource().getSourceBoost());
		}
	};
	
	public static Comparator<TechnicalDictionaryTerm> extractionMethodsSorter = new Comparator<TechnicalDictionaryTerm>() {		
		@Override
		public int compare(TechnicalDictionaryTerm arg0, TechnicalDictionaryTerm arg1) {
			 int firstTermNumMethods = arg0.getTermExtractionMethods().split(",").length;
			 int secondTermNumMethods = arg1.getTermExtractionMethods().split(",").length;
			 return new Integer(firstTermNumMethods).compareTo(secondTermNumMethods);
		}
	};
}
