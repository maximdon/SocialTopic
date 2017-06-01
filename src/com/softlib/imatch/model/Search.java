package com.softlib.imatch.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.softlib.imatch.InMemoryTicket;
import com.softlib.imatch.MatcherException;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.common.configuration.IConfigurationObject;
import com.softlib.imatch.common.configuration.IConfigurationResource;
import com.softlib.imatch.common.configuration.IConfigurationResourceLoader;
import com.softlib.imatch.dbintegration.DBTicket;
import com.softlib.imatch.enums.HighlightType;
import com.softlib.imatch.enums.SearchType;
import com.softlib.imatch.matcher.IMatcher;
import com.softlib.imatch.matcher.MatchCandidate;
import com.softlib.imatch.matcher.MatchResults;
import com.softlib.imatch.matcher.ViewLayoutConfig;
import com.softlib.imatch.matcher.ViewLayoutOriginObjects;
import com.softlib.imatch.matcher.ViewLayoutSettings;
import com.softlib.imatch.score.ITopNTerms;
import com.softlib.imatch.ticketprocessing.IProcessedTicket;
import com.softlib.imatch.ticketprocessing.RematchScoreCalculator;
import com.softlib.imatch.ticketprocessing.IProcessedTicket.MatchMode;

public class Search {
	private String ticketID;
	private String searchID;
	private SearchType searchType;
	private String searchTime;
	private int totalCount;
	private int flags;
	private boolean titlesOnly;
	private boolean updated;
	private int result;
	private boolean runOnce = false;
	private SearchResultItem currentTicket;
	private Map<String, SearchResultGroup> searchResults = new LinkedHashMap<String, SearchResultGroup>();
	private static IConfigurationObject viewLayoutConfig;
	private List<String> topNTerms;
	
	private final static Logger log = Logger.getLogger(RuntimeInfo.class);
	
	public Search()
	{
		
	}
	
	public Search(SearchType searchType)
	{
		this.searchType = searchType;
		this.result = 0;
	}
	
	public void setTicketID(String ticketID) {
		this.ticketID = ticketID;
	}
	public String getTicketID() {
		return ticketID;
	}
	public String getSearchID() {
		return searchID;
	}
	public void setSearchID(String searchID) {
		this.searchID = searchID;
	}
	public void setSearchType(SearchType searchType) {
		this.searchType = searchType;
	}
	public SearchType getSearchType() {
		return searchType;
	}
	public String getSearchTime() {
		return searchTime;
	}
	public void setSearchTime(String searchTime) {
		this.searchTime = searchTime;
	}
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	public int getFlags() {
		return flags;
	}
	public void setFlags(int flags) {
		this.flags = flags;
	}
	public boolean isTitlesOnly() {
		return titlesOnly;
	}
	public void setTitlesOnly(boolean titlesOnly) {
		this.titlesOnly = titlesOnly;
	}
	public boolean isUpdated() {
		return updated;
	}
	public void setUpdated(boolean updated) {
		this.updated = updated;
	}
	public int getResult() {
		return result;
	}
	public void setResult(int result) {
		this.result = result;
	}
	public boolean isRunOnce() {
		return runOnce;
	}
	public void setRunOnce(boolean runOnce) {
		this.runOnce = runOnce;
	}
	
	public void setCurrentTicket(SearchResultItem currentTicket) {
		this.currentTicket = currentTicket;
	}
	public SearchResultItem getCurrentTicket() {
		return currentTicket;
	}
	public void setSearchResults(Map<String, SearchResultGroup> searchResults) {
		this.searchResults = searchResults;
	}
	public Map<String, SearchResultGroup> getSearchResults() {
		return searchResults;
	}
	
	public void setTopNTerms(List<String> topNTerms) {
		this.topNTerms = topNTerms;
	}

	public List<String> getTopNTerms() {
		return topNTerms;
	}

	public static Search performMatch(String objectId, String title, String body, int flags, int prevSearchID)
	{
		LogUtils.debug(log, "Performing match - title: %s, body: %s, flags: %s, prevSearchID: %s", title, body, String.valueOf(flags), String.valueOf(prevSearchID));
		Search search = new Search(SearchType.ByFreeText);
		try
		{
			RuntimeInfo info = RuntimeInfo.getCurrentInfo();
			IMatcher matcher = (IMatcher)info.getBean("matcher");
			
			DBTicket dbTicket = new DBTicket(objectId);
			String titleFieldName = dbTicket.getFieldsConfig().getTitleFields().iterator().next();
			String bodyFieldName = dbTicket.getFieldsConfig().getBodyFields(MatchMode.all).iterator().next();
			String idFieldName = dbTicket.getFieldsConfig().getIdField();
			dbTicket.setField(titleFieldName, title);
			dbTicket.setField(bodyFieldName, body);
			dbTicket.setField(idFieldName,InMemoryTicket.DEFAULT_ID);
			
			MatchResults matchResults = matcher.findMatches(dbTicket);
			
			createSearch(search, matchResults);
			return search;
		} catch (MatcherException e) {
			LogUtils.error(log, e.getMessage());
			search.result = e.getErrorCode().getValue();
		}	
		
		return search;
	}
	
	public static Search performMatch(String objectId, String ticketID, int flags, int prevSearchID)
	{
		LogUtils.debug(log, "Performing match - ticketID: %s, flags: %s, prevSearchID: %s", ticketID, String.valueOf(flags), String.valueOf(prevSearchID));
		Search search = new Search(SearchType.ById);
		
		try
		{
			RuntimeInfo info = RuntimeInfo.getCurrentInfo();
			IMatcher matcher = (IMatcher)info.getBean("matcher");
			
			MatchResults matchResults = matcher.findMatches(objectId, ticketID);
			
			search.setTicketID(ticketID);
			createSearch(search, matchResults);
			return search;
		} catch (MatcherException e) {
			LogUtils.error(log, "Error when performing Match on ticketID: %s\n error msg: %s", ticketID, e.getMessage());
			search.result = e.getErrorCode().getValue();
		}	
		
		//
		return search;
	}
	
	public static void performRematch(Search search, IProcessedTicket processedTicket)  {
		LogUtils.debug(log, "Performing rematch - ticketID: %s", search.getTicketID());
		try {
			RuntimeInfo info = RuntimeInfo.getCurrentInfo();
			IMatcher matcher = (IMatcher)info.getBean("matcher");

			processedTicket.setScoreCalculator(new RematchScoreCalculator((IConfigurationResourceLoader) info.getBean("xmlConfigurationResourceLoader")));
			
			MatchResults matchResults = matcher.findMatches(processedTicket);
			
			search.getSearchResults().clear();
			createSearch(search, matchResults);
		} catch (MatcherException e) {
			LogUtils.error(log, "Error when performing Match on ticketID: %s\n error msg: %s", search.getTicketID(), e.getMessage());
			search.result = 1;
		}
	}
	
	private static void createSearch(Search search, MatchResults matchResults)
	{	
		ITopNTerms topNTerms;
		if(viewLayoutConfig == null) {
			IConfigurationResourceLoader loader = (IConfigurationResourceLoader) RuntimeInfo.getCurrentInfo().getBean("xmlConfigurationResourceLoader");
			IConfigurationResource resource = loader.loadResource("xml:///{SolutionConfigFolder}/matcher.xml;//viewLayout");
			viewLayoutConfig = resource.getConfigurationObject(ViewLayoutConfig.class);
		}
		String objectId = matchResults.getProcessedTicket().getOriginObjectId();
		
		ViewLayoutOriginObjects originObjects = (ViewLayoutOriginObjects) viewLayoutConfig.getProperty(objectId, "originObjects");
		
		Collections.sort(originObjects.getObjects(), SearchResultGroup.TabOrderComparator); 
		LogUtils.debug(log, "Start preparing results for display");
		for (SearchResultGroup searchResultGroup : originObjects.getObjects()) {
			ViewLayoutSettings settings = (ViewLayoutSettings) viewLayoutConfig.getProperty(objectId, "resultColumns");
			searchResultGroup.getItems().clear();
			searchResultGroup.setColumns(settings.getColumns());
			search.getSearchResults().put(searchResultGroup.getId(), searchResultGroup);
		}

		MatchCandidate candidate = 
			new MatchCandidate(null,
							   matchResults.getProcessedTicket().getOriginalTicket(), 
							   !matchResults.getProcessedTicket().getData().isEmpty(), 
							   matchResults.getProcessedTicket());
		candidate.setSourceProcessedTicket(matchResults.getProcessedTicket());
		search.setCurrentTicket((new SearchResultItem(candidate, search.getSearchResults().get(objectId).getColumns(), viewLayoutConfig, HighlightType.EXTRACTED)));

		search.setTotalCount(0);

		topNTerms = (ITopNTerms) RuntimeInfo.getCurrentInfo().getBean("topNTerms");
		search.setTopNTerms(topNTerms.getTopNTerms(matchResults));
		
		for (MatchCandidate matchCandidate: matchResults.getCandidates()) {
			if (matchCandidate.getRank() != null && search.getSearchResults().get(matchCandidate.getOriginObjectId()) != null) { // && !matchCandidate.getCandidateData().getId().equals("67") && !matchCandidate.getCandidateData().getId().equals("65")) {
				SearchResultItem item = new SearchResultItem(matchCandidate, search.getSearchResults().get(matchCandidate.getOriginObjectId()).getColumns(), viewLayoutConfig, HighlightType.MATCH);				
				search.getSearchResults().get(item.getGroupID()).getItems().add(item);
				search.totalCount++;
			}
		}
		LogUtils.debug(log, "Results are ready for display - search object created");
		search.result = 0;
	}
}
