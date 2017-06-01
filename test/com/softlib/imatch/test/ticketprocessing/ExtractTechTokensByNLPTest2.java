package com.softlib.imatch.test.ticketprocessing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import junit.framework.Assert;

import org.apache.log4j.xml.DOMConfigurator;
import org.junit.BeforeClass;
import org.junit.Test;

import com.softlib.imatch.ConsoleAppRuntimeInfo;
import com.softlib.imatch.InMemoryTicket;
import com.softlib.imatch.MatcherException;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.dictionary.ITechnicalTermsContainer;
import com.softlib.imatch.dictionary.TechnicalDictionary;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.dictionary.TechnicalTermSource;
import com.softlib.imatch.dictionary.Wordnet;
import com.softlib.imatch.ticketprocessing.ExtractTechTokensByNLP;
import com.softlib.imatch.ticketprocessing.ExtractVersionsByNLP;
import com.softlib.imatch.ticketprocessing.ProcessedTicket;
import com.softlib.imatch.ticketprocessing.StepContext;

public class ExtractTechTokensByNLPTest2 {

	private static TechnicalDictionary dictionary;
	
	@BeforeClass
	public static void init()
	{
		ConsoleAppRuntimeInfo.init(null);
		DOMConfigurator.configure(RuntimeInfo.getCurrentInfo().getRealPath("/{SolutionConfigFolder}/log4j-console.xml"));
		dictionary = (TechnicalDictionary) RuntimeInfo.getCurrentInfo().getBean("dictionary");
	}
	
	private ITechnicalTermsContainer getTermsContainer() {		
		return dictionary;
	}
	
	private List<String> getTermsData(Collection<TechnicalDictionaryTerm> collection) {
		List<String> rc = new ArrayList<String>();
		for (TechnicalDictionaryTerm term : collection)
			rc.add(term.getTermText());
		return rc;
	}


	@Test
	public void testComma() {
		String stackLine = "This tool is good for all Data Base vendors: MSSQL, DB2, Oracle Etc.";
		ExtractTechTokensByNLP step = new ExtractTechTokensByNLP();
		ProcessedTicket processedTicket = new ProcessedTicket(null, null);
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(stackLine);
		try {
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("TestField", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("data base vendor", true);
			Assert.assertNotNull(term);
			term.addFrequency();
			TechnicalDictionaryTerm term2 = getTermsContainer().getDictionary().get("mssql", true);
			Assert.assertNotNull(term2);
			term2.addFrequency();
			TechnicalDictionaryTerm term3 = getTermsContainer().getDictionary().get("db2", true);
			Assert.assertNotNull(term3);
			term3.addFrequency();
			TechnicalDictionaryTerm term4 = getTermsContainer().getDictionary().get("oracle", true);
			Assert.assertNotNull(term4);
			term4.addFrequency();
			step.run("TestField", ticket, processedTicket, stepContext);
			List<String> extractedTokens = getTermsData(processedTicket.getAllTerms(false));
			Assert.assertEquals(4, extractedTokens.size());
			getTermsContainer().getDictionary().unloadDictionary();
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}		
	
	@Test
	public void testComma2() {
		String stackLine = "This tool is good for all Data Base vendors: MSSQL, DB2, Orac.";
		ExtractTechTokensByNLP step = new ExtractTechTokensByNLP();
		ProcessedTicket processedTicket = new ProcessedTicket(null, null);
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(stackLine);
		try {
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("TestField", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("data base vendor", true);
			Assert.assertNotNull(term);
			term.addFrequency();
			TechnicalDictionaryTerm term2 = getTermsContainer().getDictionary().get("mssql", true);
			Assert.assertNotNull(term2);
			term2.addFrequency();
			TechnicalDictionaryTerm term3 = getTermsContainer().getDictionary().get("db2", true);
			Assert.assertNotNull(term3);
			term3.addFrequency();
			TechnicalDictionaryTerm term4 = getTermsContainer().getDictionary().get("orac", true);
			Assert.assertNotNull(term4);
			term4.addFrequency();
			step.run("TestField", ticket, processedTicket, stepContext);
			List<String> extractedTokens = getTermsData(processedTicket.getAllTerms(false));
			Assert.assertEquals(4, extractedTokens.size());
			getTermsContainer().getDictionary().unloadDictionary();
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}		
	
	@Test
	public void testTermEndOfLine() {
		String stackLine = "This tool is good for all Data Base vendors";
		ExtractTechTokensByNLP step = new ExtractTechTokensByNLP();
		ProcessedTicket processedTicket = new ProcessedTicket(null, null);
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(stackLine);
		try {
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("TestField", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("data base vendor", true);
			Assert.assertNotNull(term);
			term.addFrequency();
			step.run("TestField", ticket, processedTicket, stepContext);
			List<String> extractedTokens = getTermsData(processedTicket.getAllTerms(false));
			Assert.assertEquals(1, extractedTokens.size());
			getTermsContainer().getDictionary().unloadDictionary();
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}		
	
	@Test
	public void testTermEndOfLineWithDot() {
		String stackLine = "This tool is good for all Data Base vendors.";
		ExtractTechTokensByNLP step = new ExtractTechTokensByNLP();
		ProcessedTicket processedTicket = new ProcessedTicket(null, null);
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(stackLine);
		try {
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("TestField", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("data base vendor", true);
			Assert.assertNotNull(term);
			term.addFrequency();
			step.run("TestField", ticket, processedTicket, stepContext);
			List<String> extractedTokens = getTermsData(processedTicket.getAllTerms(false));
			Assert.assertEquals(1, extractedTokens.size());
			getTermsContainer().getDictionary().unloadDictionary();
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}		
	

	@Test
	public void testSingleSplit() {
		String stackLine = "This tool is good for all Attunot customers.";
		ExtractTechTokensByNLP step = new ExtractTechTokensByNLP();
		ProcessedTicket processedTicket = new ProcessedTicket(null, null);
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(stackLine);
		try {
			dictionary.loadDictionary();
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("TestField", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("attunot", true);
			Assert.assertNotNull(term);
			Assert.assertEquals(103, term.getTermSource().getSourceId());
			getTermsContainer().getDictionary().unloadDictionary();
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}		
	
	@Test
	public void testJavaStackTrace() {
		String stackLine = "child record found   at oracle.jdbc.driver.DatabaseError.throwSqlException(DatabaseError.java:112)  at oracle.jdbc.driver.T4CTTIoer.processError(T4CTTIoer.java:331)  at oracle.jdbc.driver.T4CTTIoer.processError(T4CTTIoer.java:288)  at oracle.jdbc.driver.T4C8Oall.receive(T4C8Oall.java:745)  at oracle.jdbc.driver.T4CStatement.doOall8(T4CStatement.java:210)  at oracle.jdbc.driver.T4CStatement.executeForRows(T4CStatement.java:961)  at oracle.jdbc.driver.OracleStatement.doExecuteWithTimeout(OracleStatement.java:1190)  at oracle.jdbc.driver.OracleStatement.executeInternal(OracleStatement.java:1726)  at oracle.jdbc.driver.OracleStatement.execute(OracleStatement.java:1696)  at com.precise.shared.connectionpool.StatementPres.execute(StatementPres.java:101)  at com.precise.shared.dbms.utils.SQLScriptHandler.executeSqlScript(SQLScriptHandler.java:380)  at com.precise.shared.dbms.utils.SQLScriptHandler.executeSqlScript(SQLScriptHandler.java:322)  at com.precise.shared.dbms.utils.SQLScriptHandler.run(SQLScriptHandler.java:276)  at com.precise.shared.dbms.utils.SQLScriptHandler.main(SQLScriptHandler.java:115)  ========================================  ";
		ExtractTechTokensByNLP step = new ExtractTechTokensByNLP();
		ProcessedTicket processedTicket = new ProcessedTicket(null, null);
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(stackLine);
		try {
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("TestField", ticket, getTermsContainer(), stepContext);
			step.run("TestField", ticket, processedTicket, stepContext);
			List<String> extractedTokens = getTermsData(processedTicket.getAllTerms(false));
			Assert.assertEquals(0, extractedTokens.size());
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}		
	
	@Test
	public void testSingleSplit2() {
		String stackLine = "each using a different amount of memory:    PeakPageFile PageFaults DirectIO BufferedIO";
		ExtractTechTokensByNLP step = new ExtractTechTokensByNLP();
		ProcessedTicket processedTicket = new ProcessedTicket(null, null);
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(stackLine);
		try {
			dictionary.loadDictionary();
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("TestField", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("pageFault", true);
			Assert.assertNotNull(term);
			Assert.assertEquals(103, term.getTermSource().getSourceId());
			getTermsContainer().getDictionary().unloadDictionary();
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}		
	
	@Test
	public void testSingleSplit3() {
		String stackLine = "IBM provide a KEEPDICTIONARY qualifier on the REORG to allow for the REORG to be done while preserving the existing dictionary";
		ExtractTechTokensByNLP step = new ExtractTechTokensByNLP();
		ProcessedTicket processedTicket = new ProcessedTicket(null, null);
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(stackLine);
		try {
			dictionary.loadDictionary();
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("TestField", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("keepdictionary", true);
			Assert.assertNotNull(term);
			Assert.assertEquals(103, term.getTermSource().getSourceId());
			getTermsContainer().getDictionary().unloadDictionary();
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}	
	
	@Test
	public void testTookOwnership() {
		String stackLine = "Took Ownership From Americas WEB/Email Queue";
		ExtractTechTokensByNLP step = new ExtractTechTokensByNLP();
		ProcessedTicket processedTicket = new ProcessedTicket(null, null);
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(stackLine);
		try {
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("TestField", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("took ownership", true);
			Assert.assertNull(term);
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}	
	
	@Test
	public void testPleaseNote() {
		String stackLine = "PLEASE NOTE: THE ABOVE MESSAGE WAS RECEIVED FROM THE INTERNET";
		ExtractTechTokensByNLP step = new ExtractTechTokensByNLP();
		ProcessedTicket processedTicket = new ProcessedTicket(null, null);
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(stackLine);
		try {
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("TestField", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("please note", true);
			Assert.assertNull(term);
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}	
	@Test
	public void testTheFollowing() {
		String stackLine = "PLEASE, UPDATE THE FOLLOWING STRING";
		ExtractTechTokensByNLP step = new ExtractTechTokensByNLP();
		ProcessedTicket processedTicket = new ProcessedTicket(null, null);
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(stackLine);
		try {
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("TestField", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("update the following", true);
			Assert.assertNull(term);
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}	
	@Test
	public void testGetThem() {
		String stackLine = "WHAT HAVE YOU TOLD THEM DURING THOSE SALES CYCLES TO GET THEM TO STILL WANT TO USE THE PRODUCT WITHOUT SECONDARY INDEXES?";
		ExtractTechTokensByNLP step = new ExtractTechTokensByNLP();
		ProcessedTicket processedTicket = new ProcessedTicket(null, null);
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(stackLine);
		try {
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("TestField", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("get them", true);
			Assert.assertNull(term);
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testWordnet() {
		Wordnet.getInstance();
		TechnicalTermSource src = getTermsContainer().getDictionary().addSource("NLP NNP Tokens Wordnet Split");
		src.setSourceId(43);
		String stackLine = "This tool is good for all Data Base vendors: MSSQL, DB2, Oracle Etc.";
		ExtractTechTokensByNLP step = new ExtractTechTokensByNLP();
		ProcessedTicket processedTicket = new ProcessedTicket(null, null);
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(stackLine);
		try {
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("TestField", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("data base vendor", true);
			Assert.assertNotNull(term);
			Assert.assertEquals(43, term.getTermSource().getSourceId());
			term.addFrequency();
			getTermsContainer().getDictionary().unloadDictionary();
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}		
	
	@Test
	public void testSoftlibSite() {
		Wordnet.getInstance();
		TechnicalTermSource src = getTermsContainer().getDictionary().addSource("NLP NNP Tokens Wordnet Split");
		src.setSourceId(43);
		String stackLine = "Ideal for Service organizations, Technical Support and Helpdesk organizations, iMatch automatically offers Self Help for end users or experts";
		ExtractTechTokensByNLP step = new ExtractTechTokensByNLP();
		ProcessedTicket processedTicket = new ProcessedTicket(null, null);
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(stackLine);
		try {
			InMemoryTicket ticket = new InMemoryTicket("vaObject", "Ticket title", "Ticket Body");
			step.run("TestField", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("technical support", true);
			Assert.assertNotNull(term);
			term.addFrequency();
			getTermsContainer().getDictionary().unloadDictionary();
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}		

	@Test
	public void testSoftlibSite2() {
		Wordnet.getInstance();
		TechnicalTermSource src = getTermsContainer().getDictionary().addSource("NLP NNP Tokens Wordnet Split");
		src.setSourceId(43);
		String stackLine = "Home Products Solutions Services Customers Resources About Overview iSolve iMatch iSolve Software Modules iMatch by Softlib – Automated Customer Service Support agent";
		ExtractTechTokensByNLP step = new ExtractTechTokensByNLP();
		ProcessedTicket processedTicket = new ProcessedTicket(null, null);
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(stackLine);
		try {
			InMemoryTicket ticket = new InMemoryTicket("vaObject", "Ticket title", "Ticket Body");
			step.run("TestField", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("customer service support", true);
			Assert.assertNotNull(term);
			term.addFrequency();
			getTermsContainer().getDictionary().unloadDictionary();
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}		
}
