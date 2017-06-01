package com.softlib.imatch.enums;

import java.util.TreeMap;

public enum HighlightType {
	EXTRACTED(0), MATCH(1);
	  
	private final int value;
	
	HighlightType(int value)
	{
		this.value = value;
	}

	public int getValue() {
		return value;
	}
	
	public String getName() {
		return this.name();
	}
	
	private static TreeMap<Integer, HighlightType> _map;
	static
	{
		_map = new TreeMap<Integer, HighlightType>();
		
		for (HighlightType e_num: HighlightType.values())
		{
			_map.put(new Integer(e_num.value), e_num);
		}
	}
	  
	public static HighlightType lookup(int value)
	{
		return _map.get(new Integer(value));
	}
}
