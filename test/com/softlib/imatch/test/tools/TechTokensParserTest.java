package com.softlib.imatch.test.tools;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.apache.log4j.xml.DOMConfigurator;
import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.softlib.imatch.ConsoleAppRuntimeInfo;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.common.SessionMode;
import com.softlib.imatch.dictionary.SynonymsRelation;
import com.softlib.imatch.dictionary.TechnicalDictionary;
import com.softlib.imatch.dictionary.TechnicalDictionaryKey;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.dictionary.Wordnet;
import com.softlib.tools.dictionaryparsers.TechTokensParser;

public class TechTokensParserTest {
	private static TechnicalDictionary dictionary;
	private List<TechnicalDictionaryTerm> savedTerms = new ArrayList<TechnicalDictionaryTerm>();
	@BeforeClass
	public static void init()
	{
		ConsoleAppRuntimeInfo.init(null);
		DOMConfigurator.configure(RuntimeInfo.getCurrentInfo().getRealPath("/{SolutionConfigFolder}/log4j-console.xml"));
		RuntimeInfo.getCurrentInfo().getBean("dictionary");
		dictionary = new TechnicalDictionary();
		Wordnet.getInstance();
	}

	@Before
	public void restart()
	{
		dictionary = new TechnicalDictionary();
	}
	
	@After
	public void cleanup()
	{
		for(TechnicalDictionaryTerm term : savedTerms)
			dictionary.deleteTerm(term);
	}
	
	@Test
	public void testCleanTerm()
	{
		TechnicalDictionaryKey percentKey = new TechnicalDictionaryKey("% %");
		dictionary.addTerm(percentKey);
		TechnicalDictionaryKey percentGotoKey = new TechnicalDictionaryKey("% goto");
		dictionary.addTerm(percentGotoKey);
		TechnicalDictionaryKey dotGotoKey = new TechnicalDictionaryKey(".goto");
		dictionary.addTerm(dotGotoKey);
//		TechTokensParser parser = new TechTokensParser(0, 0, 0, 0, false, 0);
		try {
//			parser.cleanTerm(percentTerm);
//			Assert.assertEquals("", percentTerm.getTermText());
//			parser.cleanTerm(percentGotoTerm);
//			Assert.assertEquals("goto", percentGotoTerm.getTermText());
//			parser.cleanTerm(dotGotoTerm);
//			Assert.assertEquals("goto", dotGotoTerm.getTermText());
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		} 	
	}
	
	@Test
	public void testBuildKnownSynonyms2()
	{		
		dictionary.addTermByUser(new TechnicalDictionaryKey("internet explorer"));
		dictionary.addTermByUser(new TechnicalDictionaryKey("ie"));
		TechnicalDictionaryTerm term1 = dictionary.addTerm(new TechnicalDictionaryKey("start internet explorer"));
		dictionary.saveTerm(term1);
		savedTerms.add(term1);
		TechnicalDictionaryTerm term2 = dictionary.addTerm(new TechnicalDictionaryKey("ie start"));
		dictionary.saveTerm(term2);
		savedTerms.add(term2);
		TechTokensParser parser = new TechTokensParser(0, 0, 0, 0, false, 0);
		Session session = RuntimeInfo.getCurrentInfo().getHibernate().acquireSession(SessionMode.READ_WRITE);
		try {
			parser.buildRelations(dictionary, session);
			TechnicalDictionaryTerm term = dictionary.get("ie start",true);
			Assert.assertNotNull(term);
			Assert.assertEquals(1, term.getRelations().size());
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		} catch (SQLException e) {
			Assert.fail(e.getMessage());
		}
		finally {
			RuntimeInfo.getCurrentInfo().getHibernate().releaseSession(session);
		}
	}

	@Test
	public void testBuildKnownSynonyms3()
	{		
		dictionary.addTermByUser(new TechnicalDictionaryKey("internet explorer"));
		dictionary.addTermByUser(new TechnicalDictionaryKey("ie"));
		dictionary.addTerm(new TechnicalDictionaryKey("purge"));
		dictionary.addTerm(new TechnicalDictionaryKey("cleanup"));
		TechnicalDictionaryTerm term1 = dictionary.addTerm(new TechnicalDictionaryKey("internet explorer purge"));
		dictionary.saveTerm(term1);
		savedTerms.add(term1);
		TechnicalDictionaryTerm term2 = dictionary.addTerm(new TechnicalDictionaryKey("ie cleanup"));
		dictionary.saveTerm(term2);
		savedTerms.add(term2);
		TechTokensParser parser = new TechTokensParser(0, 0, 0, 0, false, 0);
		Session session = RuntimeInfo.getCurrentInfo().getHibernate().acquireSession(SessionMode.READ_WRITE);
		try {
			parser.buildRelations(dictionary, session);
			TechnicalDictionaryTerm term = dictionary.get("ie cleanup",true);
			Assert.assertNotNull(term);
			Assert.assertEquals(0, term.getRelations().size());
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		} catch (SQLException e) {
			Assert.fail(e.getMessage());
		}
		finally {
			RuntimeInfo.getCurrentInfo().getHibernate().releaseSession(session);
		}
	}

	@Test
	public void testBuildKnownSynonyms4()
	{		
		TechnicalDictionaryTerm term3 = dictionary.addTerm(new TechnicalDictionaryKey("internet explorer purge"));
		term3.setAbsoluteFrequency(2);
		dictionary.saveTerm(term3);
		savedTerms.add(term3);
		TechnicalDictionaryTerm term4 = dictionary.addTerm(new TechnicalDictionaryKey("ie cleanup"));
		term4.setAbsoluteFrequency(2);
		dictionary.saveTerm(term4);
		savedTerms.add(term4);
		dictionary.addTermByUser(new TechnicalDictionaryKey("internet explorer"));
		dictionary.addTermByUser(new TechnicalDictionaryKey("ie"));
		TechnicalDictionaryTerm term1 = dictionary.addTermByUser(new TechnicalDictionaryKey("purge"));	
		TechnicalDictionaryTerm term2 = dictionary.addTermByUser(new TechnicalDictionaryKey("cleanup"));
		new SynonymsRelation().relate(term1, term2, "Test");
		TechTokensParser parser = new TechTokensParser(0, 0, 0, 0, false, 0);
		Session session = RuntimeInfo.getCurrentInfo().getHibernate().acquireSession(SessionMode.READ_WRITE);
		try {
			parser.buildRelations(dictionary, session);
			TechnicalDictionaryTerm term = dictionary.get("ie cleanup",true);
			Assert.assertNotNull(term);
			Assert.assertEquals(1, term.getRelations().size());
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		} catch (SQLException e) {
			Assert.fail(e.getMessage());
		}
		finally {
			RuntimeInfo.getCurrentInfo().getHibernate().releaseSession(session);
		}
	}
	
	@Test
	public void testBuildKnownSynonyms()
	{		
		dictionary.addTermByUser(new TechnicalDictionaryKey("internet explorer"));
		dictionary.addTermByUser(new TechnicalDictionaryKey("ie"));
		TechTokensParser parser = new TechTokensParser(0, 0, 0, 0, false, 0);
		Session session = RuntimeInfo.getCurrentInfo().getHibernate().acquireSession(SessionMode.READ_WRITE);
		try {
			parser.buildRelations(dictionary, session);
			TechnicalDictionaryTerm term = dictionary.get("ie",true);
			Assert.assertNotNull(term);
			Assert.assertEquals(1, term.getRelations().size());
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		} catch (SQLException e) {
			Assert.fail(e.getMessage());
		}
		finally {
			RuntimeInfo.getCurrentInfo().getHibernate().releaseSession(session);
		}
	}
	
	@Test
	public void testBuildVendorSynonyms()
	{
		dictionary.addTermByUser(new TechnicalDictionaryKey("Microsoft SSIS Driver"));
		dictionary.addTermByUser(new TechnicalDictionaryKey("SSIS Driver"));
		TechTokensParser parser = new TechTokensParser(0, 0, 0, 0, false, 0);
		Session session = RuntimeInfo.getCurrentInfo().getHibernate().acquireSession(SessionMode.READ_WRITE);
		try {
			parser.buildRelations(dictionary, session);
			TechnicalDictionaryTerm term = dictionary.get("SSIS Driver",true);
			Assert.assertNotNull(term);
			Assert.assertEquals(1, term.getRelations().size());
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		} catch (SQLException e) {
			Assert.fail(e.getMessage());
		}
		finally {
			RuntimeInfo.getCurrentInfo().getHibernate().releaseSession(session);
		}
	}

	@Test
	public void testBuildVendorEnglishWord()
	{
		dictionary.addTerm(new TechnicalDictionaryKey("Microsoft Office"));
		dictionary.addTerm(new TechnicalDictionaryKey("Office"));
		TechTokensParser parser = new TechTokensParser(0, 0, 0, 0, false, 0);
		Session session = RuntimeInfo.getCurrentInfo().getHibernate().acquireSession(SessionMode.READ_WRITE);
		try {
			parser.buildRelations(dictionary, session);
			TechnicalDictionaryTerm term = dictionary.get("Microsoft Office",true);
			Assert.assertNotNull(term);
			Assert.assertEquals(0, term.getRelations().size());
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		} catch (SQLException e) {
			Assert.fail(e.getMessage());
		}
		finally {
			RuntimeInfo.getCurrentInfo().getHibernate().releaseSession(session);
		}
	}

	@Test
	public void testBuildVendorSynonymsLowCase()
	{
		dictionary.addTermByUser(new TechnicalDictionaryKey("microsoft SSIS Driver"));
		dictionary.addTermByUser(new TechnicalDictionaryKey("sSiS DRiveR"));
		TechTokensParser parser = new TechTokensParser(0, 0, 0, 0, false, 0);
		Session session = RuntimeInfo.getCurrentInfo().getHibernate().acquireSession(SessionMode.READ_WRITE);
		try {
			parser.buildRelations(dictionary, session);
			TechnicalDictionaryTerm term = dictionary.get("SSIS Driver",true);
			Assert.assertNotNull(term);
			Assert.assertEquals(1, term.getRelations().size());
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		} catch (SQLException e) {
			Assert.fail(e.getMessage());
		}
		finally {
			RuntimeInfo.getCurrentInfo().getHibernate().releaseSession(session);
		}
	}

	@Test
	public void testBuildVendorUnclear()
	{
		dictionary.addTerm(new TechnicalDictionaryKey("Microsoft Database Driver"));
		dictionary.addTerm(new TechnicalDictionaryKey("Oracle Database Driver"));
		dictionary.addTerm(new TechnicalDictionaryKey("Database Driver"));
		TechTokensParser parser = new TechTokensParser(0, 0, 0, 0, false, 0);
		Session session = RuntimeInfo.getCurrentInfo().getHibernate().acquireSession(SessionMode.READ_WRITE);
		try {
			parser.buildRelations(dictionary, session);
			TechnicalDictionaryTerm term = dictionary.get("Microsoft Database Driver",true);
			Assert.assertNotNull(term);	
			Assert.assertEquals(0, term.getRelations().size());
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		} catch (SQLException e) {
			Assert.fail(e.getMessage());
		}
		finally { 
			RuntimeInfo.getCurrentInfo().getHibernate().releaseSession(session);
		}
	}

	@Test
	public void testTransitiveRelations()
	{
		dictionary.loadDictionary();
		TechnicalDictionaryTerm term1 = dictionary.addTermByUser(new TechnicalDictionaryKey("Microsoft SSIS Driver"));
		term1.setTermSource(dictionary.getSource("NLP NNP Tokens"));
		TechnicalDictionaryTerm term2 = dictionary.addTermByUser(new TechnicalDictionaryKey("SSIS Driver"));
		term2.setTermSource(dictionary.getSource("NLP NNP Tokens Split"));
		TechnicalDictionaryTerm term3 = dictionary.addTermByUser(new TechnicalDictionaryKey("SSISDriver"));
		term3.setTermSource(dictionary.getSource("NLP NNP Tokens Split"));
		TechnicalDictionaryTerm term4 = dictionary.addTermByUser(new TechnicalDictionaryKey("SSIS-Driver"));
		term4.setTermSource(dictionary.getSource("errorCodes"));
		TechTokensParser parser = new TechTokensParser(0, 0, 0, 0, false, 0);
		Session session = RuntimeInfo.getCurrentInfo().getHibernate().acquireSession(SessionMode.READ_WRITE);
		try {
			parser.buildRelations(dictionary, session);
			TechnicalDictionaryTerm term = dictionary.get("Microsoft SSIS Driver",true);
			Assert.assertNotNull(term);
			Assert.assertEquals(3, term.getRelations().size());
			TechnicalDictionaryTerm dashTerm = dictionary.get("SSIS-Driver",true);
			Assert.assertNotNull(dashTerm);
			Assert.assertEquals("NLP NNP Tokens", dashTerm.getTermSource().getsourceName());
			Assert.assertTrue(dashTerm.getTermExtractionMethods().contains("errorCodes, NLP NNP Tokens(relation"));
			SynonymsRelation relation = new SynonymsRelation();
			
			TechnicalDictionaryTerm testTerm = dictionary.addTerm(new TechnicalDictionaryKey("test"));
			testTerm.setTermSource(dashTerm.getTermSource());
			relation.relate(term, testTerm, "Test");
			Assert.assertEquals(4, term.getRelations().size());
			Assert.assertEquals(4, testTerm.getRelations().size());
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		} catch (SQLException e) {
			Assert.fail(e.getMessage());
		}
		finally {
			RuntimeInfo.getCurrentInfo().getHibernate().releaseSession(session);
		}
	}
	
	@Test
	public void testUpgradeRelations()
	{
		dictionary.addTermByUser(new TechnicalDictionaryKey("softlibupgrd-100001_upgrade xx11"));
		dictionary.addTermByUser(new TechnicalDictionaryKey("softlibupgrd-100001_upgrade xx12"));
		dictionary.addTermByUser(new TechnicalDictionaryKey("softlibupgrd-100001_upgrade xx13"));
		dictionary.addTermByUser(new TechnicalDictionaryKey("softlibupgrd-100002_upgrade xx21"));
		dictionary.addTermByUser(new TechnicalDictionaryKey("softlibupgrd-100002_upgrade xx22"));
		dictionary.addTermByUser(new TechnicalDictionaryKey("softlibupgrd-100002_upgrade xx23"));
		TechTokensParser parser = new TechTokensParser(0, 0, 0, 0, false, 0);
		Session session = RuntimeInfo.getCurrentInfo().getHibernate().acquireSession(SessionMode.READ_WRITE);
		try {
			parser.buildRelations(dictionary, session);
			TechnicalDictionaryTerm term = dictionary.get("upgrade xx11",true);
			Assert.assertNotNull(term);	
			System.out.println("Term Relations size = "+term.getRelations().size());
			Assert.assertEquals(2, term.getRelations().size());
			TechnicalDictionaryTerm term1 = dictionary.get("upgrade xx21",true);
			Assert.assertNotNull(term1);	
			System.out.println("Term1 Relations size = "+term1.getRelations().size());
			Assert.assertEquals(2, term1.getRelations().size());
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		} catch (SQLException e) {
			Assert.fail(e.getMessage());
		}
		finally {
			RuntimeInfo.getCurrentInfo().getHibernate().releaseSession(session);
		}
	}

	@Test
	public void testVersionRelations() 
	{
		dictionary.loadDictionary();
		TechnicalDictionaryTerm term1 = dictionary.addTermByUser(new TechnicalDictionaryKey("Internet explorer 8"));
		term1.setTermSource(dictionary.getSource("NLP Version Tokens"));
		TechnicalDictionaryTerm term2 = dictionary.addTermByUser(new TechnicalDictionaryKey("8.0"));
		term2.setTermSource(dictionary.getSource("Versions"));
		TechTokensParser parser = new TechTokensParser(0, 0, 0, 0, false, 0);
		Session session = RuntimeInfo.getCurrentInfo().getHibernate().acquireSession(SessionMode.READ_WRITE);
		try {
			parser.buildRelations(dictionary, session);
			TechnicalDictionaryTerm term = dictionary.get("8.0",true);
			Assert.assertNotNull(term);	
			Assert.assertEquals(1, term.getRelations().size());
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		} catch (SQLException e) {
			Assert.fail(e.getMessage());
		}
		finally { 
			RuntimeInfo.getCurrentInfo().getHibernate().releaseSession(session);
		}
	}
	
	@Test
	public void testVersionRelations2() 
	{
		dictionary.loadDictionary();
		TechnicalDictionaryTerm term1 = dictionary.addTermByUser(new TechnicalDictionaryKey("Internet explorer 8"));
		term1.setTermSource(dictionary.getSource("NLP Version Tokens"));
		TechnicalDictionaryTerm term2 = dictionary.addTermByUser(new TechnicalDictionaryKey("8.00"));
		term2.setTermSource(dictionary.getSource("Versions"));
		TechTokensParser parser = new TechTokensParser(0, 0, 0, 0, false, 0);
		Session session = RuntimeInfo.getCurrentInfo().getHibernate().acquireSession(SessionMode.READ_WRITE);
		try {
			parser.buildRelations(dictionary, session);
			TechnicalDictionaryTerm term = dictionary.get("8.00",true);
			Assert.assertNotNull(term);	
			Assert.assertEquals(1, term.getRelations().size());
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		} catch (SQLException e) {
			Assert.fail(e.getMessage());
		}
		finally { 
			RuntimeInfo.getCurrentInfo().getHibernate().releaseSession(session);
		}
	}
	
	@Test
	public void testFalseVersionRelations() 
	{
		dictionary.loadDictionary();
		TechnicalDictionaryTerm term1 = dictionary.addTerm(new TechnicalDictionaryKey("Internet explorer 8"));
		term1.setTermSource(dictionary.getSource("NLP Version Tokens"));
		TechnicalDictionaryTerm term2 = dictionary.addTerm(new TechnicalDictionaryKey("8.1"));
		term2.setTermSource(dictionary.getSource("Versions"));
		TechTokensParser parser = new TechTokensParser(0, 0, 0, 0, false, 0);
		Session session = RuntimeInfo.getCurrentInfo().getHibernate().acquireSession(SessionMode.READ_WRITE);
		try {
			parser.buildRelations(dictionary, session);
			TechnicalDictionaryTerm term = dictionary.get("8.1",true);
			Assert.assertNotNull(term);	
			Assert.assertEquals(0, term.getRelations().size());
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		} catch (SQLException e) {
			Assert.fail(e.getMessage());
		}
		finally { 
			RuntimeInfo.getCurrentInfo().getHibernate().releaseSession(session);
		}
	}
	
	@Test
	public void testDoubtVersionRelations() 
	{
		dictionary.loadDictionary();
		TechnicalDictionaryTerm term1 = dictionary.addTerm(new TechnicalDictionaryKey("Internet explorer 8"));
		term1.setTermSource(dictionary.getSource("NLP Version Tokens"));
		TechnicalDictionaryTerm term11 = dictionary.addTerm(new TechnicalDictionaryKey("oracle database 8"));
		term11.setTermSource(dictionary.getSource("NLP Version Tokens"));
		TechnicalDictionaryTerm term2 = dictionary.addTerm(new TechnicalDictionaryKey("8"));
		term2.setTermSource(dictionary.getSource("Versions"));
		TechTokensParser parser = new TechTokensParser(0, 0, 0, 0, false, 0);
		Session session = RuntimeInfo.getCurrentInfo().getHibernate().acquireSession(SessionMode.READ_WRITE);
		try {
			parser.buildRelations(dictionary, session);
			TechnicalDictionaryTerm term = dictionary.get("8",true);
			Assert.assertNotNull(term);	
			Assert.assertEquals(0, term.getRelations().size());
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		} catch (SQLException e) {
			Assert.fail(e.getMessage());
		}
		finally { 
			RuntimeInfo.getCurrentInfo().getHibernate().releaseSession(session);
		}
	}	
}
