package com.softlib.imatch.ticketprocessing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.softlib.imatch.common.Pair;
import com.softlib.imatch.dictionary.TechnicalDictionary;
import com.softlib.imatch.dictionary.TechnicalDictionaryKey;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;

public class TechTokenPattern {

	static private final int PART_MAX_SIZE = 3;
	static private final int PRO_PART_MAX_SIZE = 5;
	static private final String NOT_CHANGE = "NOT_CHANGE";
	static private final String SPACE = " ";
	static private final String[] array = new String[0];
	
	private List<String> partSubjects = new ArrayList<String>();
	private TechnicalDictionary dictionary;
	
	
	public TechTokenPattern(TechnicalDictionary dictionary,
						    List<String> partSubjects) {
		this.dictionary = dictionary;
		this.partSubjects = partSubjects;
	}

	

	public void getPattern(List<TechnicalDictionaryTerm> terms,
						   List<Integer> termPos) {
//		Collection<TechnicalDictionaryTerm> pattern = 
//			new ArrayList<TechnicalDictionaryTerm>();
		
		Map<String,Pair<Integer,Integer>> partPos =
			new HashMap<String,Pair<Integer,Integer>>();
		
		String lastPart = partSubjects.get(0);
		int index=-1;
		for (TechnicalDictionaryTerm term : terms) {
			index++;
			String StemmedText = term.getTermStemmedText();
			boolean cont = false;
			for (String aPart : partSubjects) {
				if (StemmedText.equals(aPart)) {
					if (partPos.get(aPart)==null) {
						partPos.put(aPart,new Pair<Integer,Integer>(termPos.get(index),-1));
						lastPart = aPart;
					}
					cont = true;
					break;
				}
			}
			if (cont)
				continue;
			if (lastPart!=null) {
				Pair<Integer,Integer> pair = 
					new Pair<Integer,Integer>(partPos.get(lastPart).getLeft(),termPos.get(index));
				partPos.put(lastPart,pair);
				lastPart = null;
			}
		}
		
		for (String part : partPos.keySet()) {
			System.out.println("part= "+part+",pos="+partPos.get(part).getLeft()+",param pos="+partPos.get(part).getRight());
		}
	}
	
	private static final String sentence1 = "I have upgrade a new server from ver 7.5 and to version 8.0";
	private static final String sentence2 = "The new server have been upgraded to new ver 8.0 from the exist 7.5 ";

	public static void main(String[] args) {
		TechnicalDictionary dictionary = new TechnicalDictionary();
		dictionary.addTerm(new TechnicalDictionaryKey("upgrade"));
		dictionary.addTerm(new TechnicalDictionaryKey("from"));
		dictionary.addTerm(new TechnicalDictionaryKey("to"));
		dictionary.addTerm(new TechnicalDictionaryKey("7.5"));
		dictionary.addTerm(new TechnicalDictionaryKey("8.0"));
		dictionary.addTerm(new TechnicalDictionaryKey("server"));

		List<String> partSubjects = new ArrayList<String>();
		partSubjects.add("upgrad");
		partSubjects.add("from");
		partSubjects.add("to");
	
		TechTokenPattern tokenPattern = new TechTokenPattern(dictionary,partSubjects);

		Collection<TechnicalDictionaryTerm> terms = 
			dictionary.findInText(sentence1.split(SPACE));
		List<TechnicalDictionaryTerm> termsList = new ArrayList<TechnicalDictionaryTerm>(terms);
		System.out.println("> =========="+sentence1+"=========");
		for (TechnicalDictionaryTerm term : terms) {
			System.out.println("> term="+term.getTermStemmedText());
		}
		terms = 
			dictionary.findInText(sentence2.split(SPACE));
		
		List<Integer> pos = new ArrayList<Integer>();
		pos.add(1);
		pos.add(3);
//		pos.add(6);
		pos.add(9);
		pos.add(10);
		pos.add(12);
		pos.add(14);
		
		
		tokenPattern.getPattern(termsList,pos);
		
		
		
//		System.out.println("> =========="+sentence2+"=========");
//		for (TechnicalDictionaryTerm term : terms) {
//			System.out.println("> term="+term.getTermStemmedText());
//		}
//		String pattern;
//		pattern = tokenPattern.getPattern(sentence1,"upgrad");
//		System.out.println("Pattern ="+pattern);
//		pattern = tokenPattern.getPattern(sentence2,"upgrad");
//		System.out.println("Pattern ="+pattern);
	}

	
};
