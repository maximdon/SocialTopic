package com.softlib.imatch.connectors;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.softlib.imatch.ITicket;
import com.softlib.imatch.ITicketProvider;
import com.softlib.imatch.ITicketRetrievedCallback;
import com.softlib.imatch.matcher.TicketingSystemIntegrationConfig;
import com.softlib.imatch.model.User;

public abstract class BaseTicketProvider implements ITicketProvider
{
	protected Pattern idPattern;
	protected String objectId;
	protected TicketingSystemIntegrationConfig config;

	
	protected BaseTicketProvider(String objectId, TicketingSystemIntegrationConfig config) {
		this.objectId = objectId;
		this.config = config;
		
		if(config.getTicketIdPattern() != null)
			idPattern = Pattern.compile(config.getTicketIdPattern(), Pattern.CASE_INSENSITIVE);
	}

	public TicketingSystemIntegrationConfig getConfig() {
		return config;
	}
	
	protected static String concatField(String currentValue, Object newValue) {
		// TODO config for \n
		return String.format("%s%s%s", currentValue, "\n-----------------------------\n", newValue);
	}
	
	protected boolean validateTicketId(String ticketId) {
		if(idPattern == null)
			return true;
		Matcher matcher = idPattern.matcher(ticketId);
		boolean rc = matcher.matches();
		return rc;
	}

	protected Set<String> getTicketsIdsStr2Set(String ticketIds) {
		Set<String> rc = new HashSet<String>();
		String split[] = ticketIds.split(",");
		for (String id : split) {
			id = id.replaceAll("'", "");
			rc.add(id);
		}
		return rc;
	}
	
	protected String getTicketIdsSet2Str(Set<String> ids) {
		String rc = "";
		String seperator = "";
		for (String id : ids) {
			rc = rc + seperator + "'" + id + "'";
			seperator = ",";
		}
		return rc;
	}
	
	protected Set<String> getFixedTicketIds(Set<String> ids) {
		Set<String> rc = new HashSet<String>();
		for (String id : ids) {
			if (validateTicketId(id)) 
				rc.add(id);
		}
		return rc;
	}
	
	protected String getFixedTicketIdsStr(String idsStr) {
		Set<String> ids = getTicketsIdsStr2Set(idsStr);
		Set<String> fixedIdsSet = getFixedTicketIds(ids);
		return getTicketIdsSet2Str(fixedIdsSet);
	}

	protected class GetCallback implements ITicketRetrievedCallback {
		private ITicket ticket;
		
		public GetCallback(){		
		}
		
		public void ticketRetrieved(ITicket ticket) {
			this.ticket = ticket;
		}		
		
		public ITicket getTicket(){
			return ticket;
		}
		
	};
	
	public void save() {
	}
	
};
