package com.softlib.imatch.matcher.boost;

import java.util.List;

import org.apache.commons.configuration.ConfigurationException;

import com.softlib.imatch.IContextInitializationListener;
import com.softlib.imatch.MatcherException;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.common.configuration.IConfigurationObject;
import com.softlib.imatch.common.configuration.IConfigurationResource;
import com.softlib.imatch.common.configuration.IConfigurationResourceLoader;
import com.softlib.imatch.matcher.ISingleCandidateProcessor;
import com.softlib.imatch.matcher.MatchCandidate;

/**
 * This class is responsible for applying custom boosts factors on match results.
 * Custom boosts factors don't influence matching score itself but reorder (boost) specific candidates based on 
 * presence of specific words or containing specific values
 * @author Maxim Donde
 *
 */
public class CustomBooster implements ISingleCandidateProcessor, IContextInitializationListener
{
	private IConfigurationObject config;
	private IConfigurationResourceLoader loader;

	public CustomBooster(IConfigurationResourceLoader loader)
	{
		this.loader = loader;
		RuntimeInfo.getCurrentInfo().registerContextInitializationListener(this);
	}
	
	@SuppressWarnings("unchecked")
	public void processCandidate(MatchCandidate candidate) throws MatcherException 
	{
		List<CustomBoostFactor> boostFactors = (List<CustomBoostFactor>) config.getProperty(candidate.getOriginObjectId(), "factors");
		for(CustomBoostFactor factor : boostFactors)
		{
			factor.applyBoost(candidate);
		}
	}
	public void contextInitialized() {
		IConfigurationResource resource = loader.loadResource("xml:///{SolutionConfigFolder}/matcher.xml;//customBoostFactors");
		config = resource.getConfigurationObject(CustomBoostFactorsConfig.class);
	}				
}
