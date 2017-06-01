package com.softlib.imatch.ticketprocessing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.softlib.imatch.ITicket;
import com.softlib.imatch.MatcherException;
import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.common.StringUtils;
import com.softlib.imatch.dictionary.ITechnicalTermsContainer;
import com.softlib.imatch.dictionary.SynonymsRelation;
import com.softlib.imatch.dictionary.TechnicalDictionaryKey;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.dictionary.TechnicalTermSource;

public class HtmlAbbreviationsStep extends BaseTicketProcessStep {

	private static Logger log = Logger.getLogger(HtmlAbbreviationsStep.class);
	private static List<String> stopWords = Arrays.asList("a", "and", "by", "to");
	
	private List<WordPosition> wordPositions;
	@Override
	public String getStepName() {
		return "HtmlAbbreviations";
	}

	@Override
	public void run(String fieldName, ITicket ticket,
			ITechnicalTermsContainer termsContainer, StepContext context)
			throws MatcherException {
		String data = getData(fieldName, ticket, context);
		Map<String, Integer>abbreviationsPositions = runRegex(data);
		TechnicalTermSource source = termsContainer.getDictionary().getSource(getStepName());
		SynonymsRelation relation = new SynonymsRelation();
		termsContainer.startSession(fieldName,context.getTempTicketId(), getStepName());
		List<TechnicalDictionaryTerm>extractedTerms = new ArrayList<TechnicalDictionaryTerm>();
		for(String abbreviation : abbreviationsPositions.keySet())
		{
			int globalTextPosition = abbreviationsPositions.get(abbreviation);
			char[] abbreviationLetters = parseAbbreviation(abbreviation);
			int currentTextPosition = data.charAt(globalTextPosition - 1) == ' ' ? globalTextPosition - 2 : globalTextPosition - 1;
			int currentAbbreviationPosition = abbreviationLetters.length - 1;
			List<String> termData = new ArrayList<String>();
			while(currentAbbreviationPosition >= 0) {
				String currentWord = getWordAtPosition(data, currentTextPosition);
				if(currentWord == null)
					break;
				WordAbbreviationInfo abbreviationInfo = testWordEligibility(currentWord, abbreviationLetters[currentAbbreviationPosition]);
				if(!abbreviationInfo.isEligible) {
					break;
				}
				currentTextPosition = currentTextPosition - (currentWord.length() + 1);
				if(abbreviationInfo.addToTerm) {
					currentAbbreviationPosition --;
					termData.add(currentWord);
				}
			}
			if(currentAbbreviationPosition < 0) {
				//indicates success
				Collections.reverse(termData);				
				TechnicalDictionaryTerm meaningTerm = termsContainer.addTerm(new TechnicalDictionaryKey(StringUtils.join(termData, " ")), source);
				TechnicalDictionaryTerm abbreviationTerm = termsContainer.addTerm(new TechnicalDictionaryKey(new String(abbreviationLetters)), source);
				extractedTerms.add(meaningTerm);
				extractedTerms.add(abbreviationTerm);
				relation.relate(meaningTerm, abbreviationTerm, "Html Abbreviations");
			}
		}
		termsContainer.endSession(0, null, false);
		wordPositions = null;
		LogUtils.debug(log, "Extract #%s# {%s} Terms:%s", ticket.getId(),fieldName,extractedTerms);
	}
	
	private Map<String, Integer> runRegex(String data) {
		MatchRegexOperation regexOp = new MatchRegexOperation("\\s\\(.*?\\)");
		Map<String, Integer> result = regexOp.runWithPositions(data);
		return result;
	}

	private String getWordAtPosition(String data, int currentTextPosition) {
		if(wordPositions == null) {
			wordPositions = splitData(data);
		}
		if(currentTextPosition > data.length())
			return null;
		for(WordPosition wordPosition : wordPositions) {
			if(wordPosition.startPosition <= currentTextPosition && wordPosition.endPosition >= currentTextPosition)
				return wordPosition.word;
		}
		return null;
	}

	private List<WordPosition> splitData(String data) {
		//TODO define suitable tokenizer
		String[] words = data.split(" ");
		int currentPosition = 0;
		List<WordPosition> result = new ArrayList<WordPosition>();
		for(String word : words) {
			int endPosition = currentPosition + word.length() - 1;
			WordPosition wordPosition = new WordPosition(currentPosition, endPosition, word);
			result.add(wordPosition);
			currentPosition = endPosition + 2;
		}
		return result;
	}

	private WordAbbreviationInfo testWordEligibility(String currentWord, char c) {
		Character firstCharacter = currentWord.charAt(0);
		boolean suitableChar = Character.toUpperCase(firstCharacter) == Character.toUpperCase(c);
		boolean isStopWord = stopWords.contains(currentWord);
		WordAbbreviationInfo result = new WordAbbreviationInfo(suitableChar || isStopWord, suitableChar, isStopWord);
		return result;
	}

	private char[] parseAbbreviation(String abbreviation) {
		List<Character> abbreviationChars = new ArrayList<Character>();
		for(char c : abbreviation.toCharArray()) {
			if(Character.isLetter(c))
				abbreviationChars.add(c);
		}
		char[] result = new char[abbreviationChars.size()];
		int i = 0;
		for(Character c : abbreviationChars)
			result[i++] = c;
		return result;
	}
	private class WordAbbreviationInfo
	{
		boolean isEligible;
		boolean addToTerm;
		boolean isStopWord;
		
		public WordAbbreviationInfo(boolean isEligible, boolean addToTerm, boolean isStopWord) {
			this.isEligible = isEligible;
			this.addToTerm = addToTerm;
			this.isStopWord = isStopWord;
		}
	}
	
	private class WordPosition
	{
		public WordPosition(int startPosition, int endPosition, String word) {
			this.startPosition = startPosition;
			this.endPosition = endPosition;
			this.word = word;
		}
		int startPosition;
		int endPosition;
		String word;
		@Override
		public String toString() {
			return word + "(" + startPosition + "..." + endPosition + ")";
		}
	}	
}
