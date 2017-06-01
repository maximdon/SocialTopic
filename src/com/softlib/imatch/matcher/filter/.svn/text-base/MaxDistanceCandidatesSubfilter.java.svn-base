package com.softlib.imatch.matcher.filter;

import java.util.ArrayList;
import java.util.List;

import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.matcher.MatchCandidate;

public class MaxDistanceCandidatesSubfilter extends BaseCandidatesSubfilter {

	public List<MatchCandidate> filterCandidates(String objectId, List<MatchCandidate> candidates) {
		int maxDistance = config.getMaxScoreDistancePercentage();
		if(maxDistance < 0)
			//This filter is disabled;
			return candidates;
		float previousScore = 0;
		List<MatchCandidate> filteredList = new ArrayList<MatchCandidate>();
		for(MatchCandidate candidate : candidates) {
			if(previousScore == 0) {
				previousScore = candidate.getScore();
				filteredList.add(candidate);
			}
			else
				if(100 * (previousScore - candidate.getScore())/previousScore <= maxDistance) {
					previousScore = candidate.getScore();
					filteredList.add(candidate);
				}
				else
					break;
		}
		LogUtils.debug(log, "After applying max distance filter the candidates are %s", filteredList);
		return filteredList;
	}

}
