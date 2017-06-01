package com.softlib.tools.fullindex;

import java.util.Collection;

import org.apache.log4j.Logger;

import com.softlib.imatch.ITicket;
import com.softlib.imatch.ITicketProvider;
import com.softlib.imatch.ITicketRetrievedCallback;
import com.softlib.imatch.MatcherException;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.TicketProviderFactory;
import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.matcher.IMatcher;
import com.softlib.imatch.matcher.MatchCandidate;
import com.softlib.imatch.matcher.MatchResults;
import com.softlib.imatch.ticketprocessing.IProcessedTicket;

public class MatchCandidateCallback implements ITicketRetrievedCallback {

	private static Logger log = Logger.getLogger(MatchCandidateCallback.class);

	private IMatcher matcher;		

	protected Collection<TechnicalDictionaryTerm> terms;
	protected MatchCandidate[] candidates;

	protected IProcessedTicket processedTicket;

	static private int numTicketsScanned = 0;

	static public int getNumTicketsScanned() {
		return numTicketsScanned;
	}

	public MatchCandidateCallback() {
		matcher = (IMatcher)RuntimeInfo.getCurrentInfo().getBean("matcher");
	}

	public void ticketRetrieved(ITicket ticket) {

		ITicketProvider ticketProvider = (ITicketProvider) RuntimeInfo.getCurrentInfo().getBean(TicketProviderFactory.getProviderId(ticket.getOriginObjectId()));
		ITicket webTicket = null;
		try {
			webTicket = ticketProvider.get(ticket.getId());
		} catch (MatcherException e) {
			LogUtils.error(log, "Unable to find matches for %s", webTicket);
		}
		if(webTicket == null)
			return;

		numTicketsScanned++;

		try {
			MatchResults matchResults = matcher.findMatches(webTicket);
			terms = matchResults.getProcessedTicket().getAllTerms(false);
			candidates = matchResults.getCandidates().toArray(new MatchCandidate[0]);
			processedTicket = matchResults.getProcessedTicket();
		}
		catch(MatcherException me) {
			LogUtils.error(log, "Unable to find matches for %s due to %s", webTicket, me.getMessage());
		}			
		ticket = null;
		webTicket = null;
	}
	
};
