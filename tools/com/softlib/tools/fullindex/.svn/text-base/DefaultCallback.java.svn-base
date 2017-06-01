package com.softlib.tools.fullindex;

import java.util.ArrayList;
import java.util.List;

import com.softlib.imatch.ITicket;
import com.softlib.imatch.ITicketRetrievedCallback;

public class DefaultCallback implements ITicketRetrievedCallback
{
	private List<ITicket> retrievedTickets = new ArrayList<ITicket>();
	public void ticketRetrieved(ITicket ticket) {
		retrievedTickets.add(ticket);
	}		
	
	public List<ITicket> getTickets()
	{
		return retrievedTickets;
	}
}