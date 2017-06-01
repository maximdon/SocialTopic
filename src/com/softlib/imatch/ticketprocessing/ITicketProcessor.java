package com.softlib.imatch.ticketprocessing;

import java.util.List;

import com.softlib.imatch.ITicket;
import com.softlib.imatch.MatcherException;
import com.softlib.imatch.ticketprocessing.IProcessedTicket.MatchMode;
import com.softlib.imatch.ticketprocessing.TicketProcessor.StepsMode;

public interface ITicketProcessor {

	public void setIndexSteps(List<ITicketProcessStep> indexSteps);

	public void setMatchSteps(List<ITicketProcessStep> matchSteps);

	public IProcessedTicket processTicket(StepsMode stepsMode,ITicket ticket,MatchMode matchMode,String objectId,boolean isSourceTicket) throws MatcherException;

}