package com.softlib.imatch.ticketprocessing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import com.softlib.imatch.dictionary.TechnicalDictionaryKey;

public class MatchRegexOperation extends RegexOperation
{
	public MatchRegexOperation(String regex) {
		super(regex);
	}

	public List<String> run(String text)
	{
		if(text == null)
			return new ArrayList<String>();
		List<String> result = new ArrayList<String>();
		Matcher matcher = this.regexPattern.matcher(text);
		
		while (matcher.find()) {
			if (matcher.group() != null & !matcher.group().equals("") ) {
				result.add(TechnicalDictionaryKey.toCanonicalForm(matcher.group()));						
			}
		}	
		return result;
	}

	public List<String> runWithSubGroups(String text)
	{
		if(text == null)
			return new ArrayList<String>();
		List<String> result = new ArrayList<String>();
		Matcher matcher = this.regexPattern.matcher(text);
		
		while (matcher.find()) {
			for(int i = 0; i <= matcher.groupCount(); ++i) {
				if (matcher.group(i) != null & !matcher.group(i).equals("") ) {
					result.add(TechnicalDictionaryKey.toCanonicalForm(matcher.group(i)));						
				}
			}
		}	
		return result;
	}
	
	public Map<String, Integer> runWithPositions(String text) {
		if(text == null)
			return new HashMap<String, Integer>();

		Map<String, Integer> result = new HashMap<String, Integer>();
		Matcher matcher = this.regexPattern.matcher(text);
		
		while (matcher.find()) {
			if (matcher.group() != null & !matcher.group().equals("") ) {
				result.put(TechnicalDictionaryKey.toCanonicalForm(matcher.group()), matcher.start());						
			}
		}	
		return result;
	}
}
