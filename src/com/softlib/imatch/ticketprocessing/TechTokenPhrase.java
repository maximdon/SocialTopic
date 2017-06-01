package com.softlib.imatch.ticketprocessing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.common.StringUtils;
import com.softlib.imatch.dictionary.ITechnicalDictionary;
import com.softlib.imatch.dictionary.ITechnicalTermsContainer;
import com.softlib.imatch.dictionary.SynonymsRelation;
import com.softlib.imatch.dictionary.TechnicalDictionaryKey;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.dictionary.TechnicalTermSource;
import com.softlib.imatch.dictionary.VendorList;
import com.softlib.imatch.dictionary.Wordnet;

public class TechTokenPhrase {

	private static int MAX_TOKENS = 10;
	private String tokenList[] = new String[MAX_TOKENS];
	private String typeList[] = new String[MAX_TOKENS];
	private int index = 0;

	final private String originalTermSource;
	final private String splitTermSource;
	final private String singleSplitTermSource;
	final private String wordnetSplitTermSource;
	final private ITechnicalTermsContainer termsContainer;
	final private NlpBaseTicketProcessStep nlpBaseTicketProcessStep;
	
	private ITechnicalDictionary techDictionary;
	private VendorList vendorList = VendorList.getInstance();
	private SynonymsRelation relation = new SynonymsRelation();
	
	private ITechnicalDictionary getDictionary() {
		if (techDictionary==null) {
			techDictionary = (ITechnicalDictionary) RuntimeInfo.getCurrentInfo().getBean("dictionary");			
		}
		return techDictionary;
	}

	public TechTokenPhrase(String originalTermSource,
						   String splitTermSource,
						   String wordnetSplitTermSource,
						   String singleSplitTermSource,
						   ITechnicalTermsContainer termsContainer, NlpBaseTicketProcessStep nlpBaseTicketProcessStep) {
		this.originalTermSource = originalTermSource;
		this.splitTermSource = splitTermSource;
		this.singleSplitTermSource = singleSplitTermSource;
		this.termsContainer = termsContainer;
		this.nlpBaseTicketProcessStep = nlpBaseTicketProcessStep;
		this.wordnetSplitTermSource = wordnetSplitTermSource;
	}

	public void init() {
		index = 0;
	}

	public void insert(String token,String type) {
		if(!StringUtils.containsAtLeastOneLetter(token))
			return;
		tokenList[index] = token;
		typeList[index] = type;
		if (index<MAX_TOKENS-1)
			index++;
	}

	private List<String> history = new ArrayList<String>();

	public List<String> getHistory() {
		return history;
	}

	private boolean isTermInOurDict(String str) {
		boolean rc = false;
		ITechnicalDictionary dictionary = getDictionary();
		if (dictionary==null)
			return rc;
		rc = dictionary.get(str)!=null;
		return rc;		
	}

	private List<TechnicalDictionaryTerm> getSplitTerm(int fromIdx,int toIdx,TechnicalDictionaryTerm originalTerm) {
		 return getSplitTerm(fromIdx,toIdx,originalTerm,false);
	}

	private List<TechnicalDictionaryTerm> getSplitTerm(int fromIdx,int toIdx,TechnicalDictionaryTerm originalTerm,boolean addRelation) {
		ArrayList<TechnicalDictionaryTerm> addedTerms = new ArrayList<TechnicalDictionaryTerm>();
		String permutation = "";
		for (int idx=fromIdx;idx<=toIdx;idx++) {
			if (permutation.equals(""))
				permutation = tokenList[idx];
			else 
				permutation += " " + tokenList[idx];
		}
		
		if (originalTerm!=null && permutation.toLowerCase().equals(originalTerm.getTermText()))
			return addedTerms;
		
		if (!nlpBaseTicketProcessStep.isEligable(permutation))
			return addedTerms;
		TechnicalDictionaryKey termKey = 
			new TechnicalDictionaryKey(permutation);
		TechnicalDictionaryTerm term = termsContainer.addTerm(termKey);
		addedTerms.add(term);
		TechnicalTermSource originalSource = termsContainer.addSource(originalTermSource);
		TechnicalTermSource source = termsContainer.addSource(splitTermSource);
		if(term != null)
			//For regular split, we can set always the source to NLP Split 
			//and add additional boost for terms from NLP Split source with multiple extraction methods 
			term.setCondTermSource(originalSource,source);
		if (addRelation && originalTerm!=null)
      		//Note this will move the splitted term to the main NLP source but it seems to be OK
			relation.relate(term,originalTerm,"NLP Split");
		return addedTerms;
	}


	public Collection<TechnicalDictionaryTerm> getSplit() {
		List<TechnicalDictionaryTerm> addedTerms = new ArrayList<TechnicalDictionaryTerm>();
		if (index > 5) 
			return addedTerms;

		String originalStr = "";
		for (int idx=0;idx<index;idx++) {
			if (idx!=0)
				originalStr+=" ";
			originalStr+=tokenList[idx];
		}

		if (history.contains(originalStr))
			return addedTerms;
		history.add(originalStr);

		if (!nlpBaseTicketProcessStep.isEligable(originalStr))
			return addedTerms;		
		TechnicalDictionaryKey originalTermKey =
			new TechnicalDictionaryKey(originalStr);
		TechnicalTermSource source;
		if(!originalStr.contains(" "))
			source = termsContainer.addSource(splitTermSource);
		else if(isAllWordsInWordnet(originalStr))
			source = termsContainer.addSource(wordnetSplitTermSource);
		else
			source = termsContainer.addSource(originalTermSource);
		TechnicalDictionaryTerm orgTerm = termsContainer.addTerm(originalTermKey, source);
		addedTerms.add(orgTerm);
		String vendor = vendorList.getTermVendor(originalTermKey.getTermText());
		if(vendor!=null) {
			String textWithoutVendor = originalTermKey.getTermText().substring(vendor.length());
			if (textWithoutVendor.startsWith(" "))
				textWithoutVendor = textWithoutVendor.substring(1);
			if (nlpBaseTicketProcessStep.isEligable(textWithoutVendor)) {
				TechnicalDictionaryKey termWithoutVendorKey =
					new TechnicalDictionaryKey(textWithoutVendor);
				TechnicalTermSource sourceWithoutVendor = termsContainer.addSource(splitTermSource);
				TechnicalDictionaryTerm termWithoutVendorTerm = termsContainer.addTerm(termWithoutVendorKey, sourceWithoutVendor);
				addedTerms.add(termWithoutVendorTerm);
			}
		}
		//TODO if we run this step during match process we need to disable split and extract only original NLP terms
		for (int idx=0;idx<index;idx++) {
			String token = tokenList[idx];

			if (nlpBaseTicketProcessStep.isEligable(token)) {
				TechnicalDictionaryKey termKey = new TechnicalDictionaryKey(token);
				TechnicalTermSource termSource; 
				termSource = termsContainer.addSource(singleSplitTermSource);
				//For single split, if the term belongs to some other source we would like to keep it there,
				//Since Single Split source is treated mostly like trash
				TechnicalDictionaryTerm singleSplitTerm = termsContainer.addTerm(termKey, termSource);
				addedTerms.add(singleSplitTerm);
			}
		}

		boolean removed = false;
		while (index>3 && !isTermInOurDict(tokenList[index-1])) {
			removed = true;
			index--;
		}
		
		addedTerms.addAll(getSplitTerm(0,index-1,orgTerm,removed));
		if (index<3) {
			return addedTerms;
		}
		else if (index==3) {
			addedTerms.addAll(getSplitTerm(0,1,orgTerm));
			addedTerms.addAll(getSplitTerm(1,2,orgTerm));
		} 
		else if (index==4) {
			addedTerms.addAll(getSplitTerm(0,2,orgTerm));
			addedTerms.addAll(getSplitTerm(1,3,orgTerm));
			addedTerms.addAll(getSplitTerm(0,1,orgTerm));
			addedTerms.addAll(getSplitTerm(2,3,orgTerm));
		}
		else if (index==5) {
			addedTerms.addAll(getSplitTerm(0,3,orgTerm));
			addedTerms.addAll(getSplitTerm(1,4,orgTerm));
			addedTerms.addAll(getSplitTerm(0,2,orgTerm));
			addedTerms.addAll(getSplitTerm(2,4,orgTerm));
			addedTerms.addAll(getSplitTerm(0,1,orgTerm));
			addedTerms.addAll(getSplitTerm(3,4,orgTerm));
		}
		else if (index==6) {
			addedTerms.addAll(getSplitTerm(0,4,orgTerm));
			addedTerms.addAll(getSplitTerm(1,5,orgTerm));
			addedTerms.addAll(getSplitTerm(0,3,orgTerm));
			addedTerms.addAll(getSplitTerm(1,4,orgTerm));
			addedTerms.addAll(getSplitTerm(2,5,orgTerm));
			addedTerms.addAll(getSplitTerm(0,2,orgTerm));
			addedTerms.addAll(getSplitTerm(1,3,orgTerm));
			addedTerms.addAll(getSplitTerm(2,4,orgTerm));
			addedTerms.addAll(getSplitTerm(3,5,orgTerm));
			addedTerms.addAll(getSplitTerm(0,1,orgTerm));
			addedTerms.addAll(getSplitTerm(2,3,orgTerm));
			addedTerms.addAll(getSplitTerm(4,5,orgTerm));
		}
		return addedTerms;
	}
		
	private boolean isAllWordsInWordnet(String originalStr) {
		//This function is only important in Extraction mode, in Match mode it doesn't make difference
		if(RuntimeInfo.getCurrentInfo().isWebAppMode())
			return false;
		String[] words = originalStr.split(" ");
		for(String word : words)
			if(!Wordnet.getInstance().containsWord(word))
				return false;
		return true;
	}

	@Override
	public String toString() {
		return StringUtils.join(tokenList, " ").replaceAll(" null", "");
	}	
	
	
};
