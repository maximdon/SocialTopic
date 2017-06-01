package com.softlib.imatch.dbintegration;

import java.util.List;

/**
 * 
 * @author Maxim Donde
 *
 */
public abstract class StatementAnalyzer 
{
	private StatementAnalyzer previousAnalyzer;
	
	public StatementAnalyzer()
	{
		this(null);
	}
	
	public StatementAnalyzer(StatementAnalyzer previousAnalyzer)
	{
		this.previousAnalyzer = previousAnalyzer;
	}
	
	public String analyzeQuery(String query, List<Object> queryParams)
	{
		String previousQuery;
		if(previousAnalyzer != null)
			previousQuery = previousAnalyzer.analyzeQuery(query, queryParams);
		else
			previousQuery = query;
		return analyzeQueryInternal(previousQuery, queryParams);
	}

	protected abstract String analyzeQueryInternal(String query, List<Object> queryParams);
}
