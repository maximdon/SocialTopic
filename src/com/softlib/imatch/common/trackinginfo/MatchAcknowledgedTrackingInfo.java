package com.softlib.imatch.common.trackinginfo;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.softlib.imatch.ITicket;
import com.softlib.imatch.common.TrackingInfo;

@Entity
@Table(name="acknowledged_tracking")
public class MatchAcknowledgedTrackingInfo extends TrackingInfo 
{
	@Basic
	@Column(name="matched_ticket_id")
	private String matchedTicketId;
	
	//This constructor for hibernate only
	public MatchAcknowledgedTrackingInfo()
	{
		matchedTicketId = "";
	}
	
	public MatchAcknowledgedTrackingInfo(String matchedTicketId)
	{
		this.matchedTicketId = matchedTicketId;
	}
	
	public String getMatchedTicketId()
	{
		return matchedTicketId;
	}

	@Override
	public void setData(ITicket ticket) {
		this.setTitle(ticket.getTitle());
	}
	
	@Override
	public String toString() {
		return String.format("Acknoledged match between %s and %s by user %s", ticketId, matchedTicketId, username);
	}
}
