package com.softlib.imatch.ticketprocessing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.softlib.imatch.ITicket;
import com.softlib.imatch.MatcherException;
import com.softlib.imatch.common.ITokenizer;
import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.common.SimpleTokenizer;
import com.softlib.imatch.common.StringUtils;
import com.softlib.imatch.dictionary.ITechnicalTermsContainer;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;

public class ExtractTechTokensByNLP extends NlpBaseTicketProcessStep {

	private static Logger log = Logger.getLogger(ExtractTechTokensByNLP.class);
	
	private TechTokenPhrase lastPhrase;
	
	public ExtractTechTokensByNLP() {
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
		
		TechTokenPhrase phrase = 
			new TechTokenPhrase(getStepName(),
								getStepName()+" Split",
								getStepName()+" Wordnet Split",
								getStepName()+" Single Split",
						        termsContainer, this);
		
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

			int nnp = 0;
			
			phrase.init();
			
			for (int i = 0; i < tags.length; i++) {
				if (nnp > 0) {
					if (tags[i].equals("NN") || tags[i].equals("NNS") || tags[i].equals("NNP") || tags[i].equals("NNPS")) {
						phrase.insert(tokens[i],tags[i]);
						nnp++;
					}
					else {
						if (nnp > 1 || tokens[i].trim().equals("or")) {
							extractedTerms.addAll(phrase.getSplit());
						}
						nnp = 0;
						phrase.init();
					}
				}
				else
				{
					phrase.init();
					if (tags[i].equals("NNP")) {
						nnp = 1;
						phrase.insert(tokens[i],tags[i]);
					}
					else {
						nnp = 0;
					}
				}
			}
			if (nnp > 1 || (nnp == 1 && tags.length > 2 && tokens[tags.length - 2].trim().equals("or"))) {
				extractedTerms.addAll(phrase.getSplit());
			}
		}
		
		if (context.getTokensBySentence()==null)
			context.setTokensBySentence(tokensBySentence);
		if (context.getTagsBySentence()==null)
			context.setTagsBySentence(tagsBySentence);

		termsContainer.endSession((float)1.0, null, true);
		lastPhrase = phrase;
		LogUtils.debug(log, "Extract #%s# {%s} Terms:%s", ticket.getId(),fieldName,extractedTerms);
	}

	
	public String getStepName() {
		return "NLP NNP Tokens";
	}
	
	/**
	 * Returns the last produced phrase by this step. 
	 * Note, this method is not thread-safe, used for testing purposes only
	 * @return
	 */
	public TechTokenPhrase getPhrase() {
		return lastPhrase;
	}
		
};
