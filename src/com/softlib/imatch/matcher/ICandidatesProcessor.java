package com.softlib.imatch.matcher;

import java.util.Collection;
import java.util.List;

import com.softlib.imatch.MatcherException;
import com.softlib.imatch.ticketprocessing.IProcessedTicket;

public interface ICandidatesProcessor 
{
	Collection<MatchCandidate> processCandidates(IProcessedTicket ticketToMatch, Collection<MatchCandidate> candidates) throws MatcherException;
}
