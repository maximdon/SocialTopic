package com.softlib.imatch.dictionary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.common.LogUtils;
//TODO handle delete transitive relations on delete relation
/**
 * This class represents synonyms relation between two terms. 
 * Synonym relation is symmetric (i.e. if term1 -> term2, then term2->term1) 
 * and transitive (i.e. if term1->term2 and term2->term3, then term1->term3)
 * So the relate method implementation is recursive to support both symmetric and transitive native 
 */
public class SynonymsRelation extends TechnicalTermRelation {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6739572184936467478L;
	private static Logger log = Logger.getLogger(SynonymsRelation.class);
	
	public SynonymsRelation() {
	}
		
	public SynonymsRelation(ITechnicalDictionary dictionary) {	
		super(dictionary);
	}

	@Override
	public void relate(TechnicalDictionaryTerm term1,
			TechnicalDictionaryTerm term2, String relationRule) {
		List<TechnicalDictionaryTerm> visitedRelations1 = new ArrayList<TechnicalDictionaryTerm>();
		List<TechnicalDictionaryTerm> visitedRelations2 = new ArrayList<TechnicalDictionaryTerm>();
		//Relate term1 to term2 
		relate(term1, term2, visitedRelations1);
		//Relate term2 to term1 
		relate(term2, term1, visitedRelations2);	
		logRelation(term1, term2, relationRule);
	}
	
	private void relate(TechnicalDictionaryTerm term1,
			TechnicalDictionaryTerm term2, List<TechnicalDictionaryTerm> visitedRelations) {
		if(isRelated(term1, term2))
			return;
		LogUtils.debug(log, "Relating terms '%s' and '%s'", term1, term2);
		term1.addRelation(term2);
		//Copy the collection to prevent modification exception
		Collection<TechnicalDictionaryTerm> relatedTerms = new ArrayList<TechnicalDictionaryTerm>(term1.getRelations());
		if(relatedTerms != null) {
			visitedRelations.add(term1);
			for(TechnicalDictionaryTerm relatedTerm : relatedTerms) { 
				if(!visitedRelations.contains(relatedTerm)) {
					relate(term2, relatedTerm, visitedRelations);
					relate(relatedTerm, term2, visitedRelations);
				}
			}
		}		
	}
}
