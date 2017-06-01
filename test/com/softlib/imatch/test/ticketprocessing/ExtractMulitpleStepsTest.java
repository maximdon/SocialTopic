package com.softlib.imatch.test.ticketprocessing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
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
import com.softlib.imatch.ticketprocessing.BaseTicketProcessStep;
import com.softlib.imatch.ticketprocessing.DictionaryTerms;
import com.softlib.imatch.ticketprocessing.ExtractTechTokensByNLP;
import com.softlib.imatch.ticketprocessing.ExtractVersionsByNLP;
import com.softlib.imatch.ticketprocessing.ITicketProcessStep;
import com.softlib.imatch.ticketprocessing.ProcessedTicket;
import com.softlib.imatch.ticketprocessing.StepContext;
import com.softlib.imatch.ticketprocessing.TechTokens;

public class ExtractMulitpleStepsTest {

	private List<ITicketProcessStep> extractionSteps;
	private List<ITicketProcessStep> matchSteps;
	@BeforeClass
	public static void init()
	{
		ConsoleAppRuntimeInfo.init(null);
		DOMConfigurator.configure(RuntimeInfo.getCurrentInfo().getRealPath("/{SolutionConfigFolder}/log4j-console.xml"));
	}
	
	public ExtractMulitpleStepsTest()
	{
		extractionSteps = new ArrayList<ITicketProcessStep>();
		extractionSteps.add(new ExtractVersionsByNLP());
		extractionSteps.add(new ExtractTechTokensByNLP());
		extractionSteps.add(new TechTokens("quoteTerms"));
		extractionSteps.add(new TechTokens("longTerms"));
		
		matchSteps = new ArrayList<ITicketProcessStep>();
		matchSteps.add(new ExtractVersionsByNLP());
		matchSteps.add(new TechTokens("quoteTerms"));
		matchSteps.add(new DictionaryTerms());
	}
	
	private ITechnicalTermsContainer getTermsContainer() {
		TechnicalDictionary dictionary = (TechnicalDictionary) RuntimeInfo.getCurrentInfo().getBean("dictionary");
		return dictionary;
	}
	
	private Collection<String> getTermsData(List<TechnicalDictionaryTerm> terms) {
		Set<String> rc = new HashSet<String>();
		for (TechnicalDictionaryTerm term : terms)
			rc.add(term.getTermText());
		return rc;
	}

	@Test
	public void test1()
	{
		String text = "Hello,  We have an Attunity prospect that needs to do the following: From Data Integrator product running under Linux Suse needs to access RMS files in Open VMS.  They have the following questions:  1) Is it possible to use field type 'quadword' in a RMS file ?  2) Is it available an ODBC o JDBC client in Linux Suse ?    If you need more info please let me know.  Thanks and regards,  Pedro - Linux Suse team";
		List<ITicketProcessStep> steps = extractionSteps;
		ProcessedTicket processedTicket = new ProcessedTicket(null, null);
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(text);
		try {
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			for(ITicketProcessStep step : steps)
				step.run("TestField", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("attunity prospect", true);
			Assert.assertNotNull(term);
			term.addFrequency();
			TechnicalDictionaryTerm term2 = getTermsContainer().getDictionary().get("data integrator product", true);
			Assert.assertNotNull(term2);
			term2.addFrequency();
			TechnicalDictionaryTerm term3 = getTermsContainer().getDictionary().get("linux suse", true);
			Assert.assertNotNull(term3);
			term3.addFrequency();
			TechnicalDictionaryTerm term4 = getTermsContainer().getDictionary().get("rms file", true);
			Assert.assertNotNull(term4);
			term4.addFrequency();
			TechnicalDictionaryTerm term5 = getTermsContainer().getDictionary().get("open vms", true);
			//Assert.assertNotNull(term5);
			//term.addFrequency();
			TechnicalDictionaryTerm term6 = getTermsContainer().getDictionary().get("quadword", true);
			Assert.assertNotNull(term6);
			term6.addFrequency();
			TechnicalDictionaryTerm term7 = getTermsContainer().getDictionary().get("rms", true);
			Assert.assertNotNull(term7);
			term7.addFrequency();
			TechnicalDictionaryTerm term8 = getTermsContainer().getDictionary().get("jdbc", true);
			Assert.assertNotNull(term8);
			term8.addFrequency();
			TechnicalDictionaryTerm term9 = getTermsContainer().getDictionary().get("odbc", true);
			Assert.assertNotNull(term9);
			term9.addFrequency();
			TechnicalDictionaryTerm term10 = getTermsContainer().getDictionary().get("linux suse team", true);
			Assert.assertNotNull(term10);
			term10.addFrequency();
			TechnicalDictionaryTerm term11 = getTermsContainer().getDictionary().get("vms", true);
			Assert.assertNotNull(term11);
			term11.addFrequency();
			for(ITicketProcessStep step : steps)
				step.run("TestField", ticket, processedTicket, stepContext);
			Collection<String> extractedTokens = getTermsData(processedTicket.getField("TestField").getTerms());
			Assert.assertEquals(10, extractedTokens.size());
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void test2()
	{
		String text = "attunity studio 5.3";
		List<ITicketProcessStep> steps = matchSteps;
		ProcessedTicket processedTicket = new ProcessedTicket(null, null);
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(text);
		try {
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().addTermByUser(new TechnicalDictionaryKey("attunity studio 5"));
			TechnicalDictionaryTerm term2 = getTermsContainer().getDictionary().addTermByUser(new TechnicalDictionaryKey("5.3"));
			for(ITicketProcessStep step : steps)
				step.run("TestField", ticket, processedTicket, stepContext);
			Collection<String> extractedTokens = getTermsData(processedTicket.getField("TestField").getTerms());
			Assert.assertEquals(1, extractedTokens.size());
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}
}
