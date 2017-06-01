package com.softlib.imatch.dbintegration;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MillisecondsToDateStatementAnalyzer extends StatementAnalyzer {

	public MillisecondsToDateStatementAnalyzer() {
		super();
	}

	public MillisecondsToDateStatementAnalyzer(StatementAnalyzer previousAnalyzer) {
		super(previousAnalyzer);
	}

	@Override
	protected String analyzeQueryInternal(String query,	List<Object> queryParams) {
		long milliseconds = (Long) queryParams.get(0);
		Date d = new Date(milliseconds);
		//TODO check there, we need to be able to support custom formats as specified by SQL statement
		queryParams.set(0, new SimpleDateFormat("yyyyMMdd").format(d));
		return query;
	}

}
