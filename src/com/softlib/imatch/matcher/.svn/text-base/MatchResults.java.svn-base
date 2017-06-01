package com.softlib.imatch.matcher;

import java.util.Collection;

import com.softlib.imatch.ticketprocessing.IProcessedTicket;

public class MatchResults {
	private Collection<MatchCandidate> candidates;
	private IProcessedTicket processedTicket;
	private boolean			 irrelevantResults = false;
	
	public void setCandidates(Collection<MatchCandidate> candidates) {
		this.candidates = candidates;
	}
	public Collection<MatchCandidate> getCandidates() {
		return candidates;
	}
	
	public void setProcessedTicket(IProcessedTicket processedTicket) {
		this.processedTicket = processedTicket;
	}
	public IProcessedTicket getProcessedTicket() {
		return processedTicket;
	}
	
	public void setIrrelevantResults()
	{
		irrelevantResults = true;
	}
	public boolean getIrrelevantResults()
	{
		return irrelevantResults;
	}
}
