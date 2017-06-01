package com.softlib.imatch.enums;

import java.util.TreeMap;

public enum UserLevel {
	ADMINISTRATOR(0), REGULAR(1), GUEST(2);
	  
	private final int value;
	
	UserLevel(int value)
	{
		this.value = value;
	}

	public int getValue() {
		return value;
	}
	
	public String getName() {
		return this.name();
	}
	
	private static TreeMap<Integer, UserLevel> _map;
	static
	{
		_map = new TreeMap<Integer, UserLevel>();
		
		for (UserLevel e_num: UserLevel.values())
		{
			_map.put(new Integer(e_num.value), e_num);
		}
	}
	  
	public static UserLevel lookup(int value)
	{
		return _map.get(new Integer(value));
	}
}
