package com.softlib.imatch.ticketprocessing;

import java.util.ArrayList;
import java.util.List;

public class ReplaceRegexOperation extends RegexOperation 
{
	public ReplaceRegexOperation(String regex, String replacement) {
		super(regex);
		this.replacement  = replacement;
	}

	private String replacement;

	public String getReplacement() {
		return replacement;
	}
	public void setReplacement(String replacement) {
		this.replacement = replacement;
	}
	
	public List<String> run(String text)
	{
		if(text == null)
			return new ArrayList<String>();
		List<String> result = new ArrayList<String>();
		String newText = text.replaceAll(this.regex, this.replacement);
		if (newText != null & !newText.equals("") ) {
			result.add(newText);
		}
		return result;
	}
}
