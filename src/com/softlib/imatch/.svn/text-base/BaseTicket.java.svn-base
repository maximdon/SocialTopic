package com.softlib.imatch;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.softlib.imatch.dbintegration.DBUtils;
import com.softlib.imatch.matcher.ITicketFieldsNames;
import com.softlib.imatch.ticketprocessing.IProcessedTicket.MatchMode;

public class BaseTicket implements ITicket {

	final private ITicketFieldsNames ticketFieldsNames;
	final private String originObjectId;
	
	private Map<String,Object> ticketFields;

	protected String id;
	protected String body;
	protected String title;
	
	private String folder;

	public BaseTicket(String originObjectId,ITicketFieldsNames ticketFieldsNames) {
		this.originObjectId = originObjectId;
		this.ticketFieldsNames = ticketFieldsNames;
		ticketFields = new HashMap<String, Object>();
		ticketFieldsNames.setObjectId(originObjectId);
		init();
	}
	
	private void init() {
		body = null;
		title = null;
	}
	
	public String getBody(MatchMode mode) {
		if(body == null) {
			Set<String> bodyFields = new HashSet<String>();
			for(String bodyFieldName : ticketFieldsNames.getBodyFields(mode)) {
				Object fieldValue = getField(bodyFieldName);
				if(fieldValue != null)
					bodyFields.add(fieldValue.toString());
			}
			body = DBUtils.concatBodyFields(bodyFields.toArray(new String[]{}));
		}
		return body;
	}
	
	public String getTitle() {
		String titleFieldName = ticketFieldsNames.getTitleFields().iterator().next();
		title = (String)getField(titleFieldName);
		return title;
	}

	public ITicketFieldsNames getFieldsConfig() {
		return ticketFieldsNames;
	}

	public TicketState getState() {
		String stateFieldName = ticketFieldsNames.getStateField();
		Object state = getField(stateFieldName);
		if(state == null)
			return TicketState.Updated;
		return TicketState.valueOf(state.toString());
	}

	public void setState(TicketState state) {
		String stateFieldName = ticketFieldsNames.getStateField();
		ticketFields.put(stateFieldName,state.name());
	}
	
	public Collection<String> getFields()
	{
		return ticketFields.keySet();
	}
	
	public Object getField(String fieldName) {
		return ticketFields.get(getCanonicalName(fieldName));
	}

	public void setField(String fieldName, Object fieldValue) {
		if (fieldValue==null)
			return;
		init();
		ticketFields.put(getCanonicalName(fieldName), fieldValue);
	}

	private String getCanonicalName(String fieldName) {
		if(fieldName == null)
			return null;
		return fieldName.toLowerCase();
	}
	
	public String getId() {
		if (id==null) {
			String idFieldName = ticketFieldsNames.getIdField();
			Object fieldVal = getField(idFieldName);
			id =  fieldVal != null ? fieldVal.toString() : null;
		}
		return id;
	}

	public void setId(String id) {
		String idFieldName = ticketFieldsNames.getIdField();
		ticketFields.put(idFieldName,id);
		this.id = id;
	}
	

	public String getOriginObjectId() {
		return originObjectId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((body == null) ? 0 : body.hashCode());
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
		BaseTicket other = (BaseTicket)obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;		
		return true;
	}
	
	@Override
	public String toString() {
		if(getId()!= null)
			return getId();
		else if(getTitle() != null)
			return getTitle();
		else
			return "Ticket with no name (long body)"; 
	}

	@Override
	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}
	


};
