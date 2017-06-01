package com.softlib.imatch.score;

import org.apache.log4j.Logger;

import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.dictionary.TechnicalDictionaryKey;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.ticketprocessing.IProcessedTicket;

public class TermsScoreReorderDoNothing implements ITermsScoreReorder {

	private static Logger log = Logger.getLogger(TermsScoreReorderDoNothing.class);
	
	public void reorder(IProcessedTicket processedTicket, String[] selectedTerms) {
		for (String item : selectedTerms) {
			TechnicalDictionaryKey termKey = new TechnicalDictionaryKey(item);
			if (!processedTicket.getDictionary().contains(termKey))
				processedTicket.addTerm(termKey);
			float originalBoost = 1;
			TechnicalDictionaryTerm term = processedTicket.getDictionary().get(termKey,true);
			if(term != null)
				originalBoost = term.getBoost(); //processedTicket.getBoostFactors().get(termKey);
			else
				LogUtils.warn(log, "Term %s not found in the dictionary", termKey);
			processedTicket.overwriteBoostFactor(termKey, originalBoost);
		}
	}

}
