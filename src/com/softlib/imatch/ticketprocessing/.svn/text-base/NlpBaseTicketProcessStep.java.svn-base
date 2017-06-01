package com.softlib.imatch.ticketprocessing;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.didion.jwnl.data.Word;

import com.softlib.imatch.IContextInitializationListener;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.dictionary.Wordnet;
import com.softlib.imatch.nlp.INlpProvider;
import com.softlib.imatch.nlp.NLP;
import com.softlib.imatch.nlp.NLP.Chunk;

public abstract class NlpBaseTicketProcessStep extends BaseTicketProcessStep implements IContextInitializationListener
{
	static INlpProvider nlp;
	//Note, it is different from standard getFieldName() as should be equal for all nlp based steps
	private final String stepName = "nlp";
	static public Pattern eligibilityPattern = Pattern.compile("[+/:<>=&{}]", Pattern.CASE_INSENSITIVE);
	
	public NlpBaseTicketProcessStep() {
		super();
		RuntimeInfo.getCurrentInfo().registerContextInitializationListener(this);
	}
	
	String[] extractSentences(StepContext context, String nlpData)
	{
		NlpStepData stepData = (NlpStepData)context.getStepData(stepName);
		String[] sentences;
		if(stepData != null && stepData.getSentences() != null) 
			sentences = stepData.getSentences();
		else {
			 sentences = nlp.extractSentences(nlpData);
			 if(stepData == null)
				 stepData = new NlpStepData(sentences);
			 else
				 stepData.setSentences(sentences);
			 context.setStepData(stepName, stepData);
		}
		return sentences;
	}
	
	List<Chunk> extractChunks(StepContext context, String nlpData)
	{
		NlpStepData stepData = (NlpStepData) context.getStepData(stepName);
		List<Chunk> chunks;
		if(stepData != null && stepData.getChunks() != null) 
			chunks = stepData.getChunks();
		else {
			String[] sentences = extractSentences(context, nlpData);
			chunks = nlp.extractChunks(sentences);
			stepData = (NlpStepData) context.getStepData(stepName);
			stepData.setChunks(chunks);
		}
		return chunks;
	}
	
	@Override
	protected boolean isEligable(String termText)
	{
		if(termText.startsWith("-"))
			return false;
		Matcher matcher = eligibilityPattern.matcher(termText);
		if(matcher.find())
			return false;
		
		if (!super.isEligable(termText))
			return false; 
		String[] words = termText.split(" ");
		if(Wordnet.getInstance().isStopWord(words[words.length -1]))
			//We don't allow terms to end with stop word
			return false;
		//String names = nlp.extractNames(new String[] {termText}, true);
		//if (names.length() > 0) 
		//	return false;
		
		return true;
	}
		
	@Override
	public void contextInitialized() 
	{
		nlp = (INlpProvider) RuntimeInfo.getCurrentInfo().getBean("nlp");			
	}
	
	private class NlpStepData
	{
		private String[] sentences;
		private List<Chunk> chunks;
		
		public NlpStepData(String[] sentences) {
			this(sentences, null);
		}
		
		public NlpStepData(List<Chunk> chunks) {
			this(null, chunks);
		}
		
		public NlpStepData(String[] sentences, List<Chunk> chunks)
		{
			this.sentences = sentences;
			this.setChunks(chunks);
		}
		
		public void setSentences(String[] sentences) {
			this.sentences = sentences;
		}

		public String[] getSentences() {
			return sentences;
		}

		public void setChunks(List<Chunk> chunks) {
			this.chunks = chunks;
		}

		public List<Chunk> getChunks() {
			return chunks;
		}
		
	}
}
