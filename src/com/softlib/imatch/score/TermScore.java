package com.softlib.imatch.score;

import java.util.Hashtable;

import org.apache.log4j.Logger;

import com.softlib.imatch.common.LogUtils;

public class TermScore {
	
	private String term;
	private float totalScore;
	private Hashtable<String, CategoryScore> categoriesScore = new Hashtable<String, CategoryScore>();
	private static final Logger log = Logger.getLogger(TermScore.class);
	
	public TermScore(String term) {
		this.term = term;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public float getTotalScore() {
		return totalScore;
	}

	public void setTotalScore(float totalScore) {
		this.totalScore = totalScore;
	}

	public Hashtable<String, CategoryScore> getCategoriesScore() {
		return categoriesScore;
	}

	public void setCategoriesScore(Hashtable<String, CategoryScore> categoriesScore) {
		this.categoriesScore = categoriesScore;
	}

	public void addCategory(String categoryTitle, float score, String extra)
	{
		if (categoriesScore.containsKey(categoryTitle))
			LogUtils.warn(log, "Score Explanation - category \"%s\" already exists for the term \"%s\".", categoryTitle, this.term);
		else
			categoriesScore.put(categoryTitle, new CategoryScore(categoryTitle, score, extra));
	}
	
	@Override
	public String toString() {
		float totalScore = this.getTotalScore();
		String temp = "term: " + this.term + " score: " + totalScore + "\n";
		if(totalScore > 0)
			for (String category : categoriesScore.keySet()) {
				temp += categoriesScore.get(category).toString();
			}
		
		return temp;
	}
}