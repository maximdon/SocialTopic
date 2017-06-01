package com.softlib.imatch.dictionary;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexBasedSpecialTextHandler implements ISpecialTextHandler {
	private Pattern pattern;
	
	public RegexBasedSpecialTextHandler(String regex) {
		 pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);		
	}
	
	public String toCanonicalForm(String text) {
		try {
			//Special case clean for java stack trace 
			Matcher matcher = pattern.matcher(text);
			if(matcher.find()) {
				return matcher.group();
			}
		}
		catch(Exception e) {
			//Do nothing, this text is not a special one
		}
		return null;
	}

	public boolean match(String text) {
		return toCanonicalForm(text) != null;
	}

	public String remove(String text) {
		try {
			//Special case clean for java stack trace 
			Matcher matcher = pattern.matcher(text);
			return matcher.replaceAll("");
		}
		catch(Exception e) {
			//Do nothing, this text is not a special one
		}
		return text;
	}

}
