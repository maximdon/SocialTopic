package com.softlib.imatch.ticketprocessing;

import com.softlib.imatch.ITicket;
import com.softlib.imatch.MatcherException;
import com.softlib.imatch.dictionary.ITechnicalTermsContainer;

public interface ITicketProcessStep {
	
	void run(String fieldName,ITicket ticket,ITechnicalTermsContainer termsContainer, StepContext context) throws MatcherException;

	void end();
	
	String getStepName();

};
