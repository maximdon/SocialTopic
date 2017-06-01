package com.softlib.imatch.dictionary;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * This class represents term source. 
 * Different terms loaded to the dictionary from different sources like FOLDOC, Wikipedia, WordNet and other.
 * Tracking term source is important for maintenance and upgrades. 
 * @author Maxim Donde
 *
 */
@Entity
@Table(name="DICTIONARY_SOURCES")
public class TechnicalTermSource implements Serializable , Comparable<TechnicalTermSource>{
	/**
	 * 
	 */
	private static final long serialVersionUID = -9140325613854547960L;

	public static final int SOFTLIB_TERMS_ID = 101;	
	public static final int NLP_SINGLE_SPLIT_ID = 103;
	public static final int NON_ENGLISH_WORDS_ID = 44;
	public static final int ENGLISH_WORDS_ID = 47;
	public static final String NLP_SINGLE_SPLIT_NAME = "NLP NNP Tokens Single Split";
	public static final String NLP_VERSION_SOURCE_NAME = "NLP Version Tokens";
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="source_id")
	private int sourceId;
	
	@Column(name="source_name")
	private String sourceName;

	@Column(name="source_boost")
	private float sourceBoost;
	
	@Column(name="source_association_rank")
	private int sourceAssociationRank;

	@Column(name="source_is_recalc_freq")
	private Boolean isRecalculateFrequencyEnabled;
	
	@Column(name="source_supported_relations_algs", nullable=true)
	//Default value allows all supported algorithms (for old solutions without this field set)
	private Integer supportedRelationsAlgorithms;
	
	static private TechnicalTermSource dummy = 
		new TechnicalTermSource("dummy",new Float(0), 0);
	
	
	public TechnicalTermSource() {
	}
	
	public TechnicalTermSource(String name) {
		this(name,null);
	}
	
	public TechnicalTermSource(String name, Float boost) {
		this(name,boost,0);
	}
	
	public TechnicalTermSource(String name, Float boost, int associationRank) {
		sourceName = name;
		if (boost!=null)
			sourceBoost = boost;
		sourceAssociationRank = associationRank;
	}

	static public TechnicalTermSource getDummySource() {
		return dummy;
	}
	
	public void setSourceId(int sourceId) {
		this.sourceId = sourceId;
	}

	public int getSourceId() {
		return sourceId;
	}

	public void setsourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	public String getsourceName() {
		return sourceName;
	}

	public void setSourceBoost(float sourceBoost) {
		this.sourceBoost = sourceBoost;
	}

	public float getSourceBoost() {
		return sourceBoost;
	}

	public int getAssociationRank() {
		return sourceAssociationRank;
	}
	
	public void setAssociationRank(int rank) {
		sourceAssociationRank = rank;
	}
	
	public boolean isRecalculateFrequencyEnabled() {
		if(isRecalculateFrequencyEnabled == null)
			return true;
		return isRecalculateFrequencyEnabled;
	}
	
	public void setRecalculateFrequencyEnabled(boolean recalcEnabled) {
		isRecalculateFrequencyEnabled = recalcEnabled;
	}
	
	public int getSupportedRelationsAlgs()
	{
		if(supportedRelationsAlgorithms == null)
			supportedRelationsAlgorithms = 1023;
		return supportedRelationsAlgorithms;
	}
	
	public void setSupportedRelationsAlgs(int supportedAlgs)
	{
		supportedRelationsAlgorithms = supportedAlgs;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + sourceId;
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
		TechnicalTermSource other = (TechnicalTermSource) obj;
		if (sourceId != other.sourceId)
			return false;
		return true;
	}
		
	public int compareTo(TechnicalTermSource source) {
		if (source.sourceName==null && sourceName==null)
			return 0;
		if (source.sourceName==null)
			return 1;
		if (sourceName==null)
			return -1;
		
		if (source.sourceName.equals(sourceName))
			return 0;

		//Check if other source is softlib
		if (sourceName.equals("SoftLibTerms"))
			return 1;
		
		//First compare by association rank
		int associationRankCompare = Integer.valueOf(getAssociationRank()).compareTo(Integer.valueOf(source.getAssociationRank()));
		if(associationRankCompare != 0)
			return associationRankCompare;
		
		//Compare by source boost
		return Float.compare(getSourceBoost(), source.getSourceBoost());
	}

	@Override
	public String toString() {
		return sourceName;
	}
}
