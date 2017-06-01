package com.softlib.imatch.common;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.softlib.imatch.ITicket;

@MappedSuperclass
public abstract class TrackingInfo implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4907399191919045109L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	@Basic
	@Column(name="ticket_id", nullable=false)
	protected String ticketId;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="tracking_date", nullable=false)
	private Date trackingDate;
	@Column(name="ticket_title", nullable=false)
	private String title = "";
	@Column(name="username", nullable=false)
	protected String username = "";
	
	public TrackingInfo()
	{
		trackingDate = new Date();
	}

	void setTicketId(String ticketId) {
		this.ticketId = ticketId;
	}

	public String getTicketId() {
		return ticketId;
	}

	public Date getTrackingDate() {
		return trackingDate;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		if(title == null)
			this.title = "";
		else
			this.title = title.substring(0, (title.length() > 100 ? 100 : title.length()));
	}
	
	public void setUsername(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public abstract void setData(ITicket ticket);
	
}
