package com.softlib.imatch.common;

import java.util.ArrayList;
import java.util.List;

public class ObjectPerListCombinations<T> {
	
	private final List<List<T>> lists;
	private final int indexs[];
	private boolean ended = false;
	
	public ObjectPerListCombinations(List<List<T>> lists) {
		this.lists = lists;
		indexs = new int[lists.size()];
	}

	private void checkEnd() {
		int sum = 0;
		for (int idx=0;idx<indexs.length;idx++)
			sum+=indexs[idx];
		ended = (sum==0);
	}
	
	public List<T> getNextCombination() {
		List<T> rc = new ArrayList<T>();
		if (ended)
			return rc;
		
		for (int idx=0;idx<indexs.length;idx++) {
			List<T> list = lists.get(idx);
			rc.add(list.get(indexs[idx]));
		}
		
		for (int idx=0;idx<indexs.length;idx++) {
			indexs[idx]++;
			if (indexs[idx]==lists.get(idx).size()) {
				indexs[idx]=0;
			}
			else {	
				break;
			}
		}
		checkEnd();
		return rc;
	}
	
	public static void main(String[] args) {		
		List<String> list1 = new ArrayList<String>();
		list1.add("A1");
		list1.add("A2");
		list1.add("A3");
		List<String> list2 = new ArrayList<String>();
		list2.add("B1");
		list2.add("B2");
		list2.add("B3");
		List<String> list3 = new ArrayList<String>();
		list3.add("C1");
		list3.add("C2");
		list3.add("C3");
		List<List<String>> lists = new ArrayList<List<String>>();
		lists.add(list1);
		lists.add(list2);
		lists.add(list3);
		
		ObjectPerListCombinations<String> perListCombinations = 
			new ObjectPerListCombinations<String>(lists);
		List<String> comb = perListCombinations.getNextCombination();
		while (!comb.isEmpty()) {
			for (String str : comb) {
				System.out.print(str + " ");
			}
			System.out.println("");
			comb = perListCombinations.getNextCombination();
		}
	}
};
