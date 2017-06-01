package com.softlib.imatch.matcher.filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import com.softlib.imatch.IContextInitializationListener;
import com.softlib.imatch.MatcherException;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.common.configuration.IConfigurationObject;
import com.softlib.imatch.common.configuration.IConfigurationResource;
import com.softlib.imatch.common.configuration.IConfigurationResourceLoader;
import com.softlib.imatch.matcher.ICandidatesSubprocessor;
import com.softlib.imatch.matcher.MatchCandidate;
import com.softlib.imatch.ticketprocessing.IProcessedTicket;
import com.softlib.imatch.ticketprocessing.IProcessedTicket.MatchMode;

public class CandidatesFilter implements ICandidatesSubprocessor, IContextInitializationListener {

	private IConfigurationResourceLoader loader;

	private Map<MatchMode,List<ICandidatesSubfilter>> subFiltersByMode =
		new HashMap<MatchMode, List<ICandidatesSubfilter>>();

	private Map<String, Map<MatchMode,CandidatesFilterModeConfig>> configModeByObjectId;
	
	public CandidatesFilter(IConfigurationResourceLoader loader) {
		this.loader = loader;
		RuntimeInfo.getCurrentInfo().registerContextInitializationListener(this);
		subFiltersByMode.put(MatchMode.match, new ArrayList<ICandidatesSubfilter>());
		subFiltersByMode.put(MatchMode.rematch, new ArrayList<ICandidatesSubfilter>());
	}
	
	public boolean runBeforeSingleProcessors() {
		return true;
	}
	
	public Collection<MatchCandidate> processCandidates(IProcessedTicket ticketToMatch,
														Collection<MatchCandidate> candidates) throws MatcherException {
		if(configModeByObjectId == null)
			contextInitialized();
		MatchMode matchMode = ticketToMatch.getMatchMode();
		
		List<MatchCandidate> candidatesList;  //Subfilters require collection to be sorted.
		if(candidates instanceof List)
			candidatesList = (List<MatchCandidate>)candidates;
		else
			candidatesList = new ArrayList<MatchCandidate>(candidates);
		
		//Remove all required candidates as they are not subject to any filter
		List<MatchCandidate> requiredCandidates = new ArrayList<MatchCandidate>();
		ListIterator<MatchCandidate> iterator = candidatesList.listIterator();
		Map<String, List<MatchCandidate>> candidatesByObject = new HashMap<String, List<MatchCandidate>>();
		
		while(iterator.hasNext()) {
			
			MatchCandidate candidate = iterator.next();
			CandidatesFilterModeConfig config = 
				configModeByObjectId.get(candidate.getOriginObjectId()).get(matchMode);
			
			if(candidate.isRequired()) {
				requiredCandidates.add(candidate);
				iterator.remove();
			}
			else {
				
				if(config.isSeparateObjectsForFilter()){
					List<MatchCandidate> objectCandidates = candidatesByObject.get(candidate.getCandidateData().getOriginObjectId());
					if(objectCandidates == null) {
						objectCandidates = new ArrayList<MatchCandidate>();
						candidatesByObject.put(candidate.getCandidateData().getOriginObjectId(), objectCandidates);
					}
					objectCandidates.add(candidate);
				}
				else {
					//Use source ticket origin id, since it should not be important in this case
					List<MatchCandidate> objectCandidates = candidatesByObject.get(ticketToMatch.getOriginObjectId());
					if(objectCandidates == null) {
						objectCandidates = new ArrayList<MatchCandidate>();
						candidatesByObject.put(ticketToMatch.getOriginObjectId(), objectCandidates);
					}
					objectCandidates.add(candidate);					
				}
			}
		}
		List<MatchCandidate> filteredCandidates = new ArrayList<MatchCandidate>();
		//Process each subFilter for each object independently. 
		//In case separateObjectsForFilter = false, there is only one objectId in keySet, so all candidates will come together for filter
		for(String objectId : candidatesByObject.keySet()) {
			filteredCandidates.addAll(filterCandidatesForSingleObject(matchMode,objectId, candidatesByObject.get(objectId)));
		}
		filteredCandidates.addAll(requiredCandidates);
		return filteredCandidates;
	}

	private List<MatchCandidate> filterCandidatesForSingleObject(MatchMode matchMode,String objectId, 
			List<MatchCandidate> candidates) {
		Collections.sort(candidates, Collections.reverseOrder());
		List<MatchCandidate> filteredCandidates = candidates;
		for(ICandidatesSubfilter subFilter : getSubFilters(matchMode))
			filteredCandidates = subFilter.filterCandidates(objectId, filteredCandidates);
		return filteredCandidates;
	}

	private List<ICandidatesSubfilter> getSubFilters(MatchMode matchMode) {
		return subFiltersByMode.get(matchMode);
	}
	
	public void setMatchSubFilters(List<ICandidatesSubfilter> matchSubFilters) {
		subFiltersByMode.get(MatchMode.match).addAll(matchSubFilters);
	}

	public void setRematchSubFilters(List<ICandidatesSubfilter> rematchSubFilters) {
		subFiltersByMode.get(MatchMode.rematch).addAll(rematchSubFilters);
	}

	private void setSubFiltersConfig(CandidatesFilterModeConfig config,MatchMode matchMode) {
		for (ICandidatesSubfilter subFilter : subFiltersByMode.get(matchMode)) {
			subFilter.setConfiguration(config);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void contextInitialized() {
		IConfigurationResource resource = loader.loadResource("xml:///{SolutionConfigFolder}/matcher.xml;//candidatesFilter");
		IConfigurationObject config = resource.getConfigurationObject(CandidatesFilterConfig.class);
		
		configModeByObjectId = new HashMap<String,Map<MatchMode, CandidatesFilterModeConfig>>();
		
		for(String objectId : config.getAllObjects()) {
			Map<MatchMode,CandidatesFilterModeConfig> configByMode = 
				new HashMap<MatchMode,CandidatesFilterModeConfig>();

			List<CandidatesFilterModeConfig> modes = (List<CandidatesFilterModeConfig>) config.getProperty(objectId, "modes");
			
			for (CandidatesFilterModeConfig filterModeConfig : modes) {
				MatchMode mode = MatchMode.valueOf(filterModeConfig.getName());
				configByMode.put(mode, filterModeConfig);
				setSubFiltersConfig(filterModeConfig,mode);
			}
			
			configModeByObjectId.put(objectId,configByMode);
		}
	}
	
};
