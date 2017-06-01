package com.softlib.imatch.common.trackinginfo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.log4j.Logger;

import com.softlib.imatch.ITicket;
import com.softlib.imatch.common.StringUtils;
import com.softlib.imatch.common.TrackingInfo;
import com.softlib.imatch.matcher.Matcher;
import com.softlib.imatch.ticketprocessing.ProcessedTicket;

@Entity
@Table(name="finished_tracking")
public class MatchProcessedTrackingInfo extends TrackingInfo {
	private static final Logger log = Logger.getLogger(Matcher.class);
	@Column(name="ticket_terms")
	private String ticketTerms = "";
	
	public MatchProcessedTrackingInfo(ProcessedTicket ticket){
	}

	@Override
	public void setData(ITicket ticket) {
		this.setTitle(ticket.getTitle());
	}
};
