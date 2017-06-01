package com.softlib.imatch.pattern;


import com.softlib.imatch.common.configuration.ConfigurationException;
import com.softlib.imatch.dictionary.TechnicalDictionary;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("part")
public class PatternsConfigPattrenPart {

	@XStreamAsAttribute
	private boolean isSubject;
	@XStreamAsAttribute
	private boolean isPreSubject;
	@XStreamAlias("anchor")
	private PatternsConfigPattrenPartAnchor partAnchor;
	@XStreamAlias("predicate")
	private PatternsConfigPattrenPartPredic partPredic;

	public boolean isSubject() {
		return isSubject;
	}

	public void setSubject(boolean isSubject) {
		this.isSubject = isSubject;
	}

	public boolean isPreSubject() {
		return isPreSubject;
	}

	public void setPreSubject(boolean isPreSubject) {
		this.isPreSubject = isPreSubject;
	}

	public PatternsConfigPattrenPartAnchor getPartAnchor() {
		return partAnchor;
	}

	public void setPartAnchor(PatternsConfigPattrenPartAnchor partAnchor) {
		this.partAnchor = partAnchor;
	}

	public PatternsConfigPattrenPartPredic getPartPredic() {
		return partPredic;
	}

	public void setPartPredic(PatternsConfigPattrenPartPredic partPredic) {
		this.partPredic = partPredic;
	}

	public void init(TechnicalDictionary dictionary,String name) {
		if (isSubject && isPreSubject)
			throw new ConfigurationException("Pattern ["+name+"] Part can't be subject and preSubject");
		partAnchor.init(dictionary,name);
		partPredic.init(dictionary);
	}

	public boolean isAllow(TechnicalDictionaryTerm term) {
		if (term==null)
			return false;
		if (term.equals(partAnchor.getAnchor()))
			return false;
		return partPredic.isAllow(term);
	}

	public TechnicalDictionaryTerm getAnchor() {
		return partAnchor.getAnchor();
	}
	
};
