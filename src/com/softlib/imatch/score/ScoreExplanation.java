package com.softlib.imatch.score;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Hashtable;

public class ScoreExplanation {
	private Hashtable<String, TermScore> termScores = new Hashtable<String, TermScore>();

	public void setScores(Hashtable<String, TermScore> scores) {
		this.termScores = scores;
	}

	public Hashtable<String, TermScore> getScores() {
		return termScores;
	}

	public void addScore(String term, String categoryTitle, float score, String extra)
	{
		if (termScores.containsKey(term)) {
			if(categoryTitle.equals("total"))
				termScores.get(term).setTotalScore(score);
			else
				termScores.get(term).addCategory(categoryTitle, score, extra);	
		}
		else
		{
			termScores.put(term, new TermScore(term));
			termScores.get(term).addCategory(categoryTitle, score, extra);
		}
	}
	
	@Override
	public String toString() {
		String temp = "";
		// sort the hashTable by its values
		Comparator<TermScore> termScoreComparator = new Comparator<TermScore>()
		{
			public int compare(TermScore o1, TermScore o2) {
				if (o1.getTotalScore() > o2.getTotalScore()) {
					return (1);
				} else if (o1.getTotalScore() < o2.getTotalScore()) {
					return (-1);
				} else {
					return (0);
				}
			}
		};
		
		Comparator<TermScore> termScoreComparatorDesc = new Comparator<TermScore>()
		{
			public int compare(TermScore o1, TermScore o2) {
				if (o1.getTotalScore() < o2.getTotalScore()) {
					return (1);
				} else if (o1.getTotalScore() > o2.getTotalScore()) {
					return (-1);
				} else {
					return (0);
				}
			}
		};
		
		TermScore[] termsScores = termScores.values().toArray(new TermScore[0]);
		Arrays.sort(termsScores, termScoreComparatorDesc);
		
		for (int i = 0; i < termsScores.length; ++i) {
			temp += ((TermScore)termsScores[i]).toString();
		}
		
		/*
		for (String term : termScores.keySet()) {
			temp += termScores.get(term).toString();
		}
		*/
		return temp;
	}
}


