package com.softlib.imatch.matcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.hibernate.Session;

import com.softlib.imatch.ITicket;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.common.HibernateUtils;
import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.common.SessionMode;
import com.softlib.imatch.ticketprocessing.IProcessedTicket;

public class MatchCandidate implements Comparable<MatchCandidate> {
	
	private CandidateScore score;
	private ITicket candidateData;
	private boolean required = false;
	private CandidateRank rank;
	private IProcessedTicket processedTicket;
	private IProcessedTicket sourceProcessedTicket;
	private Integer numStars = null;
	private Boolean hasStarForThisSource = null;
	private boolean isDataExist = false;
	private String snippet;

	private static Logger log = Logger.getLogger(MatchCandidate.class);
	
	public IProcessedTicket getSourceProcessedTicket() {
		return sourceProcessedTicket;
	}

	public void setSourceProcessedTicket(IProcessedTicket sourceProcessedTicket) {
		this.sourceProcessedTicket = sourceProcessedTicket;
	}
	
	public MatchCandidate(float score, ITicket candidateData) {
		this(new CandidateScore(score), candidateData, false, null);
	}

	public MatchCandidate(CandidateScore score, ITicket candidateData, boolean isDataExist, IProcessedTicket processedTicket) {
		super();
		this.score  = score;
		this.setCandidateData(candidateData);
		
		this.isDataExist = isDataExist;
		this.processedTicket = processedTicket;
	}
	
	public void setScore(float score) {
		this.score.setScore(score);
	}

	public float getScore() {
		return score.getScore();
	}

	public void setOppositeScore(float score) {
		this.score.setOppositeScore(score);
	}

	public float getOppositeScore() {
		return score.getOppositeScore();
	}

	public void setCandidateData(ITicket candidateData) {
		this.candidateData = candidateData;
	}

	public ITicket getCandidateData() {
		return candidateData;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public boolean isRequired() {
		return required;
	}	

	public void setRank(CandidateRank rank) {
		this.rank = rank;
	}

	public CandidateRank getRank() {
		return rank;
	}

	public boolean isDataExist() {
		return isDataExist;
	}

	public void setProcessedTicket(IProcessedTicket processedTicket) {
		this.processedTicket = processedTicket;
	}

	public IProcessedTicket getProcessedTicket() {
		return processedTicket;
	}
	
	public int getNumStars()
	{
		synchronized (this) {
			if(numStars == null)
				loadStarsInfoForTheCandidate();						
		}
		return numStars;
	}

	public boolean isStarredForThisSource()
	{
		synchronized (this) {
			if(hasStarForThisSource == null)
				loadStarsInfoForTheCandidate();						
		}		
		return hasStarForThisSource;
	}
	
	@Override
	public String toString() {
		if(rank == null)
			return String.format("Candidate %s (%s) with score %f", candidateData.getId(), candidateData.getOriginObjectId(), score.getScore());
		else
			return String.format("Candidate %s (%s) with score %f (%s)", candidateData.getId(), candidateData.getOriginObjectId(), score.getScore(), rank.getName());
	}

	@Override
	public int hashCode() {
		return candidateData.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MatchCandidate other = (MatchCandidate) obj;
		if (candidateData == null) {
			if (other.candidateData != null)
				return false;
		} else if (!candidateData.equals(other.candidateData))
			return false;
		return true;
	}

	public int compareTo(MatchCandidate otherCandidate) {
		return this.score.compareTo(otherCandidate.score); 
	}

	public String getOriginObjectId() {
		return candidateData.getOriginObjectId();
	}
	
	private void loadStarsInfoForTheCandidate()
	{
		Session session = RuntimeInfo.getCurrentInfo().getHibernate().acquireSession(SessionMode.READ_WRITE);
		PreparedStatement ps = null;
		String sql = "SELECT * FROM ACKNOWLEDGED_TRACKING WHERE matched_ticket_id=?";
		int count = 0;
		hasStarForThisSource = false;
		try {
			Connection conn = session.connection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, candidateData.getId());
			ResultSet rs = ps.executeQuery();			
			while(rs.next()) {
				String ticketId = rs.getString("ticket_id");
				count++;
				if(ticketId.equals(sourceProcessedTicket.getId()))
					hasStarForThisSource = true;
			}
		}
		catch(Exception e) {
			LogUtils.error(log, "Unable to retrieve stars info for candidate %s due to %s", this.candidateData, e.getMessage());
			if(ps != null)
				try {
					ps.close();
				} catch (SQLException e1) {
					//Do nothing
				}
		}
		finally {
			RuntimeInfo.getCurrentInfo().getHibernate().releaseSession(session);
		}
		numStars = count;		
	}

	public String getSnippet()
	{
		return snippet;
	}
	
	public void setSnippet(String snippet) {
		this.snippet = snippet;
	}
	
	public String getMimeType()
	{
		return "text\\html";
	}
}
