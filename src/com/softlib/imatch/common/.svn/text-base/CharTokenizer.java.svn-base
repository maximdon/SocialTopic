package com.softlib.imatch.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.softlib.imatch.common.HighlightText.Type;
import com.softlib.imatch.dictionary.PorterStemmer;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.dictionary.TechnicalTermSource;

public class CharTokenizer {
	private final static int MAX_BUF_SIZE = 200;
	
	private enum Mode {
		None,
		Word,
		Gap
	}
		
	final private String delimeters  = "[]{}()<> \"'?!$%;&"+'\b'+'\t'+'\n'+'\f'+'\r';  
	final private String cleanChars  = ".:=";  
	final private String numberChars = ",";  
	
	private Map<String,List<TextPosition>> positionByStem = new HashMap<String,List<TextPosition>>(); 
	private char wordChars[] = new char[MAX_BUF_SIZE];
		
	private Map<TextPosition,Pair<TechnicalDictionaryTerm,Type>> termsByPosition = 
		new HashMap<TextPosition, Pair<TechnicalDictionaryTerm,Type>>();

	private Map<TechnicalDictionaryTerm,Set<TextPosition>> positionsByTerm =
		new HashMap<TechnicalDictionaryTerm,Set<TextPosition>>();
	
	
	public Map<TextPosition,Pair<TechnicalDictionaryTerm,Type>> getTermsByPosition() {
		return termsByPosition;
	}
	
	public Map<TechnicalDictionaryTerm, Set<TextPosition>> getPositionsByTerm() {
		return positionsByTerm;
	}

	private String stem(String text) {
		String rc = PorterStemmer.stem(text);
		rc = PorterStemmer.fix(rc);
		return rc;
	}
	
	private Pair<String,Integer> clean(char wordChars[]) {
		int idx=0;
		int startIdx=idx;
		int endIdx=1;
		Integer numRemoveCharsFromEnd = 0;
		while (cleanChars.contains(Character.toString(wordChars[idx])))
			startIdx = ++idx;
		
		while (idx<wordChars.length && wordChars[idx]!='\n') {
			if (!cleanChars.contains(Character.toString(wordChars[idx]))) {
				endIdx = idx+1;
				numRemoveCharsFromEnd=0;
			}
			else
				numRemoveCharsFromEnd++;
			idx++;
		}
		String str = new String(wordChars,startIdx,endIdx).toLowerCase();
		return new Pair<String, Integer>(str,numRemoveCharsFromEnd);
	}
	
	private void insert(char wordChars[],int endIdx) {
		Pair<String,Integer> cleanResult = clean(wordChars);
		String word = cleanResult.getLeft();
		endIdx = endIdx - cleanResult.getRight();
		String stem = stem(word);
		List<TextPosition> list = positionByStem.get(stem);
		if (list==null)
			list = new ArrayList<TextPosition>();
		list.add(new TextPosition(word,endIdx-word.length()));
		positionByStem.put(stem,list);
	}
	
	private void delete(TextPosition textPosition) {
		String stem = stem(textPosition.getText());
		List<TextPosition> list = positionByStem.get(stem);
		list.remove(textPosition);
		positionByStem.put(stem, list);
	}
	
	public void process(String text) {
		char textChars[] = text.toCharArray();
		Mode mode = Mode.None;
		
		int wordIdx = 0;
		for (int i=0; i<text.length(); i++) {
			boolean isDel = isDelimeter(textChars,i);
			if (isDel) {
				if (mode==Mode.Word) {
					wordChars[wordIdx]='\n';
					insert(wordChars,i);
					wordIdx = 0;
				}
				mode = Mode.Gap;
			} 
			else {
				if (wordIdx==MAX_BUF_SIZE)
					wordIdx=0;
				wordChars[wordIdx++]=textChars[i];
				mode = Mode.Word;
			}
		}
		if (mode==Mode.Word) {
			wordChars[wordIdx]='\n';
			insert(wordChars,text.length());
		}
	}
	
	private boolean isDigit(char c) {
		return c>='0' && c<='9';
	}
	
	private boolean isDelimeter(char text[],int idx) {
		if (numberChars.contains(Character.toString(text[idx])))
			if (!isDigit(text[idx-1]) || (idx<text.length &&!isDigit(text[idx+1])) ) 
					return true;				
		if (delimeters.contains(Character.toString(text[idx])))
			return true;
		
		return false;
	}
	
	private String getSplitRegex(TechnicalDictionaryTerm term) {
		TechnicalTermSource source = term.getTermSource();
		if (source!=null) {
			String srcName = term.getTermSource().getsourceName();
			if (SourceMngr.isSource(srcName,SourceMngr.Type.Compound))
				return "[ 0]";
		}
		return " ";
	}
	
	private List<TextPosition> findWord(String word) {
		List<TextPosition> rc = positionByStem.get(word);
		if (rc!=null)
			return rc;
		
		try {
			new Integer(word);
		}
		catch (NumberFormatException e) {
			return rc;
		}
		
		rc = positionByStem.get(word+".0");
		if (rc!=null)
			return rc;
		rc = positionByStem.get(word+".0.0");
		if (rc!=null)
			return rc;
		rc = positionByStem.get(word+".00");
		if (rc!=null)
			return rc;
		rc = positionByStem.get(word+".00.00");
		if (rc!=null)
			return rc;
	
		return rc;
	}
	
	public void findTerm(TechnicalDictionaryTerm term,boolean order,Type type) {
		String splitRegex = getSplitRegex(term);
		String stem = PorterStemmer.fix(term.getTermStemmedText());
		String words[] = stem.split(splitRegex);
		List<String> fixedWords = new ArrayList<String>();
		for (String word : words) {
			if (word==null || word.isEmpty())
				continue;
			fixedWords.add(PorterStemmer.fix(word));
		}
		List<List<TextPosition>> positions = new ArrayList<List<TextPosition>>();
		for (String word : fixedWords) {
			List<TextPosition> position = findWord(word);
			if (position!=null && !position.isEmpty())
				positions.add(position);
		}
		List<TextPosition> termPositions = getMinDistanceCombination(order,positions);
		
		addResult(termPositions,new Pair<TechnicalDictionaryTerm, Type>(term,type));

	}

	private void addResult(TextPosition position,Pair<TechnicalDictionaryTerm,Type> pair) {
		termsByPosition.put(position,pair);
		
		TechnicalDictionaryTerm term = pair.getLeft();
		Set<TextPosition> positions = positionsByTerm.get(term);
		if (positions==null)
			positions = new HashSet<TextPosition>();
		positions.add(position);
		positionsByTerm.put(pair.getLeft(),positions);
	}
	
	private void addResult(List<TextPosition> termPositions,Pair<TechnicalDictionaryTerm,Type> pair) {
		TextPosition lastTextPosition = null;
		
		TreeSet<TextPosition> sortedPositions = new TreeSet<TextPosition>(termPositions);

		for (TextPosition textPosition : sortedPositions ) {
			delete(textPosition);
			if (lastTextPosition==null)
				lastTextPosition = textPosition;
			else {
				if (lastTextPosition.isNear(textPosition)) {
					String text = lastTextPosition.getText()+" "+textPosition.getText();
					lastTextPosition = new TextPosition(text,lastTextPosition.getStart());
				}
				else {
					addResult(lastTextPosition,pair);
					lastTextPosition=textPosition;
				}
			}
		}
		if (lastTextPosition!=null) {
			addResult(lastTextPosition,pair);
		}
	}

	private boolean isOrder(List<TextPosition> combination) {
		int lastPos = -1;
		for (TextPosition textPosition : combination) {
			int pos = textPosition.getStart();
			if (pos<lastPos)
				return false;
			lastPos = pos;
		}
		return true;
	}
	
	private List<TextPosition> getNextCombination(boolean order,ObjectPerListCombinations<TextPosition> combinations) {
		List<TextPosition> combination = combinations.getNextCombination();
		if (!order)
			return combination;
		
		while (combination!=null) {
			if (isOrder(combination))
				return combination;
			combination = combinations.getNextCombination();
		}
		return combination;
	}
	
	private List<TextPosition> getMinDistanceCombination(boolean order,List<List<TextPosition>> positions) {
		
		ObjectPerListCombinations<TextPosition> combinations =
			new ObjectPerListCombinations<TextPosition>(positions);
		
		List<TextPosition> combination = getNextCombination(order,combinations);
		int minDistance = 0;
		List<TextPosition> minCombination=combination;
		while (!combination.isEmpty()) {
			int distance = getDistance(combination);
			if (minDistance==0 || distance<minDistance) {
				minDistance=distance;
				minCombination=combination;
			}
			combination = combinations.getNextCombination();
		}
		return minCombination;
	}
	
	private int getDistance(List<TextPosition> positions) {
		int maxIdx = -1;
		int minIdx = -1;
		for(TextPosition position : positions ) {
			Integer startPos = position.getStart();
			if (startPos>maxIdx)
				maxIdx = startPos;
			if (minIdx==-1 || startPos<minIdx)
				minIdx = startPos;
		}
		return maxIdx-minIdx;
	}
	

};
