package com.softlib.imatch.pattern;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.softlib.imatch.dictionary.TechnicalDictionary;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("patternsConfig")
public class PatternsConfig {
	
	@XStreamImplicit(itemFieldName="pattern")
	private List<PatternsConfigPattren> patterns;

	private Map<TechnicalDictionaryTerm,PatternsConfigPattren> configPatternBySubject = 
		new HashMap<TechnicalDictionaryTerm,PatternsConfigPattren>();

	public List<PatternsConfigPattren> getPaterns() {
		return patterns;
	}

	public void setPaterns(List<PatternsConfigPattren> paterns) {
		this.patterns = paterns;
	}
	
	public void init(TechnicalDictionary dictionary) {
		configPatternBySubject = 
			new HashMap<TechnicalDictionaryTerm,PatternsConfigPattren>();
		for (PatternsConfigPattren pattern : patterns) {
			pattern.init(dictionary);
			configPatternBySubject.put(pattern.getSubjectPart().getAnchor(), pattern);
		}
	}
	
	public Map<TechnicalDictionaryTerm,PatternsConfigPattren> getConfigPatternBySubject() {
		return configPatternBySubject;
	}
	
	
};
