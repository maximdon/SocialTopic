package com.softlib.imatch.ticketprocessing;

import java.util.Collection;

import com.softlib.imatch.ITicket;
import com.softlib.imatch.MatcherException;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.common.StringUtils;
import com.softlib.imatch.dictionary.ITechnicalDictionary;
import com.softlib.imatch.dictionary.ITechnicalTermsContainer;
import com.softlib.imatch.dictionary.TechnicalDictionary;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;


public class FrequencyTerms extends BaseTicketProcessStep {

	public void run(String fieldName,ITicket ticket,ITechnicalTermsContainer termsContainer,StepContext context) throws MatcherException {
		
		TechnicalDictionary dictionary = (TechnicalDictionary) RuntimeInfo.getCurrentInfo().getBean("dictionary");			
		
		String cleanText = getData(fieldName,ticket,context);
		if (StringUtils.isEmpty(cleanText))
			return;
		
		String[] words = TechnicalDictionary.dictionaryTokenizer().split(cleanText);
		Collection<TechnicalDictionaryTerm> terms = dictionary.findInText(words);
		
		dictionary.startSession(fieldName,ticket.getId(),getStepName());
		for(TechnicalDictionaryTerm term : terms)
			if(!term.isDeletedByUser() && term.getTermSource().isRecalculateFrequencyEnabled() && !term.getSourceTickets().contains(ticket.getId()))
				//TODO think here do we need to add this ticket to the term_tickets?
				dictionary.addDocFreq(term.getTermKey());
		dictionary.endSession(0,null,false);
	}

	public String getStepName() {
		return "Frequency Terms";
	}

};
