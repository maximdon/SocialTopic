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
import com.softlib.imatch.ticketprocessing.HtmlAbbreviationsStep;
import com.softlib.imatch.ticketprocessing.SlashParserStep;
import com.softlib.imatch.ticketprocessing.UrlParserStep;
import com.softlib.imatch.ticketprocessing.ProcessedTicket;
import com.softlib.imatch.ticketprocessing.StepContext;
import com.softlib.imatch.ticketprocessing.TechTokens;

public class SlashParserTest {

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
	public void testSimpleCase()
	{
		String text = "Customer Service/Support agent";
		SlashParserStep step = new SlashParserStep();
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(text);
		try {
			//Test specific error code finds it
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("TestField", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("service", true);			
			Assert.assertNotNull(term);
			Assert.assertEquals(1, term.getRelations().size());
			getTermsContainer().getDictionary().unloadDictionary();
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testWithDash()
	{
		String text = "On-line/Off-Line system";
		SlashParserStep step = new SlashParserStep();
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(text);
		try {
			//Test specific error code finds it
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("TestField", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("on-line", true);
			Assert.assertNotNull(term);
			Assert.assertEquals(1, term.getRelations().size());
			getTermsContainer().getDictionary().unloadDictionary();
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testInTheMiddle()
	{
		String text = "CAN-2.0A/B";
		SlashParserStep step = new SlashParserStep();
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(text);
		try {
			//Test specific error code finds it
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("TestField", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("can-2.0a", true);
			Assert.assertNotNull(term);
			Assert.assertEquals(1, term.getRelations().size());
			getTermsContainer().getDictionary().unloadDictionary();
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testWithStopWords()
	{
		String text = "and/or";
		SlashParserStep step = new SlashParserStep();
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(text);
		try {
			//Test specific error code finds it
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("TestField", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("and", true);
			Assert.assertNull(term);
			getTermsContainer().getDictionary().unloadDictionary();
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testDates()
	{
		String text = "18/5/2011";
		SlashParserStep step = new SlashParserStep();
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(text);
		try {
			//Test specific error code finds it
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("TestField", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("18", true);
			Assert.assertNull(term);
			TechnicalDictionaryTerm term2 = getTermsContainer().getDictionary().get("2011", true);
			Assert.assertNull(term2);
			getTermsContainer().getDictionary().unloadDictionary();
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testPhones()
	{
		String text = "32-477-202-911/2";
		SlashParserStep step = new SlashParserStep();
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(text);
		try {
			//Test specific error code finds it
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("TestField", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("2", true);
			Assert.assertNull(term);
			TechnicalDictionaryTerm term2 = getTermsContainer().getDictionary().get("32-477-202-911", true);
			Assert.assertNull(term2);
			getTermsContainer().getDictionary().unloadDictionary();
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}
	@Test
	public void testLongText()
	{
		String text = "Softlib Software: Knowledge management, Search and Self Help solutions #BR Home Products Solutions Services Customers Resources About Overview iSolve iMatch iSolve Software Modules iMatch by Softlib – Automated Customer Service/Support agent #BR#BR Ideal for Service organizations, Technical Support and Helpdesk organizations, iMatch automatically offers Self Help for end users or experts. When a user reports an issue or when a service agent addresses an issue, iMatch automatically offers past proven solutions based on innovative text matching technology that leverages existing knowledge from past issues and solutions. iMatch integrates seamlessly with CRM systems, Service Desk applications etc. and is implemented in a matter of days. #BR#BR The Challenge: Research indicates that about 80% of the issues reported to the support/service/helpdesk staff are repetitive. Identifying solutions to repeat issues is a challenge as the success is based on a person’s vocabulary, training and expertise. This makes it highly unlikely that the second expert to handle an issue will find the first issue and leverage the knowledge gathered. Obviously the challenge remains the same for end users looking for Self Help. #BR#BR #BR#BR iMatch benefits include: Deliver effective Self Help to end users and experts Shorten service cycles Save experts time Resolve issues once – avoid repeat handling of same issues Resolve issues without involving many experts Handle more issues without added personnel Shorten training time for new personnel Reduce the impact of turnover Leverage existing knowledge bases / repositories without data duplication How iMatch works: iMatch automatically matches any new issue/story to past solutions or similar issues/stories so that end users or experts save time and leverage prior efforts and knowledge. iMatch unique technology is based on the following unique elements: #BR#BR Automated discovery of organizational terminology: iMatch scans all past issues and solutions to determine the important keywords used by the organization. Once keywords are mapped they are used to identify the essence of any new issues/stories. In addition iMatch includes a built in dictionary of common keywords that is updated regularly. The dictionaries can be edited and updated as needed. #BR#BR Federated Knowledge repositories: iMatch connects to multiple sources of information. In a typical scenario iMatch connects to the approved Solutions base for use by end users and to the Service Desk system and/or Bug Tracking system for use by experts. iMatch ability to use different repositories per user type make it a highly customizable solution. #BR#BR Text matching analytics: iMatch unique algorithms allows it to take 2 stories, identify the essence of each of them and match them even if the keywords used in each are different or are variations of the same words. #BR#BR Out of box integrations: iMatch comes with seamless integration into applications such as CRM, Service Desk, Bug Tracking systems etc. In addition its generic APIs allow new integrations to be built quickly and easily. #BR#BR iMatch – The Solution: iMatch automatically offers solutions to issues for end users or experts while eliminating escalations and reducing time to resolution! iMatch innovative analytical algorithms take the description of a new issue and identify other similar issues from the past, even if they were described in different words. Once a new issue is reported, iMatch scans the databases, files and knowledge bases it was configured to use and suggests the top matching issues or solutions. #BR#BR Download the iMatch Brochure #BR Download the iMatch Proof Of Value (POV) Service Brochure #BR Download the White Paper - Protect and Increase Customer Support Margins #BR#BR Contact Us We respect your privacy Europe +32-477-202-911 salesEU@softlibsw.com Middle East +972-3-5615190 info@softlibsw.com North America +1-203-274-8791 salesUS@softlibsw.com Exclusive Content #BR(Free Registration Required) Protect and Increase Customer Support Margins - White Paper Knowledge Delivery - Improve Customer Service while Lowering Costs - White Paper Shorter Resolution Cycles in Mainframe Environment - White Paper Making your CRM see inside out - White Paper Reinventing Support Economics - White Paper Webinar slides – iSolve &amp; ITIL v3 Knowledge Management Analyst Coverage #BR &quot;The first vendor to market with a text analytic offering specifically marketed for IT service management&quot; #BR EMA &quot;Automatically identifying solutions to repeated issues, regardless of choice of words...is a breakthrough.&quot; #BR TSIA #BR Copyright © 2012 Softlib Software - Privacy Policy #BR";
		SlashParserStep step = new SlashParserStep();
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(text);
		try {
			//Test specific error code finds it
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("TestField", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("service", true);
			Assert.assertNotNull(term);
			Assert.assertEquals(1, term.getRelations().size());
			getTermsContainer().getDictionary().unloadDictionary();
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}
}
