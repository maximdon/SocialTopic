package com.softlib.imatch.ticketprocessing;

import java.util.List;

import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.matcher.CandidateScore;

public interface IScoreCalculator {
	CandidateScore calculateScore(IProcessedTicket ticket1, IProcessedTicket ticket2);
	List<TechnicalDictionaryTerm> sortTerms(IProcessedTicket ticket);
}
