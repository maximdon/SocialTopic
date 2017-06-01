package com.softlib.imatch.ticketprocessing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.softlib.imatch.ITicket;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.dictionary.LocalTechnicalDictionary;
import com.softlib.imatch.dictionary.TechnicalDictionary;
import com.softlib.imatch.dictionary.TechnicalDictionaryKey;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.dictionary.TechnicalTermSource;
import com.softlib.imatch.matcher.CandidateScore;
import com.softlib.imatch.matcher.ITicketFieldsNames;
import com.softlib.imatch.score.ScoreExplanation;

public class ProcessedTicket implements IProcessedTicket {
	
	private static final int NUM_TERMS_TOBE_REMATCH = 4;
	
	private ITicket originalTicket;
	private static IScoreCalculator defaultCalculator;
	private IScoreCalculator scoreCalculator;
	private static Object lock = new Object();
	private ScoreExplanation scoreExplanation = new ScoreExplanation();
	
	private OverwriteBoostFactor overBoostFactor = new OverwriteBoostFactor();
	
	private List<TechnicalDictionaryTerm> sortedTerms;
	private Map<TechnicalDictionaryKey, Float> boostFactors;
	private List<TechnicalDictionaryTerm> titleTerms;

	private ProcessedField currentField;
	
	private Map<String,ProcessedField> data = new HashMap<String,ProcessedField>();
	
	private MatchMode matchMode = MatchMode.match;

	private boolean isSourceTicket;
	private boolean isSupportReduceTermFreq;

	protected TechnicalDictionary localDictionary;

	private List<String> orphanWords;
	
	protected ProcessedTicket()
	{
		//Do nothing, just for ProcessedTicketWrite initialization
	}
	
	public ProcessedTicket(ITicket originalTicket, 
			   IScoreCalculator scoreCalculator) {
		this(originalTicket,scoreCalculator,false,false);
	}

	public ProcessedTicket(ITicket originalTicket, 
						   IScoreCalculator scoreCalculator,
						   boolean isSourceTicket) {
		this(originalTicket,scoreCalculator,isSourceTicket,false);
	}
	
	public ProcessedTicket(ITicket originalTicket, 
						   IScoreCalculator scoreCalculator,
						   boolean isSourceTicket,
						   boolean isSupportReduceTermFreq) {
		init(originalTicket, scoreCalculator, isSourceTicket, isSupportReduceTermFreq);
	}
	
	protected void loadDictionary() {
		TechnicalDictionary dictionary = (TechnicalDictionary)RuntimeInfo.getCurrentInfo().getBean("dictionary");
		localDictionary = 
			new LocalTechnicalDictionary(dictionary,isSupportReduceTermFreq,isSourceTicket);
	}
	
	public void setMatchMode(MatchMode matchMode) {
		this.matchMode = matchMode;
	}
	
	public MatchMode getMatchMode() {
		return matchMode;
	}

	public TechnicalDictionary getDictionary() {
		return localDictionary;
	}
	
	public Map<String, ProcessedField> getData() {
		return data;
	}
	
	public ProcessedField getField(String fieldName) {
		return data.get(fieldName);
	}

	/**
	 * Returns the id of the original ticket.
	 * Note, the id can be null in case the ticket is not saved yet
	 */
	public String getId() {
		return originalTicket.getId();
	}
	
	public String getOriginObjectId() {
		return originalTicket.getOriginObjectId();
	}
	
	public ITicket getOriginalTicket() {
		return originalTicket;
	}	

	public static IScoreCalculator getDefaultCalculator() {
		return defaultCalculator;
	}

	public IScoreCalculator getScoreCalculator() {
		return scoreCalculator;
	}
	public void setScoreCalculator(IScoreCalculator calculator) {
		this.scoreCalculator = calculator;
	}
	
	public static CandidateScore match(IProcessedTicket ticket, IProcessedTicket candidateTicket) {
		//return calculator.calculateScore(ticket, candidateTicket);
		return ticket.getScoreCalculator().calculateScore(ticket, candidateTicket);
	}
	
	@Override
	public String toString() {
		return String.format("%s, fields:%s", originalTicket, data.values());
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((originalTicket == null) ? 0 : originalTicket.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProcessedTicket other = (ProcessedTicket) obj;
		if (originalTicket == null) {
			if (other.originalTicket != null)
				return false;
		} else if (!originalTicket.equals(other.originalTicket))
			return false;
		return true;
	}
	
	public ScoreExplanation getScoreExplanation() {
		return scoreExplanation;
	}

	public void startSession(String fieldName,String ticketId, String sourceName) {
		if (data.containsKey(fieldName)) 
			currentField = data.get(fieldName);
		else {
			currentField = new ProcessedField(localDictionary,this,fieldName);
			data.put(fieldName,currentField);
		}
		currentField.startSession(fieldName,ticketId,sourceName);
		//dictionary.startSession(fieldName, ticketId, sourceName);
	}
	
	public int getTermFreq(TechnicalDictionaryTerm term) {
		int termFreq = localDictionary.getTermFreq(term.getTermKey());
		for(TechnicalDictionaryTerm synonymTerm : term.getRelations())
			termFreq += localDictionary.getTermFreq(synonymTerm.getTermKey());
		return termFreq;
	}

	public void reduceTermFreq(TechnicalDictionaryTerm term) {
		currentField.reduceTermFreq(term);
	}

	public TechnicalDictionaryTerm addTerm(TechnicalDictionaryKey termKey) {
		return currentField.addTerm(termKey);
	}
	
	public void freezeTerms() {
		currentField.freezeTerms();
	}
	
	public ProcessedField importField(ProcessedField field)
	{
		data.put(field.getFieldName(), field);
		for(TechnicalDictionaryTerm term : field.getTerms())
			localDictionary.addTerm(term.getTermKey());
		field.init(localDictionary, this);
		return field;
	}

	public TechnicalDictionaryTerm addTerm(TechnicalDictionaryKey termKey, TechnicalTermSource source) {
		return currentField.addTerm(termKey, source);
	}

	public TechnicalTermSource addSource(String sourceName) {
		return currentField.addSource(sourceName);
	}
	
	public void endSession(float boostFactor, List<Float> itemsBoostFactors, boolean isRequired) {
		currentField.endSession(boostFactor, itemsBoostFactors, isRequired);
		//dictionary.endSession(boostFactor, itemsBoostFactors, isRequired);
	}
	
	public Collection<TechnicalDictionaryTerm> getAllTerms(boolean withTF) {
		Collection<TechnicalDictionaryTerm> rc;
		if (withTF)
			rc = new ArrayList<TechnicalDictionaryTerm>();
		else
			rc = new HashSet<TechnicalDictionaryTerm>();
		for(ProcessedField field : data.values())
			rc.addAll(field.getTerms());
		return rc;
	}
	
	public Collection<TechnicalDictionaryTerm> getOneFreqTerms() {
		Collection<TechnicalDictionaryTerm> rc = new HashSet<TechnicalDictionaryTerm>();
		for(ProcessedField field : data.values())
			rc.addAll(field.getOneFreqTerms());
		return rc;
	}

	public Collection<TechnicalDictionaryTerm> getZeroFreqTerms() {
		Collection<TechnicalDictionaryTerm> rc = new HashSet<TechnicalDictionaryTerm>();
		for(ProcessedField field : data.values())
			rc.addAll(field.getZeroFreqTerms());
		return rc;
	}

	public List<TechnicalDictionaryTerm> getTitleTerms() {
		List<TechnicalDictionaryTerm> rc = new ArrayList<TechnicalDictionaryTerm>();
		
		ITicketFieldsNames fieldsNamesConfig = originalTicket.getFieldsConfig();
		Set<String> titleFields = fieldsNamesConfig.getTitleFields();

		boolean found = false;
		for (String titleFieldName : titleFields) {
			ProcessedField titleField = data.get(titleFieldName);
			if (titleField!=null) {
				rc.addAll(titleField.getTerms());
				found = true;
			}
		}
		if (found)
			return rc;
		
		if(titleTerms == null) {
			titleTerms = new ArrayList<TechnicalDictionaryTerm>();
			TechnicalDictionary dictionary = (TechnicalDictionary)RuntimeInfo.getCurrentInfo().getBean("dictionary");
			if(getOriginalTicket() != null && getOriginalTicket().getTitle() != null) {
				String[] titleArr = getOriginalTicket().getTitle().split(" ");
				Collection<TechnicalDictionaryTerm> terms = dictionary.findInText(titleArr); 
				for(TechnicalDictionaryTerm term : terms)
					titleTerms.add(term);
			}
		}
		return titleTerms;
	}

	private class OverwriteBoostFactor {
		
		private Set<TechnicalDictionaryTerm> mustTerms = new HashSet<TechnicalDictionaryTerm>();
		
		private Map<TechnicalDictionaryTerm,Float> overwriteBoostFactor = 
			new HashMap<TechnicalDictionaryTerm,Float>(); 
		
		public void addMustTerm(TechnicalDictionaryKey termKey) {
			TechnicalDictionaryTerm term = localDictionary.get(termKey);
			if (term!=null)
				mustTerms.add(term);
		}
		
		public boolean isMustTerm(TechnicalDictionaryTerm term) { 
			return mustTerms.contains(term);
		}
		
		public float getItemBoost(TechnicalDictionaryTerm term) {
			if (overwriteBoostFactor.keySet().contains(term)) 
				return overwriteBoostFactor.get(term);
			term = localDictionary.get(term.getTermKey(), true);
			if (term!=null)
				return term.getBoost();
			return 0;
		}

		public void overwriteBoostFactor(TechnicalDictionaryKey termKey,float boost) {
			TechnicalDictionaryTerm term = localDictionary.get(termKey);
			if (term!=null) {
				if (boost == 0.0)
					overwriteBoostFactor.put(term,boost);
				else
					overwriteBoostFactor.put(term,(float)boost);
			}
		}

		public void addBoostFactor(TechnicalDictionaryKey termKey,float boost) {
			TechnicalDictionaryTerm term = localDictionary.get(termKey);
			if (term!=null) 
				overwriteBoostFactor.put(term,(float) (overwriteBoostFactor.get(term) + boost));
		}
		
		public void zeroingAllBoostFactor() {
			overwriteBoostFactor = new HashMap<TechnicalDictionaryTerm,Float>(); 
			Iterator<TechnicalDictionaryTerm> iter = localDictionary.termsIterator();
			while (iter.hasNext()) {
				TechnicalDictionaryTerm term = iter.next();
				overwriteBoostFactor.put(term,new Float(0));
			}
		}

	};
	
	public boolean isMustTerm(TechnicalDictionaryTerm term) { 
		return overBoostFactor.isMustTerm(term);
	}

	public float getItemBoost(TechnicalDictionaryTerm term) {
		return overBoostFactor.getItemBoost(term);
	}
	
	public void overwriteBoostFactor(TechnicalDictionaryKey termKey,float boost) {
		overBoostFactor.overwriteBoostFactor(termKey,boost);
	}

	public void addBoostFactor(TechnicalDictionaryKey termKey,float boost) {
		overBoostFactor.addBoostFactor(termKey,boost);
		overBoostFactor.addMustTerm(termKey);
	}
	
	public void zeroingAllBoostFactor() {
		overBoostFactor.zeroingAllBoostFactor();
	}

	public void undoReducedTermFreq() {
		localDictionary.undoReduceTermFreq();
	}
	
	public void addDocFreq(TechnicalDictionaryKey key) {
	}
	
	public int getDocFreq(TechnicalDictionaryTerm term) {
		return term.getTotalFrequency();
	}

	public void setSortedTerms(List<TechnicalDictionaryTerm> sortedTerms) {
		this.sortedTerms = sortedTerms;
	}
	
	public List<TechnicalDictionaryTerm> getSortedTerms() {
		if(sortedTerms!=null)
			return sortedTerms;
		sortedTerms = new ArrayList<TechnicalDictionaryTerm>();
		sortedTerms.addAll(getAllTerms(false));
		return sortedTerms;
	}

	public void setBoostFactors(Map<TechnicalDictionaryKey, Float> boostFactors) {
		this.boostFactors = boostFactors;
	}

	public Map<TechnicalDictionaryKey, Float> getBoostFactors() {
		return boostFactors;
	}

	public boolean contains(TechnicalDictionaryTerm term) {
		for(ProcessedField field : data.values()) {
			if(field.contains(term))
				return true;
		}
		return false;
	}
	
	public List<TechnicalDictionaryTerm> getImportantTerms(int maxSize) {
		List<TechnicalDictionaryTerm> rc = new ArrayList<TechnicalDictionaryTerm>();
		int idx = 0;
		for (TechnicalDictionaryTerm term : getSortedTerms()) {
			if (getItemBoost(term)!=0) {
				if (maxSize>0 && idx>=maxSize)
					break;
				rc.add(term);
				idx++;
			}
		}
		return rc;
	}
	
	public List<TechnicalDictionaryTerm> getImportantTerms() {
		return getImportantTerms(-1);
	}
	
	public boolean isSourceTicket() {
		return isSourceTicket;
	}

	public boolean isMatchModeWithFewTerms() {
		return 
			getMatchMode().equals(MatchMode.match) && 
			getAllTerms(false).size()<=NUM_TERMS_TOBE_REMATCH;	
	}

	@Override
	public void finish() {
	}
	
	protected void init(ITicket originalTicket, 
			   IScoreCalculator scoreCalculator,
			   boolean isSourceTicket,
			   boolean isSupportReduceTermFreq)
	{
		this.isSourceTicket = isSourceTicket;
		this.isSupportReduceTermFreq = isSupportReduceTermFreq;
		this.originalTicket = originalTicket;

		synchronized (lock) {
			if(defaultCalculator == null) {
				defaultCalculator = (IScoreCalculator)RuntimeInfo.getCurrentInfo().getBean("scoreCalculator");;
			}
		}		
		loadDictionary();
		if (scoreCalculator != null)
			this.scoreCalculator = scoreCalculator;
		else
			this.scoreCalculator = defaultCalculator;
	}
	
	public List<String> getOrphanWords()
	{
		return orphanWords;
	}
	
	
	public void setOrphanWords(List<String> orphans)
	{
		orphanWords = orphans;
	}	
};
