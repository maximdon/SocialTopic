package com.softlib.imatch.common;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class StringUtils {
	public static String regexEncode(String str)
	{
		return str.replaceAll("[\\.\\^\\(\\)\\{\\}\\]\\[\\$\\*\\+\\?\\!\\|\\\\]", "\\\\$0");
	}
	
	public static boolean containsDigits(String term) 
	{
		int length = term.length();
		for(int i = 0; i < length; ++i)
			if(Character.isDigit(term.charAt(i)))
				return true;
		return false;
	}
	
	public static boolean containsOnlyLetters(String str) {
		return str.matches("^[a-zA-Z]+$");
	}
	
	public static boolean containsAtLeastOneLetter(String str) {
		return str.matches("^.*[a-zA-Z]+.*$");
	}
	
	public static String join(Collection<String> s, String delimiter) {
	    if (s.isEmpty()) return "";
	    Iterator<String> iter = s.iterator();
	    String firstElement = iter.next();
	    if(firstElement == null)
	    	return "";
	    StringBuffer buffer = new StringBuffer(firstElement);
	    while (iter.hasNext()) 
	    	buffer.append(delimiter).append(iter.next());
	    return buffer.toString();
	}

	public static String join(String[] stringArray, String delimiter) {
	    if (stringArray.length == 0)
	    	return "";
	    StringBuffer buffer = new StringBuffer(stringArray[0]);
	    for (int i = 1 ; i < stringArray.length; ++i) 
	    	buffer.append(delimiter).append(stringArray[i]);
	    return buffer.toString();		
	}

	public static boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}
	
	public static boolean hasCommonWords(String str1, String str2)
	{
		String[]str1Words = str1.split(" ");
		String[]str2Words = str2.split(" ");
		List<String> list1 = Arrays.asList(str1Words);
		List<String> list2 = Arrays.asList(str2Words);
		return !list1.removeAll(list2);
	}
}
