package com.softlib.tools.dictionaryapi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import com.softlib.imatch.InMemoryTicket;
import com.softlib.imatch.MultitenantRuntimeInfo;
import com.softlib.imatch.MultitenantThreadInfo;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.common.HighlightText;
import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.common.SourceMngr;
import com.softlib.imatch.common.SourceMngr.Type;
import com.softlib.imatch.dictionary.FindTermsInText;
import com.softlib.imatch.dictionary.SynonymsRelation;
import com.softlib.imatch.dictionary.TechnicalDictionary;
import com.softlib.imatch.dictionary.TechnicalDictionaryKey;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.dictionary.TechnicalTermSource;
import com.softlib.imatch.distance.TermsByPositions;
import com.softlib.imatch.ticketprocessing.ExtractVersionsByNLP;
import com.softlib.imatch.ticketprocessing.ITicketProcessor;
import com.softlib.imatch.ticketprocessing.ProcessedTicket;
import com.softlib.imatch.ticketprocessing.TicketProcessor;
import com.softlib.imatch.ticketprocessing.IProcessedTicket.MatchMode;

public class DictAPIServer {

	private static final Logger log = Logger.getLogger(DictAPIServer.class);
	
	public String response(String request) {
		DictAPIRequest dictAPIRequest = DictAPIRequest.parse(request);
		String requestId = dictAPIRequest.getHeader().getRequestId();
		
		String requestXMLStr = DictAPIXmlUtil.getXMLString(dictAPIRequest);
		LogUtils.debug(log,"[#%s] Request:\n %s \n",requestId,requestXMLStr);
		
		DictAPIResponseData data = getData(dictAPIRequest);
		String responseStr=null;
		if (data.isError()) {
			DictAPIResponseError dictAPIResponseError = new DictAPIResponseError(data);
			responseStr =  DictAPIXmlUtil.getXMLString(dictAPIResponseError);
		}
		else {
			DictAPIResponseOk dictAPIResponseOk = new DictAPIResponseOk(data);
			responseStr = DictAPIXmlUtil.getXMLString(dictAPIResponseOk);
		}
		
		LogUtils.debug(log,"[#%s] Request:(clean)\n %s \n",requestId,requestXMLStr);

		if(RuntimeInfo.getCurrentInfo().isDebugMode()) {
			HighlightText highlightText = new HighlightText(data.getText(),"{","}");
			highlightText.highlight(data.getTerms(),HighlightText.Type.Active);
			String highlighed = highlightText.getHighlightText();
			LogUtils.debug(log,"[#%s] Request:(Highlighted)\n %s \n",requestId,highlighed);
	
			HighlightText highlightTextSrc = new HighlightText(data.getText(),"[#TITLE#]{","}");
			highlightTextSrc.highlight(data.getTerms(),HighlightText.Type.Active);
			String highlighedSrc = highlightTextSrc.getHighlightText();
			LogUtils.debug(log,"[#%s] Request:(Source)\n %s \n",requestId,highlighedSrc);
		}
		LogUtils.debug(log,"[#%s] Response:\n %s \n",requestId,responseStr);		
		return responseStr;
	}
	
	private void addSplitTerm(String text,
							  TechnicalTermSource termSrc,
							  ProcessedTicket processedTicket,
							  DictAPIResponseDataTerm dataTerm,
							  boolean withOriginal) {
		
		if (withOriginal) {
			TechnicalDictionaryTerm relation = 
			processedTicket.addTerm(new TechnicalDictionaryKey(text));
			relation.setTermSource(termSrc);
			dataTerm.addRelation(relation);
		}
		
		TechnicalDictionaryTerm relation_0 = 
		processedTicket.addTerm(new TechnicalDictionaryKey(text+".0"));
		relation_0.setTermSource(termSrc);
		dataTerm.addRelation(relation_0);
		
		TechnicalDictionaryTerm relation_00 = 
		processedTicket.addTerm(new TechnicalDictionaryKey(text+".00"));
		relation_00.setTermSource(termSrc);
		dataTerm.addRelation(relation_00);
		
		TechnicalDictionaryTerm relation_0_0 = 
		processedTicket.addTerm(new TechnicalDictionaryKey(text+".0.0"));
		relation_0_0.setTermSource(termSrc);
		dataTerm.addRelation(relation_0_0);
				
	}

	private void addSplitTerm(String prefix,String text,String suffix,
							  TechnicalTermSource termSrc,
							  ProcessedTicket processedTicket,
							  DictAPIResponseDataTerm dataTerm) {
		addSplitTerm(prefix+text+suffix,termSrc,processedTicket,dataTerm,!text.equals(" "));
	}
	
	private void addSplitTerms(DictAPIResponseDataTerm dataTerm,
							   ProcessedTicket processedTicket,
							   TechnicalDictionaryTerm term) {
		String text = term.getTermText();
		if (text.contains(" v ")     || 
			text.contains(" ver ")   || 
			text.contains(" version "))
			return;
		
		int splitIdx = text.lastIndexOf(" ");
		if (splitIdx<=0)
            return;

		String prefix = text.substring(0,splitIdx);
		String suffix = text.substring(splitIdx+1);
		TechnicalTermSource termSrc = term.getTermSource();
		
		addSplitTerm(prefix," "        ,suffix,termSrc,processedTicket,dataTerm);
		addSplitTerm(prefix," v"       ,suffix,termSrc,processedTicket,dataTerm);
		addSplitTerm(prefix," v "      ,suffix,termSrc,processedTicket,dataTerm);
		addSplitTerm(prefix," ver "    ,suffix,termSrc,processedTicket,dataTerm);
		addSplitTerm(prefix," version ",suffix,termSrc,processedTicket,dataTerm);
	}
	
	private DictAPIResponseData getErrorData(String errorMsg,DictAPIRequestHeader header) {
		DictAPIResponseData rc = new DictAPIResponseData("",header.getRequestId(),header.getLibraryId(),new ArrayList<DictAPIResponseDataTerm>());
		rc.setErrorMsg(errorMsg);
		return rc;
	}
	
	//This function is public for testing purposes only
	public DictAPIResponseData getData(DictAPIRequest request) {
		DictAPIRequestHeader header = request.getHeader();
		
		String requestStr = request.getBody().getQueryStr();
		if (requestStr==null || requestStr.isEmpty())
			return getErrorData("String is empty",header);
		
		ITicketProcessor ticketProcessor = 
			(ITicketProcessor) RuntimeInfo.getCurrentInfo().getBean("ticketProcessor");
		InMemoryTicket ticket =  new InMemoryTicket("DictAPI","",requestStr);
		
		ProcessedTicket processedTicket = null;
		try {
			processedTicket = 
				(ProcessedTicket)ticketProcessor.processTicket(TicketProcessor.StepsMode.DictAPI,ticket,MatchMode.match,null,true);
		} 
		catch (Exception e) {
			LogUtils.fatal(log, e, "Unable to process ticket %s due to %s", ticket, e.getMessage());
			return getErrorData("Unable to process ticket",header);
		}
		
		List<DictAPIResponseDataTerm> termsData = new ArrayList<DictAPIResponseDataTerm>();
		//for (TechnicalDictionaryTerm term : processedTicket.getAllTerms(false))	{
        Collection<TechnicalDictionaryTerm> allTerms = processedTicket.getAllTerms(false);
        allTerms.addAll(processedTicket.getZeroFreqTerms());
        allTerms.addAll(processedTicket.getOneFreqTerms());
        TechnicalDictionary dictionary = (TechnicalDictionary)RuntimeInfo.getCurrentInfo().getBean("dictionary");
		FindTermsInText findTermsInText = dictionary.getFindTermsInText();
		String[] words = ticket.getBody(MatchMode.all).split(" ");
		List<TechnicalDictionaryTerm> shortTerms = findTermsInText.findShortestTerms(words);
		SynonymsRelation relation = new SynonymsRelation();
        for (TechnicalDictionaryTerm term : allTerms)   {
        	if(term == null || term.getTermSource() == null) {
        		System.out.println("Error");
        		continue;
        	}
			String termSrcName = term.getTermSource().getsourceName();
			if (SourceMngr.isSource(termSrcName,Type.Modified))
				continue;
			List<TechnicalDictionaryTerm>substitutedTerms = new ArrayList<TechnicalDictionaryTerm>();
			for(TechnicalDictionaryTerm shortTerm : shortTerms) {
				if(term.getTermStemmedText().contains(shortTerm.getTermStemmedText()) && shortTerm.getRelations().size() > 0) {
					for(TechnicalDictionaryTerm relatedShortTerm : shortTerm.getRelations()) {
						String substitutedTermTxt = term.getTermStemmedText().replace(shortTerm.getTermStemmedText(), relatedShortTerm.getTermStemmedText());
						//TechnicalDictionaryTerm substitutedTerm = dictionary.addTermByUser(new TechnicalDictionaryKey(substitutedTermTxt, false));
						TechnicalDictionaryTerm substitutedTerm = processedTicket.addTerm(new TechnicalDictionaryKey(substitutedTermTxt, false));
						//Important note: don't add the substituted term to the result directly, will be added via relations
						substitutedTerms.add(substitutedTerm);
					}
				}
			}
			for(TechnicalDictionaryTerm substitutedTerm : substitutedTerms)
				relation.relate(term, substitutedTerm, "iSolve relation");
			DictAPIResponseDataTerm dataTerm = new DictAPIResponseDataTerm(term);
			termsData.add(dataTerm);
			if (termSrcName.equals(ExtractVersionsByNLP.FIELD_NAME)) {
				addSplitTerms(dataTerm,processedTicket,term);
			}
		}
		RuntimeInfo.getCurrentInfo().finishThread();
		return new DictAPIResponseData(requestStr,header.getRequestId(),header.getLibraryId(),termsData);
	}

	public static void main(String[] argc)
	{
		MultitenantRuntimeInfo.init(null);
		Thread t1 = new Thread(new DictAPIServer().new ThreadProc("MobileEyeSite"));
		Thread t2 = new Thread(new DictAPIServer().new ThreadProc("Attunity_New"));
		t1.start();
		t2.start();
		try {
			t1.join();
			t2.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	class ThreadProc implements Runnable
	{
		private String name; 
		ThreadProc(String name)
		{
			this.name = name;
		}
		@Override
		public void run() 
		{
			DictAPIServer apiServer = new DictAPIServer();
            DictAPIRequest request = new DictAPIRequest();
            DictAPIRequestHeader header = new DictAPIRequestHeader("1", "lib1");
            request.setHeader(header);
            DictAPIRequestBody body = new DictAPIRequestBody("eyeq  processor design oracle");
            request.setBody(body);
            RuntimeInfo.getCurrentInfo().startThread(new MultitenantThreadInfo(name));
            DictAPIResponseData resp = apiServer.getData(request);
            System.out.println(String.format("For thread %s num terms is %d", name, resp.getTerms().size()));
		}
	}
};
