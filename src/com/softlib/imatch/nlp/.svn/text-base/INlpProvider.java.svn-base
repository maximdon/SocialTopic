package com.softlib.imatch.nlp;

import java.util.List;

import com.softlib.imatch.nlp.NLP.Chunk;

public interface INlpProvider {

	public abstract String[] tokenize(String sentence);

	public abstract String[] tag(String sentence);

	public abstract String[] tag(String[] tokens);

	public abstract List<Chunk> chunk(String sentence);

	public abstract List<Chunk> extractChunks(String[] sentences);

	public abstract String[] extractSentences(String text);

	public abstract String extractNames(String[] sentences, boolean capitalize);

}