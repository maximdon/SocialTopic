package com.softlib.imatch.ticketprocessing;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;

import org.apache.lucene.search.Query;

import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.matcher.CandidateScore;

public interface ITicketMatchAction {
	public Query 	buildQuery(IProcessedTicket ticket);
	public Boolean  continueWithProcess(Set<ProcessedTicket> candidates);
	public Boolean  useiMatchScorer();
}
