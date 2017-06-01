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
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.ticketprocessing.ExtractTechTokensByNLP;
import com.softlib.imatch.ticketprocessing.ExtractVersionsByNLP;
import com.softlib.imatch.ticketprocessing.NegativeSentimentStep;
import com.softlib.imatch.ticketprocessing.ProcessedTicket;
import com.softlib.imatch.ticketprocessing.StepContext;

public class ExtractNegativeSentimentTest {

	private static TechnicalDictionary dictionary;
	private boolean initialized = false;
	@BeforeClass
	public static void init()
	{
		ConsoleAppRuntimeInfo.init(null);
		DOMConfigurator.configure(RuntimeInfo.getCurrentInfo().getRealPath("/{SolutionConfigFolder}/log4j-console.xml"));
		dictionary = (TechnicalDictionary) RuntimeInfo.getCurrentInfo().getBean("dictionary");
	}
	
	@After
	public void finish()
	{
		getTermsContainer().getDictionary().unloadDictionary();		
	}
	
	private ITechnicalTermsContainer getTermsContainer() {		
		return dictionary;
	}
	
	@Test
	public void testErrorPattern() {
		String errorMsg = "Error connecting to database, underline error is aaa";
		try {
			TechnicalDictionaryTerm term = createTestTerm(errorMsg, "error connecting database");
			Assert.assertNotNull(term);
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}		

	@Test
	public void testErrorPatternNotInBeggining() {
		String errorMsg = "I got an error connecting to database, underline error is aaa";
		try {
			TechnicalDictionaryTerm term = createTestTerm(errorMsg, "error connecting database");
			Assert.assertNotNull(term);
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}		
	
	@Test
	public void testFailedToPattern() {
		String errorMsg = "Failed to connect to database, underline error is aaa";
		try {
			TechnicalDictionaryTerm term = createTestTerm(errorMsg, "failed to connect database");
			Assert.assertNotNull(term);
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testFailedPattern() {
		String errorMsg = "Database connection failed, underline error is aaa";
		try {
			TechnicalDictionaryTerm term = createTestTerm(errorMsg, "database connection failed");
			Assert.assertNotNull(term);
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testErrorOccuredDuringPattern() {
		String errorMsg = "Error occurred during connecting to database, underline error is aaa";
		try {
			TechnicalDictionaryTerm term = createTestTerm(errorMsg, "error occurred during connecting database");
			Assert.assertNotNull(term);
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testCannotPattern() {
		String errorMsg = "Cannot connect to database, underline error is aaa";
		try {
			TechnicalDictionaryTerm term = createTestTerm(errorMsg, "cannot connect database");
			Assert.assertNotNull(term);
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testCannotPatternNotInBeggining() {
		String errorMsg = "I cannot connect to database, underline error is aaa";
		try {
			TechnicalDictionaryTerm term = createTestTerm(errorMsg, "cannot connect database");
			Assert.assertNotNull(term);
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testWillNotPattern() {
		String errorMsg = "The server will not connect to database, underline error is aaa";
		try {
			TechnicalDictionaryTerm term = createTestTerm(errorMsg, "will not connect database");
			Assert.assertNotNull(term);
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testCouldNotBeCompletedPattern() {
		String errorMsg = "Database connection could not be completed, underline error is aaa";
		try {
			TechnicalDictionaryTerm term = createTestTerm(errorMsg, "database connection could not be completed");
			Assert.assertNotNull(term);
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testAndFailedPattern() {
		String errorMsg = "Tryed to connect to database and failed, underline error is aaa";
		try {
			TechnicalDictionaryTerm term = createTestTerm(errorMsg, "connect database and failed");
			Assert.assertNotNull(term);
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testFailurePattern() {
		String errorMsg = "I got a database connection failure, underline error is aaa";
		try {
			TechnicalDictionaryTerm term = createTestTerm(errorMsg, "database connection failure");
			Assert.assertNotNull(term);
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testTryingTo() {
		String errorMsg = "Error while trying to connect to server";
		try {
			TechnicalDictionaryTerm term = createTestTerm(errorMsg, "error while connect server");
			Assert.assertNotNull(term);
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testTryingToMcKesson1() {
		String errorMsg = "Getting error message when trying to upload resouces";
		try {
			TechnicalDictionaryTerm term = createTestTerm(errorMsg, "trying to upload resouces");
			Assert.assertNotNull(term);
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testTryingToMcKesson2() {
		String errorMsg = "He is trying to upload resources and gets an error message";
		try {
			TechnicalDictionaryTerm term = createTestTerm(errorMsg, "trying to upload resources");
			Assert.assertNotNull(term);
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testCannotMcKesson() {
		String errorMsg = "Cannot upload resources while upgrading the site to sp6";
		try {
			TechnicalDictionaryTerm term = createTestTerm(errorMsg, "cannot upload resources");
			Assert.assertNotNull(term);
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testFailedToMcKesson() {
		String errorMsg = "He got a message \"failed to upload resources\"";
		try {
			TechnicalDictionaryTerm term = createTestTerm(errorMsg, "failed to upload resources");
			Assert.assertNotNull(term);
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testLogIntoMcKesson() {
		String errorMsg = "Cannot log into database";
		try {
			TechnicalDictionaryTerm term = createTestTerm(errorMsg, "cannot log database");
			Assert.assertNotNull(term);
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testWithDelimiter1() {
		String errorMsg = "Cannot log to a database";
		try {
			TechnicalDictionaryTerm term = createTestTerm(errorMsg, "cannot log database");
			Assert.assertNotNull(term);
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testWithDelimiter2() {
		String errorMsg = "Cannot log to an database";
		try {
			TechnicalDictionaryTerm term = createTestTerm(errorMsg, "cannot log database");
			Assert.assertNotNull(term);
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testWithDelimiter3() {
		String errorMsg = "Cannot log to the database";
		try {
			TechnicalDictionaryTerm term = createTestTerm(errorMsg, "cannot log database");
			Assert.assertNotNull(term);
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testWithDelimiter4() {
		String errorMsg = "Will not log to any database";
		try {
			TechnicalDictionaryTerm term = createTestTerm(errorMsg, "will not log database");
			Assert.assertNotNull(term);
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testWithDelimiter5() {
		String errorMsg = "Will not log from database";
		try {
			TechnicalDictionaryTerm term = createTestTerm(errorMsg, "will not log database");
			Assert.assertNotNull(term);
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testPositiveSentiment() {
		String errorMsg = "Database connection not failed, no underline error";
		try {
			TechnicalDictionaryTerm term = createTestTerm(errorMsg, "database connection not failed");
			Assert.assertNull(term);
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	@Ignore("This test fails")
	public void testPositiveSentiment2() {
		String errorMsg = "There is no database connection failure, no underline error";
		try {
			TechnicalDictionaryTerm term = createTestTerm(errorMsg, "database connection failure");
			Assert.assertNull(term);
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	private TechnicalDictionaryTerm createTestTerm(String errorMsg, String expectedTermText) throws MatcherException {
		NegativeSentimentStep step = new NegativeSentimentStep();
		//Simulate spring event
		if(!initialized) {
			step.contextInitialized();
			//step.setSkipWords(Arrays.asList(new String[] {"trying to"}));			
			initialized = true;
		}
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(errorMsg);
		InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
		step.run("TestField", ticket, getTermsContainer(), stepContext);
		TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get(expectedTermText, true);
		return term;
	}
}
