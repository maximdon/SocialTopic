package com.softlib.imatch.ticketprocessing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.softlib.imatch.ITicket;
import com.softlib.imatch.MatcherException;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.common.StringUtils;
import com.softlib.imatch.dictionary.FindTermsInText;
import com.softlib.imatch.dictionary.ITechnicalDictionary;
import com.softlib.imatch.dictionary.ITechnicalTermsContainer;
import com.softlib.imatch.dictionary.TechnicalDictionary;
import com.softlib.imatch.dictionary.TechnicalDictionaryKey;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.distance.TermsByPositions;


public class DictionaryTerms extends BaseTicketProcessStep {

	public static final String STEP_NAME = "DictionaryTerms";

	public void run(String fieldName,ITicket ticket,ITechnicalTermsContainer termsContainer,StepContext context) throws MatcherException {
			
		ITechnicalDictionary dictionary = (ITechnicalDictionary) RuntimeInfo.getCurrentInfo().getBean("dictionary");			
		
		String cleanText = getData(fieldName,ticket,context);
		if(StringUtils.isEmpty(cleanText))
			return;

		List<TechnicalDictionaryTerm> dictTerms = new ArrayList<TechnicalDictionaryTerm>();

		String[] words = TechnicalDictionary.dictionaryTokenizer().split(cleanText);
		
		FindTermsInText findAllTerms = dictionary.getFindTermsInText();
		TermsByPositions allTermsByPositions = findAllTerms.getFoundTerms(words,false);
		dictTerms.addAll(allTermsByPositions.getTerms());

		FindTermsInText findUserTerms = dictionary.getFindTermsInText();
		findUserTerms.setMinLength(5);
		findUserTerms.setMaxLength(9);
		findUserTerms.addAllowSource(dictionary.getSource("User Defined"));
		TermsByPositions userTermsByPositions = findUserTerms.getFoundTerms(words,false);
		dictTerms.addAll(userTermsByPositions.getTerms());
		
		FindTermsInText findQuoteTerms = dictionary.getFindTermsInText();
		findQuoteTerms.setMinLength(5);
		findQuoteTerms.setMaxLength(9);
		findQuoteTerms.addAllowSource(dictionary.getSource("quoteTerms"));
		TermsByPositions quoteTermsByPositions = findQuoteTerms.getFoundTerms(words,false);
		dictTerms.addAll(quoteTermsByPositions.getTerms());
		
		if (termsContainer instanceof ProcessedTicket) {
			ProcessedTicket processedTicket = (ProcessedTicket)termsContainer;
			
			Collection<TechnicalDictionaryTerm> existTerms = new ArrayList<TechnicalDictionaryTerm>();
			ProcessedField processedField = processedTicket.getField(fieldName);
			if (processedField!=null)
				existTerms.addAll(processedField.getTerms());
			
			List<TechnicalDictionaryTerm> newTerms = getNewTerms(dictTerms,existTerms);
			termsContainer.startSession(fieldName,null, getStepName());
			for(TechnicalDictionaryTerm term : newTerms) {
				TechnicalDictionaryKey termKey = term.getTermKey();
				termsContainer.addTerm(termKey);
			}
			termsContainer.endSession(0, null, false);
			termsContainer.freezeTerms();
		}
		
	}

	private List<TechnicalDictionaryTerm> getNewTerms(List<TechnicalDictionaryTerm> dictTerms,
													  Collection<TechnicalDictionaryTerm> existTerms) {
		List<TechnicalDictionaryTerm> tempDictTerms = new ArrayList<TechnicalDictionaryTerm>(dictTerms);
		List<TechnicalDictionaryTerm> tempExistTerms = new ArrayList<TechnicalDictionaryTerm>(existTerms);
		for (TechnicalDictionaryTerm term : dictTerms) {
			if (tempExistTerms.contains(term) && 
				tempDictTerms.contains(term)) {
				tempDictTerms.remove(term);
				tempExistTerms.remove(term);
			}
		}
		return tempDictTerms;
	}
	
	public String getStepName() {
		return STEP_NAME;
	}

};
