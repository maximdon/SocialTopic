package com.softlib.imatch.common;


public class SimpleCommaTokenizer extends SimpleTokenizer {

	public SimpleCommaTokenizer(char[] delimiters) {
		super(delimiters);
	}
	
	public SimpleCommaTokenizer(String regex) {
		super(regex);
	}

	private boolean isNotNumber(String text) {
		String numberText = text.replaceAll("[,.]", "");
		try {
			new Integer(numberText);
		}
		catch (NumberFormatException e) {
			return true;
		}
		return false;
	}
	
	public String[] split(String string) {
		SplitMngr splitMngr = new SplitMngr();
		String[] split = super.split(string);
		for (String word : split) {
			int idx = word.indexOf(',');
			while (idx==0) {
				word = word.substring(1);
				idx = word.indexOf(',');
			}
			if (idx>0 && idx<word.length() && isNotNumber(word)) {
				for (String subWord : word.split(","))
					splitMngr.add(subWord);
			}
			else
				splitMngr.add(word);
		}
		return splitMngr.split();
	}

};
