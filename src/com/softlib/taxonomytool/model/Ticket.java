package com.softlib.taxonomytool.model;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

public class Ticket {
	private String id;
	private long wordsCount;
	private TIntList wordIndicesInTicket = new TIntArrayList();
	private TIntObjectMap<Sentence> sentences = new TIntObjectHashMap<Sentence>();
	
	public Ticket(String ticketID) {
		this.id = ticketID;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public long getWordsCount() {
		return wordsCount;
	}
	public void setWordsCount(long wordsCount) {
		this.wordsCount = wordsCount;
	}	
	public TIntList getWordIndicesInTicket() {
		return wordIndicesInTicket;
	}
	public void setWordIndicesInTicket(TIntList wordIndicesInTicket) {
		this.wordIndicesInTicket = wordIndicesInTicket;
	}
	public TIntObjectMap<Sentence> getSentences() {
		return sentences;
	}
	public void setSentences(TIntObjectMap<Sentence> sentences) {
		this.sentences = sentences;
	}
	
	public void addSentence(int sentenceIndex, int wordIndexInSentence, int wordIndexInTicket)
	{
		Sentence sentence = sentences.get(sentenceIndex);
		
		if (sentence == null)
		{
			sentence = new Sentence();
			sentences.put(sentenceIndex, sentence);
		}
		
		sentence.getWordIndicesInSentence().add(wordIndexInSentence);
		this.wordIndicesInTicket.add(wordIndexInTicket);
		this.wordsCount++;
	}
}
