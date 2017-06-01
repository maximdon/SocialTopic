package com.softlib.imatch.connectors.file;

public class TicketProviderMode {
	
	public enum Mode {
		iMatch,
		iSolve
	}
	
	static TicketProviderMode obj;
	
	private Mode mode = Mode.iMatch;
	
	private TicketProviderMode() {
	}
	
	static public TicketProviderMode instance() {
		if (obj==null)
			obj = new TicketProviderMode();
		return obj;
	}

	public Mode getMode() {
		return mode;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}
	
};
