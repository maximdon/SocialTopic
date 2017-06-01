package com.softlib.imatch.density;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.softlib.imatch.common.SubListCombinations;
import com.softlib.imatch.common.TracerFile;
import com.softlib.imatch.common.TracerFileLast;
import com.softlib.imatch.dictionary.TechnicalDictionary;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.distance.TermsByPositions;

public class ExtractDensityTerms  {

	static final private int MIN_TERMS = 3;
	static final private int MAX_TERMS = 5;
	
	private TechnicalDictionary dictionary;
	 
	private TracerFile traceFile = 
		TracerFileLast.create(TracerFileLast.Density,"result",false);

	synchronized public void init(TechnicalDictionary dictionary) {
		this.dictionary = dictionary;
	}
	
	private List<DensityData> createDensityData(List<TechnicalDictionaryTerm> terms) {
		List<DensityData> rc = new ArrayList<DensityData>();
		int numTerms = terms.size();
		if (numTerms<MIN_TERMS) {
			return rc;
		}
		if (numTerms>MAX_TERMS) {
			for (int i=0;i<numTerms-MAX_TERMS;i++)
				terms.remove(0);
		}
	
		SubListCombinations<TechnicalDictionaryTerm> combinations = 
			new SubListCombinations<TechnicalDictionaryTerm>(terms,MIN_TERMS);
		List<TechnicalDictionaryTerm> combination = combinations.getNextCombination();
		while (!combination.isEmpty()) {
			rc.add(new DensityData(combination));
			combination = combinations.getNextCombination();
		}

		return rc;	
	}
	
	public List<DensityData> process(String sentence)  {
		String split[] = sentence.split(" ");
		Collection<TechnicalDictionaryTerm> terms = dictionary.findInText(split);
		Set<TechnicalDictionaryTerm> currentSentenceTerms = new HashSet<TechnicalDictionaryTerm>();	
		for (TechnicalDictionaryTerm term : terms ) 
			currentSentenceTerms.add(term);
		List<TechnicalDictionaryTerm> termsList = 
			new ArrayList<TechnicalDictionaryTerm>(currentSentenceTerms);
		
		List<DensityData> rc = new ArrayList<DensityData>();
		if (currentSentenceTerms.size()>=MIN_TERMS) 
			rc = createDensityData(termsList);
		
		TermsByPositions termsByPositions = new TermsByPositions();
		termsByPositions.process(termsList,sentence);
		String text = termsByPositions.replace("-<",">-",true);

		if (traceFile.isActive() && rc.size()>0 ) {
			traceFile.close();
			traceFile = 
				TracerFileLast.create(TracerFileLast.Density,"result",false);
			traceFile.write("[S] "+text);
			for (DensityData data : rc)
				traceFile.write("[K] "+data.getText());
			traceFile.write(" ");
		}
		
		return rc;
	}	


};
