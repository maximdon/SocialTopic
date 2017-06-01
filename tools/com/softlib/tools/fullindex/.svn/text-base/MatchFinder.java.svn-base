package com.softlib.tools.fullindex;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import com.softlib.imatch.ITicket;
import com.softlib.imatch.ITicketProvider;
import com.softlib.imatch.ITicketRetrievedCallback;
import com.softlib.imatch.MatcherException;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.TicketProviderFactory;
import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.common.configuration.IConfigurationResource;
import com.softlib.imatch.common.configuration.IConfigurationResourceLoader;
import com.softlib.imatch.dbintegration.DBTicket;
import com.softlib.imatch.dictionary.TechnicalDictionary;
import com.softlib.imatch.matcher.IMatcher;
import com.softlib.imatch.matcher.MatchCandidate;
import com.softlib.imatch.matcher.MatchResults;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;

public class MatchFinder 
{
	private static Logger log = Logger.getLogger(MatchFinder.class);
	private MatchFinderConfiguration config;
	private int numTicketsScanned = 0;
	private int numTicketsScannedForDuplicates = 0;
	private int numTicketsScannedForUniques = 0;
	
	public MatchFinder()
	{
		RuntimeInfo.init(new FictiveServletCtxt());
		DOMConfigurator.configure(RuntimeInfo.getCurrentInfo().getRealPath("/{SolutionConfigFolder}/log4j-console.xml"));
		try {
			TechnicalDictionary dictionary = (TechnicalDictionary) RuntimeInfo.getCurrentInfo().getBean("dictionary");
			dictionary.loadDictionary();
			IConfigurationResourceLoader loader = (IConfigurationResourceLoader) RuntimeInfo.getCurrentInfo().getBean("xmlConfigurationResourceLoader"); 
			IConfigurationResource resource = loader.loadResource("xml:///{SolutionConfigFolder}/matchFinder.xml;//matchFinder");
			config = (MatchFinderConfiguration) resource.getCustomConfiguration(MatchFinderConfiguration.class);
		}
		catch(Exception e)
		{
			LogUtils.error(log, "Unable to load dictionary %s", e.getMessage());
		}
	}
	
	public void findPerfectMatches(String objectId) throws MatcherException
	{
		ITicketProvider ticketProvider;
		List<MatchFinderResult> duplicates = new ArrayList<MatchFinderResult>();
		List<MatchFinderResult> uniques = new ArrayList<MatchFinderResult>();
		try {
			LogUtils.debug(log, "ObjectId %s", objectId);
			RuntimeInfo info = RuntimeInfo.getCurrentInfo();
			ticketProvider = (ITicketProvider) info.getBean(TicketProviderFactory.getProviderId(objectId));
			ticketProvider.getAllTickets(new MatchFinderTicketCallback(duplicates, uniques));
			if(config.getDuplicatesConfig().getSortOrder() != null) {
				MatchFinderResultComparator duplicatesComparator = new MatchFinderResultComparator(config.getDuplicatesConfig().getSortOrder());
				Collections.sort(duplicates, Collections.reverseOrder(duplicatesComparator));
			}
			if(config.getUniquesConfig().getSortOrder() != null) {
				MatchFinderResultComparator uniquesComparator = new MatchFinderResultComparator(config.getUniquesConfig().getSortOrder());
				if(config.getUniquesConfig().getSortOrder() == SortOrder.SCORE)
					Collections.sort(uniques, uniquesComparator);
				else
					Collections.sort(uniques, Collections.reverseOrder(uniquesComparator));
			}
			Logger duplicatesLogger = Logger.getLogger("duplicatesLogger");		
			if(config.getReportFormat() == ReportFormat.TEXT) {
				for(MatchFinderResult result : duplicates) {
					LogUtils.debug(duplicatesLogger, "%s", result);
				}
				Logger uniquesLogger = Logger.getLogger("uniquesLogger");		
				for(MatchFinderResult result : uniques) {
					LogUtils.debug(uniquesLogger, "%s", result);
				}				
			}
			else if(config.getReportFormat() == ReportFormat.HTML) {
				MatchFinderHtmlFormatter formatter = null;
				try {
					formatter = new MatchFinderHtmlFormatter();
				} catch (IOException e) {
					LogUtils.error(log, "Unable to generate report, report file is locked");
				}
				if(duplicates.size() > 0)
					formatter.generateHtml(duplicates, numTicketsScannedForDuplicates, true);
				if(uniques.size() > 0)
					formatter.generateHtml(uniques, numTicketsScannedForUniques, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new MatcherException("Match finder failed due to " + e.getMessage(), e);
		}
	}
	
	class MatchFinderResultComparator implements Comparator<MatchFinderResult>
	{
		private SortOrder sortOrder;
		
		public MatchFinderResultComparator(SortOrder sortOrder)
		{
			this.sortOrder = sortOrder;
		}
		
		public int compare(MatchFinderResult res1, MatchFinderResult res2) {
			switch(sortOrder) {
			case ID:
				try {
					Integer id1 = Integer.parseInt(res1.getTicket().getId());
					Integer id2 = Integer.parseInt(res2.getTicket().getId());
					if(id1 > id2)
						return 1;
					else if(id1 == id2)
						return 0;
					else 
						return -1;
				}
				catch(Exception e) {
					return 1;
				}
			case SCORE:
				return Float.compare(res1.getScore(), res2.getScore());
			case GROUP_SIZE:
				if(res1.getCandidates().size() > res2.getCandidates().size())
					return 1;
				else if(res1.getCandidates().size() == res2.getCandidates().size())
					return 0;
				else
					return -1;
			default:
				return 1;
			}
		}
	}
	
	class MatchFinderResult
	{
		private ITicket ticket;
		private List<MatchCandidate> candidates;
		private float score;
		public MatchFinderResult(ITicket ticket, List<MatchCandidate> candidates, float score) {
			this.setTicket(ticket);
			this.setCandidates(candidates);
			this.setScore(score);
		}
		private void setTicket(ITicket ticket) {
			this.ticket = ticket;
		}
		public ITicket getTicket() {
			return ticket;
		}
		private void setCandidates(List<MatchCandidate> candidates) {
			this.candidates = candidates;
		}
		public List<MatchCandidate> getCandidates() {
			return candidates;
		}
		private void setScore(float score) {
			this.score = score;
		}
		public float getScore() {
			return score;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((ticket == null) ? 0 : ticket.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			MatchFinderResult other = (MatchFinderResult) obj;
			if (ticket == null) {
				if (other.ticket != null)
					return false;
			} else if (ticket.equals(other.ticket))
				return true;
			if(candidates == null || candidates.size() == 0 || other.candidates == null || other.candidates.size() == 0)
				return false;
			MatchCandidate firstCandidate = candidates.get(0);
			MatchCandidate otherFirstCandidate = other.candidates.get(0);
			return firstCandidate.equals(otherFirstCandidate);
		}				
	}
	
	class MatchFinderTicketCallback implements ITicketRetrievedCallback
	{
		private List<MatchFinderResult> duplicates;
		private List<MatchFinderResult> uniques;
		private IMatcher matcher;		
		
		public MatchFinderTicketCallback(List<MatchFinderResult> duplicates, List<MatchFinderResult> uniques) 
		{
			this.duplicates = duplicates;
			this.uniques = uniques;
			matcher = (IMatcher)RuntimeInfo.getCurrentInfo().getBean("matcher");
		}
		
		public void ticketRetrieved(ITicket ticket) {
			boolean duplicatesDone = !config.getDuplicatesConfig().isEnabled() || duplicates.size() == config.getDuplicatesConfig().getNumSamplesInReport();
			boolean uniquesDone = !config.getUniquesConfig().isEnabled() || uniques.size() == config.getUniquesConfig().getNumSamplesInReport(); 
			boolean done = duplicatesDone && uniquesDone;
			done = done || numTicketsScanned > config.getMaxNumTickets();
			if(done)
				return;
			numTicketsScanned ++;
			if(!duplicatesDone)
				numTicketsScannedForDuplicates ++;
			if(!uniquesDone)
				numTicketsScannedForUniques ++;
			ITicketProvider ticketProvider = (ITicketProvider) RuntimeInfo.getCurrentInfo().getBean(TicketProviderFactory.getProviderId(ticket.getOriginObjectId()));
			ITicket webTicket = null;
			try {
				webTicket = ticketProvider.get(ticket.getId());
			} catch (MatcherException e) {
				LogUtils.error(log, "Unable to find matches for %s", webTicket);
			}
			if(webTicket == null)
				return;
			try {
				MatchResults matchResults = matcher.findMatches(webTicket);
				if(matchResults.getProcessedTicket().getAllTerms(false).size() <= config.getMinNumberOfTerms())
					return;
				MatchCandidate[] candidates = matchResults.getCandidates().toArray(new MatchCandidate[0]);
				if(candidates.length == 0)
					return;
				MatchCandidate firstCandidate = candidates[0];
				List<MatchCandidate> relevantCandidates = new ArrayList<MatchCandidate>();
				if(firstCandidate.getRank() == null || duplicates.contains(new MatchFinderResult(firstCandidate.getProcessedTicket().getOriginalTicket(), null, 0))) 
					return;
				if(firstCandidate.getScore() >= config.getDuplicatesConfig().getMinCandidateScore() && !duplicatesDone) {
					LogUtils.info(log, "For ticket %s found perfect match %s", webTicket, firstCandidate);
					if(config.isAllowCandidateWithSuccessiveId() || !isSuccessiveIds(webTicket, firstCandidate.getCandidateData()))
						relevantCandidates.add(firstCandidate);
					if(config.getDuplicatesConfig().isGenerateCompleteGroup()) {
						for(int i = 1; i < candidates.length; ++i)
							if(candidates[i].getScore() >= config.getDuplicatesConfig().getMinCandidateScore() &&
									(config.isAllowCandidateWithSuccessiveId() || !isSuccessiveIds(webTicket, candidates[i].getCandidateData()))) {
								LogUtils.info(log, "For ticket %s found perfect match %s", webTicket, candidates[i]);
								relevantCandidates.add(candidates[i]);
							}
					}				
					if(relevantCandidates.size() > 0) {
						MatchFinderResult res = new MatchFinderResult(webTicket, relevantCandidates, Math.min(firstCandidate.getScore(), 100));
						if(!duplicates.contains(res))
							duplicates.add(res);
					}
				}
				else if(firstCandidate.getScore() <= config.getUniquesConfig().getMaxCandidateScore() && !uniquesDone) {
					LogUtils.info(log, "Ticket %s was found as unique", webTicket);
					uniques.add(new MatchFinderResult(webTicket, new ArrayList<MatchCandidate>(), firstCandidate.getScore()));
				}
			}
			catch(MatcherException me) {
				LogUtils.error(log, "Unable to find matches for %s due to %s", webTicket, me.getMessage());
			}			
			ticket = null;
			webTicket = null;
		}

		private boolean isSuccessiveIds(ITicket ticket, ITicket candidateTicket) {
			String idStr = ticket.getId();
			String candidateIdStr = candidateTicket.getId();
			float id, candidateId;
			try {
				id = Float.parseFloat(idStr);
				candidateId = Float.parseFloat(candidateIdStr);
			}
			catch(Exception e) {
				//For non numeric ids, it's unable to verify successive ids, so we allows all candidates
				return false;
			}
			return Math.abs(id - candidateId) == 1;
		}		
	}
	
	class MatchFinderHtmlFormatter
	{
		private PrintWriter out;
		public MatchFinderHtmlFormatter() throws IOException {
			FileWriter outFile = null;
			outFile = new FileWriter(config.getReportDestPath() + "\\report.html");
			out = new PrintWriter(outFile);
			out.println("<html>");
		}

		public void generateHtml(List<MatchFinderResult> results, int numTicketsScanned, boolean isDuplicates) {
			if(isDuplicates)
				out.println("<H1>Duplicates - Estimated duplicates percentage " + (int) (100 * results.size() / numTicketsScanned) + "% </H1>");
			else
				out.println("<P><H1>Uniques - Estimated uniques percentage " + (int) (100 * results.size() / numTicketsScanned) + "% </H1>");
			out.println("<table>");
			out.println("<th style=\"width:90px; background-repeat: repeat-x;color: #66a3d3;	border: 1px solid #e5eff8;background-color: #f7fbff;font-weight: bold;font-size: 1em;padding-left: 1em;	padding-right: 1em;padding-top: 0.3em;padding-bottom: 0.3em;\">Ticket Id</th>");
			if(isDuplicates)
				out.print("<th style=\"width:90px; background-repeat: repeat-x;color: #66a3d3;	border: 1px solid #e5eff8;background-color: #f7fbff;font-weight: bold;font-size: 1em;padding-left: 1em;	padding-right: 1em;padding-top: 0.3em;padding-bottom: 0.3em;\">Number of candidates</th>");
			out.print("<th style=\"width:90px; background-repeat: repeat-x;color: #66a3d3;	border: 1px solid #e5eff8;background-color: #f7fbff;font-weight: bold;font-size: 1em;padding-left: 1em;	padding-right: 1em;padding-top: 0.3em;padding-bottom: 0.3em;\">Best candidate score</th>");
			if(isDuplicates)
				out.print("<th style=\"width:90px; background-repeat: repeat-x;color: #66a3d3;	border: 1px solid #e5eff8;background-color: #f7fbff;font-weight: bold;font-size: 1em;padding-left: 1em;	padding-right: 1em;padding-top: 0.3em;padding-bottom: 0.3em;\">Average candidates score</th>");
			for (MatchFinderResult result : results) {
				out.println("<tr>");
				out.println("<td style=\"background-color: #f7fbff;padding-left: 1em;padding-right: 1em;padding-top: 0.3em;padding-bottom: 0.3em;\"><a href='http://localhost:8080/iMatch/pages/index.jsf?id="
								+ result.getTicket().getId()
								+ "' target='_blank'/>"
								+ result.getTicket().getId() + "</td>");
				if(isDuplicates) {
					out.println("<td style=\"background-color: #f7fbff;padding-left: 1em;padding-right: 1em;padding-top: 0.3em;padding-bottom: 0.3em;\">"
								+ result.getCandidates().size() + "</td>");
				}
				out.println("<td style=\"background-color: #f7fbff;padding-left: 1em;padding-right: 1em;padding-top: 0.3em;padding-bottom: 0.3em;\">"
								+ (int) Math.min(100 * result.getScore(), 100) + "</td>");
				if(isDuplicates) {
					float averageScore = 0;
					int totalAverage;
					for (MatchCandidate candidate : result.getCandidates())
						averageScore += candidate.getScore();
					totalAverage = (int)Math.min((100 * averageScore) / (1.0 * result.getCandidates().size()), 100);
					out.println("<td style=\"background-color: #f7fbff;padding-left: 1em;padding-right: 1em;padding-top: 0.3em;padding-bottom: 0.3em;\">"
							+ totalAverage + "</td>");
				}
				out.println("</tr>");
			}
			out.println("</table>");
			if (!isDuplicates)
				// Uniques is the last, close writer
				out.close();			 
		}		
	}
}	
