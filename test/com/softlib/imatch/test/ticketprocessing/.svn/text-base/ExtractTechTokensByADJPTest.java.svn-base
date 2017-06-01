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
import com.softlib.imatch.ticketprocessing.ExtractADJPbyNLP;
import com.softlib.imatch.ticketprocessing.StepContext;

public class ExtractTechTokensByADJPTest {

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
	public void testEligibilityPaterns() {
		String phrase = "Unable to add a new AIX server";
		ExtractADJPbyNLP step = new ExtractADJPbyNLP();
		ITicket ticket = new InMemoryTicket("", "", phrase);
		TechnicalDictionary container = new TechnicalDictionary();
		StepContext context = new StepContext("1");
		try {
			step.run("TestField",ticket,container, context);
			Assert.assertEquals(0, container.getTermsCount());
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testEligibilityNames() {
	}

};
