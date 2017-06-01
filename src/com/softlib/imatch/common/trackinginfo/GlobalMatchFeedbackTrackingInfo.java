package com.softlib.imatch.common.trackinginfo;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.softlib.imatch.ITicket;
import com.softlib.imatch.common.TrackingInfo;

@Entity
@Table(name="global_match_tracking")
public class GlobalMatchFeedbackTrackingInfo extends TrackingInfo 
{
	@Basic
	@Column(name="feedback")
	private String feedback;
	
	//This constructor for hibernate only
	public GlobalMatchFeedbackTrackingInfo()
	{
		feedback = "";
	}
	
	public GlobalMatchFeedbackTrackingInfo(String feedback)
	{
		this.feedback = feedback;
	}
	
	public String getFeedback()
	{
		return feedback;
	}

	@Override
	public void setData(ITicket ticket) {
		this.setTitle(ticket.getTitle());
	}
	
	@Override
	public String toString() {
		return "Global feedback " + feedback;
	}
}
