package com.softlib.imatch.score;

import com.softlib.imatch.ticketprocessing.IProcessedTicket;

public interface ITermsScoreReorder {

	public abstract void reorder(IProcessedTicket processedTicket,
			String[] selectedTerms);

}