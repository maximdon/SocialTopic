package com.softlib.imatch.matcher;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.softlib.imatch.IContextInitializationListener;
import com.softlib.imatch.MatcherException;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.common.configuration.IConfigurationObject;
import com.softlib.imatch.common.configuration.IConfigurationResource;
import com.softlib.imatch.common.configuration.IConfigurationResourceLoader;
import com.softlib.imatch.ticketprocessing.IProcessedTicket;
import com.softlib.imatch.ticketprocessing.IProcessedTicket.MatchMode;

public class CandidatesRanker implements ICandidatesSubprocessor, IContextInitializationListener, IRankingConfigurationProvider {

	private Logger log = Logger.getLogger(CandidatesRanker.class);
	private IConfigurationResourceLoader loader;

	private Map<String, Map<String, CandidatesRankingModeConfig>> objects;
	
	public CandidatesRanker(IConfigurationResourceLoader loader)
	{
		this.loader = loader;
		RuntimeInfo.getCurrentInfo().registerContextInitializationListener(this);
	}
	
	public boolean runBeforeSingleProcessors() {
		return true;
	}

	public Collection<MatchCandidate> processCandidates(IProcessedTicket ticketToMatch,
														Collection<MatchCandidate> candidates) throws MatcherException {
		for(MatchCandidate candidate : candidates) {
			CandidatesRankingModeConfig modeConfig = getRankingModeConfig(ticketToMatch, candidate);
			if (modeConfig==null || modeConfig.getRanks()==null) 
				throw new MatcherException("CandidatesRankingModeConfig not exist");
			for(CandidateRank rank : modeConfig.getRanks()) {
				if(rank.isTrue(candidate)) {
					candidate.setRank(rank);
					//Condition for this candidate set, step to another candidate
					break;
				}
			}
		}
		return candidates;
	}

	/* (non-Javadoc)
	 * @see com.softlib.imatch.matcher.IRankingConfigurationProvider#getRankingModeConfig(com.softlib.imatch.ticketprocessing.IProcessedTicket, com.softlib.imatch.matcher.MatchCandidate)
	 */
	public CandidatesRankingModeConfig getRankingModeConfig(IProcessedTicket ticketToMatch, MatchCandidate candidate) {
		String matchModeName = getMatchModeName(ticketToMatch);
		return getRankingModeConfig(candidate.getOriginObjectId(), matchModeName);
	}

	/* (non-Javadoc)
	 * @see com.softlib.imatch.matcher.IRankingConfigurationProvider#getRankingModeConfig(java.lang.String, java.lang.String)
	 */
	public CandidatesRankingModeConfig getRankingModeConfig(String originObjectId, String matchModeName) {
		if(objects == null)
			contextInitialized();
		Map<String,CandidatesRankingModeConfig> configByMode =
			objects.get(originObjectId);
		CandidatesRankingModeConfig modeConfig = configByMode.get(matchModeName);
		return modeConfig;
	}
	
	/* (non-Javadoc)
	 * @see com.softlib.imatch.matcher.IRankingConfigurationProvider#getMatchModeName(com.softlib.imatch.ticketprocessing.IProcessedTicket)
	 */
	public String getMatchModeName(IProcessedTicket ticketToMatch) {
		MatchMode matchMode = ticketToMatch.getMatchMode();
		if (ticketToMatch.isMatchModeWithFewTerms())
			matchMode = MatchMode.rematch;
		String matchModeName = matchMode.name();
		return matchModeName;
	}
	
	@SuppressWarnings("unchecked")
	public void contextInitialized() {
		IConfigurationResource resource = loader.loadResource("xml:///{SolutionConfigFolder}/matcher.xml;//candidatesRanking");
		IConfigurationObject config = resource.getConfigurationObject(CandidatesRankingConfig.class);
		objects = new HashMap<String, Map<String, CandidatesRankingModeConfig>>();
		
		for(String objectId : config.getAllObjects()) {
			Map<String, CandidatesRankingModeConfig> modes = new HashMap<String, CandidatesRankingModeConfig>();

			List<CandidatesRankingModeConfig> localModes = (List<CandidatesRankingModeConfig>) config.getProperty(objectId, "modes");
			
			for (CandidatesRankingModeConfig candidatesRankingModeConfig : localModes) {
				modes.put(candidatesRankingModeConfig.getName(), candidatesRankingModeConfig);
			}
			
			objects.put(objectId, modes);
		}
	}
}
