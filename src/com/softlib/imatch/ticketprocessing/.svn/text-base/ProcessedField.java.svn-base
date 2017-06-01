package com.softlib.imatch.ticketprocessing;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.common.Pair;
import com.softlib.imatch.dictionary.ITechnicalDictionary;
import com.softlib.imatch.dictionary.ITechnicalTermsContainer;
import com.softlib.imatch.dictionary.TechnicalDictionary;
import com.softlib.imatch.dictionary.TechnicalDictionaryKey;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.dictionary.TechnicalTermSource;
import com.softlib.imatch.dictionary.TechnicalDictionary.Result;
import com.softlib.imatch.ticketprocessing.IProcessedTicket.MatchMode;

public class ProcessedField implements ITechnicalTermsContainer {
	
	private String fieldName;
	private IProcessedTicket parentTicket;
	
	private TechnicalDictionary localDictionary;

	private List<TechnicalDictionaryTerm> importTerms = new ArrayList<TechnicalDictionaryTerm>();
	private List<TechnicalDictionaryTerm> oneFreqTerms = new ArrayList<TechnicalDictionaryTerm>();
	private List<TechnicalDictionaryTerm> zeroFreqTerms = new ArrayList<TechnicalDictionaryTerm>();

	private List<TechnicalDictionaryKey> freezeKeys = new ArrayList<TechnicalDictionaryKey>();

	private SameLocationTerms sameLocationTerms = new SameLocationTerms();

	public ProcessedField(TechnicalDictionary localDictionary,IProcessedTicket parentTicket,String fieldName) {
		this.fieldName = fieldName;
		init(localDictionary, parentTicket);
	}
	
	void init(TechnicalDictionary localDictionary, IProcessedTicket parentTicket)	
	{
		this.localDictionary = localDictionary;
		this.parentTicket = parentTicket;
	}
	
	public void freezeTerms() {
		freezeKeys = new ArrayList<TechnicalDictionaryKey>();
		for (TechnicalDictionaryTerm term : importTerms)
			freezeKeys.add(term.getTermKey());
		for (TechnicalDictionaryTerm term : oneFreqTerms)
			freezeKeys.add(term.getTermKey());
		for (TechnicalDictionaryTerm term : zeroFreqTerms)
			freezeKeys.add(term.getTermKey());
	}
		
	public void startSession(String fieldName,String ticketId, String sourceName) {
		localDictionary.startSession(fieldName,ticketId,sourceName);
		TechnicalDictionary dictionary = (TechnicalDictionary)RuntimeInfo.getCurrentInfo().getBean("dictionary");
		dictionary.startSession(fieldName,ticketId,sourceName);
	}
	
	public TechnicalDictionaryTerm addTerm(TechnicalDictionaryKey termKey, TechnicalTermSource source) {
		return addTerm(termKey);
	}
	
	public TechnicalDictionaryTerm addTerm(TechnicalDictionaryKey termKey) {
		if (freezeKeys.contains(termKey))
			return null;
		
		Pair<TechnicalDictionaryTerm,Result> termAndResult = 
			localDictionary.addTermAndResult(termKey);
		
		TechnicalDictionaryTerm term = termAndResult.getLeft();
		Result result = termAndResult.getRight();
		
		if (result==Result.Import ||
			result==Result.Add || 
			result==Result.Exist) {
			if (!oneFreqTerms.contains(term) &&
				!zeroFreqTerms.contains(term) )
			importTerms.add(term);
		}
		else if (result==Result.LowFreq) 
			oneFreqTerms.add(term);
		else if (result==Result.ZeroFreq)
			zeroFreqTerms.add(term); 

		return term;
	}
	
	public TechnicalTermSource addSource(String sourceName) {
		return localDictionary.addSource(sourceName);
	}

	public void reduceTermFreq(TechnicalDictionaryTerm term) {
		if (parentTicket.isSourceTicket())
			localDictionary.reduceTermFreq(term);
	}

	public int getTermFreq(TechnicalDictionaryKey termKey) {
		int rc=0;
		for (TechnicalDictionaryTerm term : importTerms) 
			if (term.getTermKey().equals(termKey))
				rc++;
		return rc;
	}

	public void endSession(float boostFactor,List<Float> itemsBoostFactors,boolean isRequired) {
		localDictionary.endSession(0, null, true);
		TechnicalDictionary dictionary = (TechnicalDictionary)RuntimeInfo.getCurrentInfo().getBean("dictionary");
		dictionary.endSession(0, null, true);
	}
	
	public String getFieldName() {
		return fieldName;
	}
	
	public List<TechnicalDictionaryTerm> getTerms() {
		if (parentTicket.isSourceTicket()&& 
			parentTicket.getMatchMode().equals(MatchMode.match) ) {
			return sameLocationTerms.remove(importTerms);
		}
		else
			return importTerms;
		
	}

	public List<TechnicalDictionaryTerm> getOneFreqTerms() {
		if (parentTicket.isSourceTicket()&& 
			parentTicket.getMatchMode().equals(MatchMode.match) ) {
			return sameLocationTerms.remove(oneFreqTerms);
		}
		else {
			return oneFreqTerms;
		}
	}

	public List<TechnicalDictionaryTerm> getZeroFreqTerms() {
		if (parentTicket.isSourceTicket()&& 
				parentTicket.getMatchMode().equals(MatchMode.match) ) {
			return sameLocationTerms.remove(zeroFreqTerms);
		}
		else {
			return zeroFreqTerms;
		}
	}
	
	private class SameLocationTerms { 
		
		private Set<String> possibleTerms;
		
		public List<TechnicalDictionaryTerm> remove(List<TechnicalDictionaryTerm> terms) {
			createStrings();
			List<TechnicalDictionaryTerm> rc = new ArrayList<TechnicalDictionaryTerm>();
			for (TechnicalDictionaryTerm term : terms) {
				String text = term.getTermText();
				if (!possibleTerms.contains(text))
					rc.add(term);
			}
			return rc;
		}
		
		private void createStrings() {
			if (possibleTerms!=null)
				return;
			
			possibleTerms = new HashSet<String>();
			
			TechnicalDictionary dictionary = (TechnicalDictionary)RuntimeInfo.getCurrentInfo().getBean("dictionary");
			TechnicalTermSource nlpVersionSource = 
				dictionary.getSource(ExtractVersionsByNLP.FIELD_NAME); 
	
			List<TechnicalDictionaryTerm> terms = new ArrayList<TechnicalDictionaryTerm>();
			terms.addAll(importTerms);
			terms.addAll(oneFreqTerms);
			terms.addAll(zeroFreqTerms);
			
			for (TechnicalDictionaryTerm term : terms) {
				TechnicalTermSource source = term.getTermSource();
				if (source!=null && source.equals(nlpVersionSource)) {
					int spaceIdx = term.getTermText().lastIndexOf(' ');
					if(spaceIdx > -1) {
						String verToken = term.getTermText().substring(spaceIdx + 1);
						
						if (verToken.contains("."))
							possibleTerms.add(verToken);
						possibleTerms.add(verToken + ".0");
						possibleTerms.add(verToken + ".00");
						possibleTerms.add(verToken + ".0.0");
					}
				}
			}
		}
	
	};
	
	public boolean contains(TechnicalDictionaryTerm term) {
		boolean result = get(term)!=null;
		return result;
	}
	
	private TechnicalDictionaryTerm get(TechnicalDictionaryTerm term) {
		if (importTerms.contains(term))
			return term;
		for(TechnicalDictionaryTerm relation : term.getRelations()) 
			if (importTerms.contains(relation))
				return relation;
		return null;	
	}

	public IProcessedTicket getTicket() {
		return parentTicket;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fieldName == null) ? 0 : fieldName.hashCode());
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
		ProcessedField other = (ProcessedField) obj;
		if (fieldName == null) {
			if (other.fieldName!= null)
				return false;
		} else if (!fieldName.equals(other.fieldName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("%s:%s",fieldName, importTerms);
	}

	public TechnicalDictionary getDictionary() {
		return localDictionary;
	}

	public void addDocFreq(TechnicalDictionaryKey key) {
		localDictionary.addDocFreq(key);
	}

	public void addSubTerms() {
	   List<TechnicalDictionaryTerm> copyOfData = 
			new ArrayList<TechnicalDictionaryTerm>(importTerms);

	   TechnicalDictionary dictionary = (TechnicalDictionary)RuntimeInfo.getCurrentInfo().getBean("dictionary");
		for (TechnicalDictionaryTerm term : copyOfData) {
			Set<TechnicalDictionaryTerm> subTerms = dictionary.getSubTerms(term.getTermKey());
			for (TechnicalDictionaryTerm subTerm : subTerms) {
				addTerm(subTerm.getTermKey());
			}
		}
	}

	@Override
	public void finish() {
	}

};
