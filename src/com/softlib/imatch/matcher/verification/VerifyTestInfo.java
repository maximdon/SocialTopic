package com.softlib.imatch.matcher.verification;

import java.util.HashSet;
import java.util.Set;

import com.softlib.imatch.ITicket;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("test")
public class VerifyTestInfo {

	@XStreamAsAttribute
	private String ticketID;

	@XStreamAsAttribute
	private String originOjbectId;

	@XStreamAsAttribute
	private String expectedResults;	
	
	@XStreamAsAttribute
	private int minCount;
	@XStreamAsAttribute
	private float minScore;	

	private Set<String> expectedResultsVec;	
	
	public VerifyTestInfo(){
		//setExpectedResults(this.expectedResults);	
	}

	public VerifyTestInfo(String originObjectId, String ticket,String expectedResults,int minCount,float minScore){
		super();
		this.minCount = minCount;
		this.minScore = minScore;
		this.ticketID = ticket;
		this.originOjbectId = originObjectId;
		setExpectedResults(expectedResults);
	}

	public void setMinScore(float minScore){
		this.minScore = minScore;
	}

	public void setMinCount(int minCount){
		this.minCount = minCount;
	}

	public void setTicketID(String ticketID){
		this.ticketID = ticketID;
	}
	
	public void setExpectedResults(String expectedResults){
		this.expectedResults = expectedResults;
		this.expectedResultsVec = new HashSet<String>();//expectedResults.split("."));
		if (expectedResults.trim().length() > 0)
		{
			String[] results = expectedResults.split(",");
			for(String ticket : results) {
				expectedResultsVec.add(ticket);
			}
		}
	}

	public int getMinCount(){
		return this.minCount;
	}
	
	public float getMinScore(){
		return this.minScore;
	}
	
	public String getTicketID(){
		return this.ticketID;
	}
	
	public Set<String> getExpectedResults(){
		if (expectedResultsVec == null){
			setExpectedResults(expectedResults);
		}
		return this.expectedResultsVec;
	}

	public String getOriginObjectId() {
		if(originOjbectId == null)
			originOjbectId = "cases";
		return originOjbectId;
	}
}
