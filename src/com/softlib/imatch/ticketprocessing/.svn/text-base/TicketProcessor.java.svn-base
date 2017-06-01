package com.softlib.imatch.ticketprocessing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.softlib.imatch.ITicket;
import com.softlib.imatch.MatcherException;
import com.softlib.imatch.common.Pair;
import com.softlib.imatch.matcher.ITicketFieldsNames;
import com.softlib.imatch.ticketprocessing.IProcessedTicket.MatchMode;

public class TicketProcessor implements ITicketProcessor {
	
	public enum StepsMode {
		Match,
		Index,
		DictAPI
	}
	
	private List<ITicketProcessStep> indexSteps;
	private List<ITicketProcessStep> matchSteps;
	private List<ITicketProcessStep> dictAPISteps;

	public void setIndexSteps(List<ITicketProcessStep> indexSteps) {
		this.indexSteps = indexSteps;
	}

	public void setMatchSteps(List<ITicketProcessStep> matchSteps) {
		this.matchSteps = matchSteps;
	}

	public void setDictAPISteps(List<ITicketProcessStep> dictAPISteps) {
		this.dictAPISteps = dictAPISteps;
	}

	private List<ITicketProcessStep> getSteps(StepsMode stepsMode) {
		if (stepsMode.equals(StepsMode.Index))
			return indexSteps;
		if (stepsMode.equals(StepsMode.Match))
			return matchSteps;
		if (stepsMode.equals(StepsMode.DictAPI))
			return dictAPISteps;
		return matchSteps;
	}
	
	/* (non-Javadoc)
	 * @see com.softlib.imatch.ticketprocessing.ITicketProcessor#processTicket(com.softlib.imatch.ITicket)
	 */
	public IProcessedTicket processTicket(StepsMode stepsMode,ITicket ticket,MatchMode matchMode,String objectId,boolean isSourceTicket) throws MatcherException {
		IProcessedTicket processedTicket = new ProcessedTicket(ticket, ProcessedTicket.getDefaultCalculator(),isSourceTicket,true);
		
		ITicketFieldsNames fieldsNamesConfig = ticket.getFieldsConfig();
		fieldsNamesConfig.setObjectId(objectId);
		Set<String> fieldsNames = fieldsNamesConfig.getAllFields(MatchMode.all);
		
		Map<String,Pair<String,String>> resultSet = null;
		if (stepsMode==StepsMode.Index) 
			resultSet = ProcessedTicketDBase.getResultMap(objectId, ticket.getId(), fieldsNames);
		
		Map<String,StepContext> stepContextByFieldName = new HashMap<String, StepContext>();
		for (String fieldName : fieldsNames) {
			StepContext stepContext = new StepContext(ticket.getId()); 
			if(matchMode == MatchMode.match)
				stepContext.setSplitEnabled(false);
			if (resultSet!=null) {
				stepContext.setFieldTerms(resultSet.get(fieldName));
			}
			stepContextByFieldName.put(fieldName, stepContext);
		}
		
		for(ITicketProcessStep step : getSteps(stepsMode)) {
			for (String fieldName : fieldsNames)
				step.run(fieldName,ticket,processedTicket, stepContextByFieldName.get(fieldName));
		}
		return processedTicket;
	}
		
};
