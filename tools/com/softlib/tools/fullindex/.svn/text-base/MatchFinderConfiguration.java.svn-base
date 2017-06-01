package com.softlib.tools.fullindex;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("matchFinder")
public class MatchFinderConfiguration 
{
	@XStreamAsAttribute
	@XStreamAlias("minNumberOfTerms")
	private int minNumberOfTerms;
	
	@XStreamAsAttribute
	@XStreamAlias("maxNumberOfTickets")
	private int maxNumTickets;

	@XStreamAsAttribute
	@XStreamAlias("reportFormat")
	private ReportFormat reportFormat;
	
	@XStreamAsAttribute
	@XStreamAlias("reportDestPath")
	private String reportDestinationPath;

	@XStreamAsAttribute
	@XStreamAlias("companyName")
	private String companyName;

	@XStreamAsAttribute
	@XStreamAlias("allowCandidateWithSuccessiveId")
	private boolean allowCandidateWithSuccessiveId;
	
	@XStreamAlias("uniquesReport")
	private MatchFinderUniquesConfiguration uniquesConfig;
	
	@XStreamAlias("duplicatesReport")
	private MatchFinderDuplicatesConfiguration duplicatesConfig;

	public int getMinNumberOfTerms() {
		return minNumberOfTerms;
	}

	public ReportFormat getReportFormat() {
		return reportFormat;
	}

	public boolean isAllowCandidateWithSuccessiveId() {
		return allowCandidateWithSuccessiveId;
	}

	public MatchFinderUniquesConfiguration getUniquesConfig() {
		return uniquesConfig;
	}

	public MatchFinderDuplicatesConfiguration getDuplicatesConfig() {
		return duplicatesConfig;
	}

	public String getReportDestPath() {
		return reportDestinationPath;
	}

	public int getMaxNumTickets() {
		if(maxNumTickets == -1)
			return Integer.MAX_VALUE;
		return maxNumTickets;
	}

	public String getCompanyName() {
		if(companyName == null)
			return "";
		return companyName;
	}
}
