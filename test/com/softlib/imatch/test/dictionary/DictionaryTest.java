package com.softlib.imatch.test.dictionary;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.apache.log4j.xml.DOMConfigurator;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.softlib.imatch.ConsoleAppRuntimeInfo;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.dictionary.SynonymsRelation;
import com.softlib.imatch.dictionary.TechnicalDictionary;
import com.softlib.imatch.dictionary.TechnicalDictionaryKey;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.dictionary.TechnicalTermRelation;
import com.softlib.imatch.ticketprocessing.DictionaryTermsMerge;

public class DictionaryTest {

	private static TechnicalDictionary dictionary;

	public DictionaryTest()
	{
	}
	
	@BeforeClass
	public static void init()
	{
		ConsoleAppRuntimeInfo.init(null);
		DOMConfigurator.configure(RuntimeInfo.getCurrentInfo().getRealPath("/{SolutionConfigFolder}/log4j-console.xml"));
		RuntimeInfo info = RuntimeInfo.getCurrentInfo();
		info.startThread();
		dictionary = (TechnicalDictionary) info.getBean("dictionary");
		dictionary.loadDictionary();
		info.finishThread();
	}

	@Test
	public void testFindRegularTerms() {
		dictionary.addTerm(new TechnicalDictionaryKey("abstract"));
		dictionary.addTerm(new TechnicalDictionaryKey("accumulator"));
		dictionary.addTerm(new TechnicalDictionaryKey("ampersand"));
		String[] successTerms = new String[] {"abstract", "accumulator", "ampersand"};
		for(String successTerm : successTerms) {
			TechnicalDictionaryTerm t = dictionary.get(successTerm,true);
			Assert.assertNotNull("Term " + successTerm + " not found ", t);
			Assert.assertNotNull("regular term " + successTerm + " should be stemmed", t.getTermStemmedText());				
		}
	}
	@Test
	public void testFindMultiTerm() {
		dictionary.addTerm(new TechnicalDictionaryKey("artificial intelligence"));
		String[] successTerms = new String[] {"artificial intelligence"};
		for(String successTerm : successTerms) {
			TechnicalDictionaryTerm t = dictionary.get(successTerm,true);
			Assert.assertNotNull("Term " + successTerm + " not found ", t);
		}
	}
	@Test
	public void testFindSpecialTerms() {
		dictionary.addTerm(new TechnicalDictionaryKey("Aminet"));
		dictionary.addTerm(new TechnicalDictionaryKey("AML"));
		String[] successTerms = new String[] {"Aminet", "AML"};
		for(String successTerm : successTerms) {
			TechnicalDictionaryTerm t = dictionary.get(successTerm,true);
			Assert.assertNotNull("Term " + successTerm + " not found ", t);
		}
	}
	@Test
	public void testFindNotExistTerms() {
		String[] failedTerms = new String[] {"abstract1", "Alta", "Alta Vist", "artificial intelligene", "artificial"};
		for(String failedTerm : failedTerms) {
			TechnicalDictionaryTerm t = dictionary.get(failedTerm,true);
			Assert.assertNull("Term " + failedTerm + " found ", t);
		}
	}
	
	@Test
	public void testFindPluralNoun()
	{
		String[] pluralNouns = new String[] {"abstracts"};
		for(String pluralNoun : pluralNouns) {
			TechnicalDictionaryTerm t = dictionary.get(pluralNoun,true);
			Assert.assertNotNull("Term " + pluralNoun + " not found ", t);
			Assert.assertNotNull("regular term " + pluralNoun + " should be stemmed", t.getTermStemmedText());				
		}
	}

	@Test
	public void testFindWithSynonyms() {
		TechnicalDictionaryTerm term1 = dictionary.addTerm(new TechnicalDictionaryKey("JVM"));
		TechnicalDictionaryTerm term2 = dictionary.addTerm(new TechnicalDictionaryKey("Java Virtual Machine"));
		TechnicalTermRelation relation = new SynonymsRelation();
		relation.relate(term1, term2, "Test");
		String[] words = new String[] {"JVM"};
		for(String word : words) {
			TechnicalDictionaryTerm t = dictionary.get(word,true);
			Assert.assertNotNull("Term " + word + " not found ", t);
			Assert.assertEquals("For  " + word + " there should be 1 synonym", 1, t.getRelations().size());				
		}
	}
	
	@Test
	public void testFindInText1() {
		String[] text = new String[] {"abort"};
		Collection<TechnicalDictionaryTerm> terms = dictionary.findInText(text);
		Assert.assertEquals(1, terms.size());
	}
	
	@Test
	public void testFindInText2() {
		String[] text = new String[] {"abstract1"};
		Collection<TechnicalDictionaryTerm> terms = dictionary.findInText(text);
		Assert.assertEquals(0, terms.size());
	}
	
	@Test
	public void testFindInText3() {
		String[] text = new String[] {"garbageee", "abort", "garbagee"};
		Collection<TechnicalDictionaryTerm> terms = dictionary.findInText(text);
		Assert.assertEquals(1, terms.size());
	}
	
	@Test
	public void testFindInText4() {
		dictionary.addTermByUser(new TechnicalDictionaryKey("AML"));
		String[] text = new String[] {"garbageee", "abort", "garbagee", "AML"};
		Collection<TechnicalDictionaryTerm> terms = dictionary.findInText(text);
		Assert.assertEquals(2, terms.size());
	}
	
	@Test
	public void testFindInText5() {
		dictionary.addTermByUser(new TechnicalDictionaryKey("AML"));
		String[] text = new String[] {"garbageee", "abort", "garbagee", "AML", "artificial"};
		Collection<TechnicalDictionaryTerm> terms = dictionary.findInText(text);
		Assert.assertEquals(2, terms.size());
	}

	@Test
	public void testFindInText6() {
		dictionary.addTermByUser(new TechnicalDictionaryKey("AML"));
		String[] text = new String[] {"garbageee", "abort", "garbagee", "AML", "artificial", "intellegince"};
		Collection<TechnicalDictionaryTerm> terms = dictionary.findInText(text);
		Assert.assertEquals(2, terms.size());
	}

	@Test
	public void testFindInText7() {
		dictionary.addTermByUser(new TechnicalDictionaryKey("AML"));
		dictionary.addTermByUser(new TechnicalDictionaryKey("artificial intelligence"));
		String[] text = new String[] {"garbageee", "abort", "garbagee", "AML", "artificial", "intelligence"};
		Collection<TechnicalDictionaryTerm> terms = dictionary.findInText(text);
		Assert.assertEquals(3, terms.size());
	}

	@Test
	public void testFindInText8() {
		dictionary.addTermByUser(new TechnicalDictionaryKey("abort class"));
		String[] text = new String[] {"garbageee", "abort", "class", "intelegince"};
		Collection<TechnicalDictionaryTerm> terms = dictionary.findInText(text);
		Assert.assertEquals(1, terms.size());
		TechnicalDictionaryTerm term;
		Iterator<TechnicalDictionaryTerm> it = terms.iterator();
		term = it.next();
		Assert.assertEquals("abort class", term.getTermText());
	}

	@Test
	public void testStemmingDictTerms() {
		dictionary.addTermByUser(new TechnicalDictionaryKey("colon"));
		String[] text = new String[] {"column", "columns", "colon"};
		Collection<TechnicalDictionaryTerm> terms = dictionary.findInText(text);
		Assert.assertEquals(3, terms.size());
	}
	
	@Test
	@Ignore("This test currently deprecated")
	public void testStemmingTechTerms() {
		String[] text = new String[] {"sql2", "microsoft", "sql", "server", "sql", "server", "risql", "sqlwindows", "sql4"};
		Collection<TechnicalDictionaryTerm> terms = dictionary.findInText(text);
		Assert.assertEquals(5, terms.size());
	}
	
	@Test
	public void testStemmingTechTerms2() {
		dictionary.addTermByUser(new TechnicalDictionaryKey("AIS"));
		dictionary.addTermByUser(new TechnicalDictionaryKey("SSIS"));
		String[] text = new String[] {"AIS", "SSIS"}; 
		Collection<TechnicalDictionaryTerm> terms = dictionary.findInText(text);
		Assert.assertEquals(2, terms.size());
	}

	@Test
	public void testFindStemmedWord() {
		String[] text = new String[] {"character", "characters"}; 
		Collection<TechnicalDictionaryTerm> terms = dictionary.findInText(text);
		Assert.assertEquals(2, terms.size());
	}
	
	@Test
	public void testStemMultiWord() {
		dictionary.addTerm(new TechnicalDictionaryKey("connection pooling"));
		String[] text = new String[] {"connection", "pool"}; 
		Collection<TechnicalDictionaryTerm> terms = dictionary.findInText(text);
		Assert.assertEquals(1, terms.size());
	}
	
	@Test
	@Ignore("This test currently deprecated")
	public void testLongTerms()
	{
		dictionary.addTerm(new TechnicalDictionaryKey("This very loNG text of SEVEN terms"));
		String[] text = new String[] {"This", "very", "loNG", "text", "of", "SEVEN", "terms"};
		Collection<TechnicalDictionaryTerm> terms = dictionary.findInText(text);
		Assert.assertEquals(1, terms.size());
	}

	@Test
	@Ignore("This test currently deprecated")
	public void testLongTerms2()
	{
		dictionary.addTerm(new TechnicalDictionaryKey("This very loNG text of SEVEN terms"));
		dictionary.addTerm(new TechnicalDictionaryKey("loNG text of SEVEN terms"));
		String[] text = new String[] {"Another", "long", "text", "of", "Seven", "terms", "well", "almost", "seven"};
		Collection<TechnicalDictionaryTerm> terms = dictionary.findInText(text);
		Assert.assertEquals(1, terms.size());
	}

	@Test
	public void testCleanJavaStack()
	{
		dictionary.addTermByUser(new TechnicalDictionaryKey("com.precise.javaperf.instrumenter.Instrumenter.defineClass1"));
		String[] text = new String[] {"com.precise.javaperf.instrumenter.Instrumenter.defineClass1(ClassLoader.java)"};
		Collection<TechnicalDictionaryTerm> terms = dictionary.findInText(text);
		Assert.assertEquals(1, terms.size());
		TechnicalDictionaryTerm term = terms.toArray(new TechnicalDictionaryTerm[0])[0];
		Assert.assertEquals("com.precise.javaperf.instrumenter.instrumenter.defineclass1", term.getTermText());
	}
	
	@Test
	public void testCleanJavaStack2()
	{
		dictionary.addTermByUser(new TechnicalDictionaryKey("com.precise.shared.communication.ClientRequestor.sendAndReceiveWrapped"));
		String[] text = new String[] {"com.precise.shared.communication.ClientRequestor.sendAndReceiveWrapped(ClientRequestor.java:775)"};
		Collection<TechnicalDictionaryTerm> terms = dictionary.findInText(text);
		Assert.assertEquals(1, terms.size());
		TechnicalDictionaryTerm term = terms.toArray(new TechnicalDictionaryTerm[0])[0];
		Assert.assertEquals("com.precise.shared.communication.clientrequestor.sendandreceivewrapped", term.getTermText());
	}

	@Test
	public void testCleanJavaStack3()
	{
		dictionary.addTermByUser(new TechnicalDictionaryKey("com.precise.infra.patches.requests.install.RequestInstallBundle.performRequest"));
		String[] text = new String[] {"com.precise.infra.patches.requests.install.RequestInstallBundle.performRequest(RequestInstallBundle.java:29)"};
		Collection<TechnicalDictionaryTerm> terms = dictionary.findInText(text);
		Assert.assertEquals(1, terms.size());
		TechnicalDictionaryTerm term = terms.toArray(new TechnicalDictionaryTerm[0])[0];
		Assert.assertEquals("com.precise.infra.patches.requests.install.requestinstallbundle.performrequest", term.getTermText());
	}

	@Test
	public void testCleanJavaStackNoFile()
	{
		dictionary.addTerm(new TechnicalDictionaryKey("com.precise.shared.communication.ClientRequestor.sendAndReceiveWrapped"));
		String[] text = new String[] {"com.precise.shared.communication.ClientRequestor.sendAndReceiveWrapped "};
		Collection<TechnicalDictionaryTerm> terms = dictionary.findInText(text);
		Assert.assertEquals(1, terms.size());
		TechnicalDictionaryTerm term = terms.toArray(new TechnicalDictionaryTerm[0])[0];
		Assert.assertEquals("com.precise.shared.communication.clientrequestor.sendandreceivewrapped", term.getTermText());
	}
	
	@Test
	public void testCleanJavaStackWithDollar()
	{
		dictionary.addTerm(new TechnicalDictionaryKey("com.precise.javaperf.instrumenter.Instrumenter.defineClass1"));
		String[] text = new String[] {"java.lang.ClassLoader.$com$precise$javaperf$instrumenter$Instrumenter$defineClass1(ClassLoader.java)"};
		Collection<TechnicalDictionaryTerm> terms = dictionary.findInText(text);
		Assert.assertEquals(1, terms.size());
		TechnicalDictionaryTerm term = terms.toArray(new TechnicalDictionaryTerm[0])[0];
		Assert.assertEquals("com.precise.javaperf.instrumenter.instrumenter.defineclass1", term.getTermText());
	}
	
	@Test
	public void testMissedTerms()
	{
		dictionary.addTermByUser(new TechnicalDictionaryKey("avoiding sa privileges when monitoring an sql server"));
		String[] text = new String[] {"avoiding sa privileges when monitoring an sql server"};
		Collection<TechnicalDictionaryTerm> terms = dictionary.findInText(text);
		Assert.assertEquals(1, terms.size());
		TechnicalDictionaryTerm term = terms.toArray(new TechnicalDictionaryTerm[0])[0];
		Assert.assertEquals("avoiding sa privileges when monitoring an sql server", term.getTermText());
	}
	
	@Test
	public void testSap1()
	{
		dictionary.addTermByUser(new TechnicalDictionaryKey("portal login"));
		String[] text = new String[] {"Question", "Regarding", "Portal", "Login", "procedure"};
		Collection<TechnicalDictionaryTerm> terms = dictionary.findInText(text);
		Assert.assertEquals(2, terms.size());
		TechnicalDictionaryTerm term = terms.toArray(new TechnicalDictionaryTerm[0])[0];
		Assert.assertEquals("portal login", term.getTermText());
	}

	@Test
	public void testOverlappingTerms1()
	{
		dictionary.addTermByUser(new TechnicalDictionaryKey("sql database"));
		dictionary.addTermByUser(new TechnicalDictionaryKey("database configuration"));
		String[] text = new String[] {"sql", "database", "configuration"};
		Collection<TechnicalDictionaryTerm> terms = dictionary.findInText(text);
		Assert.assertEquals(2, terms.size());
		TechnicalDictionaryTerm term = terms.toArray(new TechnicalDictionaryTerm[0])[1];
		Assert.assertEquals("database configuration", term.getTermText());
	}
	
	@Test
	public void testOverlappingTerms2()
	{
		dictionary.addTermByUser(new TechnicalDictionaryKey("sql database"));
		dictionary.addTermByUser(new TechnicalDictionaryKey("database configuration"));
		dictionary.addTermByUser(new TechnicalDictionaryKey("sql database configuration"));
		String[] text = new String[] {"sql", "database", "configuration"};
		Collection<TechnicalDictionaryTerm> terms = dictionary.findInText(text);
		Assert.assertEquals(1, terms.size());
		TechnicalDictionaryTerm term = terms.toArray(new TechnicalDictionaryTerm[0])[0];
		Assert.assertEquals("sql database configuration", term.getTermText());
	}
	
	@Test
	public void testOverlappingTerms3()
	{
		dictionary.addTermByUser(new TechnicalDictionaryKey("microsoft sql"));
		dictionary.addTermByUser(new TechnicalDictionaryKey("sql database"));
		dictionary.addTermByUser(new TechnicalDictionaryKey("database configuration"));
		dictionary.addTermByUser(new TechnicalDictionaryKey("sql database configuration"));
		String[] text = new String[] {"microsoft", "sql", "database", "configuration"};
		Collection<TechnicalDictionaryTerm> terms = dictionary.findInText(text);
		Assert.assertEquals(2, terms.size());
		TechnicalDictionaryTerm term = terms.toArray(new TechnicalDictionaryTerm[0])[1];
		Assert.assertEquals("sql database configuration", term.getTermText());
	}
	
	@Test
	public void testOverlappingTerms4()
	{
		dictionary.addTermByUser(new TechnicalDictionaryKey("windows server 2008"));
		dictionary.addTermByUser(new TechnicalDictionaryKey("log"));
		dictionary.addTermByUser(new TechnicalDictionaryKey("Application Error 1000"));
		dictionary.addTermByUser(new TechnicalDictionaryKey("fault offset"));
		String[] text = "event log is complaining hard drive run out of space [windows server 2008] Application Error 1000 check fault offset test 0x4ab0fb7e".split(" ");
		Collection<TechnicalDictionaryTerm> terms = dictionary.findInText(text);
		Assert.assertEquals(8, terms.size());
		TechnicalDictionaryTerm term = terms.toArray(new TechnicalDictionaryTerm[0])[1];
		Assert.assertEquals("windows server 2008", term.getTermText());
		term = terms.toArray(new TechnicalDictionaryTerm[0])[5];
		Assert.assertEquals("fault offset", term.getTermText());
	}
	@Test
	public void testSubTerms() {
		ConsoleAppRuntimeInfo.init(null);
		DOMConfigurator.configure(RuntimeInfo.getCurrentInfo().getRealPath("/{SolutionConfigFolder}/log4j-console.xml"));

		TechnicalDictionary dictionary = new TechnicalDictionary();
		dictionary.addTerm(new TechnicalDictionaryKey("aaa"));
		dictionary.addTerm(new TechnicalDictionaryKey("bbb"));
		dictionary.addTerm(new TechnicalDictionaryKey("ccc"));
		dictionary.addTerm(new TechnicalDictionaryKey("aaa bbb"));
		dictionary.addTerm(new TechnicalDictionaryKey("bbb ccc"));
		dictionary.addTerm(new TechnicalDictionaryKey("aaa ccc"));
		dictionary.addTerm(new TechnicalDictionaryKey("aaa bbb ccc"));
		dictionary.addTerm(new TechnicalDictionaryKey("aaa bbb ccc ddd"));

		Set<TechnicalDictionaryTerm> subTerms = dictionary.getSubTerms(new TechnicalDictionaryKey("aaa bbb ccc ddd"));
		System.out.println("Sub Terms = "+subTerms.toString());
	}

	@Test
	public void testMergeTerms() {
		ConsoleAppRuntimeInfo.init(null);
		DOMConfigurator.configure(RuntimeInfo.getCurrentInfo().getRealPath("/{SolutionConfigFolder}/log4j-console.xml"));

		TechnicalDictionary dictionary = new TechnicalDictionary();
		dictionary.addTerm(new TechnicalDictionaryKey("aaa"));
		dictionary.addTerm(new TechnicalDictionaryKey("bbb"));
		dictionary.addTerm(new TechnicalDictionaryKey("ccc"));
		dictionary.addTerm(new TechnicalDictionaryKey("aaa bbb"));
		dictionary.addTerm(new TechnicalDictionaryKey("bbb ccc"));
		dictionary.addTerm(new TechnicalDictionaryKey("aaa ccc"));
		dictionary.addTerm(new TechnicalDictionaryKey("aaa bbb ccc"));
		dictionary.addTerm(new TechnicalDictionaryKey("aaa bbb ccc ddd"));

		dictionary.addTerm(new TechnicalDictionaryKey("xx1"));
		dictionary.addTerm(new TechnicalDictionaryKey("xx1"));
		dictionary.addTerm(new TechnicalDictionaryKey("xx1 xx2 xx3"));

		dictionary.addTerm(new TechnicalDictionaryKey("yy1"));
		dictionary.addTerm(new TechnicalDictionaryKey("yy2"));
		dictionary.addTerm(new TechnicalDictionaryKey("yy1 yy2 yy3"));

		Collection<TechnicalDictionaryTerm> terms = dictionary.getAllTerms(true);
		List<TechnicalDictionaryTerm> mergedTerms = DictionaryTermsMerge.mergeSubTerms(terms);
		
		System.out.println("Terms = "+terms.toString());

		System.out.println("Merge Terms = "+mergedTerms.toString());
	
		Assert.assertEquals(6, mergedTerms.size());

	}

	@Test
	public void testRelateTermsSetSource()
	{
		TechnicalDictionaryTerm term1 = dictionary.addTermByUser(new TechnicalDictionaryKey("bts2005"));
		TechnicalDictionaryTerm term2 = dictionary.addTermByUser(new TechnicalDictionaryKey("bts 2005"));
		term1.setTermSource(dictionary.getSource(41));
		term2.setTermSource(dictionary.getSource(19));
		SynonymsRelation relation = new SynonymsRelation();
		relation.relate(term1, term2, "Test");
		Assert.assertEquals(19, term1.getTermSource().getSourceId());
		Assert.assertEquals(19, term2.getTermSource().getSourceId());
	}
}
