package com.softlib.imatch.relations;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.tools.dictionaryparsers.KnownSynonyms;

public class KnownSynonymsAlgorithm implements IRelationAlgorithm{
	private static Logger log = Logger.getLogger(KnownSynonymsAlgorithm.class);
	
	//This map contains pairs of known synonyms (for example, abbreviation and its translation) 
	private Map<String, String> knownSynonyms = KnownSynonyms.getMap();

	public KnownSynonymsAlgorithm() {
		super();
	}

	public boolean relate(RelationAlgorithmContext context, TechnicalDictionaryTerm term, String[] termParts) {
		return false;
	}
	
	public void finish(RelationAlgorithmContext context) {
		for(Entry<String, String> knownSynonym: knownSynonyms.entrySet()) {
			try {				
				//Relate the known synonyms themselves
				TechnicalDictionaryTerm knownSynonymKeyTerm = context.dictionary.get(knownSynonym.getKey());
				TechnicalDictionaryTerm knownSynonymValueTerm = context.dictionary.get(knownSynonym.getValue());
				if(knownSynonymKeyTerm != null && knownSynonymValueTerm != null)
					context.relation.relateWithContaining(knownSynonymKeyTerm, knownSynonymValueTerm, "KnownSynonyms");
			}
			catch (Exception e) {
				e.printStackTrace();
				LogUtils.error(log,"buildRelations.knownSynonyms(%s): Exception = %s",knownSynonym,e.getMessage());
			}
		}
						

	}
	
}
