package com.softlib.tools.dictionaryapi;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("ParseQueryResponseError")
public class DictAPIResponseError {

	@XStreamAlias("Header")
	private DictAPIResponseHeader header;

	@XStreamAlias("ErrorMsg")
	private String ErrorMsg;

	public String getErrorMsg() {
		return ErrorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		ErrorMsg = errorMsg;
	}

	public DictAPIResponseHeader getHeader() {
		return header;
	}

	public void setHeader(DictAPIResponseHeader header) {
		this.header = header;
	}

	public DictAPIResponseError(DictAPIResponseData data) {
		header = new DictAPIResponseHeader(data.getRequestId(),data.getLibraryId());
		ErrorMsg = data.getErrorMsg();
	}

};