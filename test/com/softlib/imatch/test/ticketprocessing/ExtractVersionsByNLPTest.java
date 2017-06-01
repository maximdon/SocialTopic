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

public class ExtractVersionsByNLPTest {

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
	public void testJavaStackTrace()
	{
		String stackLine = "com.precise.api.dataloader.outputmanager.BranchManager.getBranchOrderedByTime(BranchManager.java:125) at we must solve.";
		ExtractVersionsByNLP step = new ExtractVersionsByNLP();
		ProcessedTicket processedTicket = new ProcessedTicket(null, null);
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(stackLine);
		try {
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("TestField", ticket, getTermsContainer(), stepContext);
			step.run("TestField", ticket, processedTicket, stepContext);
			List<String> extractedTokens = getTermsData(processedTicket.getField("TestField").getTerms());
			Assert.assertEquals(0, extractedTokens.size());
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}
		
	@Test
	public void testIE8() 
	{
		test("IE 8");
	}
	

	@Test
	public void testIE8Close() 
	{
		//TODO this test currently fails, NLP Versions step can't identify such terms 
		test("IE8", "IE 8");
	}

	@Test
	public void testInternetExplorer8() 
	{
		test("Internet Explorer 8");
	}
	
	@Test
	public void testInternetExplorer8Close() 
	{
		//TODO this test currently fails, NLP Versions step can't identify such terms
		test("Internet Explorer8", "Internet Explorer 8");
	}
	
	@Test
	public void testInternetExplorerV8Close() 
	{
		test("Internet Explorer v8", "Internet Explorer 8");
	}

	@Test
	public void testInternetExplorerVer8() 
	{
		test("Internet Explorer ver 8", "Internet Explorer 8");
	}
	
	@Test
	public void testInternetExplorerVer8Close() 
	{
		test("Internet Explorer ver8", "Internet Explorer 8");
	}

	@Test
	public void testInternetExplorerVersion8Close() 
	{
		test("Internet Explorer version8", "Internet Explorer 8");
	}
	
	@Test
	public void testIEV8() 
	{
		test("IE v 8", "IE 8");
	}
	
	@Test
	public void testIEV8Close() 
	{
		test("IE v8", "IE 8");
	}
	
	
	@Test
	public void testIEVer8() 
	{
		test("IE ver 8", "IE 8");
	}

	@Test
	public void testIEVer8Close() 
	{
		test("IE ver8", "IE 8");
	}
	
	@Test
	public void testIEVersion8() 
	{
		test("IE version 8", "IE 8");
	}
	
	@Test
	public void testIEVersion8Close() 
	{
		test("IE version8", "IE 8");
	}

	@Test
	public void testIEVersion802() 
	{
		test("IE version 8.0.2", "IE 8.0.2");
	}

	@Test
	public void testInternetExplorer8Sentence() 
	{
		test("I hate Internet Explorer 8 La la la! Firefox is much better.", "Internet Explorer 8");
	}
	
	@Test
	public void testUniformVersionRegular() 
	{
		String version = "8.1.7.1";
		String uniformVersion = ExtractVersionsByNLP.getUniformVersion(version);
		Assert.assertEquals("8.1.7.1", uniformVersion);
	}

	@Test
	public void testUniformVersionLeadingZeros() 
	{
		String version = "8.1.7.01";
		String uniformVersion = ExtractVersionsByNLP.getUniformVersion(version);
		Assert.assertEquals("8.1.7.01", uniformVersion);
	}

	@Test
	public void testUniformVersionTrailingZerosBeforeDot() 
	{
		String version = "80";
		String uniformVersion = ExtractVersionsByNLP.getUniformVersion(version);
		Assert.assertEquals("80", uniformVersion);
	}
	
	@Test
	public void testUniformVersionTrailingZerosAfterDot2() 
	{
		String version = "8.1.0";
		String uniformVersion = ExtractVersionsByNLP.getUniformVersion(version);
		Assert.assertEquals("8.1", uniformVersion);
	}
	
	@Test
	public void testUniformVersionTrailingZerosAfterDot3() 
	{
		String version = "8.1.0.0";
		String uniformVersion = ExtractVersionsByNLP.getUniformVersion(version);
		Assert.assertEquals("8.1", uniformVersion);
	}
	
	@Test
	public void testUniformVersionTrailingZerosAfterDot4() 
	{
		String version = "8.1.00";
		String uniformVersion = ExtractVersionsByNLP.getUniformVersion(version);
		Assert.assertEquals("8.1", uniformVersion);
	}
	
	@Test
	public void testUniformVersionTrailingZerosAfterDot5() 
	{
		String version = "8.1.0.0";
		String uniformVersion = ExtractVersionsByNLP.getUniformVersion(version);
		Assert.assertEquals("8.1", uniformVersion);
	}
	
	@Test
	public void testUniformVersionTrailingZerosAfterDot6() 
	{
		String version = "8.1.20.0";
		String uniformVersion = ExtractVersionsByNLP.getUniformVersion(version);
		Assert.assertEquals("8.1.20", uniformVersion);
	}
	
	@Test
	public void testVehicles()
	{
		String text = "This is not there in the adabas VEHICLES";
		ExtractVersionsByNLP step = new ExtractVersionsByNLP();
		ProcessedTicket processedTicket = new ProcessedTicket(null, null);
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(text);
		try {
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("TestField", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("adabas ehicle", true);
			Assert.assertNull(term);
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testVMS()
	{
		String text = "	From Data Integrator product running under Linux Suse needs to access RMS files in Open VMS";
		ExtractVersionsByNLP step = new ExtractVersionsByNLP();
		ProcessedTicket processedTicket = new ProcessedTicket(null, null);
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(text);
		try {
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("TestField", ticket, getTermsContainer(), stepContext);
			Assert.assertEquals(0, getTermsContainer().getDictionary().getTermsCount());
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testEndOfLine()
	{
		String text = "This issue does not occur in Studio 5.0.0";
		ExtractVersionsByNLP step = new ExtractVersionsByNLP();
		ProcessedTicket processedTicket = new ProcessedTicket(null, null);
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(text);
		try {
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("TestField", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("studio 5", true);
			Assert.assertNotNull(term);
			term.addFrequency();
			step.run("TestField", ticket, processedTicket, stepContext);
			List<String> extractedTokens = getData(processedTicket.getAllTerms(false));
			Assert.assertEquals(Arrays.asList(new String[]{"studio 5"}), extractedTokens);
			getTermsContainer().getDictionary().unloadDictionary();
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testEndOfLineWithDot()
	{
		String text = "This issue does not occur in Studio 5.0.0.";
		ExtractVersionsByNLP step = new ExtractVersionsByNLP();
		ProcessedTicket processedTicket = new ProcessedTicket(null, null);
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(text);
		try {
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("TestField", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("studio 5", true);
			Assert.assertNotNull(term);
			term.addFrequency();
			step.run("TestField", ticket, processedTicket, stepContext);
			List<String> extractedTokens = getData(processedTicket.getAllTerms(false));
			Assert.assertEquals(Arrays.asList(new String[]{"studio 5"}), extractedTokens);
			getTermsContainer().getDictionary().unloadDictionary();
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testVersionWithoutProduct()
	{
		String text = "upgrading to version 5.0.1 in the Tandem";
		ExtractVersionsByNLP step = new ExtractVersionsByNLP();
		ProcessedTicket processedTicket = new ProcessedTicket(null, null);
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(text);
		try {
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("TestField", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("version 5.0.1", true);
			Assert.assertNull(term);
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testVWithoutProduct()
	{
		String text = "upgrading to v 5.0.1 in the Tandem";
		ExtractVersionsByNLP step = new ExtractVersionsByNLP();
		ProcessedTicket processedTicket = new ProcessedTicket(null, null);
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(text);
		try {
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("TestField", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("version 5.0.1", true);
			Assert.assertNull(term);
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	private void test(String text) {
		test(text, text);
	}

	private List<String> getData(Collection<TechnicalDictionaryTerm> terms) {
		List<String> rc = new ArrayList<String>();
		for (TechnicalDictionaryTerm term : terms)
			if(term.getTermSource().getsourceName().equals("NLP Version Tokens"))
				rc.add(term.getTermText());
		return rc;
	}

	private void test(String text, String extractedText) {
		ExtractVersionsByNLP step = new ExtractVersionsByNLP();
		ProcessedTicket processedTicket = new ProcessedTicket(null, null);
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(text);
		try {
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("TestField", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get(extractedText, true);
			Assert.assertNotNull(term);
			term.addFrequency();
			step.run("TestField", ticket, processedTicket, stepContext);
			List<String> extractedTokens = getData(processedTicket.getAllTerms(false));
			Assert.assertEquals(Arrays.asList(new String[]{extractedText.toLowerCase()}), extractedTokens);
			getTermsContainer().getDictionary().unloadDictionary();
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}
}
