package com.softlib.imatch.test.tools;


import junit.framework.Assert;

import org.apache.log4j.xml.DOMConfigurator;
import org.junit.BeforeClass;
import org.junit.Test;

import com.softlib.imatch.ConsoleAppRuntimeInfo;
import com.softlib.imatch.InMemoryTicket;
import com.softlib.imatch.MatcherException;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.dictionary.TechnicalDictionary;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.matcher.lucene.LuceneIndexer;
import com.softlib.imatch.ticketprocessing.IProcessedTicket;
import com.softlib.imatch.ticketprocessing.ITicketProcessor;
import com.softlib.imatch.ticketprocessing.TicketProcessor;
import com.softlib.imatch.ticketprocessing.IProcessedTicket.MatchMode;

public class IndexerTest {
	private static TechnicalDictionary dictionary;
	private static LuceneIndexer indexer;
	
	@BeforeClass
	public static void init()
	{
		ConsoleAppRuntimeInfo.init(null);
		DOMConfigurator.configure(RuntimeInfo.getCurrentInfo().getRealPath("/{SolutionConfigFolder}/log4j-console.xml"));
		dictionary = (TechnicalDictionary) RuntimeInfo.getCurrentInfo().getBean("dictionary");
		dictionary.loadDictionary();
		indexer = (LuceneIndexer) RuntimeInfo.getCurrentInfo().getBean("lucene.indexer");
	}

	@Test
	public void testIndexTicket()
	{
		InMemoryTicket ticket = new InMemoryTicket("", "", "ef:00D7JwLo.50079M87y:ref  Jim Beck Support Engineer www.precise.com --------------------------------------------------- Precise Customer Support: 1-877-845-1886 ---------------------------------------------------\"I also found on the top of page 35 of the version 8.5 Precise i³ Deployment Best Practices Guide: Avoiding SA privileges when monitoring an SQL Server.\"  Good, you found it! I knew we had something in either the FAQ or the Installation Guide.");
		ITicketProcessor ticketProcessor = (ITicketProcessor) RuntimeInfo.getCurrentInfo().getBean("ticketProcessor");
		try {
			IProcessedTicket processedTicket = ticketProcessor.processTicket(TicketProcessor.StepsMode.Index,
																			 ticket,MatchMode.all,null,false);
			indexer.index(processedTicket);
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}	

	@Test
	public void testProcessTicket()
	{
		InMemoryTicket ticket = new InMemoryTicket("", "", "after we upgrade j2ee to the 8.0.2 xxx from 7.5.1 it failed");
		ITicketProcessor ticketProcessor = (ITicketProcessor) RuntimeInfo.getCurrentInfo().getBean("ticketProcessor");
		try {
			IProcessedTicket processedTicket = ticketProcessor.processTicket(TicketProcessor.StepsMode.Index,
					 														 ticket,MatchMode.all,null,false);
			for (TechnicalDictionaryTerm term : processedTicket.getAllTerms(false)) {
				System.out.println(" Term="+term.getTermText());
			}
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}	
}
