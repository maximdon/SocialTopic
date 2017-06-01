package com.softlib.imatch.enums;

import java.util.TreeMap;

public enum SearchType {
	ById(0), ByFreeText(1);
	  
	private final int value;
	
	SearchType(int value)
	{
		this.value = value;
	}

	public int getValue() {
		return value;
	}
	
	public String getName() {
		return this.name();
	}
	
	private static TreeMap<Integer, SearchType> _map;
	static
	{
		_map = new TreeMap<Integer, SearchType>();
		
		for (SearchType e_num: SearchType.values())
		{
			_map.put(new Integer(e_num.value), e_num);
		}
	}
	  
	public static SearchType lookup(int value)
	{
		return _map.get(new Integer(value));
	}
}
