package com.softlib.imatch.ticketprocessing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.softlib.imatch.ITicket;
import com.softlib.imatch.MatcherException;
import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.common.StringUtils;
import com.softlib.imatch.dictionary.ITechnicalTermsContainer;
import com.softlib.imatch.dictionary.TechnicalDictionaryKey;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.dictionary.TechnicalTermSource;
import com.softlib.imatch.dictionary.Wordnet;
import com.softlib.imatch.dictionary.WordnetWord;

public class ExtractNonEnglishWords extends NlpBaseTicketProcessStep {

	private static Logger log = Logger.getLogger(ExtractNonEnglishWords.class);
	
	public ExtractNonEnglishWords() {
		super();
	}
		
	public void run(String fieldName,ITicket ticket,ITechnicalTermsContainer termsContainer, StepContext context)
			throws MatcherException {
		
		termsContainer.startSession(fieldName,context.getTempTicketId(), getStepName());
		
		List<TechnicalDictionaryTerm> extractedTerms = new ArrayList<TechnicalDictionaryTerm>();

		String nlpData = context.getNlpData();
		if (nlpData==null) {
			nlpData = getData(fieldName,ticket,context);
			if (StringUtils.isEmpty(nlpData))
				return;
			context.setNlpData(nlpData);
		}
		
		String[] sentences = context.getSentences();
		if (sentences==null) {
			sentences = extractSentences(context, nlpData);
			context.setSentences(sentences);
		}
		
		String tokens[];
		Map<String,String[]> tokensBySentence = context.getTokensBySentence();
		if (tokensBySentence==null) 
			tokensBySentence = new HashMap<String,String[]>();
			
		String tags[];
		Map<String,String[]> tagsBySentence = context.getTagsBySentence();
		if (tagsBySentence==null) 
			tagsBySentence = new HashMap<String,String[]>();
	
		for (String sentence : sentences) {
			if (context.getTokensBySentence()==null) {
				tokens = nlp.tokenize(sentence);
				tokensBySentence.put(sentence,tokens);
			}
			else {
				tokens = tokensBySentence.get(sentence);
			}
	
			if (context.getTagsBySentence()==null) {
				tags = nlp.tag(tokens);
				tagsBySentence.put(sentence,tags);
			}
			else {
				tags = tagsBySentence.get(sentence);
			}
			
			for (int i = 0; i < tags.length; i++) {
				if (tags[i].startsWith("NN")) {
					String word = tokens[i];
					WordnetWord wordnetWord = Wordnet.getInstance().findWord(word, tags[i]);
					if(wordnetWord == null && StringUtils.containsOnlyLetters(word)) {
						TechnicalDictionaryKey termKey = new TechnicalDictionaryKey(word);
						TechnicalTermSource source = termsContainer.addSource(getStepName());
						TechnicalDictionaryTerm term = termsContainer.addTerm(termKey);
						if(term != null)
							term.setTermSource(source);
						extractedTerms.add(term);
					}
				}
			}
		}
		
		if (context.getTokensBySentence()==null)
			context.setTokensBySentence(tokensBySentence);
		if (context.getTagsBySentence()==null)
			context.setTagsBySentence(tagsBySentence);

		termsContainer.endSession((float)1.0, null, true);
		LogUtils.debug(log, "Extract #%s# {%s} Terms:%s", ticket.getId(), fieldName, extractedTerms);
	}

	
	public String getStepName() {
		return "Non English Words";
	}
};
