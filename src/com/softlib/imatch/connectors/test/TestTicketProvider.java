package com.softlib.imatch.connectors.test;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.softlib.imatch.ITicket;
import com.softlib.imatch.ITicketRetrievedCallback;
import com.softlib.imatch.MatcherException;
import com.softlib.imatch.connectors.BaseTicketProvider;
import com.softlib.imatch.matcher.TicketingSystemIntegrationConfig;

public class TestTicketProvider extends BaseTicketProvider {

	private final static Logger log = Logger.getLogger(TestTicketProvider.class);
	private ITest test;
	
	public TestTicketProvider(String objectId, TicketingSystemIntegrationConfig config) {
		super(objectId, config);
	}

	@Override
	public ITicket get(String id) throws MatcherException {
		for(ITicket ticket : test.getTickets())
		{
			if(ticket.getId().equals(id))
				return ticket;
		}
		return null;
	}

	@Override
	public void getAllTickets(ITicketRetrievedCallback callback) throws MatcherException {
		for(ITicket ticket : test.getTickets())
		{
			callback.ticketRetrieved(ticket);
		}
	}

	@Override
	public int getAllTicketsCount() throws MatcherException {
		return test.getTickets().size();
	}

	@Override
	public void getChangedTickets(long lastRunTime, ITicketRetrievedCallback callback) throws MatcherException {
		
	}
	
	public void setTest(ITest test)
	{
		this.test = test;
	}
	
	public ITest getTest()
	{
		return test;
	}
};
