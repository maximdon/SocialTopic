package com.softlib.imatch.matcher.lucene.agent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import opennlp.tools.util.HashList;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.DefaultSimilarity;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.search.TopDocsCollector;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.highlight.TokenSources;

//import antlr.Token;
//import antlr.TokenStream;
//import antlr.TokenStreamException;

import com.softlib.imatch.MatcherException;
import com.softlib.imatch.common.HighlightText;
import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.common.StringUtils;
import com.softlib.imatch.common.TracerFile;
import com.softlib.imatch.common.TracerFileLast;
import com.softlib.imatch.common.HighlightText.Type;
import com.softlib.imatch.common.configuration.IConfigurationObject;
import com.softlib.imatch.common.configuration.IConfigurationResource;
import com.softlib.imatch.common.configuration.IConfigurationResourceLoader;
import com.softlib.imatch.dbintegration.DBTicket;
import com.softlib.imatch.dictionary.TechnicalDictionaryKey;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.dictionary.Wordnet;
import com.softlib.imatch.matcher.CandidateScore;
import com.softlib.imatch.matcher.DBTicketFieldsNames;
import com.softlib.imatch.matcher.ICandidatesSubprocessor;
import com.softlib.imatch.matcher.ITicketFieldsNames;
import com.softlib.imatch.matcher.MatchCandidate;
import com.softlib.imatch.matcher.SearcherConfiguration;
import com.softlib.imatch.matcher.lucene.LuceneIndexer;
import com.softlib.imatch.matcher.lucene.LuceneSearcher;
import com.softlib.imatch.matcher.lucene.customscore.IQueryTester;
import com.softlib.imatch.test.isolveindex.LuceneSearchProcessedTicketTest;
import com.softlib.imatch.ticketprocessing.IDocFreqProvider;
import com.softlib.imatch.ticketprocessing.IProcessedTicket;
import com.softlib.imatch.ticketprocessing.ITicketMatchAction;
import com.softlib.imatch.ticketprocessing.ProcessedField;
import com.softlib.imatch.ticketprocessing.ProcessedTicket;
import com.softlib.imatch.ticketprocessing.IProcessedTicket.MatchMode;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

class LuceneVirtualAgentSearcher extends LuceneSearcher implements IQueryTester {
	private int numDocs = -1;
	
	
	private List<ITicketMatchAction> subProcessors;
	
	
	public List<ITicketMatchAction> getSubProcessors() {
		return subProcessors;
	}

	public void setSubProcessors(List<ITicketMatchAction> subProcessors) {
		this.subProcessors = subProcessors;
	}

	public LuceneVirtualAgentSearcher(IConfigurationResourceLoader loader) 
	{
		super(null);
		IConfigurationResource resource = loader
				.loadResource("xml:///{SolutionConfigFolder}/matcher.xml;//searcher");
		this.configuration = resource.getConfigurationObject(SearcherConfiguration.class);
		initializeIndexSearcher();
	}
	
	public List<String> searchIsolve(String text) throws MatcherException {
		List<String> rc = new ArrayList<String>();
		
		TopDocsCollector collector = TopScoreDocCollector.create(10000, true);
		Query query = LuceneSearchProcessedTicketTest.buildQuery(text);
		try {
			searcher.search(query, collector);
		} catch (IOException e) {
			throw new MatcherException("search: searcher.search " + e.getMessage(), e);
		}

		if (collector.getTotalHits() > 0) {
 		
			for(ScoreDoc scoreDoc : collector.topDocs().scoreDocs) {
				Document hitDoc;
				try {
					hitDoc = searcher.doc(scoreDoc.doc);
				} catch (Exception e) {
					throw new MatcherException("search:searcher.doc " + e.getMessage(), e);
				}
				String itemnum = hitDoc.get("itemnum");
				rc.add(itemnum);
			}
		}
		
		return rc;
	}
	
	protected Set<ProcessedTicket> runQuery(Query query,IProcessedTicket ticket)
	{
		Set<ProcessedTicket> candidates = new HashSet<ProcessedTicket>();
		int maximumNumCandidates = (Integer)configuration.getProperty(ticket.getOriginObjectId(), "maximumNumCandidates");
		TopDocsCollector collector = TopScoreDocCollector.create(maximumNumCandidates, true);

		try {
			LogUtils.debug(log, "Executing lucene query %s", query.toString());
			searcher.search(query, collector);
			LogUtils.debug(log, "For query %s total %d documents found", query.toString(), collector.getTotalHits());		
			for(ScoreDoc scoreDoc : collector.topDocs().scoreDocs) 
			{
				Document hitDoc = searcher.doc(scoreDoc.doc);
				String originObjectId = hitDoc.get(LuceneIndexer.TICKET_OBJECT_ID_FIELD);				
								
				DBTicket imCandidateTicket = new DBTicket(originObjectId);
				imCandidateTicket.setId(hitDoc.get(LuceneIndexer.TICKET_ID_FIELD));
						
				// add url field
				String URLValue = hitDoc.get(LuceneIndexer.TICKET_URL_FIELD);
				//candidateTicket.getOriginalTicket().
				imCandidateTicket.setField("Url", URLValue);
				
				// add title field
				String titleValue = hitDoc.get(LuceneIndexer.TICKET_TITLE_FIELD);
				//candidateTicket.getOriginalTicket().
				imCandidateTicket.setField("Title", titleValue);

				// add content field
				String contentValue = hitDoc.get(LuceneIndexer.TICKET_CONTENT_FIELD);
				//candidateTicket.getOriginalTicket().
				imCandidateTicket.setField("Text", contentValue);
				
				// add content field
				String linksValue = hitDoc.get(LuceneIndexer.TICKET_LINKS_FIELD);
				//candidateTicket.getOriginalTicket().
				imCandidateTicket.setField("Links", linksValue);
				
				// add score field
				imCandidateTicket.setField("lucene_score", String.valueOf(scoreDoc.score));
				
				ProcessedTicket candidateTicket = new ProcessedTicket(imCandidateTicket, ProcessedTicket.getDefaultCalculator());
				
				ITicketFieldsNames fieldsNamesConfig = new DBTicketFieldsNames();
				fieldsNamesConfig.setObjectId(originObjectId);				
				Set<String> fieldsNames = fieldsNamesConfig.getAllFields(ticket.getMatchMode());
				
				for (String fieldName : fieldsNames) {
					String candidateFieldValue = hitDoc.get(fieldName);
					if (!StringUtils.isEmpty(candidateFieldValue)) {
						List<String> candidateField = new ArrayList<String>(Arrays.asList(candidateFieldValue.split(" \\$a\\$ ")));
						candidateTicket.startSession(fieldName,imCandidateTicket.getId(),null);
						for (String str : candidateField) 
							candidateTicket.addTerm(new TechnicalDictionaryKey(str, false));
						candidateTicket.endSession(0,null,false);
					}
				}
				
				candidates.add(candidateTicket);
			}		
		} catch (IOException e) {
			//throw new MatcherException("Match failed due to internal Lucene error " + e.getMessage(), e);
		}
		
		return candidates;
		
	}
	protected Collection<MatchCandidate> searchInternal(IProcessedTicket ticket)
			throws MatcherException {
		// TODO check here, standard analyzer is for english, do we need another one?
		//Set<ProcessedTicket> candidates;// = new HashSet<ProcessedTicket>();
		
		List<String> orphans = new ArrayList<String>();
		
		// first get the orphan words
		String body = ticket.getOriginalTicket().getBody(MatchMode.all);
		LogUtils.debug(log, "processing input: %s", body);
		try
		{
			TokenStream tokenStream = TokenSources.getTokenStream("text", body, analyzer);
			CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);		
			while (tokenStream.incrementToken())
			{
			    String word = charTermAttribute.toString();
			    
			    // first check if part of term
				Boolean found = false;
				for(TechnicalDictionaryTerm term : ticket.getAllTerms(false)) 
				{
					if (term.hasWord(word))
					{
						found = true;
						break;
					}
				}
				if (!found)			    
				{
					// check if stopword first
					if (Wordnet.getInstance().isStopWord(word))
						continue;
					
					orphans.add(word);					
				}
				    
			}
		}
		catch(Exception e)
		{
			LogUtils.error(log, "exception: %s",e.toString());
		}
		
		if (orphans.size() != 0)
			LogUtils.debug(log, "Orphans list: %s", orphans.toString());
		else
			LogUtils.debug(log,"No orphans detected");
		
		// put Orphans field
		ticket.setOrphanWords(orphans);
		
		LogUtils.debug(log,"Terms list: %s",ticket.getAllTerms(false));
		
		
		// now we have a ticket with terms and orphans. we pass it along to chain of processors
		for (ITicketMatchAction action : subProcessors) {
			Query query = action.buildQuery(ticket);
			if (query != null)
			{
				//LogUtils.debug(log,"searching with query: %s",query.toString());
				// run it and get results
				Set<ProcessedTicket> candidates = runQuery(query,ticket);
				// check what the action bean has to say on it
				if (action.continueWithProcess(candidates) == false)
				{
					// end of line
					LogUtils.debug(log,	"Raw candidates (based on index search only) list prepared, total %d candidates found", candidates.size());
					Collection<MatchCandidate> matchCandidates = new HashSet<MatchCandidate>();
					for (IProcessedTicket candidateTicket : candidates) {
						
						CandidateScore matchScore = null;
						if (!action.useiMatchScorer())
						{
							String score = (String)candidateTicket.getOriginalTicket().getField("lucene_score");
							if (score != null)
								matchScore = new CandidateScore(Float.valueOf(score));
							else
								matchScore = new CandidateScore(0.1f);
						}
						else
							matchScore = ProcessedTicket.match(ticket, candidateTicket);
						
						
						candidateTicket.setMatchMode(ticket.getMatchMode());
						MatchCandidate matchCandidate = new MatchCandidate(matchScore, candidateTicket.getOriginalTicket(), !ticket.getData().isEmpty(), candidateTicket);
						matchCandidate.setSourceProcessedTicket(ticket);
						matchCandidates.add(matchCandidate);
					}
					LogUtils.debug(log, "Candidates after score calculation %s", matchCandidates);
					return matchCandidates;
				}
			}
		}

		return new HashSet<MatchCandidate>();
	}

	@Override
	protected Similarity getSimilarity() {
		return new DefaultSimilarity();
	}

	public TopDocsCollector test(BooleanQuery tempQuery) throws MatcherException {
		return test(tempQuery, true);
	}

	private TopDocsCollector test(BooleanQuery tempQuery, boolean logMsg) throws MatcherException {
		TopDocsCollector collector = TopScoreDocCollector.create(10000, true);
		try {
			searcher.search(tempQuery, collector);
			if(logMsg)
				LogUtils.debug(log, "Testing query %s. This query returns %d results", tempQuery.toString(), collector.getTotalHits());
		} catch (IOException e) {
			throw new MatcherException("Match failed due to internal Lucene error " + e.getMessage(), e);
		}
		return collector;
	}
	
	public int getNumDocs() throws MatcherException {
		if(numDocs == -1)
		   numDocs = searcher.getIndexReader().numDocs();
		return numDocs;
	}
	
	public int getDf(String item) throws MatcherException {
		return 0;
	}
	
};
