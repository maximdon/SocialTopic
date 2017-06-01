package com.softlib.imatch.dictionary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.common.HibernateUtils;
import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.common.SessionMode;
import com.softlib.imatch.common.SourceMngr;
import com.softlib.imatch.common.StringUtils;
import com.softlib.imatch.common.SourceMngr.Type;
import com.softlib.imatch.density.DensityData;

/**
 * This class represents single term in the technical dictionary.
 * The term can be single word term and multi-word term. 
 * Single word terms are stemmed, so computer and computing results the same term (comput) while multi-word terms are not stemmed.
 * Note, all terms are kept in canonical form (all lower case letters) 
 * @author Maxim Donde
 *
 */
@Entity
@Table(name="DICTIONARY_TERMS")
public class TechnicalDictionaryTerm implements Comparable<TechnicalDictionaryTerm>
{
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="term_id")
	private int termId;

	@Column(name="term_frequency")
	private int termFrequency;

	@Column(name="term_status")
	private int termStatus;

	@Column(name="term_description")
	private String description;

	@Column(name="term_generic")
	private boolean isGeneric;

	@OneToMany(fetch=FetchType.LAZY)
	@JoinTable(name="DICTIONARY_TERMS_RELATIONS", joinColumns=@JoinColumn(name="term1_id"), inverseJoinColumns = @JoinColumn( name="term2_id"))
	private List<TechnicalDictionaryTerm> relations;
	//Contains only enabled relations
	@Transient
	private List<TechnicalDictionaryTerm> enabledRelations;
		
	@Transient
	private String termTextDisplay;
	
	@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="term_source_id")
	private TechnicalTermSource termSource;
	@Transient
	private int numTokens = -1;
	@Transient
	private static ITechnicalTermBooster booster = null;
	@Transient
	private static Object lock = new Object(); 
		
	@Transient
	private TechnicalDictionaryFrequency frequency;
	
	@Transient
	private boolean relationsLoaded = false;
	@Transient
	private TechnicalDictionary dictionary;
	@Embedded
	private TechnicalDictionaryKey termKey;
	
	@Transient
	private Set<String> ticketIDs = new HashSet<String>();

	@Column(name="term_tickets")
	private String ticketIDsStr;
	
	@Column(name="term_extraction_methods", nullable=true)
	private String termExtractionMethods = ""; 
	
	@Transient
	private int hashCode = 1;
	@Transient
	private static int loadedTermsCount = 0;
	
	private enum Status {
		Default,
		DeleteByUser,
		EditedByUser,
		LowFrequency
	};

	private static Logger log = Logger.getLogger(TechnicalDictionaryTerm.class);
	//This constructor for hibernate use only! 
	//Don't use it inside your code
    public TechnicalDictionaryTerm() {
    	termStatus = Status.Default.ordinal();
		relations = new ArrayList<TechnicalDictionaryTerm>();
		termFrequency = 0;
		loadedTermsCount ++;
		if(loadedTermsCount % 1000 == 0) {
			LogUtils.debug(log, "%d terms loaded", loadedTermsCount);
		}
	}
	
	/**
	 * Creates technical term for the given text.
	 * If the text doesn't contain spaces (single-word) it is stemmed, otherwise it is saved as is.
	 * @param termText
	 */
	public TechnicalDictionaryTerm(String termText) {
		this();
		setTermText(termText);
	}
	
	public TechnicalDictionaryTerm(String termText, String termDescription, TechnicalTermSource source) {
		this(termText);
		setDescription(termDescription);
		setTermSource(source);
	}

	public TechnicalDictionaryTerm(TechnicalDictionaryKey termKey) 
	{	
		this();
		setTermText(termKey);
	}

	//This constructor is used by hibernate.
	public TechnicalDictionaryTerm(int termId, TechnicalDictionaryKey termKey, int termFrequency, int termStatus, TechnicalTermSource source) {
		this(termKey);
		this.termId = termId;
		this.termFrequency = termFrequency;
		this.termStatus = termStatus;
		this.termSource = source;
	}

	public TechnicalDictionaryKey getTermKey() 
	{		
		return termKey;
	}
	
	public static String toCanonicalForm(String text) {
		return text.trim().toLowerCase();
	}
	
	public void setTermId(int termId) {
		this.termId = termId;
	}

	public int getTermId() {
		return termId;
	}

	private void setTermText(String termText) {
		setTermText(new TechnicalDictionaryKey(termText));
	}

	private void setTermText(TechnicalDictionaryKey termKey) {		
		this.termKey = termKey;		
	}

	public void setTermFrequency(TechnicalDictionaryFrequency frequency) {
		this.frequency = frequency;
	}
	
	public int addFrequency() {
		termFrequency++;
		return termFrequency;
	}

	public int getFrequency() {
		return termFrequency;
	}
	
	public int getTotalFrequency() {
		int freq = getFrequency();
		for(TechnicalDictionaryTerm relatedTerm : getRelations()) {
			freq += relatedTerm.getFrequency();
		}
		return freq;
	}
	
	public void calculateStatusAfterFreq() {
		if (frequency==null)
			return;
		else if (termStatus==Status.Default.ordinal() && frequency.isLow(termFrequency)) 
			termStatus = Status.LowFrequency.ordinal();
		else if (termStatus==Status.LowFrequency.ordinal() && !frequency.isLow(termFrequency)) 
			termStatus = Status.Default.ordinal();
	}
		
	public void setStatusEditByUser() {
		termStatus = Status.EditedByUser.ordinal();
	}

	public void setStatusDeleteByUser() {
		termStatus = Status.DeleteByUser.ordinal();
	}

	public boolean isEnabled() {
		if (termStatus == Status.Default.ordinal() ||
			termStatus == Status.EditedByUser.ordinal())
			return true;
		return false;
	}
	
	public boolean isDeletedByUser() {
		return termStatus == Status.DeleteByUser.ordinal();
	}
	

	/* (non-Javadoc)
	 * @see com.softlib.imatch.dictionary.ITechnicalDictionaryTerm#getTermText()
	 */
	public String getTermText() {
		return termKey.getTermText();
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	/* (non-Javadoc)
	 * @see com.softlib.imatch.dictionary.ITechnicalDictionaryTerm#getTermStemmedText()
	 */
	public String getTermStemmedText() {
		return termKey.getStemmedText();
	}

	public static String addLoaded2WhereString(String string) {
		return (string.equals("") ? "" : string + " and ") +
			   "("+
		       "term_status="+Status.Default.ordinal()+
		       " or " +
		       "term_status="+Status.DeleteByUser.ordinal()+
		       " or " +
		       "term_status="+Status.EditedByUser.ordinal()+
		       ")";
	}

	public int getNumTokens() {
		if(numTokens == -1)
			numTokens = TechnicalDictionary.dictionaryTokenizer().split(getTermText()).length;		
		return numTokens;
	}
	
	public float getBoost() {
		synchronized (lock) {
			if(booster == null) {
				booster = (ITechnicalTermBooster)RuntimeInfo.getCurrentInfo().getBean("termBooster");
				if(dictionary == null)
					dictionary = (TechnicalDictionary)RuntimeInfo.getCurrentInfo().getBean("dictionary");
				booster.init(dictionary);
			}			
		}

		return booster.getBoost(this);
	}

	@SuppressWarnings("unchecked")
	public Collection<TechnicalDictionaryTerm> getRelations() {
		if(enabledRelations == null) {			
			//We need to acquire session here to prevent multiple simultaneous requests (for example from tracker) during synonyms loading by hibernate
			Session session = RuntimeInfo.getCurrentInfo().getHibernate().acquireSession(SessionMode.READONLY);
			enabledRelations = new ArrayList<TechnicalDictionaryTerm>();
			//Filters only enabled terms
			try {
				for(TechnicalDictionaryTerm relatedTerm : relations)
					if(relatedTerm != null && relatedTerm.isEnabled())
						enabledRelations.add(relatedTerm);
			}
			catch(Exception e) {
				if(relationsLoaded || !RuntimeInfo.getCurrentInfo().isWebAppMode())
					//We already tried to load relations for this term
					return enabledRelations;
				//Due to Hibernate bug when accessing Lazy "relations" collection sometimes got Exception
				try {
					LogUtils.debug(log, "Applying hibernate Lazy relation patch for %s", this);
					SQLQuery query = session.createSQLQuery("Select term2_id from dictionary_terms_relations where term1_id=?");
					query.setInteger(0, termId);
					List<Integer> tmpRelations = new ArrayList<Integer>();
					if(dictionary == null)
						dictionary = (TechnicalDictionary)RuntimeInfo.getCurrentInfo().getBean("dictionary");
					try {
						tmpRelations = query.list();
						for(Integer tmpRelationId : tmpRelations) {
							TechnicalDictionaryTerm relatedTerm = dictionary.getTermById(String.valueOf(tmpRelationId)); 
							relations.add(relatedTerm);
							if(relatedTerm != null && relatedTerm.isEnabled())
								enabledRelations.add(relatedTerm);
						}
					}
					catch(Exception e2){
						//Do nothing, indicates no relations for this term. Both lists will remain empty
					}
					LogUtils.debug(log, "Hibernate Lazy relation patch applyied for %s, %d relations loaded", this, enabledRelations.size());
				}
				catch(Exception e1) {
					LogUtils.error(log, "Unable to complete relations for %s due to %s", this, e.getMessage());
				}
			}
			finally {
				relationsLoaded = true;
				RuntimeInfo.getCurrentInfo().getHibernate().releaseSession(session);
			}
		}		
		return enabledRelations;
	}
	
	public Collection<TechnicalDictionaryTerm> getAllRelations() {
		return relations;
	}
	
	public void removeRelation(TechnicalDictionaryTerm relation) {
		boolean remove = false;
		for(TechnicalDictionaryTerm relatedTerm : this.relations) {
			if(relatedTerm.equals(relation)) {
				remove = true;
				
				relatedTerm.relations.remove(this);
				if (relatedTerm.enabledRelations!=null)
					relatedTerm.enabledRelations.remove(this);

				this.relations.remove(relatedTerm);
				if (this.enabledRelations!=null)
					this.enabledRelations.remove(relatedTerm);

				break;
			}
		}
		if (!remove) 
			LogUtils.error(log, "Unable to remove term relation %s", relation);

	}

	public void addRelation(TechnicalDictionaryTerm synonym) {
		if (synonym==this) {
			LogUtils.debug(log, "Relation %s have not been added : Relation to itself ", synonym);
			return;
		}	
		if (!isEnabled()) {
			LogUtils.debug(log, "Relation %s have not been added : Term have low frequency ", synonym);
			return;
		}
		synchronized (this) {
			if (!relations.contains(synonym)) {
				relations.add(synonym);
				if (enabledRelations != null && synonym.isEnabled())
					enabledRelations.add(synonym);
				//Make the same source
				if(synonym.getTermSource() != null)
					setTermSource(synonym.getTermSource(), synonym.getTermSource().getsourceName() + "(relation '" + synonym + "')");
				if(getTermSource() != null)
					synonym.setTermSource(getTermSource(), getTermSource().getsourceName() + "(relation '" + this + "')");
			}
		}
	}

	/**
	 * Changes this term source to the new source without comparison between the new source and the current source if the conditionSource equals to the current source.
	 * In other words, forces changing of the current source to the new source if the condition source equals to the current source
	 * #see TechicalTermSource.compareTo
	 * @param newTermSource
	 */
	public void setCondTermSource(TechnicalTermSource conditionSource,
							      TechnicalTermSource newTermSource) 
	{
		setCondTermSource(conditionSource, newTermSource, newTermSource.getsourceName());
	}

	/**
	 * Changes this term source to the new source without comparison between the new source and the current source if the conditionSource equals to the current source.
	 * In other words, forces changing of the current source to the new source if the condition source equals to the current source
	 * In addition adds given extraction method to the list of term extraction methods
	 * #see TechicalTermSource.compareTo
	 * @param newTermSource
	 */
	public void setCondTermSource(TechnicalTermSource conditionSource,
								  TechnicalTermSource newTermSource,
								  String extractionMethod)
	{
		if(termSource == null || termSource.getSourceId() == 44 || newTermSource.getSourceId() == 44) {
			setTermSource(newTermSource);
			return;
		}
		if (termSource.equals(conditionSource) && !termSource.getsourceName().equals("SoftLibTerms")) {			
			termSource = newTermSource;
			addExtractionMethod(extractionMethod);
		}
		else
			setTermSource(newTermSource);
	}
	
	/**
	 * Changes this term source to the given source if the new source is "greater than" current source (according to source.compare)
	 * #see TechicalTermSource.compareTo
	 * @param newTermSource
	 */
	public void setTermSource(TechnicalTermSource newTermSource) {
		if (newTermSource==null)
			return;
		setTermSource(newTermSource, newTermSource.getsourceName());
	}

	/**
	 * Changes this term source to the given source if the new source is "greater than" current source (according to source.compare)
	 * In addition adds given extraction method to the list of term extraction methods
	 * #see TechicalTermSource.compareTo
	 * @param newTermSource
	 */
	public void setTermSource(TechnicalTermSource newTermSource, String extractionMethod) {
		if (newTermSource==TechnicalTermSource.getDummySource())
			return;
		if (termSource==null) {
			termSource = newTermSource;
			addExtractionMethod(extractionMethod);
			return;
		}
		//Special case for source 44.
		//In general the idea is that: terms that were identified by 
		//non english words only or non english words + nlp single split are most probably garbage and should remain in Non English Words (they will be automatically deleted by sql script)
		//Terms that were found by non english words + nlp single split + something else (usually some regex source) are good and we put them in nlp single split and use them for proximity
		if(termSource.getSourceId() == TechnicalTermSource.NON_ENGLISH_WORDS_ID) {
			if(newTermSource.getSourceId() == TechnicalTermSource.NLP_SINGLE_SPLIT_ID) {
				//The source remains 44, but the term is marked 
				addExtractionMethod(extractionMethod);
				return;
			}
			else {
				if(termExtractionMethods != null && termExtractionMethods.contains(TechnicalTermSource.NLP_SINGLE_SPLIT_NAME)) {
					if(dictionary == null)
						dictionary = (TechnicalDictionary)RuntimeInfo.getCurrentInfo().getBean("dictionary");
					termSource = dictionary.getSource(TechnicalTermSource.NLP_SINGLE_SPLIT_ID);
					addExtractionMethod(extractionMethod);
					return;
				}
				else {
					termSource = newTermSource;
					addExtractionMethod(extractionMethod);
					return;
				}
			}
		}
		else if(newTermSource.getSourceId() == TechnicalTermSource.NON_ENGLISH_WORDS_ID) {
			if(termSource.getSourceId() == TechnicalTermSource.NLP_SINGLE_SPLIT_ID) {
				if(termExtractionMethods.equals(TechnicalTermSource.NLP_SINGLE_SPLIT_NAME)) {
					termSource = newTermSource;
					addExtractionMethod(extractionMethod);
					return;
				}
				else {
					//The source remains 103
					//Just mark but don't move
					addExtractionMethod(extractionMethod);
					return;
				}
			}
			else {
				//Just mark but don't move
				addExtractionMethod(extractionMethod);
				return;
			}
		}
		
		if (termSource.compareTo(newTermSource)<0) {
			termSource = newTermSource;
			addExtractionMethod(extractionMethod);
		}
		else
			//Even if this source is not set we want to add to the list of extraction methods for this term
			addExtractionMethod(extractionMethod);
	}
	
	private void addExtractionMethod(String extractionMethod)
	{
		int parIndex = extractionMethod.indexOf('(');
		String extractionMethodName;
		if(parIndex > -1)
			extractionMethodName = extractionMethod.substring(0, parIndex);
		else
			extractionMethodName = extractionMethod;
		if(termExtractionMethods.contains(extractionMethodName))
			return;
		if(!termExtractionMethods.equals(""))
			termExtractionMethods += ", ";
		termExtractionMethods += extractionMethod;
	}

	public TechnicalTermSource getTermSource() {
		return termSource;
	}
	
	public String getTermExtractionMethods()
	{
		return termExtractionMethods;
	}

	public int compareTo(TechnicalDictionaryTerm otherTerm) {
		return termKey.compareTo(otherTerm.getTermKey());
	}

	public void removeTermsRelations() {
		relations = new ArrayList<TechnicalDictionaryTerm>();
		enabledRelations = null;
	}

	@Override
	public int hashCode() {
		if(hashCode == 1) {
			synchronized (this) {
				hashCode = termKey.hashCode();
			}			
		}
		return hashCode;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TechnicalDictionaryTerm other = (TechnicalDictionaryTerm) obj;		
		return termKey.equals(other.termKey);
	}

	@Override
	public String toString() {
		return termKey.toString();
	}

	public String toDisplayString() {
		String rc = getTermText();
		if (SourceMngr.isSource(termSource,Type.Complex))
			rc = rc.replaceAll(" 0 ", " ... ");
		return rc;
	}
	
	public void addSourceTicket(String id) {
		synchronized (this) {
			if(!ticketIDs.contains(id))
				addFrequency();
			ticketIDs.add(id);
		}
	}

	public void prepareSave() {
		if(!ticketIDs.isEmpty()) {
			ticketIDsStr = StringUtils.join(ticketIDs, ",");
		}
	}
	
	public Set<String> getSourceTickets() {
		if(ticketIDs.isEmpty() && !StringUtils.isEmpty(ticketIDsStr)) {
			String[] ticketIDsArr = ticketIDsStr.split(","); 
			for(String ticketId : ticketIDsArr)
				ticketIDs.add(ticketId);
		}
		return ticketIDs;
	}

	public void setAbsoluteFrequency(int frequency) {
		this.termFrequency = frequency;
	}
	
	public boolean isGeneric() {
		return isGeneric;
	}
	
	public void setTermTextDisplay(String termTextDisplay) {
		this.termTextDisplay = termTextDisplay;
	}

	public String getTermTextDisplay() {
		if (SourceMngr.isSource(termSource,Type.Complex))
			termTextDisplay = getTermText().replaceAll(DensityData.SEPERATOR, " ... ");
		else
			termTextDisplay = getTermText();
		return termTextDisplay;
	}

	public Boolean getIsGeneric() {
		return isGeneric;
	}

	public void setIsGeneric(Boolean isGeneric) {
		this.isGeneric = isGeneric;
	}

	public void notifyStemmingException(String oldStemming, String stemm) {
		termKey.notifyStemmingException(oldStemming, stemm);
	}

	public Boolean hasWord(String word)
	{
		String[] words = getTermStemmedText().split(" ");
		for (String termword : words) {
			if (termword.equalsIgnoreCase(PorterStemmer.stem(word)))
				return true;
			
		}
		return false;
	}	
	
};
