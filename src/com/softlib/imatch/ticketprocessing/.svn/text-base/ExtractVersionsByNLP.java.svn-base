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
import com.softlib.imatch.dictionary.SynonymsRelation;
import com.softlib.imatch.dictionary.TechnicalDictionaryKey;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.dictionary.TechnicalTermSource;

public class ExtractVersionsByNLP extends NlpBaseTicketProcessStep {

	public static final String FIELD_NAME = "NLP Version Tokens";
	
	private static Logger log = Logger.getLogger(ExtractVersionsByNLP.class);
	
	public ExtractVersionsByNLP() {
		LogUtils.debug(log, "Extract Versions by NLP step constructor");
	}

	public void run(String fieldName,ITicket ticket,ITechnicalTermsContainer container, StepContext context) throws MatcherException {

		container.startSession(fieldName,context.getTempTicketId(), getStepName());
		List<String> extractedTerms = new ArrayList<String>();
		String nlpData = context.getNlpData();
		if (nlpData==null) {
			nlpData = getData(fieldName,ticket,context);
			if (StringUtils.isEmpty(nlpData))
				return;
			context.setNlpData(nlpData);
		}
		
		String[] sentences = context.getSentences();
		if (sentences==null) {
			sentences = extractSentences(context,nlpData);
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
			
			int nnp = 0;
			String phrase = "";
			
			for (int i = 0; i < tags.length; i++) {
				if(tokens[i].equalsIgnoreCase("v") || tokens[i].equalsIgnoreCase("ver") || tokens[i].equalsIgnoreCase("version"))
					continue;
				if (nnp > 0) {
					String currToken = tokens[i].toLowerCase();
					if ((tags[i].equals("CD")) || 
						(tags[i].equals("CC") && currToken.startsWith("version")) ||						
						(tags[i].equals(".")  && currToken.startsWith("v"))  ) {
						
						if(currToken.startsWith("version"))
							currToken = currToken.substring("version".length());
						else if(currToken.startsWith("ver"))
							currToken = currToken.substring("ver".length());
						else if(currToken.startsWith("v"))
							currToken = currToken.substring("v".length());
						currToken = getUniformVersion(currToken);

						String tmpPhrase = phrase + " " + currToken;
						if (isEligable(tmpPhrase)) {
							if (context.isSplitEnabled() && isEligable(phrase)) {
								TechnicalDictionaryKey splitTermKey = new TechnicalDictionaryKey(phrase);
								TechnicalTermSource splitSource = container.addSource(getStepName()+" Split");
								TechnicalDictionaryTerm splitTerm = container.addTerm(splitTermKey, splitSource);
							}					
							TechnicalDictionaryKey termKey = new TechnicalDictionaryKey(tmpPhrase);
							extractedTerms.add(tmpPhrase);
							TechnicalTermSource source = container.addSource(getStepName());
							TechnicalDictionaryTerm term = container.addTerm(termKey);
							if(term != null)
								term.setTermSource(source);
						}
						nnp = 0;
						phrase = "";
					}					
					else if (tags[i].equals("NN") || tags[i].equals("NNS") || tags[i].equals("NNP") || tags[i].equals("NNPS")) {
						phrase += " " + tokens[i];
						nnp++;	
					}
					else {					
						nnp = 0;
						phrase = "";
					}
				}
				else {			
					if (tags[i].equals("NNP") || tags[i].equals("NNS") || tags[i].equals("NN")) {
						nnp = 1;
						phrase = tokens[i];
					}
					else {
						nnp = 0;
						phrase = "";
					}
				}
			}		
		}
		
		if (context.getTokensBySentence()==null)
			context.setTokensBySentence(tokensBySentence);
		if (context.getTagsBySentence()==null)
			context.setTagsBySentence(tagsBySentence);
		
		container.endSession((float)1.0, null, true);
		LogUtils.debug(log, "Extract #%s# {%s} Terms:%s", ticket.getId(),fieldName,extractedTerms);
	}

	//Parses version string in form XX.YY.ZZ... where XX, YY, ZZ should be numbers
	//If the dot part is like .0 or .00 this part will be removed, so 8.1.0 will be transformed to 8.1
	//But 8.20 will remain 8.20 and not 8.2
	static public String getUniformVersion(String version) {
		String trailing = version;
		int currentPos = version.length();
		int dotPos = -1;
		while ((dotPos = trailing.lastIndexOf('.', currentPos)) > -1) { 
			String dotPart = trailing.substring(dotPos + 1);
			int dotPartNumber = -1;
			try {
				dotPartNumber = Integer.parseInt(dotPart);
			}
			catch(Exception e) {				
			}
			if(dotPartNumber == 0) {
				trailing = trailing.substring(0, dotPos);
				currentPos = trailing.length();
			}
			else
				currentPos = dotPos - 1;
		}
		return trailing;
	}
	
	public String getStepName() {
		return FIELD_NAME;
	}

};
