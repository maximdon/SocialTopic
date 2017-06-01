package com.softlib.imatch.ticketprocessing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.softlib.imatch.common.configuration.ConfigurationException;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;

public class NegativeSentiment 
{
	private static Set<NegativeSentiment> negativeSentiments = new HashSet<NegativeSentiment>();

	private String anchor;
	private String subject;
	private String pattern;
	//TODO support subject in the middle
	private boolean isAnchorBefore;
	private int anchorNumTerms;
		
	public NegativeSentiment(String pattern)
	{
		if(pattern.indexOf("{XXX}") == -1)
			throw new ConfigurationException("/pattern", "Invalid pattern " + pattern + " subject not found");		
		anchor = pattern.replace("{XXX}", "").trim();
		anchorNumTerms = anchor.split(" ").length;
		isAnchorBefore = pattern.startsWith(anchor);
		this.pattern = pattern;
		this.subject = null;
	}

	public boolean matchInText(String[] text, int subjectStartIdx, int subjectEndIdx, int skipStartIdx, int skipEndIdx) 
	{
		if(isAnchorBefore && (subjectStartIdx - anchorNumTerms) < 0)
			//The string is too short
			return false;
		if(!isAnchorBefore && (subjectEndIdx + anchorNumTerms) >= text.length)
			//The string is too short
			return false;
		if(skipStartIdx == - 1)
			subject = StringUtils.join(Arrays.copyOfRange(text, subjectStartIdx, subjectEndIdx + 1), " ");
		else {
			String subjectPart1 = StringUtils.join(Arrays.copyOfRange(text, subjectStartIdx, skipStartIdx), " "); 
			String subjectPart2 = StringUtils.join(Arrays.copyOfRange(text, skipEndIdx + 1, subjectEndIdx + 1), " ");
			subject = subjectPart1 + " " + subjectPart2;			
		}
		String anchorInText = "";		
		if(isAnchorBefore) {
			for(int i = subjectStartIdx - anchorNumTerms; i < subjectStartIdx; ++i) {
				anchorInText += text[i];
				anchorInText += " ";
			}
		}
		else {
			//anchor after
			for(int i = subjectEndIdx + 1; i <= subjectEndIdx + anchorNumTerms; ++i) {
				anchorInText += text[i];
				anchorInText += " ";
			}			
		}
		return compareAnchors(anchorInText.trim(), anchor);
	}
	
	public String getSubject() {
		return subject;
	}
	
	public String toString() 
	{
		if(subject == null)
			return pattern;
		if(isAnchorBefore)
			return anchor + " " + subject;
		else
			return subject + " " + anchor;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pattern == null) ? 0 : pattern.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NegativeSentiment other = (NegativeSentiment) obj;
		if (pattern == null) {
			if (other.pattern != null)
				return false;
		} else if (!pattern.equals(other.pattern))
			return false;
		return true;
	}

	public static NegativeSentiment getFromTerm(TechnicalDictionaryTerm term)
	{
		for(NegativeSentiment sentiment : negativeSentiments)
			if(sentiment.matchesTerm(term)) {
				sentiment.instantiate(term);
				return sentiment;
			}
		return null;
	}

	public static Collection<NegativeSentiment> getAllSentiments() {
		return negativeSentiments;
	}

	static void addSentimentPattern(String negativeSentimentPattern) {
		negativeSentiments.add(new NegativeSentiment(negativeSentimentPattern));
	}

	private boolean compareAnchors(String anchorInText, String patternAnchor)
	{
		//TODO negative pattern, check if need stemming
		return anchorInText.equalsIgnoreCase(patternAnchor);
	}

	private void instantiate(TechnicalDictionaryTerm term)
	{
		String subject;
		String termText = term.getTermText();
		if(isAnchorBefore) {
			int subjectIdx = termText.indexOf(anchor.toLowerCase()) + anchor.length() + 1;
			subject = termText.substring(subjectIdx, termText.length());
		}
		else {
			int subjectIdx = termText.indexOf(anchor.toLowerCase());
			subject = termText.substring(0, subjectIdx - 1);
		}
		this.subject = subject;
	}
	
	private boolean matchesTerm(TechnicalDictionaryTerm term)
	{
		if(isAnchorBefore)
			return term.getTermText().startsWith(anchor.toLowerCase());
		else
			return term.getTermText().endsWith(anchor.toLowerCase());
	}
}
