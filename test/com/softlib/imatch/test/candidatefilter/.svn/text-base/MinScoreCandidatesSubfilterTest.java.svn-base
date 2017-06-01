package com.softlib.imatch.test.candidatefilter;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.softlib.imatch.InMemoryTicket;
import com.softlib.imatch.matcher.MatchCandidate;
import com.softlib.imatch.matcher.filter.CandidatesFilterModeConfig;
import com.softlib.imatch.matcher.filter.MinScoreCandidatesSubfilter;

public class MinScoreCandidatesSubfilterTest {

	@Test
	public void testFilterDisabled() {
		MinScoreCandidatesSubfilter filter = new MinScoreCandidatesSubfilter();
		CandidatesFilterModeConfig config = new CandidatesFilterModeConfig();
		config.setMinCandidateScore(-1);
		filter.setConfiguration(config);
		List<MatchCandidate> candidates = new ArrayList<MatchCandidate>();
		candidates.add(new MatchCandidate((float) 0.22, null));
		candidates.add(new MatchCandidate((float) 0.44, null));
		List<MatchCandidate> filteredCandidates = filter.filterCandidates("", candidates);
		assertEquals(candidates, filteredCandidates);
	}

	@Test
	public void testFilter() {
		MinScoreCandidatesSubfilter filter = new MinScoreCandidatesSubfilter();
		CandidatesFilterModeConfig config = new CandidatesFilterModeConfig();
		config.setMinCandidateScore((float)0.33);
		filter.setConfiguration(config);
		List<MatchCandidate> candidates = new ArrayList<MatchCandidate>();
		candidates.add(new MatchCandidate((float) 0.22, new InMemoryTicket("")));
		candidates.add(new MatchCandidate((float) 0.44, new InMemoryTicket("")));
		List<MatchCandidate> filteredCandidates = filter.filterCandidates("", candidates);
		assertEquals(1, filteredCandidates.size());
	}

	@Test
	public void testEmptyList() {
		MinScoreCandidatesSubfilter filter = new MinScoreCandidatesSubfilter();
		CandidatesFilterModeConfig config = new CandidatesFilterModeConfig();
		config.setMinCandidateScore((float)0.44);
		filter.setConfiguration(config);
		List<MatchCandidate> candidates = new ArrayList<MatchCandidate>();
		List<MatchCandidate> filteredCandidates = filter.filterCandidates("", candidates);
		assertEquals(candidates, filteredCandidates);
	}
}
