package com.softlib.imatch.matcher;

import com.softlib.imatch.ITicket;
import com.softlib.imatch.MatcherException;
import com.softlib.imatch.ticketprocessing.IProcessedTicket;

/**
 * Matcher is the heart of iMatch product. 
 * Responsible for finding all relevant matches for the given ticket
 * @author Maxim Donde
 *
 */
public interface IMatcher {

	/**
	 * Finds all matches for the given ticket (providing ticket partial information)
	 * @param ticket
	 * @return
	 * @throws MatcherException
	 */
	MatchResults findMatches(ITicket ticket) throws MatcherException;
	
	/**
	 * Finds all matches for the ticket which already exists in the ticketing system
	 * given this ticket id
	 * @param ticketId
	 * @return
	 * @throws MatcherException
	 */
	MatchResults findMatches(String originObjectId, String ticketId) throws MatcherException;
	
	
	/**
	 * Rematch - Finds all matches for the twicked processed ticket
	 * @param processedTicket
	 * @return
	 * @throws MatcherException
	 */
	public MatchResults findMatches(IProcessedTicket processedTicket) throws MatcherException;
}
