package com.softlib.imatch.matcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.softlib.imatch.BaseTicket;
import com.softlib.imatch.IContextInitializationListener;
import com.softlib.imatch.MatcherException;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.common.configuration.IConfigurationObject;
import com.softlib.imatch.common.configuration.IConfigurationResource;
import com.softlib.imatch.common.configuration.IConfigurationResourceLoader;
import com.softlib.imatch.dbintegration.DBTicket;
import com.softlib.imatch.matcher.filter.CandidatesFilterConfig;
import com.softlib.imatch.matcher.filter.CandidatesFilterModeConfig;
import com.softlib.imatch.ticketprocessing.IProcessedTicket;
import com.softlib.imatch.ticketprocessing.IProcessedTicket.MatchMode;

/**
 * This class is responsible for boosting starred candidates and duplicating them into stars tab.
 * @author Maxim Donde
 */
public class CandidatesStarHandler implements ICandidatesSubprocessor, IContextInitializationListener 
{
	private static final String STARRED_CANDIDATES_OBJECT_ID = "starredCases";
	private Logger log = Logger.getLogger(CandidatesStarHandler.class);
	private IConfigurationResourceLoader loader;
	private List<CandidatesFilterModeConfig> filterConfigModes;
	private IRankingConfigurationProvider rankingConfigurationProvider;
	
	public CandidatesStarHandler(IConfigurationResourceLoader loader, IRankingConfigurationProvider rankingConfigurationProvider)
	{
		this.loader = loader;
		this.rankingConfigurationProvider = rankingConfigurationProvider;
		RuntimeInfo.getCurrentInfo().registerContextInitializationListener(this);
	}
	
	public boolean runBeforeSingleProcessors() {
		return true;
	}

	public Collection<MatchCandidate> processCandidates(IProcessedTicket ticketToMatch,
			Collection<MatchCandidate> candidates) throws MatcherException {	
		List<MatchCandidate>starredCandidates = new ArrayList<MatchCandidate>();
		for(MatchCandidate candidate : candidates) {			
			CandidatesRankingModeConfig modeConfig = rankingConfigurationProvider.getRankingModeConfig(ticketToMatch, candidate);
			CandidateRank highestRank = modeConfig.getHighestRank();
			if(candidate.isStarredForThisSource()) {
				LogUtils.info(log, "Candidate %s score was set to absolute since it already starred as an aswer for this ticket", candidate);
				if(candidate.getRank().equals(highestRank)) {
					//Just to make sure it is more than other 4 stripes candidates
					candidate.setScore((float)1.1);
				}
				else {
					candidate.setScore(1);
					highestRank.isTrue(candidate);
					candidate.setRank(highestRank);
				}
				candidate.setRequired(true);
				continue;
			}
			int numStars = candidate.getNumStars();
			if(numStars == 0 || candidate.getRank() == null)
				continue;
			float maxScoreForInterval;
			if(candidate.getRank().equals(highestRank)) {
				maxScoreForInterval = Math.max((float)1.0, candidate.getScore());
				if(candidate.getScore() == maxScoreForInterval)
					//Just in case the candidate has already maximum score slightly increase the interval
					maxScoreForInterval += (float)0.3;
			}
			else
				maxScoreForInterval =  modeConfig.getRank(candidate.getRank().getIntValue() + 1).getCondition().getMinScore();
			//Adjust candidate score according to the number of stars
			float newScore = candidate.getScore() + (maxScoreForInterval - candidate.getScore()) * ((float)1.0 * numStars / modeConfig.getMaxNumStars());
			if(newScore >= maxScoreForInterval)
				newScore = (float) (maxScoreForInterval - 0.00001);
			LogUtils.debug(log, "Candidate %s has %d stars, as a result it score was changed from %f to %f", candidate, numStars, candidate.getScore(), newScore);
			candidate.setScore(newScore);
			starredCandidates.add(candidate);
		}
		//Add starred candidates to the starred tab 
		Collections.sort(starredCandidates, new StarComparator());
		int numStarredCandidates = 0;
		String matchModeName = rankingConfigurationProvider.getMatchModeName(ticketToMatch);
		CandidatesRankingModeConfig modeConfig = rankingConfigurationProvider.getRankingModeConfig(STARRED_CANDIDATES_OBJECT_ID, matchModeName);
		CandidatesFilterModeConfig filterConfig = null;
		for(CandidatesFilterModeConfig tmpConfig : filterConfigModes)
			if(tmpConfig.getName().equals(matchModeName))
				filterConfig = tmpConfig;
		for(MatchCandidate starredCandidate : starredCandidates) {
			//Note starred candidates are sorted by the number of stars
			if(numStarredCandidates >= filterConfig.getMaxNumCandidates() || !modeConfig.getLowestRank().getCondition().isTrue(starredCandidate))
				continue;
			MatchCandidate duplicateCandidate = duplicateCandidate(starredCandidate, STARRED_CANDIDATES_OBJECT_ID, matchModeName);
			candidates.add(duplicateCandidate);
			LogUtils.debug(log, "Marking candidate %s as required for stars tab", duplicateCandidate);
			numStarredCandidates++;
		}
		return candidates;
	}

	public static MatchCandidate duplicateCandidate(MatchCandidate candidate, String objectId, String matchModeName) {
		BaseTicket duplicateCandidateTicket = new DBTicket(objectId);
		BaseTicket originalCandidateTicket = (BaseTicket)candidate.getCandidateData();
		//The prefix is really not important there, just to make sure the ids are not the same
		duplicateCandidateTicket.setId("Duplicate:" + originalCandidateTicket.getId());
		duplicateCandidateTicket.setField(CandidateEnricher.ORIGINAL_ID, originalCandidateTicket.getId());
		duplicateCandidateTicket.setField(CandidateEnricher.ORIGINAL_OBJECT_ID, originalCandidateTicket.getOriginObjectId());
		for(String fieldName : originalCandidateTicket.getFieldsConfig().getAllFields(MatchMode.valueOf(matchModeName)))
			duplicateCandidateTicket.setField(fieldName, originalCandidateTicket.getField(fieldName));
		MatchCandidate duplicateCandidate = new MatchCandidate(new CandidateScore(candidate.getScore()), duplicateCandidateTicket, candidate.isDataExist(), candidate.getProcessedTicket());
		duplicateCandidate.setSourceProcessedTicket(candidate.getSourceProcessedTicket());
		duplicateCandidate.setRequired(true);
		duplicateCandidate.setRank(candidate.getRank());
		return duplicateCandidate;
	}
	
	@SuppressWarnings("unchecked")
	public void contextInitialized() {
		IConfigurationResource filterResource = loader.loadResource("xml:///{SolutionConfigFolder}/matcher.xml;//candidatesFilter");
		IConfigurationObject config = filterResource.getConfigurationObject(CandidatesFilterConfig.class);
		filterConfigModes = (List<CandidatesFilterModeConfig>) config.getProperty(STARRED_CANDIDATES_OBJECT_ID, "modes");
		
	}	
	private class StarComparator implements Comparator<MatchCandidate> {

		@Override
		public int compare(MatchCandidate matchCandidate1, MatchCandidate matchCandidate2) {
			Integer stars1 = matchCandidate1.getNumStars();
			Integer stars2 = matchCandidate2.getNumStars();
			return stars1.compareTo(stars2);			
		}		
	}
}

