package com.softlib.imatch.ticketprocessing;

import java.util.ArrayList;
import java.util.List;

import com.softlib.imatch.dictionary.ITechnicalTermsContainer;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.dictionary.TechnicalTermSource;

public class LongTermsTechTokens extends TechTokens {

	@Override
	protected void addFieldData(List<String> fieldData, StepContext context, ITechnicalTermsContainer container) {
		List<String> removed = new ArrayList<String>();
		List<String> added = new ArrayList<String>();
		
		for (String termStr : fieldData) {
			if (termStr.startsWith("the ")) {
				removed.add(termStr);
				added.add(termStr.replaceFirst("the ",""));
			}
		}
		
		fieldData.removeAll(removed);
		fieldData.addAll(added);
		    	
		super.addFieldData(fieldData, context, container);
	}

	@Override
	protected String getFileName() {
		return "longTerms.txt";
	}
	
	
}
