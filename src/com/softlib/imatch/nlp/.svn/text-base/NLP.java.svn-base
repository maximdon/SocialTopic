package com.softlib.imatch.nlp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import opennlp.maxent.io.SuffixSensitiveGISModelReader;
import opennlp.tools.lang.english.NameFinder;
import opennlp.tools.lang.english.PosTagger;
import opennlp.tools.lang.english.SentenceDetector;
import opennlp.tools.lang.english.Tokenizer;
import opennlp.tools.lang.english.TreebankChunker;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.postag.POSDictionary;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.util.Span;

import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.dictionary.ISpecialTextHandler;
import com.softlib.imatch.dictionary.TechnicalDictionary;

public class NLP implements INlpProvider
{
	SentenceDetectorME sdetector;
	Tokenizer tokenizer;
	POSDictionary dictionary;
	PosTagger posTagger;
	TreebankChunker chunker;
	NameFinder mNameFinder[];
	//String[] models = new String[] {"money", "percentage", "date", "time", "location", "person"};
	String[] models = new String[] {"location","person"};
	NameFinderME[] finders = new NameFinderME[models.length];
	
	private TechnicalDictionary technicalDictionary;
	
	public NLP() {
		try {
			sdetector = new SentenceDetector(RuntimeInfo.getCurrentInfo().getRealPath("/models/English/EnglishSD.bin.gz"));
			tokenizer = new Tokenizer(RuntimeInfo.getCurrentInfo().getRealPath("/models/English/EnglishTok.bin.gz"));
			dictionary = new POSDictionary(RuntimeInfo.getCurrentInfo().getRealPath("/models/English/tagdict.txt"));
	        posTagger = new PosTagger(RuntimeInfo.getCurrentInfo().getRealPath("/models/English/tag.bin.gz"), dictionary);
	        chunker = new TreebankChunker(RuntimeInfo.getCurrentInfo().getRealPath("/models/English/EnglishChunk.bin.gz"));
	        
	        for (int fi=0; fi < models.length; fi++)
			{
				String modelName = models[fi];
				finders[fi] = new NameFinderME(new SuffixSensitiveGISModelReader(new File(RuntimeInfo.getCurrentInfo().getRealPath("/models/English/NameFind/"+modelName + ".bin.gz"))).getModel());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see com.softlib.imatch.nlp.INlpProvider#tokenize(java.lang.String)
	 */
	public String[] tokenize(String sentence) {		
		if(technicalDictionary == null)
			technicalDictionary = (TechnicalDictionary)RuntimeInfo.getCurrentInfo().getBean("dictionary");
		if(technicalDictionary.getSpecialTextHandlers() != null) {
			for(ISpecialTextHandler handler : technicalDictionary.getSpecialTextHandlers()) {
				sentence = handler.remove(sentence);
			}
		}
		sentence = sentence.trim();
		if(sentence.endsWith("."))
			sentence = sentence.substring(0, sentence.length() -1);
		//Skip punctuation mark in the middle of NLP sequence
		sentence = sentence.replaceAll("[,;\\[\\]\\(\\)\\{\\}]", " or ");

		synchronized (tokenizer) {
			String[] tokens = null;
			tokens = tokenizer.tokenize(sentence);
			return tokens;
		}
	}
	
	/* (non-Javadoc)
	 * @see com.softlib.imatch.nlp.INlpProvider#tag(java.lang.String)
	 */
	public String[] tag(String sentence) {
		synchronized (posTagger) {
			String[] tags;
			tags = posTagger.tag(tokenize(sentence));
			return tags;
		}
	}
	
	/* (non-Javadoc)
	 * @see com.softlib.imatch.nlp.INlpProvider#tag(java.lang.String[])
	 */
	public String[] tag(String[] tokens) {
		synchronized (posTagger) {
			String[] tags;
			tags = posTagger.tag(tokens);
			return tags;
		}
	}

	/* (non-Javadoc)
	 * @see com.softlib.imatch.nlp.INlpProvider#chunk(java.lang.String)
	 */
	public List<Chunk> chunk(String sentence)
	{
		String[] tokens = tokenize(sentence);
		String[] tags = tag(sentence) ;
		String[] rawChunks;
		synchronized (chunker) {
			rawChunks = chunker.chunk(tokens, tags); 
		}		
		List<Chunk> chunks = new ArrayList<Chunk>();
		Chunk chunk = null;
		boolean saveChunk = false;
		
		for (int i = 0; i < rawChunks.length; i++) {
			String chunkData = rawChunks[i];
			if (chunkData.charAt(0) == 'B') {
				if (saveChunk)
					chunks.add(chunk);
				
				chunk = new Chunk(chunkData.substring(2));
				saveChunk = true;
				chunk.getTokens().add(tokens[i]);
				chunk.getTags().add(tags[i]);
			}
			else
			{
				if (chunk != null)
				{
					chunk.getTokens().add(tokens[i]);
					chunk.getTags().add(tags[i]);
				}
			}
		}
		
		if (saveChunk && chunk != null)
			chunks.add(chunk);
		
		return chunks;
	}
	
	/* (non-Javadoc)
	 * @see com.softlib.imatch.nlp.INlpProvider#extractChunks(java.lang.String[])
	 */
	public List<Chunk> extractChunks(String[] sentences)
	{
		List<Chunk> chunks = new ArrayList<Chunk>();
		
		for (String sentence : sentences) {
			chunks.addAll(chunk(sentence));
			chunks.add(new Chunk("ES"));
		}
		
		return chunks;
	}
	
	/* (non-Javadoc)
	 * @see com.softlib.imatch.nlp.INlpProvider#extractSentences(java.lang.String)
	 */
	public String[] extractSentences(String text) {
		String[] singleLines = text.split("\n");
		List<String> sentences = new ArrayList<String>();
		synchronized (sdetector) {
			for(String singleLine : singleLines) {
				String[] singleLineSentences = sdetector.sentDetect(singleLine);
				for(String singleLineSentence : singleLineSentences) {
					singleLineSentence = singleLineSentence.replace("\"", "");
					sentences.add(singleLineSentence);
				}
			}
		}
		return sentences.toArray(new String[0]);
	}

	/* (non-Javadoc)
	 * @see com.softlib.imatch.nlp.INlpProvider#extractNames(java.lang.String[], boolean)
	 */
	public String extractNames(String[] sentences, boolean capitalize)
	{
		//ArrayList<String> names=new ArrayList<String>();
		Span[] inter_names;
		StringBuffer names = new StringBuffer();

		for (int fi=0; fi < models.length; fi++) 
		{
			SimpleTokenizer tokenizer = new SimpleTokenizer(); 
			for (int si = 0; si < sentences.length; si++) {
				String[] tokens = tokenizer.tokenize(sentences[si]);
				for (int i = 0; i < tokens.length; i++) {
					tokens[i] = Character.toUpperCase(tokens[i].charAt(0)) + tokens[i].substring(1);
				}
				synchronized (finders) {
					inter_names = finders[fi].find(tokens);				
				}				
				if (inter_names.length > 0) {
					names.append(displayNames(models[fi], inter_names, tokens));
					names.append("\n");	
				}
			}
		}

		return names.toString();
	}
	
	static private String displayNames(String modelName, Span[] names, String[] tokens)
	{
		StringBuffer cb = new StringBuffer();
		
		cb.append("Model Name: ").append(modelName).append("\n");
		
		for (int si = 0; si < names.length; si++)
		{ 
			for (int ti = names[si].getStart(); ti < names[si].getEnd(); ti++)
			{
				cb.append(tokens[ti]).append(", "); 
			}
		}

		return cb.toString();
	}

	public class Chunk
	{
		List<String> tokens = new ArrayList<String>();
		List<String> tags = new ArrayList<String>();
		String tag;
		
		public Chunk(String tag) {
			super();
			this.tag = tag;
		}
		
		public List<String> getTokens() {
			return tokens;
		}

		public void setTokens(List<String> tokens) {
			this.tokens = tokens;
		}

		public List<String> getTags() {
			return tags;
		}

		public void setTags(List<String> tags) {
			this.tags = tags;
		}
		
		public String getTag() {
			return tag;
		}

		public void setTag(String tag) {
			this.tag = tag;
		}

		public String getText()
		{
			StringBuilder sb = new StringBuilder();
			
			for (int i = 0; i < this.tokens.size(); i++) {
				sb.append(this.tokens.get(i));
				sb.append(" ");
			}
						
			return sb.toString().trim();
		}
		
		public String getText(String[] tagsToUse)
		{
			StringBuilder sb = new StringBuilder();
			
			for (int i = 0; i < this.tokens.size(); i++) {
				boolean useToken = false;
				
				for (int j = 0; j < tagsToUse.length; j++) {
					if (this.tags.get(i).startsWith(tagsToUse[j])) {
						useToken = true;
						break;
					}
				}
				
				if (useToken) {
					sb.append(this.tokens.get(i));
					sb.append(" ");	
				}
			}
						
			return sb.toString().trim();
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			
			sb.append("[");
			sb.append(this.tag);
			
			for (int i = 0; i < this.tokens.size(); i++) {
				sb.append(" ");
				sb.append(this.tokens.get(i));
				sb.append("/");
				sb.append(this.tags.get(i));
			}
			
			sb.append("]\n");
			
			return sb.toString();
		}		
	}
}