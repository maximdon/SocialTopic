package com.softlib.tools.dictionaryapi;

import com.thoughtworks.xstream.annotations.XStreamAlias;

public class DictAPIResponseHeader {

	@XStreamAlias("RequestId")
	private String RequestId;

	@XStreamAlias("LibraryId")
	private String LibraryId;
	

	public String getRequestId() {
		return RequestId;
	}

	public void setRequestId(String requestId) {
		RequestId = requestId;
	}

	public String getLibraryId() {
		return LibraryId;
	}

	public void setLibraryId(String libraryId) {
		LibraryId = libraryId;
	}

	public DictAPIResponseHeader(String requestId,String libraryId) {
		this.RequestId = requestId;
		this.LibraryId = libraryId;
	}
	
	public String toString() {
		return "RequestId="+RequestId+",LibraryId="+LibraryId;
	}

};
