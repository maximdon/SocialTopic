package com.softlib.imatch.score;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("scorer")
public class ScoreConfig 
{
	private static ScoreConfig defaultConfig = null;
	@XStreamAsAttribute
	private float lengthWeight;
	@XStreamAsAttribute
	private float sourceWeight;
	@XStreamAsAttribute
	private float freqWeight;
	@XStreamAsAttribute
	private float countWeight;
	@XStreamAsAttribute
	private float titleWeight;
	@XStreamAsAttribute
	private boolean useZeroFreqTerms;
	@XStreamAsAttribute
	private boolean useOneFreqTerms;

	public ScoreConfig()
	{	
	}
	
	public static ScoreConfig createDefaultConfig()
	{
		if(defaultConfig != null)
			return defaultConfig;
		defaultConfig = new ScoreConfig();
		defaultConfig.countWeight = (float) 0.1;
		defaultConfig.freqWeight = (float) 0.05;
		defaultConfig.lengthWeight = (float) 0.15;
		defaultConfig.sourceWeight = (float) 0.5;
		defaultConfig.titleWeight = (float) 0.3;
		defaultConfig.useOneFreqTerms = true;
		defaultConfig.useZeroFreqTerms = true;
		return defaultConfig;
	}
	public float getLengthWeight() {
		return lengthWeight;
	}

	public float getSourceWeight() {
		return sourceWeight;
	}

	public float getFreqWeight() {
		return freqWeight;
	}

	public float getCountWeight() {
		return countWeight;
	}

	public float getTitleWeight() {
		return titleWeight;
	}
	
	public boolean isUseZeroFreqTerms() {
		return useZeroFreqTerms;
	}

	public boolean isUseOneFreqTerms() {
		return useOneFreqTerms;
	}
}
