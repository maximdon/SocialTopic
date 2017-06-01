package com.softlib.imatch.matcher.lucene;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;

import com.softlib.imatch.MatcherException;
import com.softlib.imatch.common.configuration.IConfigurationObject;
import com.softlib.imatch.ticketprocessing.IProcessedTicket;

public abstract class BaseLuceneIndexer 
{
	protected IndexWriter indexWriter;
	protected Document document;
	protected String indexFilesLocation;
	protected IConfigurationObject config;

	protected static final Logger log = Logger.getLogger(LuceneIndexer.class);

	public static final String TICKET_ID_FIELD = "ticket_id";
	public static final String TICKET_OBJECT_ID_FIELD = "object_id" ;

	public BaseLuceneIndexer(IConfigurationObject configuration) throws MatcherException
	{
		config = configuration;
		indexFilesLocation = getIndexFilesLocation();
	}

	public abstract void index(IProcessedTicket ticket) throws MatcherException;
	
	protected String getIndexFilesLocation()
	{
		return (String) config.getCommonProperty("indexFilesLocation");
	}
	
	protected Set<String> getStopWords()
	{
		return new HashSet<String>();
	}
	
	public void startBatch() throws MatcherException {
		try {
			initWriter();
	        document = new Document();
		}
		catch(Exception e)
		{
			throw new MatcherException("Unable to start batch indexing job, reason " + e.getMessage());
		}
	}

	public void flush() throws MatcherException  {
		try {
	        indexWriter.optimize();
	        indexWriter.commit();
		}
		catch(Exception e)
		{
			throw new MatcherException("Unable to finish batch indexing job", e);			
		}
	}

	public void endBatch() throws MatcherException {
		try {
			flush();
			indexWriter.close();
		} catch (Exception e) {
			throw new MatcherException("Unable to finish batch indexing job", e);
		}
		finally
		{
	        indexWriter = null;
	        document = null;
		}
	}
	
	protected void initWriter() throws CorruptIndexException, LockObtainFailedException, IOException 
	{
		indexWriter = 
				new IndexWriter(FSDirectory.open(new File(indexFilesLocation)), 
						        new StandardAnalyzer(Version.LUCENE_CURRENT, getStopWords()), 
						        MaxFieldLength.UNLIMITED);
			//TODO advanced configuration
			indexWriter.setRAMBufferSizeMB(100);
			indexWriter.setMergeFactor(1000);
			//TODO check here, is it important to set similarity (currently we're working with DefaultSimilarity any way)
	}
}
