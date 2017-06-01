package com.softlib.tools.dictionaryapi;

import java.util.ArrayList;
import java.util.List;

import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;

public class DictAPIResponseDataTerm {

	private TechnicalDictionaryTerm term;
	private List<TechnicalDictionaryTerm> relations;
	
	public DictAPIResponseDataTerm(TechnicalDictionaryTerm term) {
		this.term = term;
		relations = new ArrayList<TechnicalDictionaryTerm>();
		for (TechnicalDictionaryTerm relation : term.getRelations()) {
			relations.add(relation);
		}
	}
	
	public void addRelation(TechnicalDictionaryTerm term) {
		relations.add(term);
	}
	
	public TechnicalDictionaryTerm getTerm() {
		return term;
	}
	
	public List<TechnicalDictionaryTerm> getRelations() {
		return relations;
	}
	
	
};
