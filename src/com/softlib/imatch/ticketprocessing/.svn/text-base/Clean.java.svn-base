package com.softlib.imatch.ticketprocessing;

import java.util.List;

import com.softlib.imatch.ITicket;
import com.softlib.imatch.MatcherException;
import com.softlib.imatch.common.StringUtils;
import com.softlib.imatch.dictionary.ITechnicalTermsContainer;

public class Clean extends RegexFileStep {

	public Clean() {
	}

	
	@Override
	public void run(String fieldName, ITicket ticket,
			ITechnicalTermsContainer termsContainer, StepContext context)
			throws MatcherException {
		if(fieldName.equals("Url"))
			return;
		super.run(fieldName, ticket, termsContainer, context);
	}

	@Override
	protected void complete(ITicket ticket, String fieldName, List<String> matches, ITechnicalTermsContainer container, StepContext context) {
		context.setCleanText(StringUtils.join(matches, "\n"));
	}

	@Override
	protected String getFileName() {
		return "clean.txt";
	}

	@Override
	protected boolean isSingleOperationMode() {
		return false;
	}

};
