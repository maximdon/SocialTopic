package com.softlib.imatch.matcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;

import com.softlib.imatch.IContextInitializationListener;
import com.softlib.imatch.MatcherException;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.common.configuration.IConfigurationObject;
import com.softlib.imatch.common.configuration.IConfigurationResource;
import com.softlib.imatch.common.configuration.IConfigurationResourceLoader;
import com.softlib.imatch.ticketprocessing.IProcessedTicket;

public class CandidatesIncluder implements ICandidatesSubprocessor, IContextInitializationListener {

	private Logger log = Logger.getLogger(CandidatesIncluder.class);
	private IConfigurationResourceLoader loader;
	private Map<String, Map<String, CandidateRank>> includeRanks;
	
	public CandidatesIncluder(IConfigurationResourceLoader loader)
	{
		this.loader = loader;
		RuntimeInfo.getCurrentInfo().registerContextInitializationListener(this);
	}
	
	public boolean runBeforeSingleProcessors() {
		return true;
	}

	public Collection<MatchCandidate> processCandidates(IProcessedTicket ticketToMatch,
			Collection<MatchCandidate> candidates) throws MatcherException {
		if(includeRanks == null)
			contextInitialized();
		if(includeRanks.isEmpty())
			//Nothing to do, includer is disabled
			return candidates;
		for(MatchCandidate candidate : candidates) {
			CandidateRank includeRank = includeRanks.get(candidate.getOriginObjectId()).get(ticketToMatch.getMatchMode().name());
			if(includeRank == null)
				continue;
			if(includeRank.isTrue(candidate)) {
				LogUtils.debug(log, "Marking candidate %s as required since it opposite score is more than %f", candidate, candidate.getOppositeScore());
				candidate.setRequired(true);
			}
		}
		return candidates;
	}
	
	@SuppressWarnings("unchecked")
	public void contextInitialized() {
		IConfigurationResource resource = loader.loadResource("xml:///{SolutionConfigFolder}/matcher.xml;//candidatesRanking");
		IConfigurationObject config = resource.getConfigurationObject(CandidatesRankingConfig.class);
		includeRanks = new HashMap<String, Map<String, CandidateRank>>();
		
		for(String objectId : config.getAllObjects()) {	
			Map<String, CandidateRank> modes = new HashMap<String, CandidateRank>();
			List<CandidatesRankingModeConfig> localModes = (List<CandidatesRankingModeConfig>) config.getProperty(objectId, "modes");
			
			for (CandidatesRankingModeConfig candidatesRankingModeConfig : localModes) {
				List<CandidateRank> objectRanks = candidatesRankingModeConfig.getRanks();
				
				for(CandidateRank rank : objectRanks) {
					if(rank.getName().equals("included")) {
						modes.put(candidatesRankingModeConfig.getName(), rank);
						break;
					}
				}
				
				includeRanks.put(objectId, modes);
			}
		}
	}
}
