package com.softlib.imatch.test.ticketprocessing;

import java.util.ArrayList;
import java.util.Arrays;

import junit.framework.Assert;

import org.apache.log4j.xml.DOMConfigurator;
import org.junit.BeforeClass;
import org.junit.Test;

import com.softlib.imatch.ConsoleAppRuntimeInfo;
import com.softlib.imatch.InMemoryTicket;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.dictionary.TechnicalDictionary;
import com.softlib.imatch.dictionary.TechnicalDictionaryKey;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.ticketprocessing.ProcessedField;
import com.softlib.imatch.ticketprocessing.ProcessedTicket;

public class ProcessedTicketTest {

	public static final String FIELD_TITLE = "Title";
	public static final String FIELD_BODY = "Body";

	private void newField(ProcessedTicket ticket,
						  String fieldName,String[] strings) {
		ticket.startSession(fieldName,"100",fieldName);
		ArrayList<String> stringsList = 
			new ArrayList<String>(Arrays.asList(strings));
		for (String str : stringsList) 
			ticket.addTerm(new TechnicalDictionaryKey(str));
		ticket.endSession(0,null,false);
	}
	
	private ProcessedTicket newTicket() {
		//ObjectId is not important here
		return new ProcessedTicket(new InMemoryTicket("_COMMON_"), ProcessedTicket.getDefaultCalculator());
	}
	
	@BeforeClass
	public static void init() {
		ConsoleAppRuntimeInfo.init(null);
		DOMConfigurator.configure(RuntimeInfo.getCurrentInfo().getRealPath("/{SolutionConfigFolder}/log4j-console.xml"));
		TechnicalDictionary dictionary = (TechnicalDictionary) RuntimeInfo.getCurrentInfo().getBean("dictionary");
		dictionary.loadDictionary();
	}

	private void printProcessedTicket(ProcessedTicket ticket) {
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		System.out.println("printProcessedTicket :");
		System.out.println("----------------------");
		for (ProcessedField field : ticket.getData().values()) {
			System.out.println(" --> Field :"+field.getFieldName());
			for ( TechnicalDictionaryTerm term : field.getTerms() )
				System.out.println("       Term = " + term.getTermText());
		}
		System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
	}

	@Test
	public void test_1Field_NoMatch() {
		ProcessedTicket ticket1 = newTicket();
		newField(ticket1,FIELD_BODY, new String[] {"Center"});
		ProcessedTicket ticket2 = newTicket();
		newField(ticket2,FIELD_BODY, new String[] {"access"});
		float matchScore = ProcessedTicket.match(ticket1, ticket2).getScore();
		Assert.assertEquals("No matches, the score should be 0", (float)0.0, matchScore);
	}
	
	@Test
	public void test_1Field_Match1() {
		ProcessedTicket ticket1 = newTicket();
		newField(ticket1,FIELD_TITLE, new String[] {"access"});
		ProcessedTicket ticket2 = newTicket();
		newField(ticket2,FIELD_TITLE, new String[] {"access"});
		newField(ticket2,FIELD_BODY, new String[] {"backup"});
		float matchScore = ProcessedTicket.match(ticket1, ticket2).getScore();
		Assert.assertEquals("Partial match, the score should be 0.5", (float)1, matchScore);
	}
	
	@Test
	public void test_2Field_PartialMatch() {
		ProcessedTicket ticket1 = newTicket();
		newField(ticket1,FIELD_TITLE, new String[] {"access"});
		newField(ticket1,FIELD_BODY, new String[] {"backup"});
		ProcessedTicket ticket2 = newTicket();
		newField(ticket2,FIELD_TITLE, new String[] {"access"});
		newField(ticket2,FIELD_BODY, new String[] {"backup"});
		float matchScore = ProcessedTicket.match(ticket1, ticket2).getScore();
		printProcessedTicket(ticket1);
		printProcessedTicket(ticket2);
		boolean result = (matchScore >= 0.4) && (matchScore <= 0.5);
		System.out.println("Match Score ="+matchScore);
		Assert.assertEquals("Partial match, the score should be ~ 0.45", true, result);
	}

	@Test
	public void test_2Field_Match() {
		ProcessedTicket ticket1 = newTicket();
		newField(ticket1,FIELD_TITLE, new String[] {"access","backup"});
		newField(ticket1,FIELD_BODY, new String[] {"cancel","callback"});
		ProcessedTicket ticket2 = newTicket();
		newField(ticket2,FIELD_TITLE, new String[] {"access","backup"});
		newField(ticket2,FIELD_BODY, new String[] {"cancel","callback"});
		float matchScore = ProcessedTicket.match(ticket1,ticket2).getScore();
		printProcessedTicket(ticket1);
		printProcessedTicket(ticket2);
		Assert.assertEquals("Partial match, the score should be 1", (float)1, matchScore);
	}

	@Test
	public void test_1Field_Match() {
		ProcessedTicket ticket1 = newTicket();
		newField(ticket1,FIELD_TITLE, new String[] {"access","backup"});
		ProcessedTicket ticket2 = newTicket();
		newField(ticket2,FIELD_TITLE, new String[] {"access","backup"});
		float matchScore = ProcessedTicket.match(ticket1,ticket2).getScore();
		printProcessedTicket(ticket1);
		printProcessedTicket(ticket2);
		Assert.assertEquals("Partial match, the score should be 1", (float)1, matchScore);
	}

	@Test
	public void test_1Field_Match3() {
		ProcessedTicket ticket1 = newTicket();
		newField(ticket1,FIELD_TITLE, new String[] {"access", "backup"});
		ProcessedTicket ticket2 = newTicket();
		newField(ticket2,FIELD_TITLE, new String[] {"access", "center"});
		float matchScore = ProcessedTicket.match(ticket1, ticket2).getScore();
		printProcessedTicket(ticket1);
		printProcessedTicket(ticket2);
		boolean result = (matchScore >= 0.4) && (matchScore <= 0.5);
		System.out.println("Match Score ="+matchScore);
		Assert.assertEquals("Partial match, the score should be ~ 0.45", true, result);
	}
	
	@Test
	public void test_1Field_FullMatch() {
		ProcessedTicket ticket1 = newTicket();
		newField(ticket1,FIELD_TITLE, new String[] {"access", "backup"});
		ProcessedTicket ticket2 = newTicket();
		newField(ticket2,FIELD_TITLE, new String[] {"access", "backup"});
		float matchScore = ProcessedTicket.match(ticket1, ticket2).getScore();
		printProcessedTicket(ticket1);
		printProcessedTicket(ticket2);
		Assert.assertEquals("Full match, the score should be 1", (float)1.0, matchScore);
	}

	@Test
	public void test_2Field_PartialMatch2() {
		ProcessedTicket ticket1 = newTicket();
		newField(ticket1,FIELD_TITLE, new String[] {"access", "center"});
		newField(ticket1,FIELD_BODY, new String[] {"backup", "char"});
		ProcessedTicket ticket2 = newTicket();
		newField(ticket2,FIELD_TITLE, new String[] {"access", "backup"});
		newField(ticket2,FIELD_BODY, new String[] {"char", "class"});
		float matchScore = ProcessedTicket.match(ticket1, ticket2).getScore();
		printProcessedTicket(ticket1);
		printProcessedTicket(ticket2);
		boolean result = (matchScore >= 0.4) && (matchScore <= 0.5);
		System.out.println("Match Score ="+matchScore);
		Assert.assertEquals("Partial match, the score should be ~ 0.45", true, result);
	}
	
	@Test
	public void test_2Field_FullMatch() {
		ProcessedTicket ticket1 = newTicket();
		newField(ticket1,FIELD_TITLE, new String[] {"access", "backup"});
		newField(ticket1,FIELD_BODY, new String[] {"center", "char"});
		ProcessedTicket ticket2 = newTicket();
		newField(ticket2,FIELD_TITLE, new String[] {"access", "backup"});
		newField(ticket2,FIELD_BODY, new String[] {"center", "char"});
		float matchScore = ProcessedTicket.match(ticket1, ticket2).getScore();
		printProcessedTicket(ticket1);
		printProcessedTicket(ticket2);
		Assert.assertEquals("Full match, the score should be 1", (float)1.0, matchScore);
	}

};
