package com.softlib.imatch.pattern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.softlib.imatch.common.configuration.ConfigurationException;
import com.softlib.imatch.dictionary.TechnicalDictionary;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("pattern")
public class PatternsConfigPattren {

	static public final String DEFAULT_SOURCE_NAME = "Patterns";

	@XStreamImplicit(itemFieldName="part")
	private List<PatternsConfigPattrenPart> parts;
	@XStreamAsAttribute
	private boolean subjectEmptySplit;
	@XStreamAsAttribute
	private boolean subjectCanBeBehind;
	@XStreamAsAttribute
	private String source;
	@XStreamAsAttribute
	private String name;
	@XStreamAsAttribute
	private int maxWordsInPart;


	private PatternsConfigPattrenPart preSubjectPart;
	private PatternsConfigPattrenPart subjectPart;
	private List<PatternsConfigPattrenPart> otherParts;
	
	
	private Map<TechnicalDictionaryTerm,PatternsConfigPattrenPart> configByTerm;


	public int getMaxWordsInPart() {
		return maxWordsInPart;
	}

	public void setMaxWordsInPart(int maxWordsInPart) {
		this.maxWordsInPart = maxWordsInPart;
	}

	public boolean isSubjectEmptySplit() {
		return subjectEmptySplit;
	}

	public void setSubjectEmptySplit(boolean subjectEmptySplit) {
		this.subjectEmptySplit = subjectEmptySplit;
	}

	public boolean isSubjectCanBeBehind() {
		return subjectCanBeBehind;
	}

	public void setSubjectCanBeBehind(boolean subjectCanBeBehind) {
		this.subjectCanBeBehind = subjectCanBeBehind;
	}

	public String getSource() {
		if (source==null)
			return DEFAULT_SOURCE_NAME;
		return source;
	}

	public void setSource(String source) {
		if (source==null)
			this.source = DEFAULT_SOURCE_NAME;
		else
			this.source = source;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<PatternsConfigPattrenPart> getParts() {
		return parts;
	}

	public void setParts(List<PatternsConfigPattrenPart> parts) {
		this.parts = parts;
	}
	
	public void init(TechnicalDictionary dictionary) {
		
		if (maxWordsInPart==0)
			maxWordsInPart=4;
		if (maxWordsInPart<2)
			maxWordsInPart=2;
		
		for (PatternsConfigPattrenPart part : parts)
			part.init(dictionary,name);
		
		otherParts = new ArrayList<PatternsConfigPattrenPart>();
		configByTerm = new HashMap<TechnicalDictionaryTerm, PatternsConfigPattrenPart>();
		
		for (PatternsConfigPattrenPart part : parts) {
			if (part.isPreSubject()) {
				if (preSubjectPart!=null)
					throw new ConfigurationException("Pattern ["+name+"] More the one preSubject part");	
				preSubjectPart = part;
			}
			else if (part.isSubject()) {
				if (subjectPart!=null)
					throw new ConfigurationException("Pattern ["+name+"] More the one subject part");	
				subjectPart = part;
			}
			else {
				otherParts.add(part);
			}
		}

		if (subjectPart==null)
			throw new ConfigurationException("Pattern ["+name+"] There is no subject part");

		parts.clear();
		addPart(subjectPart);
		for (PatternsConfigPattrenPart part : otherParts )
			addPart(part);
		if (preSubjectPart!=null)
			addPart(preSubjectPart);
			
	}

	private void addPart(PatternsConfigPattrenPart part) {
		configByTerm.put(part.getAnchor(),part);
		parts.add(part);
	}
	
	public PatternsConfigPattrenPart getSubjectPart() {
		return subjectPart;
	}
		
	public List<TechnicalDictionaryTerm> getTerms() {
		List<TechnicalDictionaryTerm> terms = new ArrayList<TechnicalDictionaryTerm>();
		
		if (preSubjectPart!=null)
			terms.add(preSubjectPart.getAnchor());
		if (subjectPart!=null)
			terms.add(subjectPart.getAnchor());
		for (PatternsConfigPattrenPart part : otherParts)
			terms.add(part.getAnchor());

		return terms;
	}
	
	public PatternsConfigPattrenPart getPartConfig(TechnicalDictionaryTerm term) {
		return configByTerm.get(term);
	}

};
