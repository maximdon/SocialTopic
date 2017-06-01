package com.softlib.imatch.ticketprocessing;

import com.softlib.imatch.MatcherException;

public interface IDocFreqProvider {

	int getDf(String item) throws MatcherException;
	int getNumDocs() throws MatcherException;
}
