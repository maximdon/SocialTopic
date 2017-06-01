package com.softlib.imatch.ticketprocessing;


import java.util.ArrayList;
import java.util.List;

import com.softlib.imatch.ITicket;
import com.softlib.imatch.MatcherException;
import com.softlib.imatch.common.Pair;
import com.softlib.imatch.common.SimpleTokenizer;
import com.softlib.imatch.dictionary.ITechnicalTermsContainer;
import com.softlib.imatch.dictionary.TechnicalDictionaryKey;


public class IndexTerms extends BaseTicketProcessStep {
	
	public static final String STEP_NAME = "IndexTerms";
	private static final SimpleTokenizer tokenizer  = new SimpleTokenizer(" \\$a\\$ ");
    
	public void run(String fieldName,ITicket ticket,ITechnicalTermsContainer termsContainer,StepContext context) throws MatcherException {
		if(fieldName.equals("Url"))
			return;
		List<TechnicalDictionaryKey> newKeys = getTerms(fieldName,context);
		termsContainer.startSession(fieldName,null, getStepName());
		for(TechnicalDictionaryKey termKey : newKeys) {
			termsContainer.addTerm(termKey);
		}
		termsContainer.endSession(0, null, false);
		termsContainer.freezeTerms();
	}

    private List<TechnicalDictionaryKey> getTerms(String fieldName,StepContext context) {
    	List<TechnicalDictionaryKey> rc = new ArrayList<TechnicalDictionaryKey>();
    	Pair<String,String> fieldTerms = context.getFieldTerms();
    	if (fieldTerms==null)
    		return rc;
    	rc.addAll(getKeysFromStr(fieldTerms.getLeft()));
       	rc.addAll(getKeysFromStr(fieldTerms.getRight()));
        return rc;
    }
    
    synchronized String[] getSplit(String text) {
    	return tokenizer.split(text);
    }
    
    private List<TechnicalDictionaryKey> getKeysFromStr(String fieldStr) {
    	List<TechnicalDictionaryKey> rc = new ArrayList<TechnicalDictionaryKey>();
    	if (fieldStr==null || fieldStr.isEmpty())
    		return rc;
    	String split[] = getSplit(fieldStr);
    	if (split==null)
    		return rc;
    	for (String text : split)
    		rc.add(new TechnicalDictionaryKey(text));
    	return rc;
    }
    
	public String getStepName() {
		return STEP_NAME;
	}

};
