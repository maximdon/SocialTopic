package com.softlib.imatch.dictionary;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

import com.softlib.imatch.RuntimeInfo;

@Embeddable
public class TechnicalDictionaryKey implements Comparable<TechnicalDictionaryKey> 
{
	@Column(name="term_text")
	private String termText = "";
	@Column(name="term_stemmed_text")
	private String termStemmedText;
	@Transient
	private boolean clean = false;
	@Transient
	private static TechnicalDictionary dictionary;
		 
	static private String getText(List<TechnicalDictionaryTerm> patternTerms) {
		String text = "";
		for (TechnicalDictionaryTerm patternTerm : patternTerms) {
			if (!text.equals(""))
				text += " ";
			text += patternTerm.getTermText(); 
		}
		return text;
	}
	
	public TechnicalDictionaryKey()
	{
	}
	
	public TechnicalDictionaryKey(List<TechnicalDictionaryTerm> patternTerms) {
		this(getText(patternTerms),true);
	}

	public TechnicalDictionaryKey(String termText) {
		this(termText, true);
	}

	public TechnicalDictionaryKey(String text, boolean stem) {
		if(stem)
			setStemmedText(text);
		else {
			this.termText = text;
			this.termStemmedText = text;
		}
	}

	public String getTermText() {
		return termText;
	}
	
	public String getTermStemmedText() {
		return termStemmedText;
	}
	
	public void clean() {
		if (!clean) {
			String cleanTermText = cleanText(termText);
			setStemmedText(cleanTermText);
			clean = true;
		}
	}

	static String symbol = "<>.,:%?!*+$;&'\"";
	private static Pattern regexPattern = Pattern.compile("[(){}\\[\\]]");
	
	static public String cleanText(String text) {
		try {
			if(dictionary == null)
				dictionary = (TechnicalDictionary)RuntimeInfo.getCurrentInfo().getBean("dictionary");
			for(ISpecialTextHandler cleaner : dictionary.getSpecialTextHandlers()) {
				String cleanedText = cleaner.toCanonicalForm(text);
				if(cleanedText != null)
					return cleanedText;
			}
		}
		catch(Exception e) {
			//Do nothing, continue with regular cleaning
		}

		Matcher matcher = regexPattern .matcher(text);
		text = matcher.replaceAll("");
		text = text.trim();

		int length = text.length();
		int endIdx = length-1;
		int startIdx=0;

		while (endIdx>0 && symbol.indexOf(text.charAt(endIdx))>=0) 
			endIdx--;
		while (startIdx<endIdx && symbol.indexOf(text.charAt(startIdx))>=0)
			startIdx++;
		
		if (startIdx>0 || endIdx<length-1)
			text = text.substring(startIdx,endIdx+1);

		return text;
	}
	
	public static String toCanonicalForm(String text) {
		return text.trim().toLowerCase();
	}

	private void setStemmedText(String termText) {
		String canonicalText = toCanonicalForm(termText);
		if (canonicalText.equals(this.termText))
			return;
		this.termText = canonicalText;		

		termStemmedText = PorterStemmer.stem(this.termText);
	}

	public String getStemmedText() {
		return termStemmedText;
	}

	public String getText() {
		return termText;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		if(termStemmedText != null)
			result = prime * result + termStemmedText.hashCode();
		else
			result = prime * result + termText.hashCode();
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TechnicalDictionaryKey other = (TechnicalDictionaryKey) obj;
		if (termStemmedText != null && other.termStemmedText != null)
			return termStemmedText.equals(other.termStemmedText);
		return termText.equals(other.termText);
	}

	@Override
	public String toString() {
		return termText;
	}

	@Override
	public int compareTo(TechnicalDictionaryKey otherKey) {
		if(termStemmedText != null && otherKey.getTermStemmedText() != null)
			return termStemmedText.compareTo(otherKey.getTermStemmedText());
		else
			return termText.compareTo(otherKey.getTermText());	
	}

	void notifyStemmingException(String oldStemming, String stemm) {
		if(!termStemmedText.contains(oldStemming))
			//False alarm, this term not influence by this stemming exception
			return;
		termStemmedText = termStemmedText.replace(oldStemming, stemm);
	}

	
};