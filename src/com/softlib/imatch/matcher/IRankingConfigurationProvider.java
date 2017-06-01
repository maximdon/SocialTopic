package com.softlib.imatch.matcher;

import com.softlib.imatch.ticketprocessing.IProcessedTicket;

public interface IRankingConfigurationProvider {

	public abstract CandidatesRankingModeConfig getRankingModeConfig(
			IProcessedTicket ticketToMatch, MatchCandidate candidate);

	public abstract CandidatesRankingModeConfig getRankingModeConfig(
			String originObjectId, String matchModeName);

	public abstract String getMatchModeName(IProcessedTicket ticketToMatch);

}