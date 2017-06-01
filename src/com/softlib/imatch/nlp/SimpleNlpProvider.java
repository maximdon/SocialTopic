package com.softlib.imatch.nlp;

import java.util.ArrayList;
import java.util.List;

import com.softlib.imatch.nlp.NLP.Chunk;

public class SimpleNlpProvider implements INlpProvider {

	@Override
	public List<Chunk> chunk(String sentence) {
		return new ArrayList<Chunk>();
	}

	@Override
	public List<Chunk> extractChunks(String[] sentences) {
		return new ArrayList<Chunk>();
	}

	@Override
	public String extractNames(String[] sentences, boolean capitalize) {
		return "";
	}

	@Override
	public String[] extractSentences(String text) {
		String[] result = new String[1];
		result[0] = text;
		return result;
	}

	@Override
	public String[] tag(String sentence) {
		return new String[0];
	}

	@Override
	public String[] tag(String[] tokens) {
		return new String[0];
	}

	@Override
	public String[] tokenize(String sentence) {
		return new String[0];
	}

}
