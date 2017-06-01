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

public class ExtractErrorCodesTest {

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
	public void testSpecificErorCode()
	{
		String text = "Error ora-1111 from oracle";
		TechTokens specificStep = new TechTokens("errorCodes");
		TechTokens genericStep = new TechTokens("errorCodesGeneric");
		ProcessedTicket processedTicket = new ProcessedTicket(null, null);
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(text);
		try {
			//Test specific error code finds it
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			specificStep.run("TestField", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("ora-1111", true);
			Assert.assertNotNull(term);
			term.addFrequency();
			specificStep.run("TestField", ticket, processedTicket, stepContext);
			List<String> extractedTokens = getTermsData(processedTicket.getField("TestField").getTerms());
			Assert.assertEquals(1, extractedTokens.size());
			getTermsContainer().getDictionary().unloadDictionary();
			//Test generic error code doesn't find it
			genericStep.run("TestField", ticket, getTermsContainer(), stepContext);
			term = getTermsContainer().getDictionary().get("ora-1111", true);
			Assert.assertNull(term);
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testGenericErrorCode()
	{
		String text = "Error db400 from attunity";
		TechTokens specificStep = new TechTokens("errorCodes");
		TechTokens genericStep = new TechTokens("errorCodesGeneric");
		ProcessedTicket processedTicket = new ProcessedTicket(null, null);
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(text);
		try {
			//Test specific term doesn't find it
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			specificStep.run("TestField", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("db400", true);
			Assert.assertNull(term);
			//Test generic term finds it
			ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			genericStep.run("TestField", ticket, getTermsContainer(), stepContext);
			term = getTermsContainer().getDictionary().get("db400", true);
			Assert.assertNotNull(term);
			term.addFrequency();
			genericStep.run("TestField", ticket, processedTicket, stepContext);
			List<String> extractedTokens = getTermsData(processedTicket.getField("TestField").getTerms());
			Assert.assertEquals(1, extractedTokens.size());
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testSpecificErorCodeEndOfTheLine()
	{
		String text = "Error ora-1111";
		TechTokens specificStep = new TechTokens("errorCodes");
		TechTokens genericStep = new TechTokens("errorCodesGeneric");
		ProcessedTicket processedTicket = new ProcessedTicket(null, null);
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(text);
		try {
			//Test specific error code finds it
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			specificStep.run("TestField", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("ora-1111", true);
			Assert.assertNotNull(term);
			term.addFrequency();
			specificStep.run("TestField", ticket, processedTicket, stepContext);
			List<String> extractedTokens = getTermsData(processedTicket.getField("TestField").getTerms());
			Assert.assertEquals(1, extractedTokens.size());
			getTermsContainer().getDictionary().unloadDictionary();
			//Test generic error code doesn't find it
			genericStep.run("TestField", ticket, getTermsContainer(), stepContext);
			term = getTermsContainer().getDictionary().get("ora-1111", true);
			Assert.assertNull(term);
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testGenericErrorCodeEndOfTheLine()
	{
		String text = "Error db400";
		TechTokens specificStep = new TechTokens("errorCodes");
		TechTokens genericStep = new TechTokens("errorCodesGeneric");
		ProcessedTicket processedTicket = new ProcessedTicket(null, null);
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(text);
		try {
			//Test specific term doesn't find it
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			specificStep.run("TestField", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("db400", true);
			Assert.assertNull(term);
			//Test generic term finds it
			ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			genericStep.run("TestField", ticket, getTermsContainer(), stepContext);
			term = getTermsContainer().getDictionary().get("db400", true);
			Assert.assertNotNull(term);
			term.addFrequency();
			genericStep.run("TestField", ticket, processedTicket, stepContext);
			List<String> extractedTokens = getTermsData(processedTicket.getField("TestField").getTerms());
			Assert.assertEquals(1, extractedTokens.size());
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}
	@Test
	public void testSpecificErorCodeBegginingOfTheLine()
	{
		String text = "ora-1111 error from oracle";
		TechTokens specificStep = new TechTokens("errorCodes");
		TechTokens genericStep = new TechTokens("errorCodesGeneric");
		ProcessedTicket processedTicket = new ProcessedTicket(null, null);
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(text);
		try {
			//Test specific error code finds it
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			specificStep.run("TestField", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("ora-1111", true);
			Assert.assertNotNull(term);
			term.addFrequency();
			specificStep.run("TestField", ticket, processedTicket, stepContext);
			List<String> extractedTokens = getTermsData(processedTicket.getField("TestField").getTerms());
			Assert.assertEquals(1, extractedTokens.size());
			getTermsContainer().getDictionary().unloadDictionary();
			//Test generic error code doesn't find it
			genericStep.run("TestField", ticket, getTermsContainer(), stepContext);
			term = getTermsContainer().getDictionary().get("ora-1111", true);
			Assert.assertNull(term);
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testGenericErrorCodeBegginingOfTheLine()
	{
		String text = "db400 from attunity";
		TechTokens specificStep = new TechTokens("errorCodes");
		TechTokens genericStep = new TechTokens("errorCodesGeneric");
		ProcessedTicket processedTicket = new ProcessedTicket(null, null);
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(text);
		try {
			//Test specific term doesn't find it
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			specificStep.run("TestField", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("db400", true);
			Assert.assertNull(term);
			//Test generic term finds it
			ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			genericStep.run("TestField", ticket, getTermsContainer(), stepContext);
			term = getTermsContainer().getDictionary().get("db400", true);
			Assert.assertNotNull(term);
			term.addFrequency();
			genericStep.run("TestField", ticket, processedTicket, stepContext);
			List<String> extractedTokens = getTermsData(processedTicket.getField("TestField").getTerms());
			Assert.assertEquals(1, extractedTokens.size());
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}
}
