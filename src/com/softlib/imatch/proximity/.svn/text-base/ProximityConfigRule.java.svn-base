package com.softlib.imatch.proximity;

import com.softlib.imatch.common.StringUtils;
import com.softlib.imatch.common.configuration.ConfigurationException;
import com.softlib.imatch.dictionary.TechnicalDictionary;
import com.softlib.imatch.dictionary.TechnicalTermSource;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("rule")
public class ProximityConfigRule {

	static private final int MAX_WORDS_IN_GAP = 3;

	@XStreamAsAttribute
	@XStreamAlias("name")
	private String ruleName;
	@XStreamAsAttribute
	private String proxSrcName;
	@XStreamAsAttribute
	private String maxWordGapSize;
	@XStreamAsAttribute
	@XStreamAlias("term1SourceGroup")
	private String term1SourceGroupName;
	private ProximityConfigSourceGroup term1SourceGroup;
	@XStreamAlias("term2SourceGroup")
	@XStreamAsAttribute
	private String term2SourceGroupName;
	private ProximityConfigSourceGroup term2SourceGroup;
	
	private ProximityConfig config;
	private TechnicalTermSource proxSrc;
	private int maxGapSize;

	public String getRuleName()
	{
		return ruleName;
	}
	
	public int getMaxGapSize() {
		return maxGapSize;
	}
	
	public String getMaxWordGapSize() {
		return maxWordGapSize;
	}

	public TechnicalTermSource getProxSrc() {
		return proxSrc;
	}	
	
	public boolean matchSources(TechnicalTermSource term1Src, TechnicalTermSource term2Src) {
		if(term1SourceGroup.containsSource(term1Src) && term2SourceGroup.containsSource(term2Src) ||
		   term2SourceGroup.containsSource(term1Src) && term1SourceGroup.containsSource(term2Src))
			return true;
		return false;
	}
	
	void init(ProximityConfig config, TechnicalDictionary dictionary) {
		this.config = config;
		term1SourceGroup = getGroup(term1SourceGroupName);
		term1SourceGroup.init(dictionary);
		term2SourceGroup = getGroup(term2SourceGroupName);
		term2SourceGroup.init(dictionary);
		maxGapSize = Integer.parseInt(maxWordGapSize);
		if (maxGapSize<0)
			maxGapSize = MAX_WORDS_IN_GAP;
		proxSrc = getSource(proxSrcName,dictionary);
	}
	
	static TechnicalTermSource getSource(String sourceName,TechnicalDictionary dictionary) {
		if (sourceName==null || sourceName.isEmpty())
			throw new ConfigurationException("Proximity : source name is empty");
		TechnicalTermSource source = dictionary.addSource(sourceName);
		return source;
	}
	
	private ProximityConfigSourceGroup getGroup(String sourceGroupName) {
		if (StringUtils.isEmpty(sourceGroupName))
			throw new ConfigurationException("Proximity config error: source group name is empty");
		for(ProximityConfigSourceGroup group : config.getSourceGroups())
			if(group.getGroupName().equals(sourceGroupName))
				return group;
		throw new ConfigurationException("Proximity config error: unrecognized group " + sourceGroupName);
	}

};
