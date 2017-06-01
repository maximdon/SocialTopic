package com.softlib.imatch.pattern;

import java.util.ArrayList;
import java.util.List;

import com.softlib.imatch.common.Pair;
import com.softlib.imatch.common.configuration.ConfigurationException;
import com.softlib.imatch.dictionary.TechnicalDictionary;
import com.softlib.imatch.dictionary.TechnicalDictionaryKey;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.dictionary.TechnicalTermSource;
import com.softlib.imatch.dictionary.TechnicalDictionary.Result;
import com.softlib.imatch.ticketprocessing.ExtractTechPatternByNLP;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("anchor")
public class PatternsConfigPattrenPartAnchor {
	
	@XStreamAsAttribute
	private String term;
	
	private TechnicalDictionaryTerm anchor;
	private List<TechnicalDictionaryTerm> anchors;

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public void init(TechnicalDictionary dictionary,String name) {
		boolean first = true;
		anchors = new ArrayList<TechnicalDictionaryTerm>();
		for (String text : term.split(",")) {
			TechnicalDictionaryTerm newTerm = createTerm(dictionary,text);
			if (term!=null) {
				if (first) {
					anchor = newTerm;
					first=false;
				}
				anchors.add(newTerm);
			}
		}
		if (first)
			throw new ConfigurationException("Pattern ["+name+"] Anchor term is not define");

	}

	private TechnicalDictionaryTerm createTerm(TechnicalDictionary dictionary,String text) {
		Pair<TechnicalDictionaryTerm,Result> result = 
			dictionary.addTermAndResult(new TechnicalDictionaryKey(text));
		TechnicalDictionaryTerm term = result.getLeft();
		if (term!=null) {
			if (result.getRight()==Result.Add)
				term.setStatusDeleteByUser();
			TechnicalTermSource source = dictionary.addSource(ExtractTechPatternByNLP.SOURCE_NAME);
			term.setTermSource(source);
		}
		return term;

	}
	
	public TechnicalDictionaryTerm getAnchor() {
		return anchor;
	}

	public List<TechnicalDictionaryTerm> getAnchors() {
		return anchors;
	}


};
