package com.softlib.imatch.pattern;

import java.util.ArrayList;
import java.util.List;

import com.softlib.imatch.dictionary.TechnicalDictionary;
import com.softlib.imatch.dictionary.TechnicalDictionaryKey;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.dictionary.TechnicalTermSource;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("predicate")
public class PatternsConfigPattrenPartPredic {
	
	@XStreamAsAttribute
	private String sources;
	@XStreamAsAttribute
	private Boolean allow;
	@XStreamAsAttribute
	private String words;

	private List<TechnicalTermSource> sourcesList;
	private List<TechnicalDictionaryTerm> wordsTermsList;

	
	public boolean isAllow() {
		return allow;
	}

	public void setAllow(boolean allow) {
		this.allow = allow;
	}

	public String getSources() {
		return sources;
	}

	public void setSources(String sources) {
		this.sources = sources;
	}
	
	public String getWords() {
		return words;
	}

	public void setWords(String words) {
		this.words = words;
	}

	public void init(TechnicalDictionary dictionary) {
		wordsTermsList = new ArrayList<TechnicalDictionaryTerm>();
		if (words!=null) {
			TechnicalTermSource source = dictionary.addSource(PatternsConfigPattren.DEFAULT_SOURCE_NAME);
			for (String word : words.split(",")) {
				if (word.equals(""))
					continue;
				TechnicalDictionaryTerm term = 
					dictionary.addTerm(new TechnicalDictionaryKey(word));
				if (term!=null) {
					term.setTermSource(source);
					wordsTermsList.add(term);
				}
			}
		}
		
		
		if (allow==null) {	
			if (sources==null)
				allow = false;
			else
				allow = true;
		}
		sourcesList = new ArrayList<TechnicalTermSource>();
		if (sources!=null) {
			for (String sourceName : sources.split(",")) {
				if (sourceName.equals(""))
					continue;
				TechnicalTermSource source = dictionary.addSource(sourceName);
				sourcesList.add(source);
			}
		}
	}
	
	public boolean isAllow(TechnicalDictionaryTerm term) {
		if (wordsTermsList.contains(term))
			return true;
		TechnicalTermSource source = term.getTermSource();
		if (source==null)
			return false;
		boolean containe = sourcesList.contains(source);
		return containe==allow; 
	}
	
	public List<TechnicalTermSource> getAllowSources() {
		return sourcesList;
	}

	public List<TechnicalDictionaryTerm> getAllowTerms() {
		return wordsTermsList;
	}

	public void setAllowSources(List<TechnicalTermSource> allowSources) {
		this.sourcesList = allowSources;
	}

};
