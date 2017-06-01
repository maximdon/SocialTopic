package com.softlib.imatch.matcher.lucene.customscore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.DefaultSimilarity;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.search.TopDocsCollector;
import org.apache.lucene.search.TopScoreDocCollector;

import com.softlib.imatch.MatcherException;
import com.softlib.imatch.common.HighlightText;
import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.common.StringUtils;
import com.softlib.imatch.common.TracerFile;
import com.softlib.imatch.common.TracerFileLast;
import com.softlib.imatch.common.HighlightText.Type;
import com.softlib.imatch.common.configuration.IConfigurationObject;
import com.softlib.imatch.dbintegration.DBTicket;
import com.softlib.imatch.dictionary.TechnicalDictionaryKey;
import com.softlib.imatch.matcher.CandidateScore;
import com.softlib.imatch.matcher.DBTicketFieldsNames;
import com.softlib.imatch.matcher.ITicketFieldsNames;
import com.softlib.imatch.matcher.MatchCandidate;
import com.softlib.imatch.matcher.lucene.LuceneIndexer;
import com.softlib.imatch.matcher.lucene.LuceneSearcher;
import com.softlib.imatch.test.isolveindex.LuceneSearchProcessedTicketTest;
import com.softlib.imatch.ticketprocessing.IDocFreqProvider;
import com.softlib.imatch.ticketprocessing.IProcessedTicket;
import com.softlib.imatch.ticketprocessing.ProcessedField;
import com.softlib.imatch.ticketprocessing.ProcessedTicket;

class LuceneCustomScoreSearcher extends LuceneSearcher implements IQueryTester {
	
	public LuceneCustomScoreSearcher(IConfigurationObject config) {
		super(config);
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
	
	protected Collection<MatchCandidate> searchInternal(IProcessedTicket ticket)
			throws MatcherException {
		// TODO check here, standard analyzer is for english, do we need another one?
		Set<ProcessedTicket> candidates = new HashSet<ProcessedTicket>();
		Query query = queryBuilder.buildQuery(ticket);
		int maximumNumCandidates = (Integer)configuration.getProperty(ticket.getOriginObjectId(), "maximumNumCandidates");
		TopDocsCollector collector = TopScoreDocCollector.create(maximumNumCandidates, true);
				
		TracerFile tracerFile = 
			TracerFileLast.getLast(TracerFileLast.MostTerms);

		if (tracerFile.isActive()) {
			
			tracerFile.write("\n### Find with : " +query.toString());
			
			for (String fieldName : ticket.getData().keySet()) {	
				ProcessedField processedField = ticket.getField(fieldName);
				if (processedField==null)
					continue;
				tracerFile.write("\n### "+fieldName+" :");
				String fieldText = (String)ticket.getOriginalTicket().getField(fieldName);
				
				HighlightText highlightText = 
					new HighlightText(fieldText,"["+HighlightText.TITLE+"]-<",">-");

				highlightText.highlight(processedField.getTerms(),Type.Active);
				highlightText.highlight(processedField.getOneFreqTerms(),Type.One);
				highlightText.highlight(processedField.getZeroFreqTerms(),Type.Zero);
				fieldText = highlightText.getHighlightText();
				tracerFile.write(fieldText);
			}
			
			tracerFile.close();
		}
		
		try {
			LogUtils.debug(log, "Executing lucene query %s", query.toString());
			searcher.search(query, collector);
		
			for(ScoreDoc scoreDoc : collector.topDocs().scoreDocs) {
				LogUtils.debug(log, "For query %s total %d documents found", query.toString(), collector.getTotalHits());
				
				Document hitDoc = searcher.doc(scoreDoc.doc);
				String originObjectId = hitDoc.get(LuceneIndexer.TICKET_OBJECT_ID_FIELD);				
								
				DBTicket imCandidateTicket = new DBTicket(originObjectId);
				imCandidateTicket.setId(hitDoc.get(LuceneIndexer.TICKET_ID_FIELD));
						
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
			throw new MatcherException("Match failed due to internal Lucene error " + e.getMessage(), e);
		}
		LogUtils.debug(log,	"Raw candidates (based on index search only) list prepared, total %d candidates found", candidates.size());
		Collection<MatchCandidate> matchCandidates = new HashSet<MatchCandidate>();
		for (IProcessedTicket candidateTicket : candidates) {
			CandidateScore matchScore = ProcessedTicket.match(ticket, candidateTicket);
			candidateTicket.setMatchMode(ticket.getMatchMode());
			MatchCandidate matchCandidate = new MatchCandidate(matchScore, candidateTicket.getOriginalTicket(), !ticket.getData().isEmpty(), candidateTicket);
			matchCandidate.setSourceProcessedTicket(ticket);
			matchCandidates.add(matchCandidate);
		}
		candidates = null;
		collector = null;
		LogUtils.debug(log, "Candidates after score calculation %s", matchCandidates);
		return matchCandidates;
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
};
