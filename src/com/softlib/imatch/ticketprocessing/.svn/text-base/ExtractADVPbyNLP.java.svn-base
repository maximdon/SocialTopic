package com.softlib.imatch.ticketprocessing;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.softlib.imatch.ITicket;
import com.softlib.imatch.MatcherException;
import com.softlib.imatch.common.ITokenizer;
import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.common.SimpleTokenizer;
import com.softlib.imatch.common.StringUtils;
import com.softlib.imatch.dictionary.ITechnicalTermsContainer;
import com.softlib.imatch.dictionary.TechnicalDictionaryKey;
import com.softlib.imatch.nlp.NLP.Chunk;

public class ExtractADVPbyNLP extends BaseMeaningExtractionStep {

	private static Logger log = Logger.getLogger(ExtractADVPbyNLP.class);
	static ITokenizer splitter = new SimpleTokenizer(new char[] {' ', '.', '_'});
	public ExtractADVPbyNLP() {
	}

	public void run(String fieldName,ITicket ticket,ITechnicalTermsContainer container, StepContext context)
			throws MatcherException {
		List<String> extractedTerms = new ArrayList<String>();

		container.startSession(fieldName,context.getTempTicketId(), getStepName());

		String nlpData = context.getNlpData();
		if (nlpData==null) {
			nlpData = getData(fieldName,ticket,context);
			if (StringUtils.isEmpty(nlpData))
				return;
			context.setNlpData(nlpData);
		}

		List<Chunk> chunks = context.getChunks();
		if (chunks==null) {
			chunks = extractChunks(context,nlpData);
			context.setChunks(chunks);
		}
		int pp = 0;
		StringBuilder phrase = new StringBuilder();
		String[] tagsToUse = {"VB", "NN"}; 
		for (Chunk chunk : chunks) {
			if (pp == 0 && chunk.getTag().equals("ADVP") && chunk.getTags().get(0).equals("WRB")) {
				pp++;
			}
			else if (pp > 0 && (chunk.getTag().equals("NP") || chunk.getTag().equals("VP"))) {
				phrase.append(chunk.getText(tagsToUse));
				phrase.append(" ");
				pp++;
			}
			else if (pp > 1 && phrase.toString().trim().split(" ").length > 1) {
				String phraseStr = phrase.toString().trim();
				boolean eligible = isEligable(phraseStr);
				if(eligible) {
					container.addTerm(new TechnicalDictionaryKey(phraseStr));
					extractedTerms.add(phraseStr);
				}
				phrase.delete(0, phrase.length());
				pp = 0;
			}
			else
			{
				phrase.delete(0, phrase.length());
				pp = 0;
			}
		}
		
		container.endSession((float)1.0, null, true);
		LogUtils.debug(log, "Extract #%s# {%s} Terms:%s", ticket.getId(),fieldName,extractedTerms);
	}

	
	public String getStepName() {
		return "ADVP Tokens";
	}
}
