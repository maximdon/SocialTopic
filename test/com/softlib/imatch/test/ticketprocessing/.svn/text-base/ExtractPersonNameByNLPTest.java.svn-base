package com.softlib.imatch.test.ticketprocessing;


import java.util.Collection;
import java.util.Set;

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
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.matcher.InMemoryTicketFieldsNames;
import com.softlib.imatch.ticketprocessing.ExtractPersonNameByNLP;
import com.softlib.imatch.ticketprocessing.StepContext;

public class ExtractPersonNameByNLPTest {

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
	public void testFrequencyIssues() {
		String phrase = 
			"hi dror, bal bal regards, merav " +
			"From: Shai Dror [mailto:xxxx.yyyy@precise.com " +
			"From: Shai Dror [mailto:shai.dror@precise.com] " +
			"To: Dirk Craen; 'Glen,Richardson'; Raviv, Karnieli;";
		ExtractPersonNameByNLP step = new ExtractPersonNameByNLP();
		ITicket ticket = new InMemoryTicket("1", "", phrase);
		TechnicalDictionary container = new TechnicalDictionary();
		StepContext context = new StepContext("1");
		try {
			step.run(InMemoryTicketFieldsNames.BODY_FIELD,ticket,container, context);
			Collection<TechnicalDictionaryTerm> terms = container.getAllTerms(false);
			for (TechnicalDictionaryTerm term : terms) {
				System.out.println("Term = ["+term+"]");
			}
			int numNames = container.getTermsCount();
			Assert.assertEquals(13,numNames);
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testEmailNamesEmail() {
		String name = "max@attunity.com";
		Set<String> names = ExtractPersonNameByNLP.getEmailNames(name);
		Assert.assertEquals(1, names.size());
	}
	
	@Test
	public void testEmailNamesLongEmail() {
		String name = "Maxim Maxim [max@attunity.com]";
		Set<String> names = ExtractPersonNameByNLP.getEmailNames(name);
		Assert.assertEquals(3, names.size());
	}
	
	@Test
	public void testEmailNamesLongEmail2() {
		String name = "Maxim Donde [max.d@attunity.com]";
		Set<String> names = ExtractPersonNameByNLP.getEmailNames(name);
		Assert.assertEquals(6, names.size());
	}
	
	@Test
	public void testEmailNamesName() {
		String name = "Maxim Maxm";
		Set<String> names = ExtractPersonNameByNLP.getEmailNames(name);
		Assert.assertEquals(3, names.size());
	}
	
	@Test
	public void testEmailNamesNotName() {
		String name = "Support at Attunity";
		Set<String> names = ExtractPersonNameByNLP.getEmailNames(name);
		Assert.assertEquals(1, names.size());
	}

	@Test
	public void testEmailNamesFromPrecise() {
		String name = " kkothari@fedex.com; David Rupert";
		Set<String> names = ExtractPersonNameByNLP.getEmailNames(name);
		Assert.assertEquals(3, names.size());
	}
	@Test
	public void testEmailNamesFromPrecise2() {
		String name = " DL-General-Support Information";
		Set<String> names = ExtractPersonNameByNLP.getEmailNames(name);
		Assert.assertEquals(1, names.size());
	}
	
};
