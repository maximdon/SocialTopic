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
import com.softlib.imatch.ticketprocessing.UrlParserStep;
import com.softlib.imatch.ticketprocessing.ProcessedTicket;
import com.softlib.imatch.ticketprocessing.StepContext;
import com.softlib.imatch.ticketprocessing.TechTokens;

public class UrlParserTest {

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
	public void testSimpleUrl()
	{
		String text = "http://www.softlibsw.com/Abouk.aspx";
		UrlParserStep step = new UrlParserStep();
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(text);
		try {
			//Test specific error code finds it
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("Url", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("abouk", true);
			Assert.assertNotNull(term);
			getTermsContainer().getDictionary().unloadDictionary();
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testSimpleUrlStopWord()
	{
		String text = "http://www.softlibsw.com/About.aspx";
		UrlParserStep step = new UrlParserStep();
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(text);
		try {
			//Test specific error code finds it
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("Url", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("about", true);
			Assert.assertNull(term);
			getTermsContainer().getDictionary().unloadDictionary();
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testSimpleUrlWordnetWord()
	{
		String text = "http://www.softlibsw.com/success.aspx";
		UrlParserStep step = new UrlParserStep();
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(text);
		try {
			getTermsContainer().getDictionary().loadDictionary();
			//Test specific error code finds it
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("Url", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("success", true);
			Assert.assertNotNull(term);
			Assert.assertEquals(47, term.getTermSource().getSourceId());
			getTermsContainer().getDictionary().unloadDictionary();
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testUrlWithSpace()
	{
		String text = "http://www.softlibsw.com/About Me.aspx";
		UrlParserStep step = new UrlParserStep();
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(text);
		try {
			//Test specific error code finds it
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("Url", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("about me", true);
			Assert.assertNotNull(term);
			getTermsContainer().getDictionary().unloadDictionary();
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testUrlWithEncodedSpace()
	{
		String text = "http://www.softlibsw.com/About%20Me.aspx";
		UrlParserStep step = new UrlParserStep();
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(text);
		try {
			//Test specific error code finds it
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("Url", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("about me", true);
			Assert.assertNotNull(term);
			getTermsContainer().getDictionary().unloadDictionary();
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testUrlWithEncodedSpace2()
	{
		String text = "http://www.softlibsw.com/Downloads/Softlib%20company%20profile.pdf";
		UrlParserStep step = new UrlParserStep();
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(text);
		try {
			//Test specific error code finds it
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("Url", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("softlib company profile", true);
			Assert.assertNotNull(term);
			getTermsContainer().getDictionary().unloadDictionary();
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testCapitalizedUrl()
	{
		String text = "http://www.softlibsw.com/SoftwareModules.aspx";
		UrlParserStep step = new UrlParserStep();
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(text);
		try {
			//Test specific error code finds it
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("Url", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("software module", true);
			Assert.assertNotNull(term);
			getTermsContainer().getDictionary().unloadDictionary();
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testUrlWithDirectory()
	{
		String text = "http://www.softlibsw.com/products/prod.aspx";
		UrlParserStep step = new UrlParserStep();
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(text);
		try {
			//Test specific error code finds it
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("Url", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("prod", true);
			Assert.assertNotNull(term);
			TechnicalDictionaryTerm term2 = getTermsContainer().getDictionary().get("product", true);
			Assert.assertNotNull(term2);
			getTermsContainer().getDictionary().unloadDictionary();
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testMultiDirUrl()
	{
		String text = "http://www.mobileye.com/technology/applications/head-lamp-control/intelligent-light-ranging/";
		UrlParserStep step = new UrlParserStep();
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(text);
		try {
			//Test specific error code finds it
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("Url", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("head lamp control", true);
			Assert.assertNotNull(term);
			TechnicalDictionaryTerm term2 = getTermsContainer().getDictionary().get("intelligent light range", true);
			Assert.assertNotNull(term2);
			getTermsContainer().getDictionary().unloadDictionary();
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testHttpsUrl()
	{
		String text = "https://www.mobileye.com/test.aspx";
		UrlParserStep step = new UrlParserStep();
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(text);
		try {
			//Test specific error code finds it
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("Url", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("test", true);
			Assert.assertNotNull(term);
			getTermsContainer().getDictionary().unloadDictionary();
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testUrlWithParameters()
	{
		String text = "https://www.mobileye.com/test.aspx?a=1&b=2";
		UrlParserStep step = new UrlParserStep();
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(text);
		try {
			//Test specific error code finds it
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("Url", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("test", true);
			Assert.assertNotNull(term);
			getTermsContainer().getDictionary().unloadDictionary();
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testUrlFromSofltib1()
	{
		String text = "http://www.softlibsw.com/PressReleases.aspx";
		UrlParserStep step = new UrlParserStep();
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(text);
		try {
			//Test specific error code finds it
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("Url", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("press release", true);
			Assert.assertNotNull(term);
			getTermsContainer().getDictionary().unloadDictionary();
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testUrlWithUnderscore()
	{
		String text = "http://www.softlibsw.com/NextNine_Press_Release.aspx";
		UrlParserStep step = new UrlParserStep();
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(text);
		try {
			//Test specific error code finds it
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("Url", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("press release", true);
			Assert.assertNotNull(term);
			TechnicalDictionaryTerm term2 = getTermsContainer().getDictionary().get("next nine", true);
			Assert.assertNotNull(term2);
			getTermsContainer().getDictionary().unloadDictionary();
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}
}
