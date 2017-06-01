package com.softlib.imatch.test.ticketprocessing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import junit.framework.Assert;

import org.apache.log4j.xml.DOMConfigurator;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.softlib.imatch.ConsoleAppRuntimeInfo;
import com.softlib.imatch.InMemoryTicket;
import com.softlib.imatch.MatcherException;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.dictionary.ITechnicalTermsContainer;
import com.softlib.imatch.dictionary.TechnicalDictionary;
import com.softlib.imatch.dictionary.TechnicalDictionaryKey;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.ticketprocessing.ExtractProximityTermsByNLP;
import com.softlib.imatch.ticketprocessing.ExtractTechTokensByNLP;
import com.softlib.imatch.ticketprocessing.ExtractVersionsByNLP;
import com.softlib.imatch.ticketprocessing.NegativeSentimentStep;
import com.softlib.imatch.ticketprocessing.ProcessedTicket;
import com.softlib.imatch.ticketprocessing.StepContext;

public class ExtractProximityTermsTest2 {

	private static TechnicalDictionary dictionary;
	private List<TechnicalDictionaryTerm> addedTerms = new ArrayList<TechnicalDictionaryTerm>();

	@BeforeClass
	public static void init()
	{
		ConsoleAppRuntimeInfo.init(null);
		DOMConfigurator.configure(RuntimeInfo.getCurrentInfo().getRealPath("/{SolutionConfigFolder}/log4j-console.xml"));
		dictionary = (TechnicalDictionary) RuntimeInfo.getCurrentInfo().getBean("dictionary");
		dictionary.loadDictionary();
	}
	
	@After
	public void clean()
	{
		for(TechnicalDictionaryTerm term : addedTerms) {
			term.getAllRelations().clear();
			term.getRelations().clear();
			dictionary.removeTermByUser(term.getTermKey());
		}
//		dictionary.unloadDictionary();
	}

	private ITechnicalTermsContainer getTermsContainer() {		
		return dictionary;
	}
	
	@Test
	public void testBasicProximity() {
		String errorMsg = "Ecg prost patient";
		try {
			TechnicalDictionaryTerm term1 = dictionary.addTermByUser(new TechnicalDictionaryKey("Ecg"));
			term1.setTermSource(dictionary.getSource(10));
			addedTerms.add(term1);
			TechnicalDictionaryTerm term2 = dictionary.addTermByUser(new TechnicalDictionaryKey("patient"));
			term2.setTermSource(dictionary.getSource(101));
			addedTerms.add(term2);
			
			TechnicalDictionaryTerm term = createTestTerm(errorMsg, "ecg 0 patient");
			Assert.assertNotNull(term);
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}		

	@Test
	public void testProximityWithTermInTheMiddle() 
	{		
		String errorMsg = "Edg tes patient";
		try {
			TechnicalDictionaryTerm term1 = dictionary.addTermByUser(new TechnicalDictionaryKey("Edg"));
			term1.setTermSource(dictionary.getSource(10));
			addedTerms.add(term1);
			TechnicalDictionaryTerm term2 = dictionary.addTermByUser(new TechnicalDictionaryKey("patient"));
			term2.setTermSource(dictionary.getSource(101));
			addedTerms.add(term2);
			TechnicalDictionaryTerm term3 = dictionary.addTermByUser(new TechnicalDictionaryKey("tes"));
			term3.setTermSource(dictionary.getSource(11));
			addedTerms.add(term3);
			
			TechnicalDictionaryTerm term = createTestTerm(errorMsg, "edg 0 patient");
			Assert.assertNotNull(term);
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testProximityWithSubProximity() 
	{		
		String errorMsg = "Ecg hemo tes patient";
		try {
			TechnicalDictionaryTerm term1 = dictionary.addTermByUser(new TechnicalDictionaryKey("Ecg"));
			term1.setTermSource(dictionary.getSource(10));
			addedTerms.add(term1);
			TechnicalDictionaryTerm term2 = dictionary.addTermByUser(new TechnicalDictionaryKey("patient"));
			term2.setTermSource(dictionary.getSource(101));
			addedTerms.add(term2);
			TechnicalDictionaryTerm term3 = dictionary.addTermByUser(new TechnicalDictionaryKey("hemo"));
			term3.setTermSource(dictionary.getSource(10));
			addedTerms.add(term3);
			
//			TechnicalDictionaryTerm termProx1 = createTestTerm(errorMsg, "ecg 0 patient");
//			Assert.assertNotNull(termProx1);
			TechnicalDictionaryTerm termProx2 = createTestTerm(errorMsg, "hemo 0 patient");
			Assert.assertNotNull(termProx2);
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testProximityWithMultiWordTerm() 
	{		
		String errorMsg = "Efg hemo tes patient";
		try {
			TechnicalDictionaryTerm term1 = dictionary.addTermByUser(new TechnicalDictionaryKey("Efg hemo"));
			term1.setTermSource(dictionary.getSource(10));
			addedTerms.add(term1);
			TechnicalDictionaryTerm term2 = dictionary.addTermByUser(new TechnicalDictionaryKey("patient"));
			term2.setTermSource(dictionary.getSource(101));
			addedTerms.add(term2);

			TechnicalDictionaryTerm termProx2 = createTestTerm(errorMsg, "efg hemo 0 patient");
			Assert.assertNotNull(termProx2);
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testProximityInIndex1() 
	{		
		String errorMsg = "Ecg hemo tes patient";
		try {
			TechnicalDictionaryTerm term1 = dictionary.addTermByUser(new TechnicalDictionaryKey("Ecg"));
			term1.setTermSource(dictionary.getSource(103));
			addedTerms.add(term1);
			TechnicalDictionaryTerm term2 = dictionary.addTermByUser(new TechnicalDictionaryKey("patient"));
			term2.setTermSource(dictionary.getSource(101));
			addedTerms.add(term2);
			TechnicalDictionaryTerm term3 = dictionary.addTermByUser(new TechnicalDictionaryKey("hemo"));
			term3.setTermSource(dictionary.getSource(10));
			addedTerms.add(term3);
			
			TechnicalDictionaryTerm termProx2 = createTestTerm(errorMsg, "hemo 0 patient");
			Assert.assertNotNull(termProx2);
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testProximityInvalidTerm() 
	{		
		String errorMsg = "Ecg#hemo tes patient";
		try {
			TechnicalDictionaryTerm term1 = dictionary.addTermByUser(new TechnicalDictionaryKey("Ecg?hemo"));
			term1.setTermSource(dictionary.getSource(103));
			addedTerms.add(term1);
			TechnicalDictionaryTerm term2 = dictionary.addTermByUser(new TechnicalDictionaryKey("patient"));
			term2.setTermSource(dictionary.getSource(101));
			addedTerms.add(term2);
			TechnicalDictionaryTerm term3 = dictionary.addTermByUser(new TechnicalDictionaryKey("hemo"));
			term3.setTermSource(dictionary.getSource(10));
			addedTerms.add(term3);
			
			TechnicalDictionaryTerm termProx2 = createTestTerm(errorMsg, "ecg#hemo 0 patient");
			Assert.assertNull(termProx2);
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testProximityZeroGap() 
	{		
		String errorMsg = "Eug hemo";
		try {
			TechnicalDictionaryTerm term1 = dictionary.addTermByUser(new TechnicalDictionaryKey("Eug"));
			term1.setTermSource(dictionary.getSource(10));
			addedTerms.add(term1);
			TechnicalDictionaryTerm term3 = dictionary.addTermByUser(new TechnicalDictionaryKey("hemo"));
			term3.setTermSource(dictionary.getSource(10));
			addedTerms.add(term3);
			
			TechnicalDictionaryTerm termProx2 = createTestTerm(errorMsg, "eug 0 hemo");
			Assert.assertNotNull(termProx2);
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testProximityLargeGap() 
	{		
		String errorMsg = "Epg tes tes tes tes tes tes hemo";
		try {
			TechnicalDictionaryTerm term1 = dictionary.addTermByUser(new TechnicalDictionaryKey("Epg"));
			term1.setTermSource(dictionary.getSource(10));
			addedTerms.add(term1);
			TechnicalDictionaryTerm term3 = dictionary.addTermByUser(new TechnicalDictionaryKey("hemo"));
			term3.setTermSource(dictionary.getSource(10));
			addedTerms.add(term3);
			
			TechnicalDictionaryTerm termProx2 = createTestTerm(errorMsg, "epg 0 hemo");
			Assert.assertNull(termProx2);
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testProximitySoftlibSoftlib() 
	{		
		String errorMsg = "Cant pull from tab";
		try {
			TechnicalDictionaryTerm term1 = dictionary.addTermByUser(new TechnicalDictionaryKey("pull"));
			term1.setTermSource(dictionary.getSource(101));
			addedTerms.add(term1);
			TechnicalDictionaryTerm term2 = dictionary.addTermByUser(new TechnicalDictionaryKey("tab"));
			term2.setTermSource(dictionary.getSource(101));
			addedTerms.add(term2);
			
			TechnicalDictionaryTerm termProx1 = createTestTerm(errorMsg, "pull 0 tab");
			Assert.assertNotNull(termProx1);
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testProximityExternWord() 
	{		
		String errorMsg = "Error doing tag";
		try {
			TechnicalDictionaryTerm term2 = dictionary.addTermByUser(new TechnicalDictionaryKey("tag"));
			term2.setTermSource(dictionary.getSource(101));
			addedTerms.add(term2);
			
			TechnicalDictionaryTerm termProx1 = createTestTerm(errorMsg, "error 0 tag");
			Assert.assertNotNull(termProx1);
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testNoProximity() 
	{		
		String errorMsg = "Ekg hemo patient";
		try {
			TechnicalDictionaryTerm term1 = dictionary.addTermByUser(new TechnicalDictionaryKey("ekg"));
			term1.setTermSource(dictionary.getSource(11));
			addedTerms.add(term1);
			TechnicalDictionaryTerm term2 = dictionary.addTermByUser(new TechnicalDictionaryKey("patient"));
			term2.setTermSource(dictionary.getSource(103));
			addedTerms.add(term2);
			
			TechnicalDictionaryTerm termProx1 = createTestTerm(errorMsg, "ekg 0 patient");
			Assert.assertNull(termProx1);
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}

	private TechnicalDictionaryTerm createTestTerm(String errorMsg, String expectedTermText) throws MatcherException {
		ExtractProximityTermsByNLP step = new ExtractProximityTermsByNLP();
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(errorMsg);
		InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
		step.run("TestField", ticket, getTermsContainer(), stepContext);
		TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get(expectedTermText, true);
		return term;
	}
}
