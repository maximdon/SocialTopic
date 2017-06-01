package com.softlib.imatch.matcher;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("mode")
public class CandidatesRankingModeConfig {
	@XStreamAsAttribute
	private String name;
	//Indicates for how many stars, the candidate rank should be raised to the end of the interval
	@XStreamAsAttribute	
	private Integer maxNumStars;
	@XStreamImplicit(itemFieldName="rank")
	private List<CandidateRank> ranks;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void setRanks(List<CandidateRank> ranks) {
		this.ranks = ranks;
	}

	public List<CandidateRank> getRanks() {
		return ranks;
	}
	
	public CandidateRank getHighestRank() {
		CandidateRank highestRank = null;
		for(CandidateRank rank : ranks) {
			if(highestRank == null || highestRank.getIntValue() < rank.getIntValue())
				highestRank = rank;
		}
		return highestRank;
	}

	public CandidateRank getLowestRank() {
		CandidateRank lowestRank = null;
		for(CandidateRank rank : ranks) {
			if(lowestRank == null || lowestRank.getIntValue() > rank.getIntValue())
				lowestRank = rank;
		}
		return lowestRank;
	}
	
	public int getMaxNumStars() {
		if(maxNumStars == null)
			return 10;
		return maxNumStars;
	}

	public CandidateRank getRank(int intValue) {
		for(CandidateRank rank : ranks) {
			if(rank.getIntValue() == intValue)
				return rank;
		}
		return null;
	}
}
