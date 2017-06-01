package com.softlib.imatch.relations;

import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;

public interface IRelationAlgorithm {
	
	boolean relate(RelationAlgorithmContext context, TechnicalDictionaryTerm term, String[] termParts);
	void finish(RelationAlgorithmContext context);

}
