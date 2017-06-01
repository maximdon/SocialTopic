package com.softlib.imatch.matcher;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;

@XStreamAlias("authorize")
public class AuthorizeConfig {

	@XStreamAsAttribute
	@XStreamAlias("type")
	private String type;
	private int batchSize;
	@XStreamConverter(Statement.class)
	private Statement query = new Statement();
	
	public void setType(String type) {
		this.type =type;
	}
	public String getType() {
		return type;
	}
	public int getBatchSize() {
		return batchSize;
	}
	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}
	public Statement getQuery() {
		return query;
	}
	public void setQuery(Statement query) {
		this.query = query;
	}


};