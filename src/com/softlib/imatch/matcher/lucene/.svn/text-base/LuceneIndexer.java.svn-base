package com.softlib.imatch.matcher.lucene;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.Term;

import com.softlib.imatch.ITicket;
import com.softlib.imatch.MatcherException;
import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.common.configuration.IConfigurationObject;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.matcher.DBTicketFieldsNames;
import com.softlib.imatch.matcher.ITicketFieldsNames;
import com.softlib.imatch.ticketprocessing.IProcessedTicket;
import com.softlib.imatch.ticketprocessing.ProcessedField;
import com.softlib.imatch.ticketprocessing.IProcessedTicket.MatchMode;

//TODO bug, when no index exists but no tickets to index, the empty index is created, but next index is unable to access it
public class LuceneIndexer extends BaseLuceneIndexer
{	
	public static final String TICKET_TITLE_FIELD = "ticket_title";
	public static final String TICKET_CONTENT_FIELD = "ticket_content";
	public static final String TICKET_URL_FIELD = "ticket_url";
	public static final String TICKET_LINKS_FIELD = "ticket_links";
	public static final String DUPLICATE_TICKET_ID_FIELD = "_dup_ticket_id";
	public static final String TERM_SEPERATOR = " $a$ ";

	public LuceneIndexer(IConfigurationObject configuration) throws MatcherException {
		super(configuration);
	}
	
	private void index(IProcessedTicket processedTicket, String fieldName) {
		ProcessedField processedField = processedTicket.getField(fieldName);
		processedField.addSubTerms();
		Collection<TechnicalDictionaryTerm> terms = processedField.getTerms();
		
		List<String> allTerms = new ArrayList<String>();
		
		for (TechnicalDictionaryTerm term : terms) {
			String termText = term.getTermStemmedText();
			//TODO ';' patch, remove when ; will be defined as delimiter
			if(termText.indexOf(';') > 0) {
				String[] termTextParts = termText.split(";");
				for(String termTextPart : termTextParts)
					if(termTextPart != null && termTextPart.trim().length() > 0)
						allTerms.add(termTextPart);
			}
			allTerms.add(termText);
		}
		
		String allTermsStr = StringUtils.join(allTerms, " $a$ ");
		Field luceneField = document.getField(fieldName);
		if (luceneField == null) {
			luceneField = new Field(fieldName, allTermsStr,
					Field.Store.YES, Field.Index.ANALYZED);
			document.add(luceneField);
		} else {
			luceneField.setValue(allTermsStr);
		}

	}
	
	public void index(IProcessedTicket processedTicket) throws MatcherException {
		synchronized (this) {
			try {				
				//Patch, Lucene doesn't support query like -ticket_id:4. For this reason we add synthetic field which has value 0 for all documents and doesn't change the score

				Field idField = document.getField(TICKET_ID_FIELD);
				Field idDuplicateField = document.getField(DUPLICATE_TICKET_ID_FIELD);
				if (idField == null) {
					idField = 
						new Field(TICKET_ID_FIELD, processedTicket.getId(),
							      Field.Store.YES, Field.Index.ANALYZED_NO_NORMS);
					idDuplicateField = 
						new Field(DUPLICATE_TICKET_ID_FIELD,"0",
								  Field.Store.YES, Field.Index.ANALYZED_NO_NORMS);
					document.add(idField);
					document.add(idDuplicateField);
				} 
				else
					idField.setValue(processedTicket.getId());	
				
				//Object ID field
				Field objectIdField = document.getField(TICKET_OBJECT_ID_FIELD);
				if(objectIdField == null) {
					objectIdField = 
						new Field(TICKET_OBJECT_ID_FIELD, processedTicket.getOriginObjectId(), 
								  Field.Store.YES, Field.Index.ANALYZED_NO_NORMS);	
					document.add(objectIdField);
				}
				else
					objectIdField.setValue(processedTicket.getOriginObjectId());
				
				for (String fieldName : processedTicket.getData().keySet()) {
					index(processedTicket,fieldName);
				}
				//Filter fields
				List<String> filterFields = (List<String>)config.getProperty(processedTicket.getOriginObjectId(), "filterFields");
				for (String filterFieldName : filterFields) {
					ITicket originalTicket = processedTicket.getOriginalTicket();
					Object filterFieldValue;
					if (originalTicket == null || (filterFieldValue = originalTicket.getField(filterFieldName)) == null)
						filterFieldValue = "";
					Field filterField = document.getField(filterFieldName);
					if (filterField == null) {
						filterField = new Field(filterFieldName,
								filterFieldValue.toString(), Field.Store.YES,
								Field.Index.ANALYZED_NO_NORMS);
						document.add(filterField);
					} else
						filterField.setValue(filterFieldValue.toString());
				}	
				
				// we're storing the original title and content now
				//Title field
				Field titleField = document.getField(TICKET_TITLE_FIELD);
				String titleString = "";

				ITicket originalTicket = processedTicket.getOriginalTicket();
				titleString = (String)originalTicket.getField("OriginalTitle");
				if(com.softlib.imatch.common.StringUtils.isEmpty(titleString))
					titleString = originalTicket.getTitle();
				if(titleField == null) {
					titleField = 
						new Field(TICKET_TITLE_FIELD, titleString, 
								  Field.Store.YES, Field.Index.ANALYZED_NO_NORMS);	
					document.add(titleField);
				}
				else
					titleField.setValue(titleString);				
				
				//body field
				Field bodyField = document.getField(TICKET_CONTENT_FIELD);
				String bodyString = "";
				bodyString = (String)originalTicket.getField("Text");
				if(bodyField == null) {
					bodyField = 
						new Field(TICKET_CONTENT_FIELD, bodyString, 
								  Field.Store.YES, Field.Index.ANALYZED_NO_NORMS);	
					document.add(bodyField);
				}
				else
					bodyField.setValue(bodyString);						
				
				// save url for later fetch 
				Field urlField = document.getField(TICKET_URL_FIELD);
				String urlString = "";
				urlString = (String)originalTicket.getField("url");
				if(urlField == null && urlString != null) {
					urlField = 
						new Field(TICKET_URL_FIELD, urlString, 
								  Field.Store.YES, Field.Index.NO);	
					document.add(urlField);
				}
				else if (urlField != null)
					urlField.setValue(urlString);
				
				// save links for later fetch 
				Field linksField = document.getField(TICKET_LINKS_FIELD);
				String linksString = "";
				linksString = (String)originalTicket.getField("Links");
				if(linksField == null && linksString != null) {
					linksField = 
						new Field(TICKET_LINKS_FIELD, linksString, 
								  Field.Store.YES, Field.Index.NO);	
					document.add(linksField);
				}
				else if (linksField != null)
					linksField.setValue(linksString);
				
				indexWriter.addDocument(document);
			} 
			catch (Exception e) {
				throw new MatcherException(e.getMessage(), e);
			}
		}
	}

	@Override
	protected Set<String> getStopWords()
	{
		return new HashSet<String>();
	}

	private Term getId(Document document) {
		Field field = document.getField(TICKET_ID_FIELD);
		return getId(field.stringValue());
	}
	
	private Term getId(String id) {
		return new Term(TICKET_ID_FIELD,id);
	}

	private boolean isTermExistInFieldStr(String termStr,String fieldStr) {
		if (fieldStr.equals(termStr))
			return true;
		if(fieldStr.startsWith(termStr+TERM_SEPERATOR))
			return true;
		if(fieldStr.endsWith(TERM_SEPERATOR+termStr))
			return true;
		if(fieldStr.contains(TERM_SEPERATOR+termStr+TERM_SEPERATOR))
			return true;
		return false;
	}
	
	private String addTermToFieldStr(String termStr,String fieldStr) {
		if (isTermExistInFieldStr(termStr,fieldStr))
			return fieldStr;
		if (fieldStr==null || fieldStr.equals(""))
			return termStr;
		return fieldStr + TERM_SEPERATOR + termStr;

	}
    
	private String removeTermFromFieldStr(String termStr,String fieldStr) {
		if (fieldStr.equals(termStr))
			return "";
		if(fieldStr.startsWith(termStr+TERM_SEPERATOR))
			return fieldStr.substring(termStr.length()+TERM_SEPERATOR.length());
		if(fieldStr.endsWith(TERM_SEPERATOR+termStr))
			return fieldStr.substring(0,fieldStr.length()-termStr.length()-TERM_SEPERATOR.length());
		String rc = fieldStr.replace(TERM_SEPERATOR+termStr+TERM_SEPERATOR, TERM_SEPERATOR);
		if (!rc.equals(fieldStr))
			return rc;
		return fieldStr;
	}

	private String removeAllTermFromFieldStr(String termStr,String fieldStr) {
		String beforeRemove = "";
		String afterRemove = fieldStr;
		while (!beforeRemove.equals(afterRemove)) {
			beforeRemove = afterRemove;
			afterRemove = removeTermFromFieldStr(termStr,beforeRemove);
		}
		return afterRemove;
	}
	
	private enum Function {
		Remove,Add
	}

	public void addTerm(TechnicalDictionaryTerm term,Document document) throws MatcherException {
		modifyTerm(term.getTermStemmedText(),document,Function.Add);
	}
	
	public void removeTerm(TechnicalDictionaryTerm term,Document document) throws MatcherException {
		modifyTerm(term.getTermStemmedText(),document,Function.Remove);
	}

	private void modifyTerm(String termStr,Document document,Function func) throws MatcherException {
		if (document==null)
			throw new MatcherException("Unable to add Term to document, reason : id not exist");

		ITicketFieldsNames fieldsNamesConfig = new DBTicketFieldsNames();
		Set<String> fieldsNames = fieldsNamesConfig.getAllFields(MatchMode.all);

		// TODO: Bug: add term to the first field found
		boolean modified = false;
		for (String fieldName : fieldsNames) {
			Field field = document.getField(fieldName);
			if (field==null)
				continue;
			document.removeField(fieldName);
			String modifyStr;
			if (func==Function.Remove) {
				String originalStr = field.stringValue();
				modifyStr = removeAllTermFromFieldStr(termStr,originalStr);
				if (originalStr.equals(modifyStr))
					continue;
			}
			else
				modifyStr = addTermToFieldStr(termStr,field.stringValue());
			field.setValue(modifyStr);
			document.add(field);
			modified = true;
			break;
		}
		
		if (!modified)
			LogUtils.error(log,"Unable to %s Term to document, reason : no field was found",func.name());
		
		try {
			indexWriter.updateDocument(getId(document),document);
		} catch (Exception e) {
			throw new MatcherException("Unable to update document, reason " + e.getMessage());
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
