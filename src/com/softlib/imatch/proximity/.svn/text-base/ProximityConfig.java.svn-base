package com.softlib.imatch.proximity;

import java.util.List;

import com.softlib.imatch.dictionary.TechnicalDictionary;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("proximityConfig")
public class ProximityConfig {

	@XStreamImplicit(itemFieldName="sourceGroup")
	private List<ProximityConfigSourceGroup> sourceGroups;

	@XStreamImplicit(itemFieldName="rule")
	private List<ProximityConfigRule> rules;

	@XStreamImplicit(itemFieldName="wordGroup")
	private List<ProximityConfigWordGroup> wordGroups;

	
	public List<ProximityConfigWordGroup> getWordGroups() {
		return wordGroups;
	}

	public List<ProximityConfigSourceGroup> getSourceGroups() {
		return sourceGroups;
	}

	public List<ProximityConfigRule> getRules() {
		return rules;
	}

	public void init(TechnicalDictionary dictionary) {
		if (rules==null)
			return;
		for (ProximityConfigRule rule : rules) {
			rule.init(this, dictionary);
		}
	}

	public int getMaxGap() {
		int maxGap = -1;
		for(ProximityConfigRule rule : rules)
			if(rule.getMaxGapSize() > maxGap)
				maxGap = rule.getMaxGapSize();
		return maxGap;
	}
	
	
};
