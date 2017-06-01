package com.softlib.imatch.test.dbintegration;

import java.util.List;

import com.softlib.imatch.dbintegration.StatementAnalyzer;

public class AdvanNetServiceDeskStatementAnalyzer extends StatementAnalyzer {

	public AdvanNetServiceDeskStatementAnalyzer() {
	}

	public AdvanNetServiceDeskStatementAnalyzer(StatementAnalyzer previousAnalyzer) {
		super(previousAnalyzer);
	}

	@Override
	protected String analyzeQueryInternal(String query,	List<Object> queryParams) {
		long milliseconds;
		try {
			milliseconds = (Long)queryParams.get(0);
		}
		catch(Exception e)
		{
			throw new RuntimeException("Unexpected argument for AdvanNet ServiceDesk query analyzer");
		}
		queryParams.clear();
		for(int i = 0; i < 3; ++i)
			//This query requires 3 parameters all of them are equal in milliseconds format
			queryParams.add(milliseconds);
			
		return query;
	}

}
