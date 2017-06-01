package com.softlib.imatch.common;

import java.util.ArrayList;
import java.util.List;

public class SubTightListCombinations<T> {
	private final List<T> list;

	private int size;
	private int startIndex;
	
	private final int maxSize;
	private final int minSize;
	
	private int lastIndex;

	public SubTightListCombinations(List<T> list) {
		this(list,list.size()-1,1);
	}
	
	public SubTightListCombinations(List<T> list,int maxSize,int minSize) {
		super();
		this.list = list;
		
		if (maxSize>list.size())
			maxSize=list.size();
		if (minSize<1)
			minSize=1;
		
		this.maxSize = maxSize;
		this.minSize = minSize;

		size = this.maxSize;
		startIndex = 0;
		lastIndex = -1;
	}
	
	public int getLastPos() {
		return lastIndex;
	}
	
	public List<T> getNextCombination() {
		if (size<minSize)
			return new ArrayList<T>();
		List<T> rc = getCombination(startIndex,size);
		lastIndex = startIndex;
		if (startIndex+size<list.size())
			startIndex++;
		else {
			startIndex = 0;
			size--;
		}
		return rc;
	}
	
	private List<T> getCombination(int startIdx,int size) {
		List<T> rc = new ArrayList<T>();
		for (int idx=startIdx;idx<size+startIdx;idx++) 
			rc.add(list.get(idx));
		return rc;
	}
	
	static public String toString(List<String> combination) {
		String text = combination.toString().replace(",","");
		text = text.substring(1,text.length()-1);
		return text;
	}
	
	public static void main(String[] args) {
		List<String> l = new ArrayList<String>();
		l.add("A");
		l.add("B");
		l.add("C");
		l.add("D");
		l.add("E");
		
		System.out.println("List :  "+l.toString().replace(",",""));
		
		SubTightListCombinations<String> s = 
			new SubTightListCombinations<String>(l,4,2);
		
		List<String> c = s.getNextCombination();
		while (!c.isEmpty()) {
			System.out.println("Combination: "+c);
			c = s.getNextCombination();
		}
	}
	
	
};
