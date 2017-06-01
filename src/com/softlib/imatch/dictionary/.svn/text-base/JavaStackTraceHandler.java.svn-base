package com.softlib.imatch.dictionary;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sun.awt.geom.AreaOp.AddOp;

public class JavaStackTraceHandler extends RegexBasedSpecialTextHandler 
{
	private Pattern additionalPattern;
	
	public JavaStackTraceHandler(String regex) {
		super(regex);
		additionalPattern = Pattern.compile("\\(\\w+.java:\\d+\\)", Pattern.CASE_INSENSITIVE);
	}

	
	@Override
	public String toCanonicalForm(String text) {
		int position = text.indexOf(".$");		
		String newText;
		if(position > -1)
			newText = text.substring(position + 2);
		else
			newText = text;
		newText = newText.replace('$', '.');
		return super.toCanonicalForm(newText);
	}


	@Override
	public String remove(String text) {
		text = super.remove(text);
		Matcher matcher = additionalPattern.matcher(text);
		return matcher.replaceAll("");
	}
}
