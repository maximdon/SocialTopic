package com.softlib.imatch.test.dictionaryapi;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.apache.log4j.xml.DOMConfigurator;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import com.softlib.imatch.ConsoleAppRuntimeInfo;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.dictionary.SynonymsRelation;
import com.softlib.imatch.dictionary.TechnicalDictionary;
import com.softlib.imatch.dictionary.TechnicalDictionaryKey;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.dictionary.TechnicalTermRelation;
import com.softlib.tools.dictionaryapi.DictAPIRequest;
import com.softlib.tools.dictionaryapi.DictAPIRequestBody;
import com.softlib.tools.dictionaryapi.DictAPIRequestHeader;
import com.softlib.tools.dictionaryapi.DictAPIResponseData;
import com.softlib.tools.dictionaryapi.DictAPIServer;

public class DictAPIServerTest 
{
	private DictAPIServer dictAPIServer = new DictAPIServer();
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

	@Test
	public void testISolveQueryAsTerm() {
		
	}
	
	@Test
	public void testNLPVersions() {
		
	}
	
	@Test
	public void testShortSynonymsReplacement() {
		TechnicalDictionaryTerm term1 = dictionary.addTermByUser(new TechnicalDictionaryKey("hc12"));
		TechnicalDictionaryTerm term2 = dictionary.addTermByUser(new TechnicalDictionaryKey("hc 12"));
		TechnicalDictionaryTerm term3 = dictionary.addTermByUser(new TechnicalDictionaryKey("hc12 start"));
		term3.setTermSource(dictionary.getSource(10));
		TechnicalTermRelation relation = new SynonymsRelation();
		relation.relate(term1, term2, "Test");
		addedTerms.add(term1);
		addedTerms.add(term2);
		addedTerms.add(term3);
		DictAPIRequest request = new DictAPIRequest();
		request.setHeader(new DictAPIRequestHeader("1", "1"));
		request.setBody(new DictAPIRequestBody("hc12 start"));
		DictAPIResponseData response = dictAPIServer.getData(request);
		Assert.assertEquals(1, response.getTerms().size());
		Assert.assertEquals(1, response.getTerms().get(0).getRelations().size());
	}

	@Test
	public void testShortSynonymsReplacementNoSynonyms() {
		TechnicalDictionaryTerm term1 = dictionary.addTermByUser(new TechnicalDictionaryKey("hc12"));
		TechnicalDictionaryTerm term3 = dictionary.addTermByUser(new TechnicalDictionaryKey("hc12 start"));
		addedTerms.add(term1);
		addedTerms.add(term3);
		DictAPIRequest request = new DictAPIRequest();
		request.setHeader(new DictAPIRequestHeader("1", "1"));
		request.setBody(new DictAPIRequestBody("hc12 start"));
		DictAPIResponseData response = dictAPIServer.getData(request);
		Assert.assertEquals(1, response.getTerms().size());
	}

	@Test
	public void testShortSynonymsReplacementInTheMiddle() {
		TechnicalDictionaryTerm term1 = dictionary.addTermByUser(new TechnicalDictionaryKey("hc12"));
		TechnicalDictionaryTerm term2 = dictionary.addTermByUser(new TechnicalDictionaryKey("hc 12"));
		TechnicalDictionaryTerm term3 = dictionary.addTermByUser(new TechnicalDictionaryKey("unable hc12 start"));
		TechnicalTermRelation relation = new SynonymsRelation();
		relation.relate(term1, term2, "Test");
		addedTerms.add(term1);
		addedTerms.add(term2);
		addedTerms.add(term3);
		DictAPIRequest request = new DictAPIRequest();
		request.setHeader(new DictAPIRequestHeader("1", "1"));
		request.setBody(new DictAPIRequestBody("unable hc12 start"));
		DictAPIResponseData response = dictAPIServer.getData(request);
		Assert.assertEquals(1, response.getTerms().size());
		Assert.assertEquals(1, response.getTerms().get(0).getRelations().size());
	}

	@Test
	public void testShortSynonymsReplacementVersions() {
		TechnicalDictionaryTerm term1 = dictionary.addTermByUser(new TechnicalDictionaryKey("hc12"));
		TechnicalDictionaryTerm term2 = dictionary.addTermByUser(new TechnicalDictionaryKey("12"));
		term2.setTermSource(dictionary.getSource(19));
		TechnicalTermRelation relation = new SynonymsRelation();
		relation.relate(term1, term2, "Test");
		addedTerms.add(term1);
		addedTerms.add(term2);
		DictAPIRequest request = new DictAPIRequest();
		request.setHeader(new DictAPIRequestHeader("1", "1"));
		request.setBody(new DictAPIRequestBody("uselocalizeddata 12"));
		DictAPIResponseData response = dictAPIServer.getData(request);
		Assert.assertEquals(2, response.getTerms().size());
		Assert.assertEquals(1, response.getTerms().get(0).getRelations().size());
	}

	@Test
	public void testShortSynonymsReplacementTwoTerms() {
		TechnicalDictionaryTerm term1 = dictionary.addTermByUser(new TechnicalDictionaryKey("hc12"));
		TechnicalDictionaryTerm term2 = dictionary.addTermByUser(new TechnicalDictionaryKey("12"));
		TechnicalDictionaryTerm term3 = dictionary.addTermByUser(new TechnicalDictionaryKey("hc12 test"));
		TechnicalDictionaryTerm term4 = dictionary.addTermByUser(new TechnicalDictionaryKey("test environment"));
		term2.setTermSource(dictionary.getSource(19));
		TechnicalTermRelation relation = new SynonymsRelation();
		relation.relate(term1, term2, "Test");
		addedTerms.add(term1);
		addedTerms.add(term2);
		addedTerms.add(term3);
		addedTerms.add(term4);
		DictAPIRequest request = new DictAPIRequest();
		request.setHeader(new DictAPIRequestHeader("1", "1"));
		request.setBody(new DictAPIRequestBody("uselocalizeddata hc12 in hc12 test environment"));
		DictAPIResponseData response = dictAPIServer.getData(request);
		Assert.assertEquals(3, response.getTerms().size());
		Assert.assertEquals(1, response.getTerms().get(0).getRelations().size());
	}

}
