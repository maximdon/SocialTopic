package com.softlib.tools.fullindex;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import com.softlib.imatch.ITicket;
import com.softlib.imatch.ITicketProvider;
import com.softlib.imatch.ITicketRetrievedCallback;
import com.softlib.imatch.InMemoryTicket;
import com.softlib.imatch.MatcherException;
import com.softlib.imatch.MultitenantRuntimeInfo;
import com.softlib.imatch.MultitenantThreadInfo;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.TicketProviderFactory;
import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.common.configuration.IConfigurationObject;
import com.softlib.imatch.common.configuration.IConfigurationResource;
import com.softlib.imatch.common.configuration.IConfigurationResourceLoader;
import com.softlib.imatch.dictionary.TechnicalDictionary;
import com.softlib.imatch.matcher.IMatcher;
import com.softlib.imatch.matcher.MatchCandidate;
import com.softlib.imatch.matcher.MatchResults;
import com.softlib.imatch.matcher.Matcher;
import com.softlib.imatch.matcher.SearcherConfiguration;
import com.softlib.imatch.matcher.SecurityConfig;
import com.softlib.imatch.matcher.verification.MatcherVerificationConfig;
import com.softlib.imatch.matcher.verification.VerifyTestInfo;

public class VASearchesSetRunner 
{
	private static Logger log = Logger.getLogger(VASearchesSetRunner.class);
	public VASearchesSetRunner()
	{
		MultitenantRuntimeInfo.init(new FictiveServletCtxt("C:\\Softlib\\imatchdata"));
        RuntimeInfo.getCurrentInfo().startThread(new MultitenantThreadInfo("ua-1234567-8"));
		
		DOMConfigurator.configure(RuntimeInfo.getCurrentInfo().getRealPath("/{SolutionConfigFolder}/log4j-console.xml"));
		try 
		{
			RuntimeInfo.getCurrentInfo().getBean("_no_name_");
		}
		catch(Exception e){}
		
//		RuntimeInfo.init(new FictiveServletCtxt());
//		DOMConfigurator.configure(RuntimeInfo.getCurrentInfo().getRealPath("/{SolutionConfigFolder}/log4j-console.xml"));
//		try {
//			TechnicalDictionary dictionary = (TechnicalDictionary) RuntimeInfo.getCurrentInfo().getBean("dictionary");
//			dictionary.loadDictionary();
//		}
//		catch(Exception e)
//		{
//			LogUtils.error(log, "Unable to load dictionary %s", e.getMessage());
//		}
//		IConfigurationObject config;
//		IConfigurationResourceLoader loader = (IConfigurationResourceLoader) RuntimeInfo.getCurrentInfo().getBean("xmlConfigurationResourceLoader");
//		IConfigurationResource resource = loader.loadResource("xml:///{SolutionConfigFolder}/matcher.xml;//security");
//		config = resource.getConfigurationObject(SecurityConfig.class);		
	}
	
	private Logger verifyLog = Logger.getLogger("verifylog");		

	public boolean verify()
	{
		IConfigurationResourceLoader loader;
		MatcherVerificationConfig config;
		
		List<VerifyTestInfo> tests = null;
		
		loader = (IConfigurationResourceLoader) RuntimeInfo.getCurrentInfo().getBean("xmlConfigurationResourceLoader");
		IConfigurationResource resource = loader.loadResource("xml:///{SolutionConfigFolder}/verification.xml;//matcherVerification");
		config = (MatcherVerificationConfig) resource.getCustomConfiguration(MatcherVerificationConfig.class);
		tests = config.getTests();			
		
       for (VerifyTestInfo test : tests) {
    	   LogUtils.info(verifyLog, "query = %s ", test.getTicketID());
           Set<String> fixExpected = new HashSet<String>();
    	   for (String id :test.getExpectedResults() ) {
    		   fixExpected.add(id);
    	   }
    	   LogUtils.info(verifyLog, "Expect = %s ", test.getExpectedResults());
    	   boolean status =  verifyMatch(test.getOriginObjectId(), test.getTicketID(), fixExpected, test.getMinScore(), test.getMinCount()); 	   
    	   LogUtils.info(verifyLog, "Status = %s",(status?"SUCCEEDED":"FAILED"));
    	   LogUtils.info(verifyLog, "=====================================");
       }
		
		return true;
	}
	
	static private String fixId(String id) {
		if(!id.contains(".0"))
			//This fix should be applied only on very special situation in solution of type SalesForce. 
			//For all other solutions it's irrelevant
			return id;
		id = "0000000"+id;
		id = id.replace(".0", "");
		int len = id.length();
		id = id.substring(len-8,len);
		return id;
	}
		
	private boolean verifyMatch(String objectId, String ticketID, Set<String> expectedResults,float minimalScore,int count)
	{
		
		int matchesCount = 0;
		int matchesOverScore = 0;
		ITicketProvider ticketProvider;
		try 
		{
			
			
			InMemoryTicket ticket = new InMemoryTicket("vaObject","",ticketID);
			Matcher matcher = (Matcher)RuntimeInfo.getCurrentInfo().getBean("matcher");
			try
			{
				MatchResults results = matcher.findMatches(ticket);
				int idx=0;
				for(MatchCandidate candidate : results.getCandidates())
				{
					String URL = (String) candidate.getProcessedTicket().getOriginalTicket().getField("url");
					String title = (String) candidate.getProcessedTicket().getOriginalTicket().getField("title");

					if(candidate.getScore() >  minimalScore)
					{
						matchesOverScore++;
						if (expectedResults.contains(candidate.getCandidateData().getId()) || expectedResults.size() == 0)
						{
							
							LogUtils.info(verifyLog,"Found: %s (ID: %s) index=%s score=%s" ,URL, candidate.getCandidateData().getId(),idx,candidate.getScore());

							matchesCount++;
							if (matchesCount >= count)
							{
								LogUtils.debug(log, "For ticket %s found requested matches", ticket);									
								return true;
							}
						}
				
					}
					idx++;
					if (expectedResults.size() == 0)
					{
						if (matchesOverScore == 0)
						{
							LogUtils.info(verifyLog,"Comment: No matches as expected" + ticket);
							LogUtils.debug(log, "For query %s no matches as expected", ticket);									
							return true;
						}
						/*else
						{
							LogUtils.info(verifyLog,"Comment: Found unexpected matches" + ticket);

							LogUtils.debug(log, "For ticket %s found unexpected matches", ticket);									
							return false;
						}*/
					}
				
				}
			}
			catch(MatcherException me) {
				LogUtils.error(log, "Unable to find matches for %s due to %s", ticket, me.getMessage());
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}	
		return false;
	}
}
