package com.softlib.imatch.matcher.filter;

import java.util.ArrayList;
import java.util.List;

import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.matcher.MatchCandidate;

public class MinScoreCandidatesSubfilter extends BaseCandidatesSubfilter {

	public List<MatchCandidate> filterCandidates(String objectId, List<MatchCandidate> candidates) {
		float minCandidateScore = config.getMinCandidateScore();
		if(minCandidateScore < 0)
			//This filter is disabled;
			return candidates;
		List<MatchCandidate> filteredList = new ArrayList<MatchCandidate>();
		for(MatchCandidate candidate : candidates) {
			if(candidate.getScore() >= minCandidateScore)
				filteredList.add(candidate);
			else
				continue;
		}
		LogUtils.debug(log, "After applying min score filter the candidates are %s", filteredList);
		return filteredList;
	}

}
