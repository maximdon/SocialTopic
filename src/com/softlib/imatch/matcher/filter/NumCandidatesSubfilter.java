package com.softlib.imatch.matcher.filter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.matcher.MatchCandidate;

public class NumCandidatesSubfilter extends BaseCandidatesSubfilter {

	public List<MatchCandidate> filterCandidates(String objectId, List<MatchCandidate> candidates) {
		int maxNumCandidates = config.getMaxNumCandidates();
		int maxAuthCandidates = config.getMaxAuthCandidates();
		int filterPercentageDropOff = config.getFilterPercentageDropOff();
		if(maxNumCandidates < 0)
			return candidates;
		
		List<MatchCandidate> filteredList = new ArrayList<MatchCandidate>(maxNumCandidates);
		int count = 0;
		float lastScore = 0;
		Iterator<MatchCandidate> candidatesIterator = candidates.iterator();

		while(candidatesIterator.hasNext()) {
			MatchCandidate candidate = candidatesIterator.next();
			count++;
			float score = candidate.getScore();
			
			if (count>maxAuthCandidates ||
				(count>maxNumCandidates && (((1-(score/lastScore)) * 100) > filterPercentageDropOff ||score==0)))
				break;
			filteredList.add(candidate);
			lastScore = score;
		}
			
		LogUtils.debug(log, "After applying num candidates filter the candidates are %s", filteredList);

		return filteredList;
	}

}
