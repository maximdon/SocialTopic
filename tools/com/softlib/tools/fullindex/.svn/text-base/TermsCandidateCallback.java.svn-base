package com.softlib.tools.fullindex;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.softlib.imatch.ITicket;
import com.softlib.imatch.MatcherException;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.common.SourceMngr;
import com.softlib.imatch.common.TracerFile;
import com.softlib.imatch.common.TracerFileLast;
import com.softlib.imatch.common.SourceMngr.Type;
import com.softlib.imatch.common.configuration.IConfigurationResourceLoader;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.dictionary.TechnicalTermSource;
import com.softlib.imatch.matcher.IMatcher;
import com.softlib.imatch.matcher.MatchCandidate;
import com.softlib.imatch.matcher.MatchResults;
import com.softlib.imatch.matcher.lucene.customscore.IQueryTester;
import com.softlib.imatch.ticketprocessing.IProcessedTicket;
import com.softlib.imatch.ticketprocessing.IScoreCalculator;
import com.softlib.imatch.ticketprocessing.ProcessedTicket;
import com.softlib.imatch.ticketprocessing.RematchScoreCalculator;
import com.softlib.imatch.ticketprocessing.IProcessedTicket.MatchMode;

public class TermsCandidateCallback extends MatchCandidateCallback{

	private float minCandidateScore = (float)0.2;

	private IScoreCalculator calc = new RematchScoreCalculator((IConfigurationResourceLoader) RuntimeInfo.getCurrentInfo().getBean("xmlConfigurationResourceLoader"));
	public void ticketRetrieved(ITicket ticket) {

		if (getNumTicketsScanned()>1000)
			return;

		super.ticketRetrieved(ticket);
		Set<MatchCandidate> relevantCandidates = new HashSet<MatchCandidate>();

		if (candidates==null)
			return;

		for(int i = 0; i < candidates.length; ++i) {
			MatchCandidate currCandidate = candidates[i];
			if(currCandidate.getOriginObjectId().equals("cases") && 
					currCandidate.getScore() >= minCandidateScore) {
				relevantCandidates.add(currCandidate);
			}
		}

		if (relevantCandidates.size()>0 && terms.size()>2 && terms.size()<20) {
			for (MatchCandidate candidate : relevantCandidates) {
				CommonTerms commonTerms = new CommonTerms();
				commonTerms.add(terms);
				commonTerms.add(candidate.getProcessedTicket().getAllTerms(false));

				Set<TechnicalDictionaryTerm> commonTermsSet = commonTerms.getCommon();
				if (commonTermsSet.size()>2) {
					int candidatesCount;
					try {
						candidatesCount = rematch(processedTicket, commonTermsSet);
					} catch (Exception e) {
						candidatesCount = -1;
					} 						
					write(ticket,candidate,commonTermsSet,candidatesCount, terms);
				}
			}
		}


	}	

	public int rematch(IProcessedTicket processedTicket, Set<TechnicalDictionaryTerm> terms) throws MatcherException {

		IProcessedTicket processedTicketClone;
		processedTicketClone = new ProcessedTicket(processedTicket.getOriginalTicket(), calc, false);

		processedTicketClone.zeroingAllBoostFactor();

		processedTicketClone.setMatchMode(MatchMode.rematch);

		processedTicketClone.startSession("field", processedTicket.getId(), "source");
		for (TechnicalDictionaryTerm term : terms) {
			processedTicketClone.addTerm(term.getTermKey());
			processedTicketClone.overwriteBoostFactor(term.getTermKey(), processedTicket.getItemBoost(term));
		}			
		processedTicketClone.endSession(0, null, false);
		IMatcher matcher = (IMatcher)RuntimeInfo.getCurrentInfo().getBean("matcher");
		MatchResults matchResults = matcher.findMatches(processedTicketClone);
		int candidatesCount = 0;
		for(MatchCandidate candidate : matchResults.getCandidates())
			if(candidate.getOriginObjectId().equals("cases") &&
					candidate.getRank() != null &&
					candidate.getRank().getName().equals("duplicate"))
				candidatesCount ++;
		return candidatesCount;
	}


	private String getIdStr(String id) {
		int index = id.lastIndexOf(".");
		if (index>0)
			id = id.substring(0,index);
		return id;
	}

	private String getScoreStr(float fScore) {
		Float f = new Float(fScore);
		String score = f.toString();
		int index = score.lastIndexOf(".");
		if (index>0)
			score = score.substring(0,index+2);
		return score;
	}

	private TracerFile analyzedFile = 
		TracerFileLast.create("Analyzed","Analyzed",true);

	static IQueryTester searcher = null;

	synchronized private void write(ITicket ticket,
			MatchCandidate candidate,
			Set<TechnicalDictionaryTerm> commonTermsSet,
			int groupSize,
			Collection<TechnicalDictionaryTerm> terms) {

		if (searcher==null)
			searcher = (IQueryTester)RuntimeInfo.getCurrentInfo().getBean("lucene.searcher");

		/*int totalHits = -1;
	BooleanQuery query = TermsQueryBuilder.buildQuery(ticket, commonTermsSet);
	try {
		TopDocsCollector collector = searcher.test(query);
		totalHits = collector.getTotalHits();

	} catch (MatcherException e) {
		e.printStackTrace();
	}*/

		String text = 
			"["+getIdStr(ticket.getId())+"]--{"+
			getIdStr(candidate.getCandidateData().getId())+"}("+
			getScoreStr(candidate.getScore())+")"+commonTermsSet.toString()+"("+terms.size()+")|"+groupSize+"|";
		analyzedFile.write(text);

	}

	private class CommonTerms {

		private Map<TechnicalDictionaryTerm,Integer> countTerms = 
			new HashMap<TechnicalDictionaryTerm,Integer>();
		private int num = 0;

		public void add(Collection<TechnicalDictionaryTerm> terms) {
			num++;
			for (TechnicalDictionaryTerm term : terms) {
				TechnicalTermSource termSource = term.getTermSource();
				if (SourceMngr.isSource(termSource,Type.Complex))
					continue;
				Integer count = countTerms.get(term);
				if (count==null)
					count = new Integer(0);
				count++;
				countTerms.put(term, count);
			}
		}

		public Set<TechnicalDictionaryTerm> getCommon() {
			Set<TechnicalDictionaryTerm> rc = new HashSet<TechnicalDictionaryTerm>();
			for (TechnicalDictionaryTerm term : countTerms.keySet()) {
				Integer count = countTerms.get(term);
				if (count==num)
					rc.add(term);
			}
			return rc;
		}

	};

};
