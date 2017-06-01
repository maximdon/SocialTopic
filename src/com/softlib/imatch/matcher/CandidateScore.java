package com.softlib.imatch.matcher;

public class CandidateScore implements Comparable<CandidateScore> 
{
	private float score;
	private float oppositeScore;
	
	
	public CandidateScore(float score) {
		this(score, -1);
	}
	
	public CandidateScore(float score, float oppositeScore) {
		this.score = score;
		this.oppositeScore = oppositeScore;
	}
	
	public void setScore(float score) {
		this.score = score;
	}
	public float getScore() {
		return score;
	}
	public void setOppositeScore(float oppositeScore) {
		this.oppositeScore = oppositeScore;
	}
	public float getOppositeScore() {
		return oppositeScore;
	}

	public int compareTo(CandidateScore otherScore) {
		int result = Float.compare(score, otherScore.score);
		if(result == 0)
			result = Float.compare(oppositeScore, otherScore.oppositeScore);
		return result;
	}

	@Override
	public String toString() {
		return "score: " + score + " opposite score: " + oppositeScore;
	}
	
	
}
