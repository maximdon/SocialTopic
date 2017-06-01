package com.softlib.imatch.test.ticketprocessing;

import junit.framework.Assert;

import org.apache.log4j.xml.DOMConfigurator;
import org.junit.BeforeClass;
import org.junit.Test;

import com.softlib.imatch.ConsoleAppRuntimeInfo;
import com.softlib.imatch.ITicket;
import com.softlib.imatch.InMemoryTicket;
import com.softlib.imatch.MatcherException;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.dictionary.TechnicalDictionary;
import com.softlib.imatch.ticketprocessing.StepContext;
import com.softlib.imatch.ticketprocessing.TechTokens;

public class JspBasedStepTest {

	@BeforeClass
	public static void init()
	{
		try {
			ConsoleAppRuntimeInfo.init(null);
			DOMConfigurator.configure(RuntimeInfo.getCurrentInfo().getRealPath("/{SolutionConfigFolder}/log4j-console.xml"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testWordnet() {
		String phrase = "I have a very strange ''eRroR''";
		TechTokens quoteStep = new TechTokens("quoteTerms");
		ITicket ticket = new InMemoryTicket("1", "", phrase);
		TechnicalDictionary container = new TechnicalDictionary();
		StepContext context = new StepContext("1");
		try {
			quoteStep.run("TestField",ticket,container, context);
			Assert.assertEquals(0, container.getTermsCount());
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testLongTerms() {
		String phrase = "A solution has been provided to you by telephone or in the long text of your SAPNet - R/3 Frontend system message.";
		TechTokens quoteStep = new TechTokens("longTerms");
		ITicket ticket = new InMemoryTicket("1", "", phrase);
		TechnicalDictionary container = new TechnicalDictionary();
		StepContext context = new StepContext("1");
		try {
			quoteStep.run("TestField",ticket,container, context);
			Assert.assertEquals(2, container.getTermsCount());
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testErrorCodesTerms() {
		String phrase = "31032009 Portal Exception occurred";
		TechTokens errorCodesStep = new TechTokens("errorCodes");
		ITicket ticket = new InMemoryTicket("1", "", phrase);
		TechnicalDictionary container = new TechnicalDictionary();
		StepContext context = new StepContext("1");
		try {
			errorCodesStep.run("TestField",ticket,container, context);
			Assert.assertEquals(1, container.getTermsCount());
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}

};
