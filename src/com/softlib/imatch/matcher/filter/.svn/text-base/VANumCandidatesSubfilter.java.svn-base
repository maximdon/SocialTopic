package com.softlib.imatch.matcher.filter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.matcher.MatchCandidate;


// very similar to NumCandidatesSubfilter - with small changes to fit the VA environment
public class VANumCandidatesSubfilter extends BaseCandidatesSubfilter {

	public List<MatchCandidate> filterCandidates(String objectId, List<MatchCandidate> candidates) {
		int maxNumCandidates = config.getMaxNumCandidates();
		int candidatesFactor = config.getCandidatesFactor();
		int filterPercentageDropOff = config.getFilterPercentageDropOff();
		if(maxNumCandidates < 0)
			return candidates;
		
		List<MatchCandidate> filteredList = new ArrayList<MatchCandidate>(maxNumCandidates);
		int count = 0;
		float lastScore = 0;
		boolean hadDropOff = false;
		Iterator<MatchCandidate> candidatesIterator = candidates.iterator();

		while(candidatesIterator.hasNext()) {
			MatchCandidate candidate = candidatesIterator.next();
			count++;
			float score = candidate.getScore();
			
			if (count <= maxNumCandidates)
			{
				// check if we reached drop-off
				if (score == 0 || ((1-(score/lastScore)) * 100) > filterPercentageDropOff)
				{
					hadDropOff = true;
					break;
				}
				else
					filteredList.add(candidate);
			}
			else if (!hadDropOff)
			{
				if (count > maxNumCandidates * candidatesFactor)
				{
					// irrelevant results
					// mark first candidate
					filteredList.get(0).setScore(-1.0f);
					// remove the others
					for (int k = maxNumCandidates-1;k>0;k--)
						filteredList.remove(k);
					LogUtils.debug(log, "After applying VA num candidates filter - candidates were found irrelevant");
					return filteredList;
				}
				
				if (((1-(score/lastScore)) * 100) > filterPercentageDropOff ||score==0)
				{
					hadDropOff = true;
					break;
				}
			}
			lastScore = score;
		}
			
		LogUtils.debug(log, "After applying num candidates filter the candidates are %s", filteredList);

		return filteredList;
	}

}
