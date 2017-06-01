package com.softlib.imatch.common;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class SimpleTokenizer implements ITokenizer {
	protected Pattern pattern;
	private static Logger log = Logger.getLogger(SimpleTokenizer.class);
	
	public SimpleTokenizer(String regex) {
		this.pattern = Pattern.compile(regex);
		LogUtils.debug(log, "Created new tokenizer for %s", regex);
	}
	
	public SimpleTokenizer(char[] delimiters) {
		String tmpRegex = "";
		for(char c : delimiters)
			tmpRegex += c;
		String regex = "[" + StringUtils.regexEncode(tmpRegex) + "]";
		LogUtils.debug(log, "Created new tokenizer for %s", regex);
		pattern = Pattern.compile(regex);
	}
	
	protected class SplitMngr {
		private List<String> split = new ArrayList<String>();
		
		public void add(String word) {
			if (word!=null && !word.equals(""))
				split.add(word);
		}
		
		public String[] split() {
			return split.toArray(new String[0]);
		}
	}
	
	public String[] split(String string) {
		SplitMngr splitMngr = new SplitMngr();
		for (String word : pattern.split(string))
			splitMngr.add(word);
		return splitMngr.split();
	}
		
	
};
