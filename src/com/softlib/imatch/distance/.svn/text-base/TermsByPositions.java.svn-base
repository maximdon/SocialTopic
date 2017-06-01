package com.softlib.imatch.distance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.softlib.imatch.common.ObjectPerListCombinations;
import com.softlib.imatch.common.StringUtils;
import com.softlib.imatch.dictionary.PorterStemmer;
import com.softlib.imatch.dictionary.TechnicalDictionaryKey;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;

public class TermsByPositions {
		
	final static private String BEFORE_TAG = "---<<<";
	final static private String AFTER_TAG = ">>>---";
	
	final static public String SIMPLE = "Simple";
	
	private static final String DEL_BRACKETS = "\\[|\\]|\\(|\\)|\\{|\\}|\\<|\\>";
	private static final String DEL_SEPERATORS = "\\s|\\?|\\<|\\>|\\!|\\$|\\%|\\;|\\&|\\,";
	private static final String DEL_DOTS = "\\.|\\:";
	
	private static final String FIND_DELS = DEL_BRACKETS + DEL_SEPERATORS;
	private static final String CHECK_DELS = FIND_DELS + DEL_DOTS;

	private PositionsByTerm positionsByTerm = new PositionsByTerm();
	private PositionsByTerm positionsByRelationship = new PositionsByTerm();

	private Map<TechnicalDictionaryKey,TechnicalDictionaryTerm> termByKey = 
		new HashMap<TechnicalDictionaryKey, TechnicalDictionaryTerm>();

	private Map<TermPosition,TechnicalDictionaryTerm> termsByPosition = 
		new HashMap<TermPosition,TechnicalDictionaryTerm>();

	private String text;
	private int numTerms;
	
	
	public PositionsByTerm getPositionsByRelationship() {
		return positionsByRelationship;
	}
	
	public List<TechnicalDictionaryTerm> getTerms() {
		return positionsByTerm.getTerms();
	}
	
	public Map<TermPosition,TechnicalDictionaryTerm> getTermsByPositions() {
		return termsByPosition;
	}

	public void add(TechnicalDictionaryTerm term,TermPosition position) {
		termsByPosition.put(position,term);
		positionsByTerm.add(term,position);
		
		boolean added = false;
		for (TechnicalDictionaryTerm termRelation : positionsByRelationship.getMap().keySet()) {
			Collection<TechnicalDictionaryTerm> relations = term.getRelations();
			if (relations.contains(termRelation)) {
				positionsByRelationship.add(termRelation,position);
				added=true;
				break;
			}
		}
		if (!added)
			positionsByRelationship.add(term,position);
		

	}
	
	public void process(List<TechnicalDictionaryTerm> terms,String text) {
		this.text = text;
		numTerms = terms.size();
		
		List<TechnicalDictionaryTerm> addedRelations = 
			new ArrayList<TechnicalDictionaryTerm>();
		for (TechnicalDictionaryTerm term :terms) { 
			termByKey.put(term.getTermKey(), term);
			for (TechnicalDictionaryTerm relation : term.getRelations())
				addedRelations.add(relation);

		}
		for (TechnicalDictionaryTerm relation : addedRelations)
			termByKey.put(relation.getTermKey(),relation);

		String regex = getRegex(terms);
		find(text,regex);
		
	}
	
	private String getRegex(List<TechnicalDictionaryTerm> terms) {		
		String rc = "(";
		List<TechnicalDictionaryTerm> termsWithSynonyms = new ArrayList<TechnicalDictionaryTerm>();
		for (TechnicalDictionaryTerm term : terms) {
			termsWithSynonyms.add(term);
			termsWithSynonyms.addAll(term.getRelations());
		}
		
		Comparator<TechnicalDictionaryTerm> comparator = new Comparator<TechnicalDictionaryTerm>() {
			public int compare(TechnicalDictionaryTerm o1, TechnicalDictionaryTerm o2) {
				int o1_len = o1.getTermText().split(" ").length;
				int o2_len = o2.getTermText().split(" ").length;
				if (o1_len > o2_len)
					return -1;
				else if (o1_len < o2_len)
					return 1;
				else
					return 0;
			}
		};
		Collections.sort(termsWithSynonyms,comparator);

		for (TechnicalDictionaryTerm term : termsWithSynonyms) {
			String termStemmedText = term.getTermStemmedText();
			if (termStemmedText.length() > 1) {
				if (!rc.equals("("))
					rc += "|";
				String[] wordsToFind = termStemmedText.split(" ");
				for (int i = 0; i < wordsToFind.length; i++) {
					String wordToFind = PorterStemmer.fix(wordsToFind[i]);
					if (i==0)
						rc += "(?<=["+CHECK_DELS+"]){1,3}"+StringUtils.regexEncode(wordToFind) + "\\w*?";
					else
						rc += "(["+CHECK_DELS+"]){1,3}"+StringUtils.regexEncode(wordToFind) + "\\w*" ;
				}
				rc += "(?=["+CHECK_DELS+"]){1,3}"+"?";

			}
		}
		rc += ")";
		rc = "(?<!(?:<[^>]{0,500}))(?i:\\b" + rc + ")";
		return rc;			
	}
	
	
	private String cleanWordSuffix(String word) {
		if (word.endsWith("\n") || word.endsWith("\r") ||
			word.endsWith("\t") || word.endsWith(" "))
			word = word.substring(0,word.length()-1);
		return word;
	}
	
	private void find(String text, String regex) {
		Pattern regexPattern = Pattern.compile(regex);
		Matcher matcher = regexPattern.matcher(text);
		while (matcher.find()) {
			String word = matcher.group();
			word = cleanWordSuffix(word);
			Integer startPos = matcher.start();
			Integer endPos = startPos + word.length();
			TermPosition position = new TermPosition(startPos,endPos);
			String cleanWord = word.replaceAll("(["+CHECK_DELS+"]){1,3}"," ");
			TechnicalDictionaryTerm term = termByKey.get(new TechnicalDictionaryKey(cleanWord));
			if (term!=null) {
				position.setTerm(term);
				add(term,position);
			}
			else {
				cleanWord = word.replaceAll("(["+FIND_DELS+"]){1,3}"," ");
				term = termByKey.get(new TechnicalDictionaryKey(cleanWord));
				if (term!=null) {
					position.setTerm(term);
					add(term,position);
				}
				
			}
		}
	}

	private void insert(StringBuilder sb,String text,int pos) {
		if (sb.length()<=pos)
			sb.append(text);
		else
			sb.insert(pos,text);
	}
	

	private String replace(String before,String after,
						   NavigableSet<TermPosition> reverceOrder,
						   boolean finish) {
		if (reverceOrder==null)
			return text;
		StringBuilder sb = new StringBuilder(text);
		
		for (TermPosition position : reverceOrder) {
			String srcName = position.getTermSourceName();
			insert(sb,AFTER_TAG+after,position.getEnd());
			insert(sb,before.replaceFirst(SIMPLE,srcName)+BEFORE_TAG,position.getStart());
		}
		String text = sb.toString();
		if (finish)
			text = finish(text);
		return text;
	}
	
	static public String finish(String text) {
		if (text==null)
			return null;
		text = text.replaceAll(AFTER_TAG,"");
		text = text.replaceAll(BEFORE_TAG,"");
		return text;
	}
	
	public String replace(String before,String after, boolean finish) {
		Set<TermPosition> positions = new HashSet<TermPosition>();
		for (TechnicalDictionaryTerm term : positionsByTerm.getMap().keySet()) 
			positions.addAll(positionsByTerm.getMap().get(term));
		
		TreeSet<TermPosition> sortedPositions = new TreeSet<TermPosition>(positions);
		NavigableSet<TermPosition> reverceOrder = sortedPositions.descendingSet();

		return replace(before,after,reverceOrder,finish);
	}

	public String replaceComplex(String before,String after, boolean finish) {
		NavigableSet<TermPosition> reverceOrder = getMinDistanceCombination();
		return replace(before,after,reverceOrder,finish);			
	}
	
	private NavigableSet<TermPosition> getMinDistanceCombination() {
		Collection<List<TermPosition>> collections = positionsByTerm.getMap().values();
		
		if (collections.size()!=numTerms)
			return null;
		
		List<List<TermPosition>> lists = new ArrayList<List<TermPosition>>();
		for (List<TermPosition> list : collections)
			lists.add(list);
		
		ObjectPerListCombinations<TermPosition> combinations =
			new ObjectPerListCombinations<TermPosition>(lists);
		List<TermPosition> combination = combinations.getNextCombination();
		int minDistance = 0;
		List<TermPosition> minCombination=combination;
		while (!combination.isEmpty()) {
			int distance = getDistance(combination);
			if (minDistance==0 || distance<minDistance) {
				minDistance=distance;
				minCombination=combination;
			}
			combination = combinations.getNextCombination();
		}
		TreeSet<TermPosition> sortedPositions = new TreeSet<TermPosition>(minCombination);
		NavigableSet<TermPosition> reverceOrder = sortedPositions.descendingSet();
		return reverceOrder;
		
	}
	
	private int getDistance(List<TermPosition> positions) {
		int maxIdx = -1;
		int minIdx = -1;
		for(TermPosition position : positions ) {
			Integer startPos = position.getStart();
			if (startPos>maxIdx)
				maxIdx = startPos;
			if (minIdx==-1 || startPos<minIdx)
				minIdx = startPos;
		}
		return maxIdx-minIdx;
	}
	
	public String toString() {
		return positionsByTerm.toString();
	}
	
	
};
