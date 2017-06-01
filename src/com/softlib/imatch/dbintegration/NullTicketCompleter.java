package com.softlib.imatch.dbintegration;

public class NullTicketCompleter implements DBTicketCompleter {

	private static NullTicketCompleter nullCompleter = new NullTicketCompleter();
	
	public void complete(DBTicket currentTicket) {
		//Do Nothing
	}


	public static DBTicketCompleter getCompleter() {
		return nullCompleter;
	}

}
