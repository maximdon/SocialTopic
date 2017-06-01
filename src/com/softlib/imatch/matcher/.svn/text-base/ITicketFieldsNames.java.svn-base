package com.softlib.imatch.matcher;

import java.util.Set;

import com.softlib.imatch.ticketprocessing.IProcessedTicket.MatchMode;

public interface ITicketFieldsNames {

	void setObjectId(String objectId);
	
	Set<String> getTitleFields();

	Set<String> getBodyFields(MatchMode matchMode);

	Set<String> getAllFields(MatchMode matchMode);
	
	String getIdField();
	
	String getStateField();
	
	Float  getFieldBoost(String fieldName);

};
