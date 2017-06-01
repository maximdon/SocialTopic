package com.softlib.imatch.enums;

import java.util.TreeMap;

public enum SecurityType {
	NONE(0), SECURED(1);
	  
	private final int value;
	
	SecurityType(int value)
	{
		this.value = value;
	}

	public int getValue() {
		return value;
	}
	
	public String getName() {
		return this.name();
	}
	
	private static TreeMap<Integer, SecurityType> _map;
	
	static
	{
		_map = new TreeMap<Integer, SecurityType>();
		
		for (SecurityType e_num: SecurityType.values())
		{
			_map.put(new Integer(e_num.value), e_num);
		}
	}
	  
	public static SecurityType lookup(int value)
	{
		return _map.get(new Integer(value));
	}
}
