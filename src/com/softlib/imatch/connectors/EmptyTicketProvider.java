package com.softlib.imatch.connectors;

import com.softlib.imatch.ITicket;
import com.softlib.imatch.ITicketProvider;
import com.softlib.imatch.ITicketRetrievedCallback;
import com.softlib.imatch.MatcherException;
import com.softlib.imatch.matcher.TicketingSystemIntegrationConfig;

public class EmptyTicketProvider implements ITicketProvider {
	private TicketingSystemIntegrationConfig ticketingSystemConfig;
	
	public EmptyTicketProvider(String beanName,
			TicketingSystemIntegrationConfig createTicketingSystemConfig) {
		ticketingSystemConfig = createTicketingSystemConfig;
	}

	@Override
	public ITicket get(String id) throws MatcherException {
		return null;
	}

	@Override
	public void getAllTickets(ITicketRetrievedCallback callback)
			throws MatcherException {
	}

	@Override
	public int getAllTicketsCount() throws MatcherException {
		return 0;
	}

	@Override
	public void getChangedTickets(long lastRunTime,
			ITicketRetrievedCallback callback) throws MatcherException {
	}

	@Override
	public TicketingSystemIntegrationConfig getConfig() {
		return ticketingSystemConfig;
	}
}
