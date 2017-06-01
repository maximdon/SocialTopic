package com.softlib.imatch.matcher;

import java.util.HashSet;
import java.util.Set;

import com.softlib.imatch.ticketprocessing.IProcessedTicket.MatchMode;

public class InMemoryTicketFieldsNames implements ITicketFieldsNames {

	public static final String BODY_FIELD = "BODY";
	public static final String TITLE_FIELD = "TITLE";
	public static final String ID_FIELD = "ID";
	public static final String STATE_FIELD = "STATE";

	public Set<String> getAllFields(MatchMode matchMode) {
		Set<String> rc = new HashSet<String>();
		rc.add(TITLE_FIELD);
		rc.add(BODY_FIELD);
		return rc;
	}

	public Float  getFieldBoost(String fieldName)
	{
		return 1.0f;
	}
	
	public Set<String> getBodyFields(MatchMode matchMode) {
		Set<String> rc = new HashSet<String>();
		rc.add(BODY_FIELD);
		return rc;
	}

	public Set<String> getTitleFields() {
		Set<String> rc = new HashSet<String>();
		rc.add(TITLE_FIELD);
		return rc;
	}

	public void setObjectId(String objectId) {
	}

	public String getIdField() {
		return ID_FIELD;
	}

	public String getStateField() {
		return STATE_FIELD;
	}

};
