package com.softlib.imatch.common.trackinginfo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.softlib.imatch.ITicket;
import com.softlib.imatch.common.TrackingInfo;
import com.softlib.imatch.ticketprocessing.IProcessedTicket.MatchMode;

@Entity
@Table(name="started_tracking")
public class MatchStartedTrackingInfo extends TrackingInfo {
	private static final long serialVersionUID = 1L;

	@Column(name="ticket_body", nullable=false)
	private String body = "";
	
	@Override
	public void setData(ITicket ticket) {
		this.setTitle(ticket.getTitle());
		if(this.getTicketId().equals("-1"))
			this.setBody(ticket.getBody(MatchMode.all));
	}

	public String getBody() {
		return body;
	}
	
	public void setBody(String body) {
		this.body = body.substring(0, (body.length() > 2000 ? 2000 : body.length()));
	}
	
	public String toString() {
		return String.format("Match started for ticket %s by %s", ticketId, username); 
	}
};
