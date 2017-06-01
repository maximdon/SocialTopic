package com.softlib.imatch.ticketprocessing;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.softlib.imatch.ITicket;
import com.softlib.imatch.dictionary.ITechnicalTermsContainer;
import com.softlib.imatch.dictionary.TechnicalDictionaryKey;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.score.ScoreExplanation;

public interface IProcessedTicket extends ITechnicalTermsContainer {
	
	public enum MatchMode {
		match,
		rematch,
		all
	};
	
	public boolean isSourceTicket();
	
	public void setMatchMode(MatchMode matchMode);
	
	public MatchMode getMatchMode();
	
	public boolean isMatchModeWithFewTerms();

	public Map<String, ProcessedField> getData();
	
	public ProcessedField getField(String fieldName);

	public String getId();
	
	public String getOriginObjectId();

	public ITicket getOriginalTicket();
	
	public IScoreCalculator getScoreCalculator();
	
	public void setScoreCalculator(IScoreCalculator calculator);
	
	public ScoreExplanation getScoreExplanation();

	public List<TechnicalDictionaryTerm> getTitleTerms();
	
	public List<String> getOrphanWords();
	
	public void setOrphanWords(List<String> orphans);
	
	public boolean isMustTerm(TechnicalDictionaryTerm term);

	public float getItemBoost(TechnicalDictionaryTerm term);
	
	public void overwriteBoostFactor(TechnicalDictionaryKey termKey,float boost);
	
	public void addBoostFactor(TechnicalDictionaryKey termKey,float boost);
	
	public void zeroingAllBoostFactor();
	
	public void undoReducedTermFreq();

	public int getDocFreq(TechnicalDictionaryTerm term);
	
	public int getTermFreq(TechnicalDictionaryTerm term); 

	public void setSortedTerms(List<TechnicalDictionaryTerm> sortedTerms);
	
	public List<TechnicalDictionaryTerm> getSortedTerms();
	
	public void setBoostFactors(Map<TechnicalDictionaryKey, Float> boostFactors);
	
	public Map<TechnicalDictionaryKey, Float> getBoostFactors();
	
	public Collection<TechnicalDictionaryTerm> getAllTerms(boolean addTF);

	public Collection<TechnicalDictionaryTerm> getOneFreqTerms();

	public Collection<TechnicalDictionaryTerm> getZeroFreqTerms();

	public boolean contains(TechnicalDictionaryTerm term);


}