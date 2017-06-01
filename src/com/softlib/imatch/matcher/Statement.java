package com.softlib.imatch.matcher;

import org.apache.commons.configuration.ConfigurationException;

import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.dbintegration.DBTicketCompleter;
import com.softlib.imatch.dbintegration.NullTicketCompleter;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class Statement implements Converter {
	private String statementString;
	private String analyzerName;
	private String completerName;
	private DBTicketCompleter completer;
	private String[] concatFields = new String[0];
	               
	public void setStatementString(String statementString) {
		this.statementString = statementString;
	}
	
	public String getStatementString() {
		return statementString;
	}
	
	public void setConcatFields(String[] concatFields) {
		this.concatFields = concatFields;
	}
	
	public String[] getConcatFields() {
		return concatFields;
	}
	
	public void setAnalyzerName(String analyzerName) {
		this.analyzerName = analyzerName;
	}
	
	public String getAnalyzerName() {
		return analyzerName;
	}
	
	public void setCompleterName(String completerName) {
		this.completerName = completerName;
	}
	
	public String getCompleterName() {
		return completerName;
	}
	
	public DBTicketCompleter getCompleter() {
		if(completer != null)
			return completer;
		if (getCompleterName() != null)
			completer = (DBTicketCompleter) RuntimeInfo.getCurrentInfo().getBean(getCompleterName());
		else
			completer = NullTicketCompleter.getCompleter();
		return completer;
	}
	
	public void marshal(Object source, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		// TODO implement		
	}
	
	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) {
		Statement statement = new Statement();
		if(reader.getAttribute("queryAnalyzer") != null)
			statement.analyzerName = reader.getAttribute("queryAnalyzer");
		if(reader.getAttribute("ticketCompleter") != null)
			statement.completerName = reader.getAttribute("ticketCompleter");
		if(reader.getAttribute("concatFields") != null) {
			String concatFieldStr = reader.getAttribute("concatFields");
			statement.concatFields = concatFieldStr.split(",");
		}
		else
			statement.concatFields = new String[0];
		//TODO find solution for ,
		statement.statementString = reader.getValue().replaceAll("_COMMA_", ",");
		return statement;
	}
	
	@SuppressWarnings("unchecked")
	public boolean canConvert(Class type) {
		return type.equals(Statement.class);
	}
	
};
