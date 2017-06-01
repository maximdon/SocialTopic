package com.softlib.imatch.matcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.softlib.imatch.MatcherException;
import com.softlib.imatch.ticketprocessing.IProcessedTicket;

public class DefaultCandidatesProcessor implements ICandidatesProcessor 
{
	private List<ISingleCandidateProcessor> singleProcessors;
	private List<ICandidatesSubprocessor> subProcessorsBefore;
	private List<ICandidatesSubprocessor> subProcessorsAfter;
	
	public DefaultCandidatesProcessor()
	{
		subProcessorsBefore = new ArrayList<ICandidatesSubprocessor>();
		subProcessorsAfter = new ArrayList<ICandidatesSubprocessor>();
	}

	public void setSingleProcessors(List<ISingleCandidateProcessor> singleProcessors)
	{
		this.singleProcessors = singleProcessors;
	}

	public void setSubProcessors(List<ICandidatesSubprocessor> subProcessors)
	{
		for(ICandidatesSubprocessor subprocessor : subProcessors)
			if(subprocessor.runBeforeSingleProcessors())
				subProcessorsBefore.add(subprocessor);
			else
				subProcessorsAfter.add(subprocessor);
	}

	//TODO check again the whole process, it involves a lot of list processing, can it be done more efficient?
	public Collection<MatchCandidate> processCandidates(IProcessedTicket ticketToMatch, Collection<MatchCandidate> candidates) throws MatcherException {
		Collection<MatchCandidate> processedCandidates = candidates;
		for(ICandidatesSubprocessor subprocessor : subProcessorsBefore) {
			processedCandidates = subprocessor.processCandidates(ticketToMatch, candidates);
		}
		for(MatchCandidate candidate : processedCandidates) {
			for(ISingleCandidateProcessor singleCandidateProcessor : singleProcessors)
				singleCandidateProcessor.processCandidate(candidate);
		}
		for(ICandidatesSubprocessor subprocessor : subProcessorsAfter) {
			processedCandidates = subprocessor.processCandidates(ticketToMatch, processedCandidates);
		}
		return processedCandidates;
	}
}
