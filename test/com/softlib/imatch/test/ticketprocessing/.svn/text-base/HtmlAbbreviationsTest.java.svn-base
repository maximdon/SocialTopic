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
import com.softlib.imatch.ticketprocessing.ProcessedTicket;
import com.softlib.imatch.ticketprocessing.StepContext;
import com.softlib.imatch.ticketprocessing.TechTokens;

public class HtmlAbbreviationsTest {

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
	public void testSimpleAbbreviation()
	{
		String text = "Line Departure Warning (LDW)";
		HtmlAbbreviationsStep step = new HtmlAbbreviationsStep();
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(text);
		try {
			//Test specific error code finds it
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("TestField", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("ldw", true);
			Assert.assertNotNull(term);
			TechnicalDictionaryTerm term2 = getTermsContainer().getDictionary().get("line departure warning", true);
			Assert.assertNotNull(term2);
			Assert.assertEquals(1, term.getRelations().size());
			getTermsContainer().getDictionary().unloadDictionary();
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testLowCaseAbbreviation()
	{
		String text = "Data as a Service (DaaS)";
		HtmlAbbreviationsStep step = new HtmlAbbreviationsStep();
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(text);
		try {
			//Test specific error code finds it
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("TestField", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("daas", true);
			Assert.assertNotNull(term);
			Assert.assertEquals(1, term.getRelations().size());
			getTermsContainer().getDictionary().unloadDictionary();
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testLowCaseAbbreviation2()
	{
		String text = "Network operation center (NOC)";
		HtmlAbbreviationsStep step = new HtmlAbbreviationsStep();
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(text);
		try {
			//Test specific error code finds it
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("TestField", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("noc", true);
			Assert.assertNotNull(term);
			TechnicalDictionaryTerm term2 = getTermsContainer().getDictionary().get("network operation center", true);
			Assert.assertNotNull(term2);
			Assert.assertEquals(1, term.getRelations().size());
			getTermsContainer().getDictionary().unloadDictionary();
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}
	@Test
	public void testAbbreviationWithStopWord()
	{
		String text = "Lane Keeping and Support (LKS)";
		HtmlAbbreviationsStep step = new HtmlAbbreviationsStep();
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(text);
		try {
			//Test specific error code finds it
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("TestField", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("lks", true);
			Assert.assertNotNull(term);
			Assert.assertEquals(1, term.getRelations().size());
			getTermsContainer().getDictionary().unloadDictionary();
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testAbbreviationLongText()
	{
		String text = "Softlib Software: Knowledge management, Search and Self Help solutions - Bank Leumi Press Release #BR Home Products Solutions Services Customers Resources About Softlib - iSolve Menorah Mivtahim Insurance Bar Ilan University Bank Leumi Bank Leumi add the &quot;Hot Key Fast Lookup&quot; module to its iSolve server. #BR#BR 27/04/2008, Lod, Israel Softlib's New &quot;Hot Key Fast Look Up&quot; Feature adds Quick &amp; Easy Error Code Resolution Capabilities to Leumi Bank It Team. #BR#BR Every day, over 1000 IT workers at Leumi Bank are happily using iSolve to retrieve and manage their IT technical information. With iSolve, Leumi IT workers can immediately and easily get all the relevant and only the relevant technical information they need to execute their daily IT tasks. E.g. solving problems, maintaining corporate infrastructures and finding development techniques. iSolve enables Leumi IT users to easily retrieve data of any vendor, any platform and in any format (Mainframe to dot Net). #BR#BR &quot;iSolve simply enables our IT team to focus on executing their daily IT tasks instead of wasting precious time on searching for technical information&quot; says Shlomo Amar, from the connectivity team of Leumi Bank Technology Infrastructure Branch. #BR#BR Recently, Leumi Bank have added iSolve's new module the &quot;Hot Key Fast Lookup&quot; to the IT user's desktops. The &quot;Hot Key Fast Lookup&quot; module enables users to solve error codes quickly and easily. Whenever users are facing an error code they simply mark the original error code, press Ctrl + F12, and immediately get all the relevant (and only the relevant) information they need to solve the error code. The explanation how to solve the error-code is taken from each product original error codes resolution book. The solution is presented in a small window in the user's desktop with a link to the full relevant technical documentation. #BR#BR &quot;The &quot;Hot Key Fast Lookup&quot; is one more sample of iSolve's abilities to save time and improve our IT team productivity&quot; summarize Mr. Amar. #BR#BR Leumi Bank ( http://english.leumi.co.il ) #BR Leumi Bank, the largest Bank in Israel was established in 1902 as a part of Dr. Benyamin Hertzel’s Zionistic dream. The Bank was intended to be the financial arm of the Zionist Movement and is now the oldest and one of the leading banking corporations in Israel. The Leumi Group currently has more than 317 branches and representations across 21 countries worldwide including some of the world's leading financial centers. The Leumi Group deals with a diversity of domestic and international financial and non-monetary operations. Total assets under the management of the Leumi Group (balance sheet items and non-balance sheet items) amounted to some NIS 758 billion at the end of September 2007. #BR#BR #BR#BR About Softlib (www.softlibsw.com) #BR Softlib is a leading provider of comprehensive IT technical information Management solutions. Every day, thousands of IT professionals in the financial, telecommunications, educational and governmental sectors use Softlib’s products. Softlib mission is to organize the world's IT Technical information and make it accessible and useful to any IT professional. #BR#BR Contact: #BR Rotem Ackermann #BR Softlib, #BR 65 Yigal Alon St. #BR Toyota Towers #BR Tel Aviv 67433, Israel #BR Email: rotem@softlibsw.com #BR Tel: +972-3-5615190 #BR #BR #BR Contact Us We respect your privacy Europe +32-477-202-911 salesEU@softlibsw.com Middle East +972-3-5615190 info@softlibsw.com North America +1-203-274-8791 salesUS@softlibsw.com Exclusive Content #BR(Free Registration Required) Protect and Increase Customer Support Margins - White Paper Knowledge Delivery - Improve Customer Service while Lowering Costs - White Paper Shorter Resolution Cycles in Mainframe Environment - White Paper Making your CRM see inside out - White Paper Reinventing Support Economics - White Paper Webinar slides – iSolve &amp; ITIL v3 Knowledge Management Analyst Coverage #BR &quot;The first vendor to market with a text analytic offering specifically marketed for IT service management&quot; #BR EMA &quot;Automatically identifying solutions to repeated issues, regardless of choice of words...is a breakthrough.&quot; #BR TSIA #BR Copyright © 2012 Softlib Software - Privacy Policy #BR Download proof of value (pov) there";
		HtmlAbbreviationsStep step = new HtmlAbbreviationsStep();
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(text);
		try {
			//Test specific error code finds it
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("TestField", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("pov", true);
			Assert.assertNotNull(term);
			Assert.assertEquals(1, term.getRelations().size());
			getTermsContainer().getDictionary().unloadDictionary();
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testAyehu1()
	{
		String text = "For IT Departments, Network Operation Centers (NOC), and Managed Service";
		HtmlAbbreviationsStep step = new HtmlAbbreviationsStep();
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(text);
		try {
			//Test specific error code finds it
			InMemoryTicket ticket = new InMemoryTicket("cases", "Ticket title", "Ticket Body");
			step.run("TestField", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("noc", true);
			Assert.assertNotNull(term);
			Assert.assertEquals(1, term.getRelations().size());
			getTermsContainer().getDictionary().unloadDictionary();
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testAyehu2()
	{
		String text = "IT Process Automation Solutions - Ayehu eyeShare US 1-800-652-5601 info@ayehu.com blog.ayehu.com Home Products IT Process Automation (ITPA) Run book automation (RBA) Unified Incident Management Alert Notification and Escalation Product FAQ IT Process Automation Demo Solutions Solution for IT Operations Managed Service Providers Nagios Integration SCOM 2012 Integration Solarwinds Integration ServiceNow (ITSM) Integration JIRA Integration VMware vSphere Integration Download Download Free 30-Day Trial Download White Paper System Requirements Pricing Request a Quote Resources Video Tutorials eBooks and White Papers Automated Workflows User Guides Support Packages Contact Support Professional Services Customer Support Policy Company Management Team Advisory Board Customers Partners Terms of Use Privacy Policy News & Events Contact Ayehu eyeShare IT Process Automation. .\nFor IT Departments, Network Operation Centers (NOC), and Managed Service Providers (MSP). Home &gt; Solutions Solution for IT Operations Managed Service Providers Nagios Integration SCOM 2012 Integration Solarwinds Integration ServiceNow (ITSM) Integration JIRA Integration VMware vSphere Integration Resources Product Datasheet Customer Case Studies Video Channel Automated Workflows White Paper Solutions Overview .\nAyehu provides IT Process Automation solutions that addresses the needs of IT departments, Network operation center (NOC) groups, and Managed Service Providers (MSPs). Deployed at major enterprises and supporting thousands of business users, our solutions help IT professional orchestrate, automate, and effectively manage all activities related to the resolution of IT incidents. The results? Up to 90 percent faster MTTR, drastically improved service quality, and optimal use of scarce IT resources. For IT Groups For MSPs Ayehu eyeShare helps IT and Network Operations Center (NOC) managers meet SLAs, maintain service availability, and systematically manage and improve incident resolution performance. All this ֠without expanding IT resources. Learn more about our solution for IT operations. Designed to handle multiple, complex IT infrastructures, Ayehu eyeShare helps Managed Service Providers to centrally manage, automate, and support a large number of remote data centers from a single network operation center. Learn more about our solution for MSPs. Help & Support User Guides Download eBooks eyeShare Demo FAQ Contact Support IT Process Automation Active Directory Unlock Account Active Directory Password Reset Free up disk space Automate Service Restart File Monitoring and automation Event log monitoring Automate SQL Query Change Service Account Shutdown Remote Computer Automate VMware Snapshots Follow us online Ayehu Blog Follow us on Twitter Follow us on Facebook Join our LinkedIn group Get the Newsletter News & Events Solutions IT Process Automation ITIL Incident management Run book automation Alert & Notification Nagios Integration SCOM 2012 Integration JIRA Integration Solarwinds Integration ServiceNow Integration VMware vSphere Integration Serious Stuff Terms of Use Privacy Policy About Ayehu Contact Us Sitemap";
		HtmlAbbreviationsStep step = new HtmlAbbreviationsStep();
		StepContext stepContext = new StepContext("1");
		stepContext.setCleanText(text);
		try {
			//Test specific error code finds it
			InMemoryTicket ticket = new InMemoryTicket("cases", "website-ayehu/27", "Ticket title", "Ticket Body");
			step.run("TestField", ticket, getTermsContainer(), stepContext);
			TechnicalDictionaryTerm term = getTermsContainer().getDictionary().get("noc", true);
			Assert.assertNotNull(term);
			TechnicalDictionaryTerm term3 = getTermsContainer().getDictionary().get("managed service provider", true);
			Assert.assertNotNull(term3);
			TechnicalDictionaryTerm term4 = getTermsContainer().getDictionary().get("run book automation", true);
			Assert.assertNotNull(term4);
			Assert.assertEquals(1, term.getRelations().size());
			getTermsContainer().getDictionary().unloadDictionary();
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}
}

