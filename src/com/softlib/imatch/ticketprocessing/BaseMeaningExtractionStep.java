package com.softlib.imatch.ticketprocessing;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class BaseMeaningExtractionStep extends NlpBaseTicketProcessStep {
	private static final String[] ignoreVerbsStart = new String[] {"are", "do", "did", "does", "be", "get", "have", "has", "had", "is", "was", "try"};
	private static final String[] ignoreVerbsEnd = new String[] {"is"};
	private static final String[] ignoreWords = new String[] {"please", "following"};

	private Pattern startPattern;
	private Pattern endPattern;
	private Pattern ignorePattern;
	private List<Pattern> allPatterns = new ArrayList<Pattern>();
	
	public BaseMeaningExtractionStep()
	{
		String startPatternStr = "^(" + com.softlib.imatch.common.StringUtils.join(ignoreVerbsStart, "|") + ") .*";
		startPattern = Pattern.compile(startPatternStr, Pattern.CASE_INSENSITIVE);
		allPatterns.add(startPattern);
		String endPatternStr = "(" + com.softlib.imatch.common.StringUtils.join(ignoreVerbsEnd, "|") + ")$";
		endPattern = Pattern.compile(endPatternStr, Pattern.CASE_INSENSITIVE);
		allPatterns.add(endPattern);
		String ignorePatternStr = "(" + com.softlib.imatch.common.StringUtils.join(ignoreWords, "|") + ")";
		ignorePattern = Pattern.compile(ignorePatternStr, Pattern.CASE_INSENSITIVE);
		allPatterns.add(ignorePattern);
	}
	
	@Override
	protected boolean isEligable(String termText) 
	{
		for(Pattern pattern : allPatterns) {
			Matcher matcher = pattern.matcher(termText);
			if(matcher.find())
				return false;
		}
		
		if (!super.isEligable(termText))
			return false;
		
		return true;
	}
}
