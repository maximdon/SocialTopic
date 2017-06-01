package com.softlib.imatch.enums;

import java.util.TreeMap;

public enum MatchErrorCodes {
	Generic (1), TicketNotFound(2), NoTerms(3), InvalidId(4);
	  
	private final int value;
	
	MatchErrorCodes(int value)
	{
		this.value = value;
	}

	public int getValue() {
		return value;
	}
	
	public String getName() {
		return this.name();
	}
	
	private static TreeMap<Integer, MatchErrorCodes> _map;
	static
	{
		_map = new TreeMap<Integer, MatchErrorCodes>();
		
		for (MatchErrorCodes e_num: MatchErrorCodes.values())
		{
			_map.put(new Integer(e_num.value), e_num);
		}
	}
	  
	public static MatchErrorCodes lookup(int value)
	{
		return _map.get(new Integer(value));
	}
}
