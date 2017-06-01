package com.softlib.imatch.test.candidatefilter;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.softlib.imatch.InMemoryTicket;
import com.softlib.imatch.matcher.MatchCandidate;
import com.softlib.imatch.matcher.filter.CandidatesFilterModeConfig;
import com.softlib.imatch.matcher.filter.NumCandidatesSubfilter;

public class NumCandidatesSubfilterTest {

	@Test
	public void testFilterDisabled() {
		NumCandidatesSubfilter filter = new NumCandidatesSubfilter();
		CandidatesFilterModeConfig config = new CandidatesFilterModeConfig();
		config.setMaxNumCandidates(-1);
		filter.setConfiguration(config);
		List<MatchCandidate> candidates = new ArrayList<MatchCandidate>();
		candidates.add(new MatchCandidate((float) 0.22, null));
		candidates.add(new MatchCandidate((float) 0.44, null));
		List<MatchCandidate> filteredCandidates = filter.filterCandidates("", candidates);
		assertEquals(candidates, filteredCandidates);
	}

	@Test
	public void testFilter() {
		NumCandidatesSubfilter filter = new NumCandidatesSubfilter();
		CandidatesFilterModeConfig config = new CandidatesFilterModeConfig();
		config.setMaxNumCandidates(-1);
		filter.setConfiguration(config);
		List<MatchCandidate> candidates = new ArrayList<MatchCandidate>();
		candidates.add(new MatchCandidate((float) 0.22, new InMemoryTicket("")));
		candidates.add(new MatchCandidate((float) 0.44, new InMemoryTicket("")));
		List<MatchCandidate> filteredCandidates = filter.filterCandidates("", candidates);
		assertEquals(1, filteredCandidates.size());
	}

	@Test
	public void testEmptyList() {
		NumCandidatesSubfilter filter = new NumCandidatesSubfilter();
		CandidatesFilterModeConfig config = new CandidatesFilterModeConfig();
		config.setMaxNumCandidates(-1);
		new CandidatesFilterModeConfig();
		filter.setConfiguration(config);
		List<MatchCandidate> candidates = new ArrayList<MatchCandidate>();
		List<MatchCandidate> filteredCandidates = filter.filterCandidates("", candidates);
		assertEquals(candidates, filteredCandidates);
	}
}
