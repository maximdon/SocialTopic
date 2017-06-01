package com.softlib.imatch.dictionary;

import java.util.Collection;

public class WordnetWord 
{
	private int    wordId;
	private String word;
	private String lemma;
	private String synset;
	@SuppressWarnings("unused")
	private Collection<WordnetWord> synonyms;
	
	public WordnetWord(int wordid, String word, String lemma)
	{
		this(wordid, word, lemma, null);
	}

	public WordnetWord(int wordid, String word, String lemma, String synset)
	{
		this.setWordId(wordid);
		this.setWord(word);
		this.setLemma(lemma);
		this.setSynset(synset);
	}

	public void setWordId(int wordId) {
		this.wordId = wordId;
	}

	public int getWordId() {
		return wordId;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getWord() {
		return word;
	}

	public void setLemma(String lemma) {
		this.lemma = lemma;
	}

	public String getLemma() {
		return lemma;
	}

	public void setSynset(String synset) {
		this.synset = synset;
	}

	public String getSynset() {
		return synset;
	}

	@Override
	public String toString() {
		return "WordnetWord [lemma=" + lemma + ", word=" + word + "]";
	}
}
