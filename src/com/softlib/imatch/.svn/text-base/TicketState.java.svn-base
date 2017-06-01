package com.softlib.imatch;

public enum TicketState {
	New,
	Updated,
	Deleted;

	public static TicketState fromInt(int state) {
		if(state == 0)
			return New;
		else if(state > 0)
			return Updated;
		else
			return Deleted;
	}
	
	public static int toInt(TicketState state) {
		if (state==New)
			return 0;
		if (state==Updated)
			return 1;
		return -1;
	}
	
}
