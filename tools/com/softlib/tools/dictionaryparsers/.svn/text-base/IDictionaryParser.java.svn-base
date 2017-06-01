package com.softlib.tools.dictionaryparsers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;

import org.hibernate.Session;

import com.softlib.imatch.ITicket;
import com.softlib.imatch.MatcherException;
import com.softlib.imatch.dictionary.ITechnicalDictionary;
import com.softlib.imatch.dictionary.TechnicalTermSource;

public interface IDictionaryParser {

	//TODO redefine thrown exceptions
	void parse(ITicket ticket) throws IOException, SQLException, MatcherException;	
	void buildRelations(ITechnicalDictionary dictionary, Session session) throws IOException, SQLException;
	Collection<TechnicalTermSource> getSources();
	void init() throws MatcherException;
	void end(); 
	
}