package com.softlib.imatch.ticketprocessing;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.softlib.imatch.ITicket;
import com.softlib.imatch.MatcherException;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.common.StringUtils;
import com.softlib.imatch.dictionary.ITechnicalTermsContainer;
import com.softlib.imatch.dictionary.TechnicalDictionary;
import com.softlib.imatch.dictionary.TechnicalDictionaryKey;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.dictionary.TechnicalTermSource;
import com.softlib.imatch.pattern.ExtractTechPattern;
import com.softlib.imatch.pattern.PatternData;

public class ExtractTechPatternByNLP extends NlpBaseTicketProcessStep {

	public static final String SOURCE_NAME = "Patterns";
	
	private static Object lock = new Object();
	private static Logger log = Logger.getLogger(ExtractTechPatternByNLP.class);
	private ExtractTechPattern extractPattern;	
	private TechnicalDictionary dictionary;
	
	public ExtractTechPatternByNLP() {
		LogUtils.debug(log, "Extract Tech Pattern by NLP step constructor");
		extractPattern = new ExtractTechPattern();
	}
	
	public void end() {
		extractPattern.printSummary();
	}
	
	public void run(String fieldName,ITicket ticket,ITechnicalTermsContainer container, StepContext context) throws MatcherException {
		List<String> extractedTerms = new ArrayList<String>();
		synchronized (lock) {		
			if (dictionary == null)  {
				dictionary = (TechnicalDictionary) RuntimeInfo.getCurrentInfo().getBean("dictionary");			
				extractPattern.init(dictionary,null);
			}
		}

		container.startSession(fieldName,context.getTempTicketId(), getStepName());

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

		for (String sentence : sentences) {
			List<PatternData> result =
				extractPattern.process(sentence,ticket.getId());
			for (PatternData pattern : result) {
				List<TechnicalDictionaryTerm> patternTerms = pattern.getTerms();
				TechnicalDictionaryKey termKey = new TechnicalDictionaryKey(patternTerms);
				TechnicalDictionaryTerm term = container.addTerm(termKey);
				extractedTerms.add(termKey.getTermText());
				if (term!=null) {
					TechnicalTermSource newSource = container.addSource(pattern.getSource());
					TechnicalTermSource source = container.addSource(getStepName());
					term.setCondTermSource(source,newSource);
				}
			}
		}	

		container.endSession((float)1.0, null, true);
		LogUtils.debug(log, "Extract #%s# {%s} Terms:%s", ticket.getId(),fieldName,extractedTerms);
	}

	public String getStepName() {
		return SOURCE_NAME;
	}

};
