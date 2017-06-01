package com.softlib.imatch;

import com.softlib.imatch.matcher.ITicketFieldsNames;
import com.softlib.imatch.ticketprocessing.IProcessedTicket.MatchMode;


/**
 * 
 * Ticket is the basic unit of data for iMatch.
 * Ticket has some predefined fields like id, title, etc and some solution specific fields.
 * @author Maxim Donde
 *
 */
public interface ITicket  {
	
	public String getId();
	
	public String getTitle();

	public String getBody(MatchMode mode);
	
	public String getFolder();
	
	public Object getField(String fieldName);
	
	public TicketState getState();

	public String getOriginObjectId();
	
	public ITicketFieldsNames getFieldsConfig();

};
