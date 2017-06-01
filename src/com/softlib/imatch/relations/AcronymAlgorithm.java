package com.softlib.imatch.relations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;

public class AcronymAlgorithm implements IRelationAlgorithm {

	private static Logger log = Logger.getLogger(AcronymAlgorithm.class);

	private final static int AcronymRelationAlg = 8;

	//Possible acronyms map, for each possible acronym contains a list of possible meanings
	private Map<TechnicalDictionaryTerm, List<TechnicalDictionaryTerm>> possibleAcronyms = new HashMap<TechnicalDictionaryTerm, List<TechnicalDictionaryTerm>>();
	private List<Integer> acronymSources = new ArrayList<Integer>();
	
	public AcronymAlgorithm() {
		super();		
		//By default only terms from Long Terms source are relevant
		acronymSources.add(13);
	}
	
	public void setAcronymSources(List<Integer>acronymSources)
	{
		this.acronymSources = acronymSources;
	}
	
	public boolean relate(RelationAlgorithmContext context, TechnicalDictionaryTerm term, String[] termParts) {
		//Handle acronyms
		int supportedRelationsAlgs = term.getTermSource().getSupportedRelationsAlgs();
		int numWordsInTerm = termParts.length;
		//Acronyms could be 2 to 4 character
		int minAcronymLength = 2;
		int maxAcronymLength = 4;
		//List<Integer>acronymNotRelevantSources = Arrays.asList(7, 8, 9, 11, 12, 13, 14, 15, 16, 18, 19, 21, 22, 23, 24, 25, 30, 31, 32, 33, 35, 36, 37, 38, 40, 41, 101, 103);		
		if(numWordsInTerm >= minAcronymLength && 
				numWordsInTerm <= maxAcronymLength &&
				(supportedRelationsAlgs & AcronymRelationAlg) == AcronymRelationAlg) 
		{
			String acronym = "";
			for(String termPart : termParts)
				acronym += termPart.charAt(0);
			TechnicalDictionaryTerm acronymTerm = context.dictionary.get(acronym);
			if(acronymTerm != null && acronymSources.contains(acronymTerm.getTermSource().getSourceId())) {
				List<TechnicalDictionaryTerm>possibleMeanings = possibleAcronyms.get(acronymTerm);
				if(possibleMeanings == null)
					possibleMeanings = new ArrayList<TechnicalDictionaryTerm>();
				possibleMeanings.add(term);
				possibleAcronyms.put(acronymTerm, possibleMeanings);
			}
		}
		return false;
	}
	public void finish(RelationAlgorithmContext context) {
		//Possible acronyms complete
		for(Entry<TechnicalDictionaryTerm, List<TechnicalDictionaryTerm>> possibleAcronym : possibleAcronyms.entrySet()) {
			TechnicalDictionaryTerm acronymTerm = possibleAcronym.getKey();
			List<TechnicalDictionaryTerm>acronymMeanings = possibleAcronym.getValue();
			if(acronymMeanings.size() == 1) {
				//Only one meaning found, relate automatically
				TechnicalDictionaryTerm acronymMeaning = acronymMeanings.get(0);
				LogUtils.info(log, "Acronym found: '%s'='%s'", acronymTerm, acronymMeaning);
				context.relation.relateWithContaining(acronymTerm, acronymMeaning, "Acronym");
			}
			else if(acronymMeanings.size() < 4) {
				//Possible acronyms, don't relate but just log
				for(TechnicalDictionaryTerm acronymMeaning : acronymMeanings)
					LogUtils.info(log, "Possible acronym found(please relate manually): '%s'='%s'", acronymTerm, acronymMeaning);
			}
		}
	}

}
