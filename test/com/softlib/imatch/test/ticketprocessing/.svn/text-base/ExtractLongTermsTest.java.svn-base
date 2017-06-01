package com.softlib.imatch.test.ticketprocessing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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
import com.softlib.imatch.dictionary.TechnicalDictionaryKey;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.ticketprocessing.ExtractTechTokensByNLP;
import com.softlib.imatch.ticketprocessing.ExtractVersionsByNLP;
import com.softlib.imatch.ticketprocessing.ProcessedTicket;
import com.softlib.imatch.ticketprocessing.StepContext;
import com.softlib.imatch.ticketprocessing.TechTokens;

public class ExtractLongTermsTest {

	@BeforeClass
	public static void init()
	{
		ConsoleAppRuntimeInfo.init(null);
		DOMConfigurator.configure(RuntimeInfo.getCurrentInfo().getRealPath("/{SolutionConfigFolder}/log4j-console.xml"));
		RuntimeInfo.getCurrentInfo().startThread();
	}
	
	private ITechnicalTermsContainer getTermsContainer() {
		TechnicalDictionary dictionary = (TechnicalDictionary) RuntimeInfo.getCurrentInfo().getBean("dictionary");
		return dictionary;
	}
	
	private List<String> getTermsData(List<TechnicalDictionaryTerm> terms) {
		List<String> rc = new ArrayList<String>();
		for (TechnicalDictionaryTerm term : terms)
			rc.add(term.getTermText());
		return rc;
	}

	@Test
	public void testAllCapitalWordBeggingOfSentence()
	{
		String text = "HSQLDBC good database";
		TechTokens step = new TechTokens("longTerms");
		ProcessedTicket processedTicket = new ProcessedTicket(null, null);
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(text);
		try {
			//Test specific error code finds it
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("TestField", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("hsqldbc", true);
			Assert.assertNotNull(term);
			term.addFrequency();
			step.run("TestField", ticket, processedTicket, stepContext);
			List<String> extractedTokens = getTermsData(processedTicket.getField("TestField").getTerms());
			Assert.assertEquals(1, extractedTokens.size());
			getTermsContainer().getDictionary().unloadDictionary();
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}
	@Test
	public void testAllCapitalWordEndOfSentence()
	{
		String text = "good database HSQLDBC.";
		TechTokens step = new TechTokens("longTerms");
		ProcessedTicket processedTicket = new ProcessedTicket(null, null);
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(text);
		try {
			//Test specific error code finds it
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("TestField", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("hsqldbc", true);
			Assert.assertNotNull(term);
			term.addFrequency();
			step.run("TestField", ticket, processedTicket, stepContext);
			List<String> extractedTokens = getTermsData(processedTicket.getField("TestField").getTerms());
			Assert.assertEquals(1, extractedTokens.size());
			getTermsContainer().getDictionary().unloadDictionary();
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}
	@Test
	public void testAllCapitalWordMiddleOfSentence()
	{
		String text = "good database HSQLDBC very good";
		TechTokens step = new TechTokens("longTerms");
		ProcessedTicket processedTicket = new ProcessedTicket(null, null);
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(text);
		try {
			//Test specific error code finds it
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("TestField", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("hsqldbc", true);
			Assert.assertNotNull(term);
			term.addFrequency();
			step.run("TestField", ticket, processedTicket, stepContext);
			List<String> extractedTokens = getTermsData(processedTicket.getField("TestField").getTerms());
			Assert.assertEquals(1, extractedTokens.size());
			getTermsContainer().getDictionary().unloadDictionary();
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}
	@Test
	public void testNotAllCapitalWordMiddleOfSentence()
	{
		String text = "good database Oracle not very good";
		TechTokens step = new TechTokens("longTerms");
		ProcessedTicket processedTicket = new ProcessedTicket(null, null);
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(text);
		try {
			//Test specific error code finds it
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("TestField", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("oracle", true);
			Assert.assertNull(term);
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}
	@Test
	public void testThreeWordsOnlyFirstCapital()
	{
		String text = "good database Oracle Database App not very good";
		TechTokens step = new TechTokens("longTerms");
		ProcessedTicket processedTicket = new ProcessedTicket(null, null);
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(text);
		try {
			//Test specific error code finds it
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("TestField", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("oracle database app", true);
			Assert.assertNull(term);
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}
	@Test
	public void testThreeWordsFirstAllCapital()
	{
		String text = "good database HSQLDB Database App not very good";
		TechTokens step = new TechTokens("longTerms");
		ProcessedTicket processedTicket = new ProcessedTicket(null, null);
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(text);
		try {
			//Test specific error code finds it
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("TestField", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("hsqldb database app", true);
			Assert.assertNotNull(term);
			term.addFrequency();
			step.run("TestField", ticket, processedTicket, stepContext);
			List<String> extractedTokens = getTermsData(processedTicket.getField("TestField").getTerms());
			Assert.assertEquals(1, extractedTokens.size());
			getTermsContainer().getDictionary().unloadDictionary();
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}
	@Test
	public void testThreeWordsFirstMix()
	{
		String text = "good database HsqlDB Database App not very good";
		TechTokens step = new TechTokens("longTerms");
		ProcessedTicket processedTicket = new ProcessedTicket(null, null);
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(text);
		try {
			//Test specific error code finds it
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("TestField", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("hsqldb database app", true);
			Assert.assertNotNull(term);
			term.addFrequency();
			step.run("TestField", ticket, processedTicket, stepContext);
			List<String> extractedTokens = getTermsData(processedTicket.getField("TestField").getTerms());
			Assert.assertEquals(1, extractedTokens.size());
			getTermsContainer().getDictionary().unloadDictionary();
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}
	@Test
	public void testThreeWordsFirstAllCapitalEndOfSentence()
	{
		String text = "Very good HSQLDB Database App\nNext Line";
		TechTokens step = new TechTokens("longTerms");
		ProcessedTicket processedTicket = new ProcessedTicket(null, null);
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(text);
		try {
			//Test specific error code finds it
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("TestField", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("hsqldb database app", true);
			Assert.assertNotNull(term);
			term.addFrequency();
			step.run("TestField", ticket, processedTicket, stepContext);
			List<String> extractedTokens = getTermsData(processedTicket.getField("TestField").getTerms());
			Assert.assertEquals(1, extractedTokens.size());
			getTermsContainer().getDictionary().unloadDictionary();
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testThreeWordsFirstAllCapitalWithComma()
	{
		String text = "Very good HSQLDB Database App, Next Line";
		TechTokens step = new TechTokens("longTerms");
		ProcessedTicket processedTicket = new ProcessedTicket(null, null);
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(text);
		try {
			//Test specific error code finds it
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("TestField", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("hsqldb database app", true);
			Assert.assertNotNull(term);
			term.addFrequency();
			step.run("TestField", ticket, processedTicket, stepContext);
			List<String> extractedTokens = getTermsData(processedTicket.getField("TestField").getTerms());
			Assert.assertEquals(1, extractedTokens.size());
			getTermsContainer().getDictionary().unloadDictionary();
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testODOTerm()
	{
		String text = "	Can you confirm that it will work for any OCCURS DEPENDING ON cases or should we wait for the official fix before going on with the decommissioning of flat files with flat files with ODO ?  Irv Irv Mandelbaum Optim Technical Support IBM";
		TechTokens step = new TechTokens("longTerms");
		ProcessedTicket processedTicket = new ProcessedTicket(null, null);
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(text);
		try {
			//Test specific error code finds it
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("TestField", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("odo", true);
			Assert.assertNotNull(term);
			term.addFrequency();
			step.run("TestField", ticket, processedTicket, stepContext);
			List<String> extractedTokens = getTermsData(processedTicket.getField("TestField").getTerms());
			Assert.assertEquals(1, extractedTokens.size());
			getTermsContainer().getDictionary().unloadDictionary();
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testWordnetTerm()
	{
		String text = "Activity Date: 3/30/2009 06:05 PM Type: HL-Patch Provided   Author: Dan Cleary Security: Public   Description: Elaine,  With regards to this issue and a development commitment to the $$USERID token in QP";
		TechTokens step = new TechTokens("longTerms");
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(text);
		try {
			//Test specific error code finds it
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("TestField", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("provided", true);
			Assert.assertNull(term);
			getTermsContainer().getDictionary().unloadDictionary();
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testStopWordTerm()
	{
		String text = "MODIFICATION AND ACCURACY OF, INFORMATION";
		TechTokens step = new TechTokens("longTerms");
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(text);
		try {
			//Test specific error code finds it
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("TestField", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("and", true);
			Assert.assertNull(term);
			TechnicalDictionaryTerm term2 = getTermsContainer().getDictionary().get("modification", true);
			Assert.assertNotNull(term2);
			TechnicalDictionaryTerm term3 = getTermsContainer().getDictionary().get("accuracy", true);
			Assert.assertNotNull(term3);
			TechnicalDictionaryTerm term5 = getTermsContainer().getDictionary().get("of", true);
			Assert.assertNull(term5);
			getTermsContainer().getDictionary().unloadDictionary();
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}
}
