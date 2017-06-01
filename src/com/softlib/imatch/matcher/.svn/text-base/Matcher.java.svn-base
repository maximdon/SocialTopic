package com.softlib.imatch.matcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;

import com.softlib.imatch.ITicket;
import com.softlib.imatch.ITicketProvider;
import com.softlib.imatch.MatcherException;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.TicketProviderFactory;
import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.common.Pair;
import com.softlib.imatch.common.PerformanceTracker;
import com.softlib.imatch.common.TicketTracker;
import com.softlib.imatch.common.configuration.IConfigurationResourceLoader;
import com.softlib.imatch.common.trackinginfo.MatchFinishedTrackingInfo;
import com.softlib.imatch.common.trackinginfo.MatchStartedTrackingInfo;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.enums.MatchErrorCodes;
import com.softlib.imatch.matcher.lucene.LuceneHighlighter;
import com.softlib.imatch.ticketprocessing.IProcessedTicket;
import com.softlib.imatch.ticketprocessing.ITicketProcessor;
import com.softlib.imatch.ticketprocessing.RematchScoreCalculator;
import com.softlib.imatch.ticketprocessing.TicketProcessor;
import com.softlib.imatch.ticketprocessing.IProcessedTicket.MatchMode;
/**
 * Provides common implementation for the IMatcher.
 * Ticket matching is divided into 3 phases:
 * 	1. Ticket preprocessing - analyzing ticket data and creating an appropriate search query
 *  2. Ticket matching - search index files given the search query from the first step for ticket matches
 *  3. Match candidates postprocessing - analyze the candidates from the search process, to return only relevant candidates 
 * Each of these phases itself is defined as an interface, so changing this match behavior is unlikely.
 * Instead, we may need to extend the implementation of each step.
 * @author Maxim Donde
 *
 */
public class Matcher implements IMatcher {
	private static final Logger log = Logger.getLogger(Matcher.class);
	private ITicketProcessor ticketProcessor;
	private ITicketsRepository ticketsRepository;
	private ICandidatesProcessor resultsProcessor;
	private int minTermsToMatch = 1;
	
	public Matcher(ITicketProcessor ticketProcessor, ITicketsRepository repository, ICandidatesProcessor resultsProcessor) {
		this(ticketProcessor, repository, resultsProcessor, 1);
	}

	public Matcher(ITicketProcessor ticketProcessor, ITicketsRepository repository, ICandidatesProcessor resultsProcessor, int minTermsToMatch) {
		this.ticketProcessor = ticketProcessor;
		this.ticketsRepository = repository;
		this.resultsProcessor = resultsProcessor;
		this.minTermsToMatch = minTermsToMatch;
	}

	/**
	 * Find all matches for the existing ticket with the given id.
	 * The ticket is first retrieved from the backend and processed using the second findMatches overload
	 * @see Matcher.findMatches(ITicket ticket) 
	 */
	public MatchResults findMatches(String objectId, String ticketId) throws MatcherException {		
		try { 
			LogUtils.debug(log, "Start matching for ticket id %s", ticketId);
			PerformanceTracker tracker = (PerformanceTracker) RuntimeInfo.getCurrentInfo().getBean("performanceTracker");
			String routineName = "Match" + ticketId;
			tracker.startRoutine(routineName);
			tracker.startStep(routineName, "Retrieve data");
			LogUtils.debug(log, "Retrieving ticket data for ticket %s", ticketId);
			ITicketProvider ticketProvider = (ITicketProvider) RuntimeInfo.getCurrentInfo().getBean(TicketProviderFactory.getProviderId(objectId));			
			ITicket ticket = null;
			//TODO think here ticketProvider should be part of the TicketRepository?
			ticket = ticketProvider.get(ticketId);
			tracker.finishStep(routineName, "Retrieve data");
			if(ticket == null)
				throw new MatcherException("Ticket with id " + ticketId + " was not found in the ticketing system", MatchErrorCodes.TicketNotFound);
			LogUtils.debug(log, "Ticket data for ticket %s retrieved successfully, ticket title is %s", ticketId, ticket.getTitle());
			MatchResults matchResults = findMatches(ticket);
			tracker.finishRoutine(routineName);
			
			return matchResults;
		}
		catch(MatcherException me) {
			LogUtils.fatal(log, me, "Matching failed due to %s", me.getMessage());
			throw me;			
		}
		catch(Exception e){
			LogUtils.fatal(log, e, "Internal error occured during matching. The detailed error is %s", e.getMessage());
			throw new MatcherException("Internal error occured during matching", e);						
		}
	}

	/**
	 * Find all matches for the given ticket (in-memory or backend)
	 */
	public MatchResults findMatches(ITicket ticket) throws MatcherException {
		try {		
			PerformanceTracker tracker = (PerformanceTracker) RuntimeInfo.getCurrentInfo().getBean("performanceTracker");
			//TODO bug, the ticket.getId could be null (the ticket is not saved yet)
			String routineName= "Match" + ticket.getId();
			tracker.startRoutine(routineName);
			
			//Preprocessing Phase
			tracker.startStep(routineName, "Process Ticket");
			LogUtils.debug(log, "Processing ticket %s", ticket.getId());
			IProcessedTicket processedTicket = null;
			try {
				processedTicket = ticketProcessor.processTicket(TicketProcessor.StepsMode.Match,
																ticket,MatchMode.match,null,true);
			} catch (Exception e) {
				LogUtils.fatal(log, e, "Unable to process ticket %s due to %s", ticket, e.getMessage());
				throw e;
			}
			tracker.finishStep(routineName, "Process Ticket");
			LogUtils.debug(log, "Ticket %s processed successfully", ticket.getId());
			//Search Phase
			
			return findMatches(processedTicket);
			
		}
		catch(MatcherException me) {
			LogUtils.fatal(log, me, "Matching failed due to %s", me.getMessage());
			throw me;			
		}
		catch(Exception e) {
			LogUtils.fatal(log, e, "Internal error occured during matching. The detailed error is %s", e.getMessage());
			throw new MatcherException("Internal error occured during matching", e);						
		}
	}
	
	public MatchResults findMatches(IProcessedTicket processedTicket) throws MatcherException {
		try {
			
			if (processedTicket.isMatchModeWithFewTerms()) {
				RuntimeInfo info = RuntimeInfo.getCurrentInfo();
				IConfigurationResourceLoader loader = (IConfigurationResourceLoader)info.getBean("xmlConfigurationResourceLoader");
				processedTicket.setScoreCalculator(new RematchScoreCalculator(loader));
			}
			
			ITicket ticket = processedTicket.getOriginalTicket();
			TicketTracker ticketTracker = (TicketTracker) RuntimeInfo.getCurrentInfo().getBean("ticketTracker");
			
			ticketTracker.track(ticket, new MatchStartedTrackingInfo());
			
			MatchResults matchResults = new MatchResults();
			Collection<MatchCandidate> candidates = null;
			PerformanceTracker tracker = (PerformanceTracker) RuntimeInfo.getCurrentInfo().getBean("performanceTracker");
			String routineName= "Match" + ticket.getId();
			
			tracker.startStep(routineName, "Search Repository");
			LogUtils.info(log, "Searching repository for ticket %s", ticket.getId());
			
			int termsCount = processedTicket.getDictionary().getTermsCount();
			if (termsCount < minTermsToMatch)
				throw new MatcherException(String.format("The source ticket contains only %d terms, match cancelled", termsCount), MatchErrorCodes.NoTerms);
			
			candidates = ticketsRepository.find(processedTicket);
			tracker.finishStep(routineName, "Search Repository");
			LogUtils.info(log, "Searching repository for ticket %s finished successfully, the candidates are %s", ticket.getId(), candidates);
			//Postprocessing Phase
			tracker.startStep(routineName, "Process results");
			LogUtils.info(log, "Processing results for ticket %s", ticket.getId());
			//TODO think here, actually processor step is part of the presentation, probably should be removed from here
			Collection<MatchCandidate> processedCandidates = resultsProcessor.processCandidates(processedTicket, candidates);
			tracker.finishStep(routineName, "Process Results");
			if (processedCandidates.size() == 1)
			{
				for(MatchCandidate candidate : processedCandidates) {
					if (candidate.getScore() == -1.0f)
					{
						LogUtils.info(log, "results seems irrelevant for ticket %s", ticket.getId());
						matchResults.setIrrelevantResults();
						matchResults.setCandidates(new HashSet<MatchCandidate>());
						return matchResults;
					}
					break;
				}
			}
			LogUtils.info(log, "Processing results for ticket %s finished successfully, the candidates are %s", ticket.getId(), processedCandidates);
			tracker.startStep(routineName, "Highlight");
			for(MatchCandidate candidate : processedCandidates) {
				LogUtils.debug(log, "Highlighting candidate %s (URL: %s, TITLE: %s)", candidate, (String)candidate.getCandidateData().getField("Url"), (String)candidate.getCandidateData().getTitle());
				Collection<TechnicalDictionaryTerm> terms = processedTicket.getAllTerms(false);
				List<Pair<TechnicalDictionaryTerm,Float>> listOfTerms = new ArrayList<Pair<TechnicalDictionaryTerm,Float>>();
				for(TechnicalDictionaryTerm term : terms) {
					listOfTerms.add(new Pair<TechnicalDictionaryTerm, Float>(term, candidate.getProcessedTicket().getItemBoost(term)));					
				}
				for (String orphan : processedTicket.getOrphanWords())
				{
					listOfTerms.add(new Pair<TechnicalDictionaryTerm, Float>(new TechnicalDictionaryTerm(orphan) , 0.3f));
				}
				String snippet = LuceneHighlighter.GetSnippet(listOfTerms, (String)candidate.getCandidateData().getField("Text"));
				candidate.setSnippet(snippet);
			}
			tracker.finishStep(routineName, "Highlight");
			LogUtils.info(log, "Highlighting candidates for ticket %s finished successfully", ticket.getId());
			ticketTracker.track(ticket, new MatchFinishedTrackingInfo(processedCandidates));
			
			matchResults.setCandidates(processedCandidates);
			matchResults.setProcessedTicket(processedTicket);
			
			return matchResults;
		}
		catch(MatcherException me) {
			LogUtils.fatal(log, me, "Matching failed due to %s", me.getMessage());
			throw me;			
		}
		catch(Exception e){
			LogUtils.fatal(log, e, "Internal error occured during matching. The detailed error is %s", e.getMessage());
			throw new MatcherException("Internal error occured during matching", e);						
		}
	}
	
};
