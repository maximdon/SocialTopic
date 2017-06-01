package com.softlib.tools.dictionaryapi;

import java.util.ArrayList;
import java.util.List;

import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;

public class DictAPIResponseData {
	
	private String text;
	private String errorMsg;
	private String requestId;
	private String libraryId;
	
	private List<DictAPIResponseDataTerm> termsData;
	
	public DictAPIResponseData(String text,String requestId,String libraryId, List<DictAPIResponseDataTerm> termsData) {
		this.requestId = requestId;
		this.libraryId = libraryId;
		this.termsData = termsData;
		errorMsg = "";
		this.text = text;
	}
	
	public String getText() {
		return text;
	}

	public String getRequestId() {
		return requestId;
	}

	public String getLibraryId() {
		return libraryId;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public boolean isError() {
		return !errorMsg.isEmpty();
	}

	public List<DictAPIResponseDataTerm> getTermsData() {
		return termsData;
	}

	public List<TechnicalDictionaryTerm> getTerms() {
		List<TechnicalDictionaryTerm> rc = new ArrayList<TechnicalDictionaryTerm>();
		for (DictAPIResponseDataTerm dataTerm : termsData)
			rc.add(dataTerm.getTerm());
		return rc;
	}
	
};
