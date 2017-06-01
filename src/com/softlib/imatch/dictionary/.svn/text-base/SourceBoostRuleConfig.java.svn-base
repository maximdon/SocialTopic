package com.softlib.imatch.dictionary;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("rule")
public class SourceBoostRuleConfig {

	@XStreamAsAttribute
	private String sourceName;
	@XStreamAsAttribute
	private String otherSource;
	@XStreamAsAttribute
	private String text;
	@XStreamAsAttribute
	private Boolean includeText;
	@XStreamAsAttribute

	private float addBoost;

	private List<TechnicalTermSource> otherSourcesList;
	private TechnicalTermSource source;
	private boolean allSources;
	
	public void init(TechnicalDictionary dictionary) {

		source = dictionary.addSource(sourceName);
		otherSourcesList = new ArrayList<TechnicalTermSource>();
		
		if (otherSource!=null) {
			if (otherSource.equals("*"))
				allSources = true;
			else
				for (String sourceName : otherSource.split(",")) {
					if (sourceName.equals(""))
						continue;
					TechnicalTermSource source = dictionary.addSource(sourceName);
					otherSourcesList.add(source);
				}
		}
		if (includeText==null)
			includeText = true;

	}

	private boolean checkSource(TechnicalDictionaryTerm term) {
		TechnicalTermSource termSource = term.getTermSource();
		if (termSource!=null && termSource.equals(source))
			return true;
		return false;
	}
	
	private boolean checkOtherSource(TechnicalDictionaryTerm term) {
		String extractionMethods = term.getTermExtractionMethods();
		if (allSources && extractionMethods.contains(","))
			return true;
		for (TechnicalTermSource src : otherSourcesList ) {
			if (!extractionMethods.contains(src.getsourceName()))
				return false;
		}
		return true;
	}
	
	private boolean checkText(TechnicalDictionaryTerm term) {
		if (text==null || text.equals(""))
			return true;
		String termText = term.getTermText();
		if (termText.contains(text))
			return includeText;
		return !includeText;
	}

	public float getAddBoost(TechnicalDictionaryTerm term) {
		if (checkSource(term) &&
			checkOtherSource(term) &&
			checkText(term)) 
			return addBoost;
		return 0;
	}
}
