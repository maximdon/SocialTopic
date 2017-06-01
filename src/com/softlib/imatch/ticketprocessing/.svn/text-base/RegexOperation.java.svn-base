package com.softlib.imatch.ticketprocessing;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

enum RegexOperationType
{
	MATCH,
	REPLACE
}

public abstract class RegexOperation 
{
	protected String regex;
	protected Pattern regexPattern;
	
	protected RegexOperation(String regex) {
		this.regex = regex;
		regexPattern = Pattern.compile(regex);
	}
		
	public static RegexOperation getRegexOperation(RegexOperationType opType, String regex, String replacement)
	{
		if(opType == RegexOperationType.MATCH)
			return new MatchRegexOperation(regex);
		else
			return new ReplaceRegexOperation(regex, replacement);
	}
	
	public void setRegex(String regex) {
		this.regex = regex;
	}
	public String getRegex() {
		return regex;
	}
		
	public List<String> run(List<String> texts)
	{
		List<String> result = new ArrayList<String>();
		for(String text : texts) {
			result.addAll(run(text));
		}
		return result;
	}
	
	public abstract List<String> run(String text);
		
	public void destroy() {
		regexPattern = null;
	}
}
