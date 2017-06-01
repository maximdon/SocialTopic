package com.softlib.tools.dictionaryapi;


import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("ParseQueryResponse")
public class DictAPIResponseOk {

	@XStreamAlias("Header")
	private DictAPIResponseHeader header;

	@XStreamAlias("Body")
	private DictAPIResponseBody body;
	

	public DictAPIResponseHeader getHeader() {
		return header;
	}

	public void setHeader(DictAPIResponseHeader header) {
		this.header = header;
	}

	public DictAPIResponseBody getBody() {
		return body;
	}

	public void setBody(DictAPIResponseBody body) {
		this.body = body;
	}

	public DictAPIResponseOk(DictAPIResponseData data) {
		header = new DictAPIResponseHeader(data.getRequestId(),data.getLibraryId());
		body = new DictAPIResponseBody(data.getText(),data.getTermsData());
	}
	
};
