package com.softlib.imatch.dictionary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.softlib.imatch.MatcherException;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.common.ITokenizer;
import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.common.Pair;
import com.softlib.imatch.common.SessionMode;
import com.softlib.imatch.common.SimpleCommaTokenizer;
import com.softlib.imatch.common.SimpleTokenizer;
import com.softlib.imatch.common.SourceMngr;
import com.softlib.imatch.common.StringUtils;
import com.softlib.imatch.common.SubTightListCombinations;
import com.softlib.imatch.common.TracerFile;
import com.softlib.imatch.common.TracerFileLast;
import com.softlib.imatch.common.SourceMngr.Type;
import com.softlib.imatch.common.configuration.IConfigurationResource;
import com.softlib.imatch.common.configuration.IConfigurationResourceLoader;
import com.softlib.imatch.common.progressnotifier.DefaultProgressNotifier;
import com.softlib.imatch.common.progressnotifier.IProgressNotificationListener;
import com.softlib.imatch.common.progressnotifier.IProgressNotifier;
import com.softlib.imatch.distance.TermsByPositions;
import com.softlib.imatch.matcher.ITicketsRepository;
import com.softlib.imatch.matcher.MatchCandidate;
import com.softlib.imatch.matcher.lucene.LuceneTicketsRepository;
import com.softlib.imatch.model.IComparator;
import com.softlib.imatch.proximity.ProximityConfig;
import com.softlib.imatch.proximity.ProximityConfigRule;
import com.softlib.imatch.proximity.ProximityData;
import com.softlib.imatch.ticketprocessing.ExtractVersionsByNLP;
import com.softlib.imatch.ticketprocessing.ProcessedTicketWritter;
import com.softlib.imatch.ticketprocessing.VersionsTechToknes;

//TODO refactor: split to ReadonlyDictionary / WritableDictionary ReadonlyTerm / WritableTerm
/**
 * This class represents a default implementation of the dictionary.
 * Maintains the list of all terms and allows fast lookup of each term
 */
public class TechnicalDictionary implements ITechnicalDictionary , ITechnicalTermsContainer, IProgressNotifier
{	
	private ThreadSafeTerms terms;
	private ThreadSafeTerms deletedTerms;
	private TermsNGrams termNGrams = new TermsNGrams();
	private ConcurrentMap<String,TechnicalTermSource> sources = new ConcurrentHashMap<String, TechnicalTermSource>();
	private TechnicalDictionaryFrequency frequency = new TechnicalDictionaryFrequency();
	private ITicketsRepository repository;
	
	private ThreadLocal<DictionarySession> idSession = new ThreadLocal<DictionarySession>();
	private ConcurrentMap<TechnicalDictionaryKey,Integer> termFreq = new ConcurrentHashMap<TechnicalDictionaryKey,Integer>();

	private ReduceTermFreq reduceTermFreq = new ReduceTermFreq();

	private boolean loaded = false;
	
	private List<ISpecialTextHandler> textCleaners;
	private static ITokenizer dictionaryTokenizer = 
		new SimpleCommaTokenizer(new char[] {' ','=','\n','\t','?','!',';','&'});
	private static Logger log = Logger.getLogger(TechnicalDictionary.class);
	private static TracerFile traceFile = 
		TracerFileLast.create(TracerFileLast.WordnetTerms,"result",false);
	private static List<String> extractedWordnetWords = new ArrayList<String>();
	
	private IProgressNotifier notifier = new DefaultProgressNotifier();
	
	public enum Result {
		Add,
		Exist,
		Deleted,
		Import,
		LowFreq,
		ZeroFreq,
		NotFound;
	};

	private class DictionarySession {
		private final TechnicalTermSource source;
		private String id;

		public List<TechnicalDictionaryKey> terms;
		
		public DictionarySession(TechnicalTermSource source, String id) {
			this.source = source;
			this.id = id;
			terms = new ArrayList<TechnicalDictionaryKey>();
		}

		public TechnicalTermSource getSource() {
			return source;
		}

		public String getId() {
			return id;
		}
		
	};
	
	private class ReduceTermFreq {
		
		private List<TechnicalDictionaryTerm> reducedTerms =
			new ArrayList<TechnicalDictionaryTerm>();
		private List<TechnicalDictionaryTerm> removedTerms =
			new ArrayList<TechnicalDictionaryTerm>();
		
		public void reduce(TechnicalDictionaryTerm term) {
			if (!supportReduceTermFreq())
				return;
			Integer freq = termFreq.get(term.getTermKey());
			if (freq==null || freq==0)
				return;
			if (freq==1) {
				TechnicalDictionaryTerm removeTerm = 
					terms.remove(term.getTermKey());
				removedTerms.add(removeTerm);
			}
			else {
				freq--;
				termFreq.put(term.getTermKey(), freq);
				reducedTerms.add(term);
			}
		}
		
		public void undoReduce() {
			if (!supportReduceTermFreq())
				return;
			for (TechnicalDictionaryTerm term : removedTerms) {
				terms.put(term.getTermKey(),term);
			}
			for (TechnicalDictionaryTerm term : reducedTerms) {
				Integer freq = termFreq.get(term.getTermKey());
				freq++;
				termFreq.put(term.getTermKey(), freq);				
			}
			
		}
		
	};

	public TechnicalDictionary() {
		this(true);
	}
	
	public TechnicalDictionary(boolean register) {
		terms = new ThreadSafeTerms();
		idSession.set(null);
		deletedTerms = new ThreadSafeTerms();
	}
	
	public TechnicalDictionary(IConfigurationResourceLoader loader)
	{
		this();
	}
	
	public void setTextCleaners(List<ISpecialTextHandler> cleaners)
	{
		textCleaners = cleaners;
	}
	
	public List<ISpecialTextHandler> getSpecialTextHandlers()
	{
		return textCleaners;
	}
	
	public static ITokenizer dictionaryTokenizer() 
	{
		return dictionaryTokenizer;
	}
	
	public TechnicalDictionary getDictionary() {
		return this;
	}
	/* (non-Javadoc)
	 * @see com.softlib.imatch.dictionary.ITechnicalDictionary#getTermsCount()
	 */
	public int getTermsCount()
	{
		return terms.size();
	}
	
	public boolean contains(TechnicalDictionaryKey termKey) {
		if (terms.containsKey(termKey))
			return true;
		termKey.clean();
		return terms.containsKey(termKey);
	}
	
	/* 
	 * Returns the term with the given text or null if such term not found
	 * @see com.softlib.imatch.dictionary.ITechnicalDictionary#find(java.lang.String)
	 */	
	public TechnicalDictionaryTerm get(String text) {
		TechnicalDictionaryKey termKey = new TechnicalDictionaryKey(text);
		return get(termKey);
	}
	
	public TechnicalDictionaryTerm get(String text,boolean includeLowFreq) {
		TechnicalDictionaryKey termKey = new TechnicalDictionaryKey(text);
		return get(termKey,includeLowFreq);
	}

	public TechnicalDictionaryTerm get(TechnicalDictionaryKey termKey) {		
		return get(termKey,false);
	}
	
	public TechnicalDictionaryTerm get(TechnicalDictionaryKey termKey,boolean includeLowFreq) {		
		TechnicalDictionaryTerm term = getTerm(termKey, includeLowFreq);
		if (term==null) {
			termKey.clean();
			term = getTerm(termKey, includeLowFreq);
		}
		return term;
	}
	
	private TechnicalDictionaryTerm getTerm(TechnicalDictionaryKey termKey,boolean includeLowFreq) {
		TechnicalDictionaryTerm term = terms.get(termKey);
		if (term==null)
			return null;
		if (!includeLowFreq && term.getFrequency()<2)
			return null;
		return term;
	}
	
	public TechnicalDictionaryTerm getTermById(String id)
	{
		for (TechnicalDictionaryTerm term : this.terms.values()) {
			if (term.getTermId() == Integer.parseInt(id))
				return term;
		}
		
		return null;
	}
	
	public Iterator<TechnicalDictionaryTerm> termsIterator() {
		return terms.values().iterator();
	}
	
	public Collection<TechnicalDictionaryTerm> termsCollection() {
		return terms.values();
	}
	
	public List<TechnicalDictionaryTerm> findContainingTerms(TechnicalDictionaryTerm term) {
		Session session = RuntimeInfo.getCurrentInfo().getHibernate().acquireSession(SessionMode.READONLY);
		String stemmedText = term.getTermStemmedText();
		if(stemmedText.contains("%") || stemmedText.contains("'") || stemmedText.contains("\""))			
			return new ArrayList<TechnicalDictionaryTerm>();
		//a little bit ugly due to hibernate bug with like
		String stemmedForm1 = stemmedText + " %";
		String stemmedForm2 = "% " + stemmedText + " %";
		String stemmedForm3 = "% " + stemmedText;
		String whereStr = " where term_status = 0 and (term_stemmed_text like :stemmedForm1 or term_stemmed_text like :stemmedForm2 or term_stemmed_text like :stemmedForm3)";
		LogUtils.debug(log, "Loading containing terms for %s",stemmedText);
		Query query = session.createQuery("from TechnicalDictionaryTerm"+whereStr);
		query.setString("stemmedForm1", stemmedForm1);
		query.setString("stemmedForm2", stemmedForm2);
		query.setString("stemmedForm3", stemmedForm3);
		List<TechnicalDictionaryTerm> termsList = null;
		try {
			synchronized (this) {
				termsList = query.list();
			}			
		}
		catch(org.hibernate.MappingException me) {
			//This exception is thrown when the dictionary is empty. Skip it
		}
		finally {
			RuntimeInfo.getCurrentInfo().getHibernate().releaseSession(session);
		}
		if(termsList == null)
			termsList = new ArrayList<TechnicalDictionaryTerm>();
		LogUtils.debug(log, "For term %s, total %d containing terms found", term, termsList.size());
		return termsList;
	}

	public FindTermsInText getFindTermsInText() {
		return new FindTermsInText(terms, termNGrams);
	}
	
	public List<TechnicalDictionaryTerm> findInText(String[] words) {
		return findInText(words, false); 
	}
	
	public List<TechnicalDictionaryTerm> findInText(String[] words, boolean withRelations) {
		FindTermsInText FindTermsInText = getFindTermsInText();
		TermsByPositions termsByPositions = FindTermsInText.getFoundTerms(words,withRelations);
		return termsByPositions.getTerms();
	}	
	
	public void addDocFreq(TechnicalDictionaryKey key) {
		TechnicalDictionaryTerm term = terms.get(key);
		if (term==null)
			return;
		DictionarySession session = idSession.get();
		if (session==null)
			return;
		if (!session.terms.contains(key)) {
			session.terms.add(key);
		    term.addFrequency();
		}
		
	}
	
	public TechnicalTermSource addSource(String sourceName) {
		TechnicalTermSource rc=null;
		if (sourceName==null) 
			return null;
		else
			rc = sources.get(sourceName);
		
		if (rc!=null)
			return rc;
		rc = new TechnicalTermSource(sourceName);
		sources.put(sourceName,rc);
		return rc;			
	}

	public TechnicalTermSource getSource(String sourceName) {
		if (sourceName==null)
			return null;
		return sources.get(sourceName);
	}
	
	public Pair<TechnicalDictionaryTerm,Result> addTermByUser2(TechnicalDictionaryKey termKey) {
		if (!terms.containsKey(termKey))
			 loadTerm(termKey);
		Pair<TechnicalDictionaryTerm,Result> addTerm = null;
		if(deletedTerms.containsKey(termKey) && terms.containsKey(termKey)) {
			//Allow user to recreate previously deleted term
			addTerm = new Pair<TechnicalDictionaryTerm, Result>(terms.get(termKey), Result.Add);
		}
		else {
			if(deletedTerms.containsKey(termKey))
				deletedTerms.remove(termKey);
			addTerm = addTermAndResult(termKey);
		}
		Collection<MatchCandidate> tickets = null;
		//if (!exists) 
			tickets = add2IndexRepository(addTerm.getLeft());
		int frequency = (tickets == null || tickets.size() < 2) ? 2 : tickets.size();
		addTerm.getLeft().setStatusEditByUser();
		addTerm.getLeft().setAbsoluteFrequency(frequency);
		termNGrams.addTerm(termKey);
		return addTerm;
	}
	
	public TechnicalDictionaryTerm addTermByUser(TechnicalDictionaryKey termKey) {
		return addTermByUser2(termKey).getLeft();
	}
	
	private ITicketsRepository getRepository() {
		if (repository==null) {		
			repository = (ITicketsRepository) RuntimeInfo.getCurrentInfo().getBean("ticketsRepository");
		}
		return repository;
	}

	protected Collection<MatchCandidate> add2IndexRepository(TechnicalDictionaryTerm term) {
		try {
			Collection<MatchCandidate> tickets = getRepository().add(term);
			LogUtils.debug(log, "Term have been added to the Index");
			return tickets;
		} catch (MatcherException e) {
			LogUtils.error(log, "Unable to add term %s to index due to %s", term, e.getMessage());
			return null;
		}
	}
	
	protected void removeFromIndexRepository(TechnicalDictionaryTerm term) {
		try {
			getRepository().remove(term);
			LogUtils.debug(log, "Term have been remove from the Index");
		} catch (MatcherException e) {
			LogUtils.error(log, "Unable to remove term %s from index due to %s", term, e.getMessage());
		}
	}
	
	static private TracerFile matchFile = 
		TracerFileLast.create(TracerFileLast.Index,"NotExist",false);

	public void checkExistenceTermsInIndex() {
		LuceneTicketsRepository repository = 
			(LuceneTicketsRepository)getRepository();
		for (TechnicalDictionaryTerm term : terms.values()) {
			if (term.isEnabled() && !term.getTermText().contains("-")) {
				try {
					if (!repository.isExist(term)) {
						String rc = "Term ["+term.getTermText()+"]{"+term.getTermSource()+"} Not exist in index";
						System.err.println(rc);
						matchFile.write(rc);
					}
				
				} catch (MatcherException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	protected void debugAddTerm(String debugText,TechnicalDictionaryTerm term) {
		LogUtils.debug(log,debugText, term, term.getTermId());
	}
	
	protected TechnicalDictionaryTerm setDefaultSource(TechnicalDictionaryTerm term) {
		if (getDefaultSource() != null)
			term.setTermSource(getDefaultSource());
		return term;
	}
	
	protected TechnicalTermSource getDefaultSource() {
		if (idSession.get() !=null)
			return idSession.get().getSource();
		return null;
	}

	public int getTermFreq(TechnicalDictionaryKey termKey) {
		Integer rc = termFreq.get(termKey);
		if (rc==null)
			return 0;
		return rc;
	}

	public TechnicalDictionaryTerm addTerm(TechnicalDictionaryKey termKey) {
		return addTerm(termKey, getDefaultSource());
	}

	public TechnicalDictionaryTerm addTerm(TechnicalDictionaryKey termKey, TechnicalTermSource source) {
		String termText = termKey.getTermText();
		if(!RuntimeInfo.getCurrentInfo().isWebAppMode() && !termText.contains(" ") && Wordnet.getInstance().containsWord(termText)) {
			source = getSource(TechnicalTermSource.ENGLISH_WORDS_ID);
			if (traceFile.isActive() && !extractedWordnetWords.contains(termText)) {
				traceFile.write(termText);
				extractedWordnetWords.add(termText);
			}
		}
		return addTermAndResult(termKey, source).getLeft();
	}

	public Set<TechnicalDictionaryTerm> getSubTerms(TechnicalDictionaryKey termKey) {
		Set<TechnicalDictionaryTerm> rc = new HashSet<TechnicalDictionaryTerm>();
		TechnicalDictionaryTerm term = terms.get(termKey);
		if (term==null)
			return rc;
		List<String> sequence = null;
		if (term.getTermText().contains(ProximityData.SEPERATOR))
			sequence = new ArrayList<String>(Arrays.asList(term.getTermText().split(ProximityData.SEPERATOR)));
		else
			sequence = new ArrayList<String>(Arrays.asList(term.getTermText().split(" ")));
		if (sequence.size()<2)
			return rc;
		SubTightListCombinations<String> subTightCombinations = 
			new SubTightListCombinations<String>(sequence);
		
		List<String> subTightList = subTightCombinations.getNextCombination();
		while (!subTightList.isEmpty()) {
			String text = SubTightListCombinations.toString(subTightList);
			TechnicalDictionaryTerm subTerm = get(text);
			if (subTerm!=null)
				rc.add(subTerm);
			subTightList = subTightCombinations.getNextCombination();
		}
		return rc;
	}
		
	public TechnicalDictionaryTerm syncTerm(TechnicalDictionaryKey termKey,TechnicalDictionary syncDict,boolean removeSplit) {
		TechnicalDictionaryTerm term = terms.get(termKey);
		if (term==null)
			return null;
		term = syncDict.get(termKey);
		if (term==null || (removeSplit && term.getTermSource().getsourceName().toLowerCase().contains("split"))) {
			terms.remove(termKey);
			return null;
		}
		return term;
	}
	
	public Pair<TechnicalDictionaryTerm,Result> importTermAndResult(TechnicalDictionaryTerm term) {
		if (terms.containsKey(term.getTermKey())) {
			debugAddTerm("Term %s(%d) will not be import : the same term already exist ", term);			
			return getPair(term,Result.Exist);
		}
		terms.put(term.getTermKey(),term);
		if (term.getTermSource()==null)
			setDefaultSource(term);
		return getPair(term, Result.Import);
	}
	
	public Pair<TechnicalDictionaryTerm,Result> addTermAndResult(TechnicalDictionaryKey termKey) {
		return addTermAndResult(termKey, getDefaultSource());
	}
	
	private Pair<TechnicalDictionaryTerm,Result> addTermAndResult(TechnicalDictionaryKey termKey, TechnicalTermSource source) {		
		if (deletedTerms.containsKey(termKey)) {
			TechnicalDictionaryTerm existTerm = deletedTerms.get(termKey);
			debugAddTerm("Term %s(%d) will not be added : the same term already deleted ", existTerm);
			return getPair(existTerm,Result.Deleted);
		}
		TechnicalDictionaryTerm newTerm = null;
		synchronized (terms) {
			if (terms.containsKey(termKey)) {
				TechnicalDictionaryTerm existTerm = terms.get(termKey);
				if(idSession.get() != null)
					existTerm.addSourceTicket(idSession.get().getId());
				existTerm.setTermSource(source);
				return getPair(existTerm, Result.Exist);
			}
			termKey.clean();
			if (terms.containsKey(termKey)) {
				TechnicalDictionaryTerm existTerm = terms.get(termKey);
				if(idSession.get() != null)
					existTerm.addSourceTicket(idSession.get().getId());
				existTerm.setTermSource(source);
				return getPair(existTerm, Result.Exist);
			}
			newTerm = new TechnicalDictionaryTerm(termKey);
			terms.put(termKey,newTerm);			
		}
		newTerm.setTermFrequency(frequency);
		if (newTerm.getTermText().equals("")) 
			newTerm.setStatusDeleteByUser();
		if(idSession.get() != null)
			newTerm.addSourceTicket(idSession.get().getId());
		newTerm.setTermSource(source);
		return getPair(newTerm, Result.Add);			
	}

	private Pair<TechnicalDictionaryTerm,Result> getPair(TechnicalDictionaryTerm term,Result result) {
		if (term!=null) {
			Integer freq = termFreq.get(term.getTermKey());
			if (freq==null)
				freq = new Integer(0);
			freq++;
			termFreq.put(term.getTermKey(), freq);
		}
		return new Pair<TechnicalDictionaryTerm,Result>(term,result);
	}

	protected boolean supportReduceTermFreq() {
		return false;
	}
	
	public void reduceTermFreq(TechnicalDictionaryTerm term) {
		reduceTermFreq.reduce(term);
	}

	public void undoReduceTermFreq() {
		reduceTermFreq.undoReduce();
	}

	
	public List<TechnicalDictionaryTerm> select(String text, IComparator comparator) {
		List<TechnicalDictionaryTerm> selectedTerms = new ArrayList<TechnicalDictionaryTerm>();
		
		for (Iterator<TechnicalDictionaryTerm> iterator = termsIterator(); iterator.hasNext();) {
			TechnicalDictionaryTerm term = (TechnicalDictionaryTerm) iterator.next();
			
			if (comparator.Compare(text, term)) {
				selectedTerms.add(term);
			}
		}

		return selectedTerms;
	}
	
	public Collection<TechnicalDictionaryTerm> removeTermByUser(TechnicalDictionaryKey termKey) {
		TechnicalDictionaryTerm term = terms.get(termKey);
		Collection<TechnicalDictionaryTerm> affectedTerms = new HashSet<TechnicalDictionaryTerm>();
		if (term==null)
			return affectedTerms;
		//TODO use SynonymsRelation instead to delete relations recursively
		for (TechnicalDictionaryTerm relatedTerm : term.getRelations()) {
			relatedTerm.removeRelation(term);
			affectedTerms.add(relatedTerm);
		}
		
		term.setStatusDeleteByUser();
		try {
			removeFromIndexRepository(term);
		}
		catch (Exception e) {
			LogUtils.debug(log, "Fail to remove from index ");
		}
		terms.remove(termKey);
		//Mark the term as already deleted
		deletedTerms.put(termKey, term);
		affectedTerms.add(term);
		return affectedTerms;
	}
	
	public void save() {
		Session session =  RuntimeInfo.getCurrentInfo().getHibernate().acquireSession(SessionMode.READ_WRITE);
		try {
			save(session);
		}
		finally {
			RuntimeInfo.getCurrentInfo().getHibernate().releaseSession(session);
		}
	}
	
	public void save(Session session) {	
		Transaction tx = session.beginTransaction();		
		List<TechnicalDictionaryTerm> tmpTerms = new ArrayList<TechnicalDictionaryTerm>(terms.values());
		for(TechnicalDictionaryTerm term :  tmpTerms) {
			try {
				if(term == null)
					continue;
				TechnicalTermSource termSource = term.getTermSource();
				if (termSource!=null) {
					term.prepareSave();
					session.saveOrUpdate(term);
				}
			}
			catch(Exception e){
				LogUtils.error(log, "Unable to save term '%s' due to %s", term, e.getMessage());
				tx.rollback();
				throw new RuntimeException(e);
			}
		}
		try {
			tx.commit();
		}
		catch(Exception e) {
			if(tx != null) {
				tx.rollback();
			}
			throw new RuntimeException("Unable to commit transaction due to " + e.getMessage());
		}
//		tmpSources = null;
		tmpTerms = null;
		LogUtils.debug(log, "Finish saving all dictionary terms.");
		try {
			ProcessedTicketWritter.getInstance().save();
		}
		catch(Exception e) {
			LogUtils.error(log, "Unable to save processed tickets");
			LogUtils.error(log, e);
		}
	}
	
	public String getSourceText(int id) {
		for (TechnicalTermSource source : sources.values() ) {
			if (source.getSourceId()==id)
				return source.getsourceName();
		}
		return null;	}

	public List<TechnicalTermSource> loadSources() {
		Session session = RuntimeInfo.getCurrentInfo().getHibernate().acquireSession(SessionMode.READONLY);
		return loadSources(session);
	}
	
	@SuppressWarnings("unchecked")
	public List<TechnicalTermSource> loadSources(Session session) {
		Query query = session.createQuery("from TechnicalTermSource");
		List<TechnicalTermSource> loadedSources;
		try {
			loadedSources = query.list(); 
		}
		catch(Exception e) {
			//No domains defined yet
			loadedSources = new ArrayList<TechnicalTermSource>();
		}
		finally {
			RuntimeInfo.getCurrentInfo().getHibernate().releaseSession(session);
		}
		sources = new ConcurrentHashMap<String, TechnicalTermSource>();
		for (TechnicalTermSource loadedSource : loadedSources)
			sources.put(loadedSource.getsourceName(), loadedSource);
		return loadedSources;		
	}
	
	public List<TechnicalTermSource> getSources() {
		List<TechnicalTermSource> rc = new ArrayList<TechnicalTermSource>();
		for ( TechnicalTermSource source : sources.values()) 
			rc.add(source);
		return rc;
	}
	
	static private String getUniformVersion(String text) {
		String[] split = text.split(" ");
		String ver = split[split.length-1];
		String uniformVer = ExtractVersionsByNLP.getUniformVersion(ver);
		String rc = text.substring(0,text.length()-ver.length())+uniformVer;
		return rc;
	}
		
	public void printAllRelations() {
		for (TechnicalDictionaryTerm term : terms.values()) {
			if (term.getRelations().size()>1) {
				System.out.println("Term = "+term.getTermText());
				for ( TechnicalDictionaryTerm relation : term.getRelations()) {
					System.out.println("       Relation = "+relation.getTermText());
				}
 			}
		}
	}

	public void addNLPVersionRelations() {
		
		List<TechnicalDictionaryTerm> verTerms1 = 
			new ArrayList<TechnicalDictionaryTerm>();
		List<TechnicalDictionaryTerm> verTerms2 = 
			new ArrayList<TechnicalDictionaryTerm>();
		
		for (TechnicalDictionaryTerm term : terms.values()) {
			TechnicalTermSource source = term.getTermSource();
			if (source.getsourceName().equals(ExtractVersionsByNLP.FIELD_NAME) ||
				source.getsourceName().equals(VersionsTechToknes.FIELD_NAME) ) {
				verTerms1.add(term);
				verTerms2.add(term);
			}
		}
		
		SynonymsRelation relation = new SynonymsRelation();
		for (TechnicalDictionaryTerm term1 : verTerms1) {
			for (TechnicalDictionaryTerm term2 : verTerms2) {
				if (term1==term2)
					continue;
				String termText1 = term1.getTermText();
				String termText2 = term2.getTermText();
				if (getUniformVersion(termText1).equals(getUniformVersion(termText2))) {
					relation.relate(term1,term2, "");
					LogUtils.info(log,"Add NLP Version relation: [%s]<-->[%s]",termText1,termText2);
				}
			}
		}
	}
		
	public void updateTermsAfterFreq() {
		for (TechnicalDictionaryTerm term : terms.values()) {
			term.calculateStatusAfterFreq();
			List<TechnicalDictionaryTerm> relatedTerm2remove = new ArrayList<TechnicalDictionaryTerm>();
			for (TechnicalDictionaryTerm relatedTerm : term.getRelations()) {
				if (!relatedTerm.isEnabled()) 
					relatedTerm2remove.add(relatedTerm);
			}
			for (TechnicalDictionaryTerm relatedTerm : relatedTerm2remove) {
				relatedTerm.removeRelation(term);
			}
		}
	}

	public void addComplexSynonym() {
		TechnicalTermSource sourcePattern5 = addSource("Patterns5");
		addComplexSynonym(sourcePattern5," ",false);
		TechnicalTermSource sourcePattern4 = addSource("Patterns4");
		addComplexSynonym(sourcePattern4," ",false);
		
		IConfigurationResourceLoader loader = (IConfigurationResourceLoader) RuntimeInfo.getCurrentInfo().getBean("xmlConfigurationResourceLoader");
		IConfigurationResource resource = loader.loadResource("xml:///{SolutionConfigFolder}/proximity.xml;//proximityConfig");
		ProximityConfig proximityConfig = (ProximityConfig) resource.getCustomConfiguration(ProximityConfig.class);
		proximityConfig.init(this);
		for(ProximityConfigRule rule : proximityConfig.getRules()){
			TechnicalTermSource proximitySource = rule.getProxSrc();
			addComplexSynonym(proximitySource, ProximityData.SEPERATOR, true);
		}	
	}
		
	//Note, this method is public for testing purposes only.
	public void addComplexSynonym(TechnicalTermSource source,String regex,boolean reverse) {
		SimpleTokenizer tokenizer  = new SimpleTokenizer(regex);
		
		List<TechnicalDictionaryTerm> sourceTerms1 = new ArrayList<TechnicalDictionaryTerm>();
		List<TechnicalDictionaryTerm> sourceTerms2 = new ArrayList<TechnicalDictionaryTerm>();
		
		for (TechnicalDictionaryTerm term : terms.values()) {
			if (term.getTermSource().equals(source) && term.isEnabled()) {
				sourceTerms1.add(term);
				sourceTerms2.add(term);
			}
		}
		
		for (TechnicalDictionaryTerm term1 : sourceTerms1) 
			for (TechnicalDictionaryTerm term2 : sourceTerms2) {
				if (term1.equals(term2))
					continue;
				String splitText1[] = tokenizer.split(term1.getTermStemmedText());
				String splitText2[] = tokenizer.split(term2.getTermStemmedText());
				if (splitText1.length!=splitText2.length)
					continue;
				
				boolean relate = false;
				if (!relate)
					relate = isComplexRelated(splitText1,splitText2);
				if (!relate && reverse)
					relate = isComplexRelated(splitText1,reverse(splitText2));
				
				if (relate) {
					SynonymsRelation relation = new SynonymsRelation();
					relation.relate(term1,term2, "Complex");
				}
			}
	}
	
	//This method produces additional terms based on the existing dictionary by substituting part of the existing term with it relation
	//For example having term 'start server' and relation 'server=host' this method will produce new term 'start host' 
	public void addSubstitutedTerms()
	{
		TracerFile traceFile = TracerFileLast.create(TracerFileLast.SubstitutedTerms,"result",false);
		LogUtils.info(log, "Start adding substituted terms");
		FindTermsInText findTermsInText = getFindTermsInText();
		SynonymsRelation relation = new SynonymsRelation();
		int count = 0;
		int totalSize = termsCollection().size();
		for (TechnicalDictionaryTerm term : termsCollection())   {
        	if(term == null || term.getTermSource() == null) {
        		System.out.println("Error");
        		continue;
        	}
			String termSrcName = term.getTermSource().getsourceName();
			if (SourceMngr.isSource(termSrcName,Type.Modified))
				continue;
			String[] words = term.getTermText().split(" ");
			List<TechnicalDictionaryTerm> shortTerms = findTermsInText.findShortestTerms(words);
			List<TechnicalDictionaryTerm>substitutedTerms = new ArrayList<TechnicalDictionaryTerm>();
			for(TechnicalDictionaryTerm shortTerm : shortTerms) {
				for(TechnicalDictionaryTerm relatedShortTerm : shortTerm.getRelations()) {
					try {
						if(relatedShortTerm.equals(term) || 
						   StringUtils.hasCommonWords(term.getTermStemmedText(), relatedShortTerm.getTermStemmedText()) ||
						   StringUtils.hasCommonWords(shortTerm.getTermStemmedText(), relatedShortTerm.getTermStemmedText()))
							continue;
						String substitutedTermTxt = term.getTermStemmedText().replace(shortTerm.getTermStemmedText(), relatedShortTerm.getTermStemmedText());
						TechnicalDictionaryTerm substitutedTerm = addTerm(new TechnicalDictionaryKey(substitutedTermTxt, false));
						//Important note: don't add the substituted term to the result directly, will be added via relations
						substitutedTerms.add(substitutedTerm);
						if (traceFile.isActive()) 
							traceFile.write("From term " + term.getTermText() + " substituted " + substitutedTerm.getTermText());
					}
					catch(Exception e) {
						LogUtils.warn(log, "Unable to check substituted terms for %s %s", shortTerm, relatedShortTerm);
					}
				}
			}
			for(TechnicalDictionaryTerm substitutedTerm : substitutedTerms)
				relation.relate(term, substitutedTerm, "Substitution relation");
			if(count % 1000 == 0) {
				LogUtils.debug(log, "Add substituted terms another 1000 terms processed, from the beggining %d done", count);
				notifyProgress(2, "another 1000 terms processed", count, totalSize - count);
			}
			count++;
		}
		LogUtils.info(log, "Adding substituted terms finished successfully");
	}
	static private String[] reverse(String[] splitText) {
		int len = splitText.length;
		String[] rc = new String[len];
		for (int i = 0;i<len;i++)
			rc[i]=splitText[len-1-i];
		return rc;
	}
	
	private boolean isComplexRelated(String splitText1[],String splitText2[]) {
		boolean rc = true;
		for (int idx=0;idx<splitText1.length;idx++) {
			if (splitText1[idx].equals(splitText2[idx]))
				continue;
			
			TechnicalDictionaryTerm partTerm1 = terms.get(new TechnicalDictionaryKey(splitText1[idx],false));
			TechnicalDictionaryTerm partTerm2 = terms.get(new TechnicalDictionaryKey(splitText2[idx],false));
			if (partTerm1==null || partTerm2==null) {
				rc = false;
				break;
			}
			
			if (partTerm1.equals(partTerm2))
				continue;
			if (partTerm1.getRelations().contains(partTerm2))
				continue;
			rc = false;
			break;
		}
		return rc;
	}
	
	public void changeTermsContainSource(TechnicalTermSource originSource,
										 TechnicalTermSource changeSource) {
		changeTermsContainSource(originSource,true,changeSource,true);
	}
	
	public void changeTermsContainSource(TechnicalTermSource originSource, boolean deleteOrigin,
										 TechnicalTermSource changeSource, boolean deleteChange) {
		List<TechnicalDictionaryTerm> termsFromSource = 
			new ArrayList<TechnicalDictionaryTerm>();
		boolean first=true;
		for (TechnicalDictionaryTerm term : terms.values()) {
			if (originSource.equals(term.getTermSource())) {
				termsFromSource.add(term);
				if (deleteOrigin) {
					if (first) {
						LogUtils.info(log,"Deleting all %s terms :",originSource.getsourceName(),term.getTermText());
						first=false;
					}
					term.setStatusDeleteByUser();
					LogUtils.info(log,"Delete %s term [%s]",originSource.getsourceName(),term.getTermText());
				}
			}
		}
	
		first=true;
		int count = 0;
		int totalCount = terms.values().size();
		for (TechnicalDictionaryTerm term : terms.values()) {
			count ++;
			if(count % 1000 == 0) {
				LogUtils.info(log, "Cleaning person names, 1000 terms processed from the beggining %d done, %d remaining", count, totalCount - count);
				save();
				LogUtils.info(log, "Dictionary committed successfully");
			}
			if (term.getTermSource().equals(originSource))
				continue;
			String text = " "+term.getTermText()+" ";
			for (TechnicalDictionaryTerm termFromSource : termsFromSource) {
				if (text.contains(" "+termFromSource.getTermText()+" ")) {
					term.setTermSource(changeSource);
					if (deleteChange) {
						if (first) {
							LogUtils.info(log,"Deleting all %s terms :",originSource.getsourceName(),term.getTermText());
							first=false;
						}
						term.setStatusDeleteByUser();
						LogUtils.debug(log,"Delete %s term [%s]",changeSource.getsourceName(),term.getTermText());
					}
				}
			}
		}
	}
	
	public void deleteTerm(TechnicalDictionaryTerm term) 
	{
		Session session = RuntimeInfo.getCurrentInfo().getHibernate().acquireSession(SessionMode.READONLY);
		Transaction tx = session.beginTransaction();
		try {
			if(get(term.getTermText()) == null)
				session.delete(term);			
			tx.commit();
		} catch (Exception e) {
			LogUtils.error(log, "Unable to delete term %s, reason: %s", term, e.getMessage());
			tx.rollback();
		}
		finally {
			RuntimeInfo.getCurrentInfo().getHibernate().releaseSession(session);
		}
	}

	public void saveTerm(TechnicalDictionaryTerm term) 
	{
		Session session = RuntimeInfo.getCurrentInfo().getHibernate().acquireSession(SessionMode.READ_WRITE);
		Transaction tx = session.beginTransaction();
		try {
			session.saveOrUpdate(term);
			tx.commit();
		} catch (Exception e) {
			LogUtils.error(log, "Unable to save term %s, reason: %s", term, e.getMessage());
			tx.rollback();
		}
		finally {
			RuntimeInfo.getCurrentInfo().getHibernate().releaseSession(session);
		}
	}

	
	public void updateSource(TechnicalTermSource source)
	{
		Session session = RuntimeInfo.getCurrentInfo().getHibernate().acquireSession(SessionMode.READ_WRITE);
		Transaction tx = session.beginTransaction();
		
		try {
			//session.clear();
			session.saveOrUpdate(source);
			
			tx.commit();
		} catch (Exception e) {
			tx.rollback();
		}
		finally {
			RuntimeInfo.getCurrentInfo().getHibernate().releaseSession(session);
		}
	}
	
	public TechnicalTermSource getSource(int sourceID)
	{
		TechnicalTermSource termSource = new TechnicalTermSource();
		termSource.setSourceId(sourceID);
		int sourceIndex = getSources().indexOf(termSource);
		
		if (sourceIndex == -1)
			return null;
		
		return getSources().get(sourceIndex); 
	}
	
	public void loadDictionary() {
		loadDictionary(true);
	}
	
	public void loadDictionary(Session session) {
		loadDictionary(session,true);
	}

	public void loadDictionary(boolean checkFrequency)
	{
		Session session = RuntimeInfo.getCurrentInfo().getHibernate().acquireSession(SessionMode.READONLY);
		try {
			loadDictionary(session,checkFrequency);
		}
		finally {
			RuntimeInfo.getCurrentInfo().getHibernate().releaseSession(session);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void loadDictionary(Session session,boolean checkFrequency)
	{
		if(loaded)
			return;
		LogUtils.debug(log, "Loading technical Dictionary");
		String whereStr = "";
		if (checkFrequency) {
			whereStr = frequency.add2WhereString(whereStr,"term_frequency");
			whereStr = TechnicalDictionaryTerm.addLoaded2WhereString(whereStr);
		}
		if (!whereStr.equals(""))
			whereStr = " where "+whereStr;
		Query query = null;
		if(RuntimeInfo.getCurrentInfo().isWebAppMode())
			query = session.createQuery("select new TechnicalDictionaryTerm(t.termId, t.termKey, t.termFrequency, t.termStatus, t.termSource) from TechnicalDictionaryTerm t "+whereStr);
		else
			query = session.createQuery("from TechnicalDictionaryTerm"+whereStr);
		try {
			List<TechnicalDictionaryTerm> termsList = query.list();
			for(TechnicalDictionaryTerm newTerm : termsList) {
				TechnicalDictionaryKey termKey = newTerm.getTermKey();
				newTerm.setTermFrequency(frequency);
				if (newTerm.isDeletedByUser())
					deletedTerms.put(termKey,newTerm);
				else {
					terms.put(termKey,newTerm);
					termNGrams.addTerm(termKey);
				}
			}
		}
		catch(org.hibernate.MappingException me) {
			//This exception is thrown when the dictionary is empty. Skip it
		}
		loadSources(session);
		LogUtils.info(log, "Dictionary loaded successfully, %d terms found", getTermsCount());
		loaded = true;
		GregorianCalendar.getInstance();
	}

	@SuppressWarnings("unchecked")
	private TechnicalDictionaryTerm getLoadTerm(TechnicalDictionaryKey termKey) {
		Session session = RuntimeInfo.getCurrentInfo().getHibernate().acquireSession(SessionMode.READONLY);
		String stemmedText = termKey.getStemmedText();
		LogUtils.debug(log, "Loading technical Term stemmed=%s",stemmedText);
		String whereStr = " where term_stemmed_text=:stemmedText";
		Query query = session.createQuery("from TechnicalDictionaryTerm"+whereStr).setString("stemmedText", stemmedText);
		try {
			List<TechnicalDictionaryTerm> termsList;
			synchronized (this) {
				termsList = query.list();
			}
			if (termsList!=null && termsList.size()==1) {
				TechnicalDictionaryTerm newTerm = termsList.get(0);
				LogUtils.debug(log, "Term loaded successfully: freq=%d", newTerm.getFrequency());
				return newTerm;
			}
		}
		catch(org.hibernate.MappingException me) {
			//This exception is thrown when the dictionary is empty. Skip it
		}
		finally {
			RuntimeInfo.getCurrentInfo().getHibernate().releaseSession(session);
		}
		LogUtils.debug(log, "Term not loaded ,Not found");
		return null;
	}
	
	private boolean loadTerm(TechnicalDictionaryKey termKey) {
		TechnicalDictionaryTerm newTerm = getLoadTerm(termKey);
		if (newTerm==null)
			return false;
		terms.put(newTerm.getTermKey(),newTerm);
		return true;
	}
	
	public TechnicalDictionaryTerm getLowFreqTerm(TechnicalDictionaryKey termKey) {
		if (deletedTerms.containsKey(termKey))
			return deletedTerms.get(termKey);
		TechnicalDictionaryTerm term = getLoadTerm(termKey);
		if (term==null || term.isDeletedByUser())
			return null;
		TechnicalTermSource source = term.getTermSource();
		if (!SourceMngr.isSource(source,Type.LowFequency))
			return null;
		
		DictionarySession session = idSession.get();
		if (session!=null) {
			String id = session.getId();
			for(String srcId : term.getSourceTickets()) {
				if (srcId.contains(id))
					return null;
			}
		}
		
		term = addTerm(termKey);
		term.setTermSource(source);
		Collection<MatchCandidate> tickets = add2IndexRepository(term);
		int frequency = (tickets == null || tickets.size()==0) ? 1 : tickets.size();
		term.setAbsoluteFrequency(frequency+1);
		termNGrams.addTerm(termKey);
		saveTerm(term);
		return term;
	}
	
	public void setFrequency(TechnicalDictionaryFrequency frequency) {
		this.frequency = frequency;
	}
	
	public void endSession(float boostFactor, List<Float> itemsBoostFactors,boolean isRequired) {
		idSession.set(null);
	}

	public void startSession(String fieldName,String currentTicket, String sourceName) {
		idSession.set(new DictionarySession(addSource(sourceName),currentTicket));
	}

	public void unloadDictionary() {
		terms.clear();
		deletedTerms.clear();
		sources.clear();
		loaded = false;
		idSession.set(null);
	}
	 
	public Collection<TechnicalDictionaryTerm> getAllTerms(boolean addTF) {
		List<TechnicalDictionaryTerm> rc = new ArrayList<TechnicalDictionaryTerm>();
		for (TechnicalDictionaryTerm term : termsCollection()) {
			int tf = 1;
			if (addTF)
				  tf = getTermFreq(term.getTermKey());
			for (int i=0;i<tf;i++) 
				rc.add(term);
		}
		return rc;
	}

	@Override
	public void finish() {
	}

	@Override
	public void freezeTerms() {
	}

	//Progress notification
	@Override
	public void notifyProgress(int level, String message, int processedCount,
			int remainingCount) {
		notifier.notifyProgress(level, message, processedCount, remainingCount);
	}

	@Override
	public void registerProgressNotificationListener(
			IProgressNotificationListener listener) {
		notifier.registerProgressNotificationListener(listener);
	}

	@Override
	public void unregisterProgressNotificationListener(
			IProgressNotificationListener listener) {
		notifier.unregisterProgressNotificationListener(listener);
	}
};
