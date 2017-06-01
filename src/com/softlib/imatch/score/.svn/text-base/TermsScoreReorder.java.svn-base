package com.softlib.imatch.score;

import com.softlib.imatch.dictionary.TechnicalDictionaryKey;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.ticketprocessing.IProcessedTicket;

public class TermsScoreReorder implements ITermsScoreReorder {
	/* (non-Javadoc)
	 * @see com.softlib.imatch.score.ITermsScoreReorder#reorder(com.softlib.imatch.ticketprocessing.IProcessedTicket, java.lang.String[])
	 */
	public void reorder(IProcessedTicket processedTicket, String[] selectedTerms)
	{
		int priority = selectedTerms.length;
		
		for (String item : selectedTerms) {
			TechnicalDictionaryKey termKey = new TechnicalDictionaryKey(item);
			if (!processedTicket.getDictionary().contains(termKey))
				processedTicket.addTerm(termKey);
			double boost = 10f * priority--;
			TechnicalDictionaryTerm term = processedTicket.getDictionary().get(termKey);
			float originalBoost = term.getBoost(); //processedTicket.getBoostFactors().get(termKey);
			processedTicket.overwriteBoostFactor(termKey,new Float(Math.log(originalBoost + boost)));
		}
	}
}
