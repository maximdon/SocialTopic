package com.softlib.imatch.dictionary;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("sourceBoostConfig")
public class SourceBoostConfig {
	
	@XStreamImplicit(itemFieldName="rule")
	private List<SourceBoostRuleConfig> rules;

	public void init(TechnicalDictionary dictionary) {
		if (rules==null)
			return;
		for (SourceBoostRuleConfig rule : rules) {
			rule.init(dictionary);
		}
	}

	public float getAddBoost(TechnicalDictionaryTerm term) {
		for (SourceBoostRuleConfig rule : rules) {
			float addBoost = rule.getAddBoost(term);
			if (addBoost>0)
				return addBoost;
		}
		return 0;
	}
	
};
