package com.softlib.imatch.matcher.filter;

import java.util.List;

import com.softlib.imatch.matcher.MatchCandidate;

public interface ICandidatesSubfilter {
	
	void setConfiguration(CandidatesFilterModeConfig config);	
	
	List<MatchCandidate> filterCandidates(String objectId, List<MatchCandidate> filteredCandidates);	

};
