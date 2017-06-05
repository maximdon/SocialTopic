package com.softlib.imatch;

import com.softlib.imatch.matcher.TicketingSystemIntegrationConfig;


/**
 * This interface defines ticket provider.
 * Ticket provider is responsible for retrieving a ticket from the underlined ticketing system.
 * @author Administrator
 *
 */
public interface ITicketProvider {
	/**
	 * Retrieves a ticket with the given id.
	 * Note, id could vary from one ticketing system to another and from one provider to another
	 * @param id - the id of the ticket to retrieve
	 * @return
	 * @throws MatcherException 
	 */
	ITicket get(String id) throws MatcherException;

	/**
	 * Retrieves all existing tickets in the ticketing system DB.
	 * Used for the initial indexing of the ticketing system
	 * @return
	 * @throws MatcherException
	 */
	void getAllTickets(ITicketRetrievedCallback callback) throws MatcherException;

	int getAllTicketsCount() throws MatcherException;

	void getChangedTickets(long lastRunTime, ITicketRetrievedCallback callback) throws MatcherException;
	 
	TicketingSystemIntegrationConfig getConfig();
};
