package com.softlib.imatch.ticketprocessing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.softlib.imatch.IContextInitializationListener;
import com.softlib.imatch.ITicket;
import com.softlib.imatch.MatcherException;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.common.StringUtils;
import com.softlib.imatch.common.configuration.IConfigurationResource;
import com.softlib.imatch.common.configuration.IConfigurationResourceLoader;
import com.softlib.imatch.dictionary.ITechnicalTermsContainer;
import com.softlib.imatch.dictionary.TechnicalDictionaryKey;
import com.softlib.imatch.dictionary.TechnicalTermSource;

public class NegativeSentimentStep extends NlpBaseTicketProcessStep implements IContextInitializationListener
{
	private static Logger log = Logger.getLogger(NegativeSentimentStep.class);
	private List<String> skipWords = new ArrayList<String>();

	public NegativeSentimentStep()
	{
		RuntimeInfo.getCurrentInfo().registerContextInitializationListener(this);
	}
	
	public void setSkipWords(List<String> skipWords) {
		this.skipWords = skipWords;
	}
	
	@Override
	public String getStepName() {
		return "Negative Sentiment";
	}

	@Override
	public void run(String fieldName, ITicket ticket,
			ITechnicalTermsContainer termsContainer, StepContext context)
			throws MatcherException 
	{
		termsContainer.startSession(fieldName,context.getTempTicketId(), getStepName());
		List<String> extractedTerms = new ArrayList<String>();
		String nlpData = context.getNlpData();
		TechnicalTermSource source = termsContainer.getDictionary().getSource(getStepName());
		TechnicalTermSource operationsSource = termsContainer.getDictionary().getSource("Operations");
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
			String[] filteredTags = new String[tags.length];
			String[] filteredTokens = new String[tokens.length];
			List<Integer> indexesToRemove = new ArrayList<Integer>();
			for(String skipWord : skipWords) {
				String[] skipWordParts = skipWord.split(" ");
				String[] sentenceParts = sentence.split(" ");
				for(int i = 0; i < sentenceParts.length; ++i) {
					boolean found = true;
					List<Integer> tmpIndexes = new ArrayList<Integer>();
					for(int j = 0; j < skipWordParts.length; ++j) {
						if(i + j == sentenceParts.length) {
							found = false;
							break;
						}
						if(!sentenceParts[i + j].equals(skipWordParts[j]))
							found = false;
						else
							tmpIndexes.add(i + j);
					}
					if(found)
						indexesToRemove.addAll(tmpIndexes);
				}
			}
			int idx = 0;
			for(int i = 0; i < tokens.length; ++i) {
				if(!indexesToRemove.contains(i)) {
					filteredTags[idx] = tags[i];
					filteredTokens[idx] = tokens[i];
					idx ++;
				}
			}
			int groupStartIdx = -1;
			int skipStartIdx = -1;
			int skipEndIdx = -1;
			List<String> relevantTags = Arrays.asList("JJ", "VB", "VBG", "VBP", "NN", "NNP", "NNS");
			List<String> skipTags = Arrays.asList("TO", "IN", "DT");
			for (int i = 0; i < filteredTags.length; i++) {		
				if(filteredTags[i] == null)
					break;
				if(relevantTags.contains(filteredTags[i])) {
					if(groupStartIdx == -1) {
						groupStartIdx = i;
					}
					else {
						boolean added = false;
						for(NegativeSentiment negativeSentiment : NegativeSentiment.getAllSentiments()) {
							if(negativeSentiment.matchInText(filteredTokens, groupStartIdx, i, skipStartIdx, skipEndIdx)) {
								//Negative pattern found, add it
								termsContainer.addTerm(new TechnicalDictionaryKey(negativeSentiment.toString()), source);
								if(context.isSplitEnabled())
									//Add negative pattern subject as well
									termsContainer.addTerm(new TechnicalDictionaryKey(negativeSentiment.getSubject()), operationsSource);
								//No more than 1 pattern in the same location
								extractedTerms.add(negativeSentiment.toString());
								added = true;
								break;
							}
						}
						if(!added)
							//If no negative sentiments were found at position groupStartIdx, we want to move to position groupStartIdx + 1 and not to the of the group
							i = groupStartIdx;
						groupStartIdx = -1;
						skipStartIdx = -1;
						skipEndIdx = -1;
					}
				}
				else if(skipTags.contains(filteredTags[i])&& groupStartIdx != -1) {
					//Continue the group, mark it as skip
					if(skipStartIdx == -1) {
						skipStartIdx = i;
						skipEndIdx = i;
					}
					else
						skipEndIdx = i;
					continue;
				}
				else {
					groupStartIdx = -1;
					skipStartIdx = -1;
					skipEndIdx = -1;
					//This tag is not relevant skip it
					continue;
				}
			}
		}
		LogUtils.debug(log, "Extract #%s# {%s} Terms:%s", ticket.getId(),fieldName,extractedTerms);
	}

	@Override
	public void contextInitialized() {
		IConfigurationResourceLoader loader = (IConfigurationResourceLoader) RuntimeInfo.getCurrentInfo().getBean("xmlConfigurationResourceLoader");
		IConfigurationResource resource = loader.loadResource("xml:///{SolutionConfigFolder}/negativeSentimentPatterns.xml;//negativeSentimentConfig");
		NegativeSentimentsConfig negativeSentimentsConfig = (NegativeSentimentsConfig) resource.getCustomConfiguration(NegativeSentimentsConfig.class);
		for(String negativeSentimentPattern : negativeSentimentsConfig.getNegativeSentiments()) {
			NegativeSentiment.addSentimentPattern(negativeSentimentPattern);
		}
	}

	@Override
	protected boolean isEligable(String termText) {
		if(termText.startsWith("error message"))
			return false;
		return super.isEligable(termText);
	}	
}
