package com.softlib.imatch.common.trackinginfo;

import java.util.Collection;
import java.util.Iterator;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.log4j.Logger;

import com.softlib.imatch.ITicket;
import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.common.TrackingInfo;
import com.softlib.imatch.matcher.MatchCandidate;
import com.softlib.imatch.matcher.Matcher;

@Entity
@Table(name="finished_tracking")
public class MatchFinishedTrackingInfo extends TrackingInfo 
{
	private static final Logger log = Logger.getLogger(Matcher.class);
	@Basic
	@Column(name="num_matches")
	private int numMatches;
	@Column(name="match_status")
	private int matchStatus;
	@Column(name="explanation")
	private String explanation = "";
	
	public MatchFinishedTrackingInfo(Collection<MatchCandidate> candidates)
	{
		numMatches = candidates.size();
		matchStatus = 0;
		if (this.numMatches > 0) {
			int length = (candidates.size() > 10 ? 10 : candidates.size());

			Iterator<MatchCandidate> iterator = candidates.iterator();
			for (int i = 0; i < length; i++) {
				MatchCandidate matchCandidate = iterator.next();
				if(i == 0 && matchCandidate.getRank() != null)
					matchStatus = matchCandidate.getRank().getIntValue();
				this.explanation += "\nExplanation for ticket: " + matchCandidate.getCandidateData().getId() + "\n";
				this.explanation += matchCandidate.getProcessedTicket().getScoreExplanation().toString() + "\n";	
			}
		}
		
		LogUtils.debug(log, "%s", this.explanation);
	}
	
	public int getNumMatches()
	{
		return numMatches;
	}

	public int getMatchStatus()
	{
		return matchStatus;
	}
	
	@Override
	public void setData(ITicket ticket) {
		this.setTitle(ticket.getTitle());
	}
	
	public String toString() {
		return String.format("Match finished for ticket %s by %s", ticketId, username); 
	}
}
