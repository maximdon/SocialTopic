package com.softlib.imatch.matcher.lucene;

import java.util.Set;

import org.apache.lucene.document.Field;
import org.apache.lucene.index.Term;

import com.softlib.imatch.MatcherException;
import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.common.configuration.IConfigurationObject;
import com.softlib.imatch.dbintegration.DBUtils;
import com.softlib.imatch.dictionary.PorterStemmer;
import com.softlib.imatch.matcher.ITicketFieldsNames;
import com.softlib.imatch.ticketprocessing.IProcessedTicket;
import com.softlib.imatch.ticketprocessing.IProcessedTicket.MatchMode;

//TODO bug, when no index exists but no tickets to index, the empty index is created, but next index is unable to access it
public class LuceneWordIndexer extends BaseLuceneIndexer
{
	public static final String TICKET_BODY_FIELD = "ticket_body";
	
	public LuceneWordIndexer(IConfigurationObject configuration) throws MatcherException
	{
		super(configuration);
	}
	
	protected String getIndexFilesLocation()
	{
		return getWordFileName((String) config.getCommonProperty("indexFilesLocation"));
	}
	
	static String getWordFileName(String fileName) {
		return fileName+"Word";
	}
	
	public void index(IProcessedTicket ticket) throws MatcherException {
		synchronized (this) {
			try {		
				Field bodyField = document.getField(TICKET_BODY_FIELD);

				ITicketFieldsNames fieldsNamesConfig = ticket.getOriginalTicket().getFieldsConfig();
				Set<String> fieldsNames = fieldsNamesConfig.getAllFields(MatchMode.all);
				
				String ticketData = 
					DBUtils.concatFields(fieldsNames, ticket.getOriginalTicket());
				
				ticketData = PorterStemmer.stem(ticketData);
				if(bodyField == null) {
					 bodyField = new Field(TICKET_BODY_FIELD, ticketData,Field.Store.NO, Field.Index.ANALYZED);
					 document.add(bodyField);
				}				
				else 
					bodyField.setValue(ticketData);
				Field idField = document.getField(TICKET_ID_FIELD);
				if(idField == null) {
					idField = new Field(TICKET_ID_FIELD, ticket.getId(), Field.Store.YES, Field.Index.ANALYZED_NO_NORMS);	
					document.add(idField);
				}
				else
					idField.setValue(ticket.getId());
				Field objectIdField = document.getField(TICKET_OBJECT_ID_FIELD);
				if(objectIdField == null) {
					objectIdField = new Field(TICKET_OBJECT_ID_FIELD, ticket.getOriginObjectId(), Field.Store.YES, Field.Index.ANALYZED_NO_NORMS);	
					document.add(objectIdField);
				}
				else
					objectIdField.setValue(ticket.getOriginObjectId());
		        indexWriter.addDocument(document);
			}
			catch(Exception e)
			{
				throw new MatcherException(e.getMessage(), e);
			}
		}
	}

	public void remove(String id) throws MatcherException {
		LogUtils.debug(log, "Deleting ticket %s from the index", id);
		try {
			indexWriter.deleteDocuments(new Term(TICKET_ID_FIELD, id));
		} catch (Exception e) {
			throw new MatcherException("Unable to delete document, reason " + e.getMessage());
		}
	}
	
}
