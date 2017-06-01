package com.softlib.imatch.relations;

import java.util.Arrays;
import java.util.Collection;

import com.softlib.imatch.common.StringUtils;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;

public class GeneralDelimitersAlgorithm implements IRelationAlgorithm {
	private final static int DelimeterRelationAlg = 1;
	
	public GeneralDelimitersAlgorithm() {
		super();
	}
	
	public boolean relate(RelationAlgorithmContext context, TechnicalDictionaryTerm term, String[] termParts) {
		//Handle delimiter synonyms
		boolean foundRelation = false;
		int supportedRelationsAlgs = term.getTermSource().getSupportedRelationsAlgs();
		int numWordsInTerm = termParts.length;
		Collection<String> termPartsCol = Arrays.asList(termParts);
		if(numWordsInTerm > 1 &&
			(supportedRelationsAlgs & DelimeterRelationAlg) == DelimeterRelationAlg) 
		{
			String[] delimiters = new String[]{"-", "", " ", "/"};
			for(String delimiter : delimiters) {
				String relatedText = StringUtils.join(termPartsCol, delimiter);
				TechnicalDictionaryTerm relatedTerm = context.dictionary.get(relatedText);
				if(relatedTerm != null && !term.equals(relatedTerm)) {
					context.relation.relateWithContaining(term, relatedTerm, "General Delimiters");
					foundRelation = true;
				}
			}
		}
		return foundRelation;
	
	}
	public void finish(RelationAlgorithmContext context) {
	}

}
