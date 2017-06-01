package com.softlib.taxonomytool.model;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

public class Sentence {
	private TIntList wordIndicesInSentence = new TIntArrayList();

	public void setWordIndicesInSentence(TIntList wordIndicesInSentence) {
		this.wordIndicesInSentence = wordIndicesInSentence;
	}

	public TIntList getWordIndicesInSentence() {
		return wordIndicesInSentence;
	}
	
	
}
