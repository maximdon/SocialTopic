package com.softlib.imatch.common.trackinginfo;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.log4j.Logger;

import com.softlib.imatch.ITicket;
import com.softlib.imatch.common.StringUtils;
import com.softlib.imatch.common.TrackingInfo;
import com.softlib.imatch.matcher.Matcher;

@Entity
@Table(name="review_tracking")
public class MatchReviewTrackingInfo extends TrackingInfo 
{
	private static final Logger log = Logger.getLogger(Matcher.class);

	@Column(name="important_terms")
	private String important_terms;
	@Column(name="irrelevant_terms")
	private String irrelevant_terms = "";
	
	public MatchReviewTrackingInfo(Collection<String> importantTerms, Collection<String> irrelevantTerms) {
		this.important_terms = StringUtils.join(importantTerms, ",");
		this.irrelevant_terms = StringUtils.join(irrelevantTerms, ",");
	}

	@Override
	public void setData(ITicket ticket) {
		this.setTitle(ticket.getTitle());
	}
	
	public String toString() {
		return String.format("Match review for ticket %s by %s", ticketId, username); 
	}
}
