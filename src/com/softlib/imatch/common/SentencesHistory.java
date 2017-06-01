package com.softlib.imatch.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class SentencesHistory {

	static private final String DUPLICATE =  "~~~ see #";
	
	static final private int NUM_WORD_TOBE_EQUAL = 6;
	
	private final TracerFile tracerFile;
	private final boolean printDuplicate;
	
	
    private Map<String,List<String>> history;
    private boolean printed = false;
    
    private static Logger log = Logger.getLogger(SentencesHistory.class);
	
	public SentencesHistory(TracerFile tracerFile,boolean printDuplicate) {
		this.tracerFile = tracerFile;
		this.printDuplicate = printDuplicate;
		history = new HashMap<String,List<String>>();
	}

	private boolean sentenceEqual(String s1,String s2) {
		int count = 0;
		String[] split1 = s1.split(" ");
		String[] split2 = s2.split(" ");
		String[] spt1 = split1.clone();
		String[] spt2 = split2.clone();
		int idx1 = 0;
		for (String word1 : split1) {
			int idx2 = 0;
			for (String word2 : split2) {
				if (word1.equals(word2)) {
					int index = 0;
					while ((idx1+index<spt1.length) && (idx2+index<spt2.length) ) {
						if (spt1[index+idx1].equals(spt2[index+idx2]))
							index++;
						else
							break;
					}
					if (index>count)
						count=index;
				}
				idx2++;
			}
			idx1++;
		}
		if (count >= split1.length-2 || 
			count >= split2.length-2 )
			return true;
		return count >= NUM_WORD_TOBE_EQUAL;
	}
	
	public void put(String key,String sentence) {
		if(!log.isDebugEnabled())
			return;
		synchronized (history) {			
			List<String> existSentences = history.get(key);
			if (existSentences==null) {
				existSentences = new ArrayList<String>();
			}
			int index=0;
			int exist = -1;
			for (String existSentence : existSentences) {
				if (sentenceEqual(existSentence,sentence)) {
					exist = index;
					break;
				}
				index++;
			}
			if (exist==-1) {
				existSentences.add(sentence);
			}
			else
				existSentences.add(DUPLICATE+exist);
			history.put(key, existSentences);
		}
	}
	
	public void print() {
		if (!log.isDebugEnabled() || printed) 
			return;
		printed = true;
		synchronized (history) {			
			for (String key : history.keySet() ) {
				List<String> termSentences = history.get(key);
				List<String> sentences = new ArrayList<String>();
				for (String sentence : termSentences) {
					if (printDuplicate || !sentence.contains(DUPLICATE))
						sentences.add(sentence);
				}
				if (sentences.size()<2)
					continue;
				tracerFile.write("[K] "+key);
				int idx = 0;
				for (String sentence : sentences) {
					tracerFile.write("[S]"+"{"+idx+"}"+sentence);
					idx++;
				}
			}
		}
		
	}

	
};
