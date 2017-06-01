package com.softlib.imatch.ticketprocessing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.didion.jwnl.data.POS;

import com.softlib.imatch.ITicket;
import com.softlib.imatch.StageMngr;
import com.softlib.imatch.StageMngr.Stage;
import com.softlib.imatch.common.Pair;
import com.softlib.imatch.dictionary.TechnicalDictionary;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.dictionary.WriteTechnicalDictionary;

public class ProcessedTicketWrite extends ProcessedTicket {
	
	private final static String EXTRACT_PREFIX = "E_";
	private final static String POSTEXTRACT_PREFIX= "P_";
	public final static String SEPERATOR = " $a$ ";
	private TechnicalDictionary dictionary;
	
	public ProcessedTicketWrite(TechnicalDictionary dictionary, ITicket originalTicket) {
		this.dictionary = dictionary;
		init(originalTicket, null, false, false);		
	}

	public void finish() {
		List<Pair<String,String>> values = new ArrayList<Pair<String,String>>();
		
		for (String fieldName : getData().keySet()) {
			ProcessedField field = getData().get(fieldName);
			List<TechnicalDictionaryTerm> fieldTerms = field.getTerms();
			if (!fieldTerms.isEmpty()) {
				String data = fieldData(fieldTerms);
				values.add(new Pair<String, String>(generateFieldName(StageMngr.instance().getStage(), getOriginObjectId(), fieldName),data));
			}
		}
		ProcessedTicketWritter.getInstance().addData(getId(),values);
	}
	
	public static String generateFieldName(Stage stage, String objectId, String fieldName) {
		String fieldPrefix = stage.equals(Stage.Extract) ? EXTRACT_PREFIX : POSTEXTRACT_PREFIX;
		return fieldPrefix + objectId + "_" + fieldName;
	}
	private String fieldData(List<TechnicalDictionaryTerm> terms) {
		String rc = "";
		boolean first = true;
		for (TechnicalDictionaryTerm term : terms) {
			if (first) 
				first = false;
			else 
				rc += SEPERATOR;
			
			rc = rc + term.getTermStemmedText();
		}
		return rc;
	}
	
	protected void loadDictionary() {
		localDictionary = new WriteTechnicalDictionary(dictionary);
	}

	static boolean isDataField(String fieldName) {
		return fieldName.startsWith(EXTRACT_PREFIX) || fieldName.startsWith(POSTEXTRACT_PREFIX);
	}
	
	static String[] parseFieldName(String fieldName) 
	{
		if(!isDataField(fieldName))
			//Field is not data field, unable to parse
			return null;
		String stageName = null;
		int objectIdStartPos = -1;
		if(fieldName.startsWith(EXTRACT_PREFIX)) {
			stageName = fieldName.substring(0, EXTRACT_PREFIX.length() - 1);
			objectIdStartPos = EXTRACT_PREFIX.length();
		}
		else {
			stageName = fieldName.substring(0, POSTEXTRACT_PREFIX.length() - 1);
			objectIdStartPos = POSTEXTRACT_PREFIX.length();
		}
		//Assume objectId doesn't contain _
		int objectIdEndPos = fieldName.indexOf('_', objectIdStartPos + 1);
		String objectId = fieldName.substring(objectIdStartPos, objectIdEndPos);
		String originalFieldName = fieldName.substring(objectIdEndPos + 1);
		return new String[] {stageName, objectId, originalFieldName};
	}
	
	static List<String> parseField(String field) {
		return Arrays.asList(field.split(" \\$a\\$ "));
	}
};
