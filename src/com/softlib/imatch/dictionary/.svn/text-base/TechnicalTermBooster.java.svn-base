package com.softlib.imatch.dictionary;

import com.softlib.imatch.IContextInitializationListener;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.common.configuration.IConfigurationObject;
import com.softlib.imatch.common.configuration.IConfigurationResource;
import com.softlib.imatch.common.configuration.IConfigurationResourceLoader;
import com.softlib.imatch.score.ScoreConfig;


public class TechnicalTermBooster implements ITechnicalTermBooster, IContextInitializationListener {

	private IConfigurationResourceLoader resourceLoader;
	private float lengthWeight;
	private float sourceWeight;
	
	static private SourceBoostConfig sourceBoostConfig;

	public TechnicalTermBooster(IConfigurationResourceLoader loader) {
		resourceLoader = loader;
		RuntimeInfo.getCurrentInfo().registerContextInitializationListener(this);
		
	}
	
	public void init(TechnicalDictionary dictionary) {
		if (sourceBoostConfig==null) {
			IConfigurationResource resource = 
				resourceLoader.loadResource("xml:///{SolutionConfigFolder}/sourceBoost.xml;//sourceBoostConfig");
			sourceBoostConfig = (SourceBoostConfig)resource.getCustomConfiguration(SourceBoostConfig.class);
			sourceBoostConfig.init(dictionary);
		}
	}
	
	public float getBoost(TechnicalDictionaryTerm term) {
		return  lengthScore(term) * lengthWeight  + sourceScore(term) * sourceWeight;
	}
	
	public float sourceScore(TechnicalDictionaryTerm term) {
		TechnicalTermSource source = term.getTermSource();
		float rc = 0;
		if (source!=null)
			rc = source.getSourceBoost();
		rc += sourceBoostConfig.getAddBoost(term);
		return rc;
	}

	public float lengthScore(TechnicalDictionaryTerm term) {	
		int numTokens = term.getNumTokens();
		float score = 1;
		
		score = (float) Math.min(5, Math.log(numTokens + 0.5) / Math.log(1.5));
		/*
		switch(numTokens) {
		case 1:
			score = 1;
			break;
		case 2:
			score = (float) 2.5;
			break;
		case 3:
			score = 5;
			break;
		}
		*/
		return score;
	}

	public void contextInitialized() {
		IConfigurationResource resource = resourceLoader.loadResource("xml:///{SolutionConfigFolder}/matcher.xml;//scorer");
		IConfigurationObject config = resource.getConfigurationObject(ScoreConfig.class);
		sourceWeight = (Float)config.getCommonProperty("sourceWeight");
		lengthWeight = (Float)config.getCommonProperty("lengthWeight");
	}
}
