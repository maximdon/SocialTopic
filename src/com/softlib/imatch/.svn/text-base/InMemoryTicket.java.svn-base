package com.softlib.imatch;

import com.softlib.imatch.matcher.InMemoryTicketFieldsNames;

/**
 * This class represents in-memory, not backend connected ticket.
 * Usually, this ticket is created from the client input and contains only very partial ticket information.
 * @author Maxim Donde
 *
 */

public class InMemoryTicket extends BaseTicket {

	public static final String DEFAULT_ID = "-1";
	
	public InMemoryTicket(String originObjectId) {		
		this(originObjectId, null, null);
	}	
	
	public InMemoryTicket(String originObjectId, String title, String body) {
		this(originObjectId, DEFAULT_ID, title, body);
	}
	
	public InMemoryTicket(String originObjectId, String id, String title, String body) {
		super(originObjectId,new InMemoryTicketFieldsNames());
		setField(InMemoryTicketFieldsNames.TITLE_FIELD,title);
		this.title = title;
		setField(InMemoryTicketFieldsNames.BODY_FIELD,body);
		this.body = body;
		setField(InMemoryTicketFieldsNames.ID_FIELD,id);
		this.id = id;
		setField(InMemoryTicketFieldsNames.STATE_FIELD,TicketState.New);
	}

	
};
