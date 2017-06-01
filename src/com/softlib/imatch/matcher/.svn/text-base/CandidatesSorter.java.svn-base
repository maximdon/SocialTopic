package com.softlib.imatch.matcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import com.softlib.imatch.MatcherException;
import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.ticketprocessing.IProcessedTicket;

public class CandidatesSorter implements ICandidatesSubprocessor {

	private Logger log = Logger.getLogger(CandidatesSorter.class);
	
	public boolean runBeforeSingleProcessors() {
		return false;
	}

	public Collection<MatchCandidate> processCandidates(IProcessedTicket ticketToMatch,
			Collection<MatchCandidate> candidates) throws MatcherException {
		//The collection should be sorted after applying custom boosts
		List<MatchCandidate>sortedCollection;
		if(candidates instanceof List)
			sortedCollection = (List<MatchCandidate>)candidates;
		else
			sortedCollection = new ArrayList<MatchCandidate>(candidates);
		//TODO check here, does sort by score make sense or we need another sort by relation?
		Collections.sort(sortedCollection, Collections.reverseOrder());
		LogUtils.debug(log, "After applying candidates sorter the collection is", sortedCollection);
		return sortedCollection;
	}
}
