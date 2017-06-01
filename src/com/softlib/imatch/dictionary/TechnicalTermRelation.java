package com.softlib.imatch.dictionary;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.common.SourceMngr;
import com.softlib.imatch.common.TracerFile;
import com.softlib.imatch.common.TracerFileLast;
import com.softlib.imatch.density.DensityData;
import com.softlib.tools.dictionaryparsers.KnownSynonymRelation;

/**
 * This class represents technical terms relation.
 * Example of such relation could be synonym relation where two words are synonyms according to the language.
 * Another example is definition relation which is primarily applicable for abbreviations.
 * Third example is computer <-> desktop relation which is custom relation.
 * Relations have rank which indicates how strong the relation is. For example, definition relation is very strong
 * (meaning two terms containing this relation can be safely substituted). 
 * On the other hand, custom relation is weaker, meaning while replacing one term by another we may need to reduce the score for replaced word  
 * @author Maxim Donde
 *
 */
@Entity
@Table(name="DICTIONARY_RELATIONS")
public class TechnicalTermRelation implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 618780277779678992L;
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="relation_id")
	private int relationId;
	
	@Column(name="relation_name")
	private String relationName;

	@Column(name="relation_rank")
	private int relationRank;

	@Transient
	private static TracerFile traceFile = 	TracerFileLast.create(TracerFileLast.Relations,"relations",false);

	@Transient
	private Set<String> enabledRulesForTrace = new HashSet<String>(); 
	
	@Transient	
	protected ITechnicalDictionary dictionary;
	
	public TechnicalTermRelation() {	
	}
	
	public TechnicalTermRelation(ITechnicalDictionary dictionary) {
		this.dictionary = dictionary;
	}

	public void setRelationId(int relationId) {
		this.relationId = relationId;
	}

	public int getRelationId() {
		return relationId;
	}

	public void setRelationName(String relationName) {
		this.relationName = relationName;
	}

	public String getRelationName() {
		return relationName;
	}

	public void setRelationRank(int relationRank) {
		this.relationRank = relationRank;
	}

	public int getRelationRank() {
		return relationRank;
	}
	
	public void relateWithContaining(TechnicalDictionaryTerm term1, TechnicalDictionaryTerm term2, String relationRule)
	{
		relate(term1, term2, relationRule);
		//relate with containing disabled due to performance issues
//		if(dictionary == null)
//			return;
//		if(SourceMngr.isSource(term1.getTermSource(), SourceMngr.Type.Complex) || SourceMngr.isSource(term1.getTermSource(), SourceMngr.Type.Compound) || SourceMngr.isSource(term1.getTermSource(), SourceMngr.Type.Modified) || term1.getTermExtractionMethods().contains("Proximity"))
//			//No need to change contains relation on complex terms
//			return;
//		if(SourceMngr.isSource(term2.getTermSource(), SourceMngr.Type.Complex) || SourceMngr.isSource(term2.getTermSource(), SourceMngr.Type.Compound) || SourceMngr.isSource(term2.getTermSource(), SourceMngr.Type.Modified) || term2.getTermExtractionMethods().contains("Proximity"))
//			//No need to change contains relation on complex terms
//			return;
//		List<TechnicalDictionaryTerm> containingTerms1 = dictionary.findContainingTerms(term1);
//		if(containingTerms1.size() == 0)
//			//No containing terms for the first term, no need to continue
//			return;
//		List<TechnicalDictionaryTerm> containingTerms2 = dictionary.findContainingTerms(term2);
//		if(containingTerms2.size() == 0)
//			//No containing terms for the second term, no need to continue
//			return;
//		List<KnownSynonymRelation> knownSynonyms1 = build(term1, containingTerms1);
//		List<KnownSynonymRelation> knownSynonyms2 = build(term2, containingTerms2);
//		for(KnownSynonymRelation knownSynonym1 : knownSynonyms1) {
//			int idx = knownSynonyms2.indexOf(knownSynonym1);
//			if(idx > -1) {
//				KnownSynonymRelation knownSynonym2 = knownSynonyms2.get(idx);
//				relate(knownSynonym1.getTerm(), knownSynonym2.getTerm(), "Contains related terms");
//			}			
//		}
	}
	
	private List<KnownSynonymRelation> build(TechnicalDictionaryTerm term,
			List<TechnicalDictionaryTerm> containingTerms) {
		List<KnownSynonymRelation> result = new ArrayList<KnownSynonymRelation>();
		for(TechnicalDictionaryTerm containingTerm : containingTerms) {
			String termStemmedText = containingTerm.getTermStemmedText();
			String relText = termStemmedText.replace(term.getTermStemmedText(), "").trim();
			KnownSynonymRelation rel = new KnownSynonymRelation(relText, containingTerm, dictionary);
			result.add(rel);
		}
		return result;
	}

	public void relate(TechnicalDictionaryTerm term1, TechnicalDictionaryTerm term2, String relationRule)
	{
		term1.addRelation(term2);
		logRelation(term1, term2, relationRule);
	}
	
	protected void logRelation(TechnicalDictionaryTerm term1, TechnicalDictionaryTerm term2, String relationRule) {
		if (traceFile.isActive() && enabledRulesForTrace.contains(relationRule)) {
			traceFile.write(String.format("Relation between '%s' and '%s' by '%s'", term1.getTermText(), term2.getTermText(), relationRule));
		}
	}

	public boolean isRelated(TechnicalDictionaryTerm term1,
			TechnicalDictionaryTerm term2) {
		if(term1.equals(term2))
			return true;
		return term1.getRelations().contains(term2);
	}
	
	public void enableTraceForRule(String rule)
	{
		enabledRulesForTrace.add(rule);
	}
}
