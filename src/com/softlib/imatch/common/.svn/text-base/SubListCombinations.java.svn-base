package com.softlib.imatch.common;

import java.util.ArrayList;
import java.util.List;

public class SubListCombinations<T> {

	private final List<T> list;
	private boolean array[];
	
	public SubListCombinations(List<T> list,int combinationSize) {
		this.list = list;
		int listSize = list.size();
		if (combinationSize>listSize)
			combinationSize = listSize;
		initArray(listSize,combinationSize);
	}
	 
	public List<T> getNextCombination() {
		List<T> rc = new ArrayList<T>();
		if (array==null)
			return rc;
		for (int idx=0;idx<array.length;idx++) {
			if (array[idx]==true)
				rc.add(list.get(idx));
		}
		if (!forwordArray())
			array = null;
		return rc;
		
	}
	
	private void initArray(int size,int groupSize) {
		array = new boolean[size];
		 for (int i=0;i<groupSize;i++) {
			 array[size-i-1]=true;
		 }
	 }
	 
	private boolean forwordArray() {
		int size = array.length;
		int count = 0;
		for (int i = 1; i<size;i++) {
			if (array[i-1]==true) {
				count++;
			}
			else if (array[i]==true && array[i-1]==false) {
				array[i]=false;
				array[i-1]=true;
				for (int c=i-2;c>=0;c--) {
					if (count>0) {
						array[c]=true;
						count--;
					}
					else
						array[c]=false;
				}
				return true;
			}	
		}
		return false;
	}

	public static void main(String[] args) {
		List<String> list = new ArrayList<String>();
		list.add("[1]");
		list.add("[2]");
		list.add("[3]");
		list.add("[4]");
		list.add("[5]");
		list.add("[6]");
		
		SubListCombinations<String> combinations = 
			new SubListCombinations<String>(list,3);
		
		int idx=0;
		List<String> subList = combinations.getNextCombination();
		while (!subList.isEmpty()) {
			System.out.print("Combination #"+idx+" :\t");
			for (String str : subList) {
				System.out.print(str);
			}
			System.out.println();
			idx++;
			subList = combinations.getNextCombination();
		}
	}
	
	
};
