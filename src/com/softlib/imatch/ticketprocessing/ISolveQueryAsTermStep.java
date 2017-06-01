package com.softlib.imatch.ticketprocessing;

import com.softlib.imatch.ITicket;
import com.softlib.imatch.MatcherException;
import com.softlib.imatch.common.StringUtils;
import com.softlib.imatch.dictionary.ITechnicalTermsContainer;
import com.softlib.imatch.dictionary.TechnicalDictionaryKey;

public class ISolveQueryAsTermStep extends BaseTicketProcessStep 
{
	private int numWordsInQuery;
	public ISolveQueryAsTermStep()
	{
		this(2);
	}
	
	public ISolveQueryAsTermStep(int numWordsInQuery)
	{
		this.numWordsInQuery = numWordsInQuery;
	}
	
	@Override
	public String getStepName() {		
		return "iSolve Query as Term";
	}

	@Override
	public void run(String fieldName, ITicket ticket,
			ITechnicalTermsContainer termsContainer, StepContext context)
			throws MatcherException {
		String cleanText = getData(fieldName,ticket,context);
		if(StringUtils.isEmpty(cleanText))
			return;
		if(termsContainer instanceof IProcessedTicket) {
			IProcessedTicket processedTicket = (IProcessedTicket)termsContainer;
			if(processedTicket.getAllTerms(false).size() > 0)
				return;
		}
		String[] words = cleanText.split(" ");
		if(words.length <= numWordsInQuery) {
			termsContainer.startSession(fieldName, ticket.getId(), getStepName());
			String termText = "";
			for(int i = 0; i < words.length; ++i)
				termText += words[i] + " ";
			termText = termText.substring(0, termText.length() - 1);
			termsContainer.addTerm(new TechnicalDictionaryKey(termText));
			termsContainer.endSession(0, null, false);
		}
	}
}
