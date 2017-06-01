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
import com.softlib.imatch.ticketprocessing.ExtractNonEnglishWords;
import com.softlib.imatch.ticketprocessing.ExtractTechTokensByNLP;
import com.softlib.imatch.ticketprocessing.ExtractVersionsByNLP;
import com.softlib.imatch.ticketprocessing.ProcessedTicket;
import com.softlib.imatch.ticketprocessing.StepContext;

public class ExtractNonEnglishWordsTest {

	private static TechnicalDictionary dictionary;
	
	@BeforeClass
	public static void init()
	{
		ConsoleAppRuntimeInfo.init(null);
		DOMConfigurator.configure(RuntimeInfo.getCurrentInfo().getRealPath("/{SolutionConfigFolder}/log4j-console.xml"));
		dictionary = (TechnicalDictionary) RuntimeInfo.getCurrentInfo().getBean("dictionary");
		Wordnet.getInstance();
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
	public void testCdcAgent() {
		String stackLine = "I think CDCAgent is the best product ever";
		ExtractNonEnglishWords step = new ExtractNonEnglishWords();
		ProcessedTicket processedTicket = new ProcessedTicket(null, null);
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(stackLine);
		try {
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("TestField", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("cdcagent", true);
			Assert.assertNotNull(term);
			getTermsContainer().getDictionary().unloadDictionary();
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}		
	
	@Test
	public void testDigits() {
		String stackLine = "I think CDC1 is the best product ever";
		ExtractNonEnglishWords step = new ExtractNonEnglishWords();
		ProcessedTicket processedTicket = new ProcessedTicket(null, null);
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(stackLine);
		try {
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("TestField", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("cdc1", true);
			Assert.assertNull(term);
			getTermsContainer().getDictionary().unloadDictionary();
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}		
	
	@Test
	public void testSpecialCharacters() {
		String stackLine = "I think CDC's is the best product ever";
		ExtractNonEnglishWords step = new ExtractNonEnglishWords();
		ProcessedTicket processedTicket = new ProcessedTicket(null, null);
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(stackLine);
		try {
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("TestField", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("cdc's", true);
			Assert.assertNull(term);
			getTermsContainer().getDictionary().unloadDictionary();
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}		

	@Test
	public void testTook() {
		String stackLine = "I will took ownership on this case";
		ExtractNonEnglishWords step = new ExtractNonEnglishWords();
		ProcessedTicket processedTicket = new ProcessedTicket(null, null);
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(stackLine);
		try {
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("TestField", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("took", true);
			TechnicalDictionaryTerm term2 = getTermsContainer().getDictionary().get("take", true);
			Assert.assertNull(term);
			Assert.assertNull(term2);
			getTermsContainer().getDictionary().unloadDictionary();
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}		
	
	@Test
	public void testStatus() {
		String stackLine = "I will change the status or statuses on this case";
		ExtractNonEnglishWords step = new ExtractNonEnglishWords();
		ProcessedTicket processedTicket = new ProcessedTicket(null, null);
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(stackLine);
		try {
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("TestField", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("status", true);
			Assert.assertNull(term);
			getTermsContainer().getDictionary().unloadDictionary();
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}		
	
	@Test
	public void testRegards() {
		String stackLine = "I'm wishes you best regards on this case";
		ExtractNonEnglishWords step = new ExtractNonEnglishWords();
		ProcessedTicket processedTicket = new ProcessedTicket(null, null);
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(stackLine);
		try {
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("TestField", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("regards", true);
			TechnicalDictionaryTerm term2 = getTermsContainer().getDictionary().get("regard", true);
			Assert.assertNull(term);
			Assert.assertNull(term2);
			getTermsContainer().getDictionary().unloadDictionary();
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}		
}
