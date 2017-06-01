package com.softlib.imatch.dictionary;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class TermsNGrams 
{
	private Map<String, String> ngrams;
	private static String excludeSymbols = TechnicalDictionaryKey.symbol + "[](){}";
	
	public TermsNGrams()
	{
		ngrams = new HashMap<String, String>();
	}
	
	public void addTerm(TechnicalDictionaryKey key)
	{
		String ngram = buildNGram(key);
		ngrams.put(ngram, ngram);
	}
	
	public void removeTerm(TechnicalDictionaryKey key)
	{
		throw new UnsupportedOperationException("Delete operation is currently not supported");
	}
	
	public boolean containsTerm(TechnicalDictionaryKey key)
	{
		return containsString(key.getStemmedText());
	}
	
	public boolean containsString(String termText)
	{
		String ngram = buildNGram(termText);
		return ngrams.containsKey(ngram);
	}

	private String buildNGram(String termText) {
		if(termText == null)
			return "";
		String[] words = termText.split(" ");
		String prefixes = "";
		for(String word : words)
			if(word.length() <= 2)
				prefixes += word.toLowerCase();
			else {
				int count = 0;
				for(char c : word.toCharArray()) {
					if(excludeSymbols.indexOf(c) > -1)
						continue;
					prefixes += Character.toLowerCase(c);
					count ++;
					if(count >= 2)
						break;
				}
			}
		return prefixes;
	}
	
	private String buildNGram(TechnicalDictionaryKey key) {
		return buildNGram(key.getStemmedText());
	}
}
