package com.softlib.tools.dictionaryparsers;

import java.util.List;

import com.softlib.imatch.ITicket;
import com.softlib.imatch.dictionary.ITechnicalTermsContainer;
import com.softlib.imatch.dictionary.TechnicalDictionary;
import com.softlib.imatch.dictionary.TechnicalDictionaryKey;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.dictionary.TechnicalTermSource;
import com.softlib.imatch.ticketprocessing.ProcessedTicketWrite;

public class DualTermsContainer implements ITechnicalTermsContainer {

	private TechnicalDictionary dictionary;
	private ProcessedTicketWrite processedTicket;
	

	public DualTermsContainer(TechnicalDictionary dictionary,
							  ITicket originalTicket) {
		this.dictionary = dictionary;
		processedTicket = new ProcessedTicketWrite(dictionary, originalTicket);

	}

	@Override
	public void addDocFreq(TechnicalDictionaryKey key) {
		dictionary.addDocFreq(key);
		processedTicket.addDocFreq(key);
	}

	@Override
	public TechnicalTermSource addSource(String sourceName) {
		TechnicalTermSource rc = dictionary.addSource(sourceName);
		processedTicket.addSource(sourceName);
		return rc;
	}

	@Override
	public TechnicalDictionaryTerm addTerm(TechnicalDictionaryKey termKey) {
		TechnicalDictionaryTerm rc = dictionary.addTerm(termKey);
		processedTicket.addTerm(termKey);
		return rc;
	}
	
	@Override
	public TechnicalDictionaryTerm addTerm(TechnicalDictionaryKey termKey,
										   TechnicalTermSource termSource) {
		TechnicalDictionaryTerm rc = dictionary.addTerm(termKey,termSource);
		processedTicket.addTerm(termKey,termSource);
		return rc;
	}

	@Override
	public void endSession(float boostFactor, List<Float> itemsBoostFactors,boolean isRequired) {
		dictionary.endSession(boostFactor, itemsBoostFactors, isRequired);
		processedTicket.endSession(boostFactor, itemsBoostFactors, isRequired);		
	}

	@Override
	public TechnicalDictionary getDictionary() {
		return dictionary;
	}

	@Override
	public void reduceTermFreq(TechnicalDictionaryTerm term) {
		dictionary.reduceTermFreq(term);
		processedTicket.reduceTermFreq(term);
	}

	@Override
	public void startSession(String fieldName, String ticketId,String sourceName) {
		processedTicket.startSession(fieldName, ticketId, sourceName);
		dictionary.startSession(fieldName, ticketId, sourceName);
	}

	@Override
	public void finish() {
		processedTicket.finish();
	}

	@Override
	public void freezeTerms() {
		processedTicket.freezeTerms();
		dictionary.freezeTerms();
	}

	public String toString() {
		return processedTicket.toString();
	}


};
