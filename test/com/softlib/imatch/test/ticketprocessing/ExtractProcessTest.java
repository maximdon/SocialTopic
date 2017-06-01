package com.softlib.imatch.test.ticketprocessing;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.apache.log4j.xml.DOMConfigurator;
import org.junit.BeforeClass;
import org.junit.Test;

import com.softlib.imatch.ConsoleAppRuntimeInfo;
import com.softlib.imatch.ITicket;
import com.softlib.imatch.InMemoryTicket;
import com.softlib.imatch.MatcherException;
import com.softlib.imatch.MultitenantRuntimeInfo;
import com.softlib.imatch.MultitenantThreadInfo;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.common.Pair;
import com.softlib.imatch.dictionary.ITechnicalTermsContainer;
import com.softlib.imatch.dictionary.TechnicalDictionary;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.dictionary.TechnicalTermSource;
import com.softlib.imatch.ticketprocessing.Clean;
import com.softlib.imatch.ticketprocessing.ExtractVersionsByNLP;
import com.softlib.imatch.ticketprocessing.IndexTerms;
import com.softlib.imatch.ticketprocessing.ProcessedTicketDBase;
import com.softlib.imatch.ticketprocessing.StepContext;
import com.softlib.imatch.ticketprocessing.TechTokens;

public class ExtractProcessTest {

	@BeforeClass
	public static void init()
	{
		try {
			MultitenantRuntimeInfo.setRootDir("C:\\work\\VirtualAgent\\Azure\\iMatchTest\\bin\\Debug");
			MultitenantRuntimeInfo.init(null);			
			DOMConfigurator.configure(RuntimeInfo.getCurrentInfo().getRealPath("/{SolutionConfigFolder}/log4j-console.xml"));
			//dictionary.loadDictionary();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testSourceRelations() {
		String phrase = "fs_fpags_850_p000_hot_fix_001";
		TechTokens errorCodesStep = new TechTokens("errorCodes");
		TechTokens variablesStep = new TechTokens("variables");
		Clean cleanStep = new Clean();
		ITicket ticket = new InMemoryTicket("1", "", phrase);
		TechnicalDictionary container = new TechnicalDictionary();
		TechnicalTermSource errorSource = container.addSource("errorCodes");
		errorSource.setSourceBoost(5);
		TechnicalTermSource variableSource = container.addSource("variables");
		variableSource.setSourceBoost((float)4.5);	
		StepContext context = new StepContext("1");
		try {
			cleanStep.run("TestField",ticket,container, context);
			errorCodesStep.run("TestField",ticket,container, context);
			variablesStep.run("TestField",ticket,container, context);
			Assert.assertEquals(1, container.getTermsCount());
			TechnicalDictionaryTerm term = container.termsCollection().toArray(new TechnicalDictionaryTerm[1])[0];
			Assert.assertEquals("errorCodes", term.getTermSource().getsourceName());
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testExtractWindows2003() {
		String phrase = "This application is working fine on our server that has Windows Server 2003 Standard Edition.";
		ExtractVersionsByNLP versionsByNlpStep = new ExtractVersionsByNLP();
		Clean cleanStep = new Clean();
		ITicket ticket = new InMemoryTicket("1", "", phrase);
		TechnicalDictionary container = new TechnicalDictionary();
		TechnicalTermSource errorSource = container.addSource("NLP Version Tokens");		
		StepContext context = new StepContext("1");
		try {
			cleanStep.run("TestField",ticket,container, context);
			versionsByNlpStep.run("TestField",ticket,container, context);
			Assert.assertEquals(2, container.getTermsCount());
			TechnicalDictionaryTerm term = container.termsCollection().toArray(new TechnicalDictionaryTerm[1])[0];
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testProcessedTicketDBase() {
		RuntimeInfo.getCurrentInfo().startThread(new MultitenantThreadInfo("ua-1234567-8"));
		Set<String> fields = new HashSet<String>();
		fields.add("Url");
		fields.add("Title");
		Map<String, Pair<String, String>> resultSet = ProcessedTicketDBase.getResultMap("vaObject", "https://brightinfo.blob.core.windows.net/website-ua-1234567-8/45", fields);
		String text = "MODIFICATION AND ACCURACY OF, INFORMATION";
		IndexTerms step = new IndexTerms();
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(text);
		stepContext.setFieldTerms(resultSet.get("Url"));
		try {
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("TestField", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("press release", true);
			Assert.assertNull(term);
			getTermsContainer().getDictionary().unloadDictionary();
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
		MultitenantRuntimeInfo.getCurrentInfo().finishThread();
	}
	
	private ITechnicalTermsContainer getTermsContainer() {
		TechnicalDictionary dictionary = (TechnicalDictionary) RuntimeInfo.getCurrentInfo().getBean("dictionary");
		return dictionary;
	}

};
