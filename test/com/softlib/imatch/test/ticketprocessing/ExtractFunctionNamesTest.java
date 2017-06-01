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
import com.softlib.imatch.dictionary.TechnicalDictionaryKey;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.ticketprocessing.ExtractVersionsByNLP;
import com.softlib.imatch.ticketprocessing.ProcessedTicket;
import com.softlib.imatch.ticketprocessing.StepContext;
import com.softlib.imatch.ticketprocessing.TechTokens;

public class ExtractFunctionNamesTest {

	@BeforeClass
	public static void init()
	{
		ConsoleAppRuntimeInfo.init(null);
		DOMConfigurator.configure(RuntimeInfo.getCurrentInfo().getRealPath("/{SolutionConfigFolder}/log4j-console.xml"));
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
	public void testFunctionName()
	{
		String text = "Please run testFoo() method";
		TechTokens step = new TechTokens("functionNames");
		ProcessedTicket processedTicket = new ProcessedTicket(null, null);
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(text);
		try {
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("TestField", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("testFoo", true);
			Assert.assertNotNull(term);
			term.addFrequency();
			step.run("TestField", ticket, processedTicket, stepContext);
			List<String> extractedTokens = getTermsData(processedTicket.getField("TestField").getTerms());
			Assert.assertEquals(1, extractedTokens.size());
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testFunctionNameBegginingOfLine()
	{
		String text = "testFoo() method is good in your case";
		TechTokens step = new TechTokens("functionNames");
		ProcessedTicket processedTicket = new ProcessedTicket(null, null);
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(text);
		try {
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("TestField", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("testFoo", true);
			Assert.assertNotNull(term);
			term.addFrequency();
			step.run("TestField", ticket, processedTicket, stepContext);
			List<String> extractedTokens = getTermsData(processedTicket.getField("TestField").getTerms());
			Assert.assertEquals(1, extractedTokens.size());
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testFunctionNameEndOfLine()
	{
		String text = "Please run this method: testFoo()";
		TechTokens step = new TechTokens("functionNames");
		ProcessedTicket processedTicket = new ProcessedTicket(null, null);
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(text);
		try {
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("TestField", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("testFoo", true);
			Assert.assertNotNull(term);
			term.addFrequency();
			step.run("TestField", ticket, processedTicket, stepContext);
			List<String> extractedTokens = getTermsData(processedTicket.getField("TestField").getTerms());
			Assert.assertEquals(1, extractedTokens.size());
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testClassName()
	{
		String text = "Attunity Stream from NonStop server";
		TechTokens step = new TechTokens("functionNames");
		ProcessedTicket processedTicket = new ProcessedTicket(null, null);
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(text);
		try {
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("TestField", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("NonStop", true);
			Assert.assertNotNull(term);
			term.addFrequency();
			step.run("TestField", ticket, processedTicket, stepContext);
			List<String> extractedTokens = getTermsData(processedTicket.getField("TestField").getTerms());
			Assert.assertEquals(1, extractedTokens.size());
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testClassNameBegginingOfLine()
	{
		String text = "NonStop server for Attunity Stream is good";
		TechTokens step = new TechTokens("functionNames");
		ProcessedTicket processedTicket = new ProcessedTicket(null, null);
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(text);
		try {
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("TestField", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("NonStop", true);
			Assert.assertNotNull(term);
			term.addFrequency();
			step.run("TestField", ticket, processedTicket, stepContext);
			List<String> extractedTokens = getTermsData(processedTicket.getField("TestField").getTerms());
			Assert.assertEquals(1, extractedTokens.size());
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testClassNameEndOfLine()
	{
		String text = "Please run this class: NonStop";
		TechTokens step = new TechTokens("functionNames");
		ProcessedTicket processedTicket = new ProcessedTicket(null, null);
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(text);
		try {
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("TestField", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("NonStop", true);
			Assert.assertNotNull(term);
			term.addFrequency();
			step.run("TestField", ticket, processedTicket, stepContext);
			List<String> extractedTokens = getTermsData(processedTicket.getField("TestField").getTerms());
			Assert.assertEquals(1, extractedTokens.size());
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}
}
