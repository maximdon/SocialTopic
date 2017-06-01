package com.softlib.imatch.matcher.filter;

import java.util.ArrayList;
import java.util.List;

import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.matcher.MatchCandidate;
import com.softlib.imatch.ticketprocessing.IProcessedTicket.MatchMode;

public class MaxScoreCandidatesSubfilter extends BaseCandidatesSubfilter {

	public List<MatchCandidate> filterCandidates(String objectId, List<MatchCandidate> candidates) {
		float maxCandidateScore = config.getMaxCandidateScore();
		if(maxCandidateScore < 0 || candidates.size() == 0) //This filter is disabled;
			return candidates;
		MatchCandidate firstCandidate = candidates.get(0);
		if(firstCandidate.getProcessedTicket().getMatchMode().equals(MatchMode.rematch))
			return candidates;
		List<MatchCandidate> filteredList = new ArrayList<MatchCandidate>();
		for(MatchCandidate candidate : candidates) {
			if(candidate.getScore() < maxCandidateScore)
				filteredList.add(candidate);
			else
				continue;
		}
		LogUtils.debug(log, "After applying min score filter the candidates are %s", filteredList);
		return filteredList;
	}

}
