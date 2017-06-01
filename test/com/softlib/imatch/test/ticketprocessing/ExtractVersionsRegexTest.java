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

public class ExtractVersionsRegexTest {

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
	public void testRegularVersion()
	{
		String text = "Version 1.0.1";
		TechTokens step = new TechTokens("versions");
		ProcessedTicket processedTicket = new ProcessedTicket(null, null);
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(text);
		try {
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("TestField", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("1.0.1", true);
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
	public void testVersionWithZero()
	{
		//TODO this test fails since there is no handling of .0 versions for regexes as it done for NLP versions
		String text = "Version 1.0.1.0";
		TechTokens step = new TechTokens("versions");
		ProcessedTicket processedTicket = new ProcessedTicket(null, null);
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(text);
		try {
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("TestField", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("1.0.1", true);
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
	public void testXVersion()
	{
		String text = "Version 1.0.1.x";
		TechTokens step = new TechTokens("versions");
		ProcessedTicket processedTicket = new ProcessedTicket(null, null);
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(text);
		try {
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("TestField", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("1.0.1.x", true);
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
