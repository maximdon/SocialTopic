package com.softlib.imatch.commandline;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.hibernate.Session;

import com.softlib.imatch.ConsoleAppRuntimeInfo;
import com.softlib.imatch.InMemoryTicket;
import com.softlib.imatch.MatcherException;
import com.softlib.imatch.MultitenantRuntimeInfo;
import com.softlib.imatch.MultitenantThreadInfo;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.ServerState;
import com.softlib.imatch.StageMngr;
import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.common.Pair;
import com.softlib.imatch.common.SessionMode;
import com.softlib.imatch.common.configuration.IConfigurationObject;
import com.softlib.imatch.common.configuration.IConfigurationResource;
import com.softlib.imatch.common.configuration.IConfigurationResourceLoader;
import com.softlib.imatch.dictionary.PorterStemmer;
import com.softlib.imatch.dictionary.TechnicalDictionary;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.dictionary.TechnicalTermSource;
import com.softlib.imatch.dictionary.Wordnet;
import com.softlib.imatch.matcher.Matcher;
import com.softlib.imatch.matcher.SearcherConfiguration;
import com.softlib.imatch.matcher.lucene.LuceneHighlighter;
import com.softlib.imatch.ticketprocessing.IProcessedTicket;
import com.softlib.imatch.ticketprocessing.ProcessedTicketDBase;
import com.softlib.tools.dictionaryparsers.DictionaryBuilder;
import com.softlib.tools.dictionaryparsers.DictionaryBuilderFactory;
import com.softlib.tools.dictionaryparsers.ExtractionContext;
import com.softlib.tools.dictionaryparsers.IDictionaryParser;
import com.softlib.tools.dictionaryparsers.DictionaryBuilder.ProgressStatus;
import com.softlib.tools.fullindex.FictiveServletCtxt;
import com.softlib.tools.fullindex.IndexContext;
import com.softlib.tools.fullindex.Indexer;
import com.softlib.tools.fullindex.IndexerFactory;
import com.softlib.tools.fullindex.MatchFinder;
import com.softlib.tools.fullindex.MatchFinderConfiguration;
import com.softlib.tools.fullindex.VerificationSetRunner;

public class Main 
{
	
	public static void main(String args[])
	{
		Options options = new Options();
		options.addOption("index", true, "Runs one time index based on existing dictionary");
		options.addOption("builddict", true, "Builds SoftLib technical dictionary from external sources (FOLDOC, Wikipedia, WordNet...)");
		options.addOption("afterFreq", true, "Update terms after frequency on existing dictionary");
		options.addOption("calcFreq", true, "Calculates term frequencies on existing dictionary");
		options.addOption("personsNames", false, "Change source to terms that contains person name");
		options.addOption("relateVersions", false, "Relate NLP Versions terms");
		options.addOption("printRelations", false, "Print all Terms Relation");
		options.addOption("fixRelations", false, "Fixes all term relations");
		options.addOption("findMatches", true, "Tries to find the perfect matches on the corpus");
		options.addOption("verify", false, "runs verifycation set");
		options.addOption("createIndexTable", false, "Create the index processedticket table");
		options.addOption("addStemmingException", true, "Adds new pair as a stemming exception");
		options.addOption("parseGlossary", true, "Parses glossary");
		options.addOption("snippet", false, "Run snippet generation test");
		options.addOption("testProcessedTicket", false, "Test processed ticket");
		
		CommandLineParser parser = new GnuParser();
		CommandLine cmd;
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			System.out.println("Error occured during command line handling, internal error: " + e.getMessage());
			return;
		}
		if(cmd.hasOption("index")) {			
			System.out.println("Indexing tickets...");
			try {
				MultitenantRuntimeInfo.init(null);
				DOMConfigurator.configure(RuntimeInfo.getCurrentInfo().getRealPath("/{SolutionConfigFolder}/log4j-console.xml"));
				try {
					RuntimeInfo.getCurrentInfo().getBean("_no_name_");
				}
				catch(Exception e){}
				String objectIdOption = cmd.getOptionValue("index");
				Set<String> objects;
				if(objectIdOption.equals("all")) {					
					System.out.println("Indexing all objects");
					IConfigurationResourceLoader loader = (IConfigurationResourceLoader)RuntimeInfo.getCurrentInfo().getBean("xmlConfigurationResourceLoader");
					IConfigurationResource resource = loader.loadResource("xml:///{SolutionConfigFolder}/matcher.xml;//searcher");
					IConfigurationObject config = resource.getConfigurationObject(SearcherConfiguration.class);
					objects = config.getAllObjects();
				}
				else {
					System.out.println("Indexing object " + objectIdOption);
					objects = new HashSet<String>();
					objects.add(objectIdOption);
				}
				StageMngr.instance().setStage(StageMngr.Stage.Index);
				RuntimeInfo.getCurrentInfo().startThread();
				//TODO multitenant - set solution name
				RuntimeInfo.getCurrentInfo().startThread(new MultitenantThreadInfo("MobileEyeSite"));
				for(String objectId : objects) {
					Indexer indexer = (Indexer)RuntimeInfo.getCurrentInfo().getBean(IndexerFactory.getIndexerId(objectId));
					indexer.index(new IndexContext());
				}
				RuntimeInfo.getCurrentInfo().finishThread();
				RuntimeInfo.getCurrentInfo().getHibernate().closeSession();
				System.out.println("SoftLib index built successfully");
				System.exit(0);
			}
			catch(Exception e)
			{
				System.out.println("Indexer failed due to " + e.getMessage());
				e.printStackTrace();
			}
		}			
		else if(cmd.hasOption("builddict")) {
			System.out.println("Building SoftLib dictionary...");
			try {
				RuntimeInfo.init(null);
				DOMConfigurator.configure(RuntimeInfo.getCurrentInfo().getRealPath("/{SolutionConfigFolder}/log4j-console.xml"));
				try {
					RuntimeInfo.getCurrentInfo().getBean("_no_name_");
				}
				catch(Exception e){}
				String objectIdOption = cmd.getOptionValue("builddict");
				Set<String> objects;
				if(objectIdOption.equals("all")) {					
					System.out.println("Building dictionary for all objects");
					IConfigurationResourceLoader loader = (IConfigurationResourceLoader)RuntimeInfo.getCurrentInfo().getBean("xmlConfigurationResourceLoader");
					IConfigurationResource resource = loader.loadResource("xml:///{SolutionConfigFolder}/matcher.xml;//searcher");
					IConfigurationObject config = resource.getConfigurationObject(SearcherConfiguration.class);
					objects = config.getAllObjects();
				}
				else {
					System.out.println("Building dictionary for object " + objectIdOption);
					objects = new HashSet<String>();
					objects.add(objectIdOption);
				}
				//TODO multitenant - set solution name
				RuntimeInfo.getCurrentInfo().startThread(new MultitenantThreadInfo("Default"));
				for(String objectId : objects) {
					DictionaryBuilder dictBuilder = (DictionaryBuilder)RuntimeInfo.getCurrentInfo().getBean(DictionaryBuilderFactory.getBuilderId(objectId));
					ExtractionContext ctx = new ExtractionContext();
					StageMngr.instance().setStage(StageMngr.Stage.Extract);
					ProgressStatus status = dictBuilder.buildDictionary(ctx);
					if(status == ProgressStatus.PROCESS_FAILED) {
						throw new MatcherException("Dictionary builder failed in Extract step");
					}
					RuntimeInfo.getCurrentInfo().getHibernate().closeSession();
					StageMngr.instance().setStage(StageMngr.Stage.PostExtract);
					status = dictBuilder.buildDictionary(ctx);
					if(status == ProgressStatus.PROCESS_FAILED) {
						throw new MatcherException("Dictionary builder failed in Post Extract step");
					}
				}
				
				TechnicalDictionary dictionary = (TechnicalDictionary)RuntimeInfo.getCurrentInfo().getBean("dictionary");				
				dictionary.loadDictionary(false);
				IDictionaryParser dictParser = (IDictionaryParser) RuntimeInfo.getCurrentInfo().getBean("techTokensParser");
				Session session = RuntimeInfo.getCurrentInfo().getHibernate().acquireSession(SessionMode.READ_WRITE);				
				dictParser.buildRelations(dictionary, session);
				dictionary.save();
				dictionary.updateTermsAfterFreq();
				dictionary.addComplexSynonym();
				dictionary.save();

				RuntimeInfo.getCurrentInfo().finishThread();
				RuntimeInfo.getCurrentInfo().destroy();
				System.out.println("SoftLib dictionary built successfully, restarting Tomcat");
				CommandLineUtils.restartTomcat();
				System.out.println("Tomcat service restarted successfully");
				System.exit(0);
			}
			catch(MatcherException me) {
				System.out.println(me.getMessage());
				if(StageMngr.instance().getStage().equals(StageMngr.Stage.Extract))
					CommandLineUtils.restartTomcat();
				CommandLineUtils.restartLongProcess("run_extract.bat");
				System.exit(-1);
			}
			catch(Exception e) {
				System.out.println("Dictionary parser failed due to " + e.getMessage());
				e.printStackTrace();
				System.exit(-1);
			}
		}		
		else if(cmd.hasOption("afterFreq")) {
			System.out.println("Update terms after frequency...");
			try {
				ConsoleAppRuntimeInfo.init(null);
				DOMConfigurator.configure(RuntimeInfo.getCurrentInfo().getRealPath("/{SolutionConfigFolder}/log4j-console.xml"));
				try {
					RuntimeInfo.getCurrentInfo().getBean("_no_name_");
				}
				catch(Exception e){}
				TechnicalDictionary dictionary = (TechnicalDictionary)RuntimeInfo.getCurrentInfo().getBean("dictionary");
				dictionary.loadDictionary(false);
				dictionary.updateTermsAfterFreq();
				dictionary.save();
			}
			catch(Exception e) {
				System.out.println("Calculating frequency failed due to " + e.getMessage());
				e.printStackTrace();
			}

		}
		else if(cmd.hasOption("calcFreq")) {
			System.out.println("Calculating frequency...");
			try {
				ConsoleAppRuntimeInfo.init(null);
				DOMConfigurator.configure(RuntimeInfo.getCurrentInfo().getRealPath("/{SolutionConfigFolder}/log4j-console.xml"));
				try {
					RuntimeInfo.getCurrentInfo().getBean("_no_name_");
				}
				catch(Exception e){}
				String objectIdOption = cmd.getOptionValue("calcFreq");
				Set<String> objects;
				if(objectIdOption.equals("all")) {					
					System.out.println("Calculating frequency for all objects");
					IConfigurationResourceLoader loader = (IConfigurationResourceLoader)RuntimeInfo.getCurrentInfo().getBean("xmlConfigurationResourceLoader");
					IConfigurationResource resource = loader.loadResource("xml:///{SolutionConfigFolder}/matcher.xml;//searcher");
					IConfigurationObject config = resource.getConfigurationObject(SearcherConfiguration.class);
					objects = config.getAllObjects();
				}
				else {
					System.out.println("Calculating frequency for object " + objectIdOption);
					objects = new HashSet<String>();
					objects.add(objectIdOption);
				}
				RuntimeInfo.getCurrentInfo().startThread();
				ExtractionContext ctx = new ExtractionContext();
				StageMngr.instance().setStage(StageMngr.Stage.PostExtract);
				for(String objectId : objects) {
					DictionaryBuilder freqBuilder = (DictionaryBuilder)RuntimeInfo.getCurrentInfo().getBean(DictionaryBuilderFactory.getBuilderId(objectId));
					freqBuilder.buildDictionary(ctx);						
				}
				RuntimeInfo.getCurrentInfo().finishThread();
				RuntimeInfo.getCurrentInfo().getHibernate().closeSession();
				System.out.println("SoftLib dictionary frequencies calculated successfully");
			}
			catch(Exception e) {
				System.out.println("Calculating frequency failed due to " + e.getMessage());
				e.printStackTrace();
			}
		}		
		else if(cmd.hasOption("personsNames")) {
			System.out.println("Cleaning names and related terms ...");
			try {
				ConsoleAppRuntimeInfo.init(null);
				DOMConfigurator.configure(RuntimeInfo.getCurrentInfo().getRealPath("/{SolutionConfigFolder}/log4j-console.xml"));
				try {
					RuntimeInfo.getCurrentInfo().getBean("_no_name_");
				}
				catch(Exception e){
				}

				TechnicalDictionary dictionary = (TechnicalDictionary) RuntimeInfo.getCurrentInfo().getBean("dictionary");
				dictionary.loadDictionary(false);
				TechnicalTermSource personsNames = dictionary.addSource("Persons Names");
				TechnicalTermSource changeSource = dictionary.addSource("Contain Person Name");
				changeSource.setAssociationRank(100);
				dictionary.changeTermsContainSource(personsNames,changeSource);
				dictionary.save();
				System.out.println("All terms cleaned successfully");
			}
			catch (Exception e) {
				System.out.println("Persons Names cleaning failed due to " + e.getMessage());
				e.printStackTrace();
			}

		}
		else if(cmd.hasOption("relateVersions")) {
			System.out.println("Relate NLP Versions terms ...");
			try {
				ConsoleAppRuntimeInfo.init(null);
				DOMConfigurator.configure(RuntimeInfo.getCurrentInfo().getRealPath("/{SolutionConfigFolder}/log4j-console.xml"));
				try {
					RuntimeInfo.getCurrentInfo().getBean("_no_name_");
				}
				catch(Exception e){
				}

				TechnicalDictionary dictionary = (TechnicalDictionary) RuntimeInfo.getCurrentInfo().getBean("dictionary");
				dictionary.loadDictionary(false);
				
				dictionary.addNLPVersionRelations();
				dictionary.save();
				System.out.println("Relate NLP Versions terms finished successfully");
			}
			catch (Exception e) {
				System.out.println("Relate NLP Versions terms failed due to " + e.getMessage());
				e.printStackTrace();
			}

		}
				else if(cmd.hasOption("printRelations")) {
			System.out.println("Print All terms relations ...");
			try {
				ConsoleAppRuntimeInfo.init(null);
				DOMConfigurator.configure(RuntimeInfo.getCurrentInfo().getRealPath("/{SolutionConfigFolder}/log4j-console.xml"));
				try {
					RuntimeInfo.getCurrentInfo().getBean("_no_name_");
				}
				catch(Exception e){
				}

				TechnicalDictionary dictionary = (TechnicalDictionary) RuntimeInfo.getCurrentInfo().getBean("dictionary");
				dictionary.loadDictionary(false);
				dictionary.printAllRelations();
				System.out.println("Print All terms relations finished successfully");
			}
			catch (Exception e) {
				System.out.println("Print All terms relations failed due to " + e.getMessage());
				e.printStackTrace();
			}

		}
		else if(cmd.hasOption("fixRelations")) {
			System.out.println("Fixing relations ...");
			try {
				MultitenantRuntimeInfo.init(null);
				RuntimeInfo.getCurrentInfo().startThread(new MultitenantThreadInfo("Attunity_New"));
				DOMConfigurator.configure(RuntimeInfo.getCurrentInfo().getRealPath("/{SolutionConfigFolder}/log4j-console.xml"));
				try {
					RuntimeInfo.getCurrentInfo().getBean("_no_name_");
				}
				catch(Exception e){
				}

				IDictionaryParser dictParser = (IDictionaryParser) RuntimeInfo.getCurrentInfo().getBean("techTokensParser");
				TechnicalDictionary dictionary = (TechnicalDictionary) RuntimeInfo.getCurrentInfo().getBean("dictionary");
				dictionary.loadDictionary(false);
				Session session = RuntimeInfo.getCurrentInfo().getHibernate().acquireSession(SessionMode.READ_WRITE);
				dictParser.buildRelations(dictionary, session);
				dictionary.save();
				RuntimeInfo.getCurrentInfo().getHibernate().releaseSession(session);
				RuntimeInfo.getCurrentInfo().getHibernate().closeSession();
				System.out.println("Fixing terms relations finished successfully");
			}
			catch (Exception e) {
				System.out.println("Fixing terms relation failed due to " + e.getMessage());
				e.printStackTrace();
			}

		}
		else if(cmd.hasOption("findMatches")) {
			System.out.println("Finding perfect matches...");
			try {
				MatchFinder finder = new MatchFinder();
				String objectId = cmd.getOptionValue("findMatches");
				finder.findPerfectMatches(objectId);
				System.out.println("Finding matches finished successfully, please see log for the matches");
			}
			catch(Exception e) {
				System.out.println("Match finder failed due to " + e.getMessage());
				e.printStackTrace();
			}
		}	
		else if(cmd.hasOption("povService")) {
			System.out.println("Running iMatch Proof of Value Service...");
			ConsoleAppRuntimeInfo.init(null);
			DOMConfigurator.configure(RuntimeInfo.getCurrentInfo().getRealPath("/{SolutionConfigFolder}/log4j-console.xml"));
			try {
				RuntimeInfo.getCurrentInfo().getBean("_no_name_");
			}
			catch(Exception e){}
			String firstObjectId = null;
			MatchFinderConfiguration matchFinderConfig = null;
			boolean restart = false;
			try {
				IConfigurationResourceLoader loader = (IConfigurationResourceLoader)RuntimeInfo.getCurrentInfo().getBean("xmlConfigurationResourceLoader");
				IConfigurationResource matchFinderResource = loader.loadResource("xml:///{SolutionConfigFolder}/matchFinder.xml;//matchFinder");
				matchFinderConfig = (MatchFinderConfiguration) matchFinderResource.getCustomConfiguration(MatchFinderConfiguration.class);
				ExtractionContext ctx = new ExtractionContext(matchFinderConfig.getMaxNumTickets(), matchFinderConfig.getCompanyName());
				IConfigurationResource resource = loader.loadResource("xml:///{SolutionConfigFolder}/matcher.xml;//searcher");
				IConfigurationObject config = resource.getConfigurationObject(SearcherConfiguration.class);
				Set<String> objects = config.getAllObjects();
				firstObjectId = objects.toArray(new String[0])[0];
				DictionaryBuilder builder = (DictionaryBuilder)RuntimeInfo.getCurrentInfo().getBean(DictionaryBuilderFactory.getBuilderId(firstObjectId));
				RuntimeInfo.getCurrentInfo().startThread();
				System.out.println("Running Extraction Process. Total number of tickets to be scanned is " + ctx.getMaxNumTickets());
				StageMngr.instance().setStage(StageMngr.Stage.Extract);
				StageMngr.instance().setStage(StageMngr.Stage.Extract);
				ProgressStatus status = builder.buildDictionary(ctx);
				if(status == ProgressStatus.PROCESS_FAILED) {
					restart = true;
					throw new MatcherException("Extraction failed");
				}
				System.out.println("Running Post Extraction Process...");
				StageMngr.instance().setStage(StageMngr.Stage.PostExtract);
				status = builder.buildDictionary(ctx);
				if(status == ProgressStatus.PROCESS_FAILED) {
					restart = true;
					throw new MatcherException("Extraction failed");
				}
				System.out.println("Completing dictionary extraction...");
				TechnicalDictionary dictionary = (TechnicalDictionary)RuntimeInfo.getCurrentInfo().getBean("dictionary");
				dictionary.loadDictionary(false);
				dictionary.updateTermsAfterFreq();
				dictionary.addComplexSynonym();
				TechnicalTermSource personsNames = dictionary.addSource("Persons Names");
				TechnicalTermSource changeSource = dictionary.addSource("Contain Person Name");
				changeSource.setAssociationRank(100);
				dictionary.changeTermsContainSource(personsNames,changeSource);
				dictionary.save();
				RuntimeInfo.getCurrentInfo().finishThread();
				//HibernateUtils.closeSession();
				System.out.println("SoftLib dictionary built successfully");
				System.out.println("Indexing tickets...");
				RuntimeInfo.getCurrentInfo().startThread();
				Wordnet.getInstance().disableCheck();
				StageMngr.instance().setStage(StageMngr.Stage.Index);
				Indexer indexer = (Indexer)RuntimeInfo.getCurrentInfo().getBean(IndexerFactory.getIndexerId(firstObjectId));
				IndexContext indexCtx = new IndexContext(matchFinderConfig.getMaxNumTickets());
				status = indexer.index(indexCtx);
				if(status == ProgressStatus.PROCESS_FAILED) {
					restart = true;
					throw new MatcherException("Extraction failed");
				}
				RuntimeInfo.getCurrentInfo().finishThread();
				RuntimeInfo.getCurrentInfo().getHibernate().closeSession();
				System.out.println("SoftLib index built successfully");
				RuntimeInfo.getCurrentInfo().destroy();
			}
			catch(MatcherException me) {
				System.out.println("POV Service failed due to " + me.getMessage());
				if(restart) {
					System.out.println("Restarting the service ");
					me.printStackTrace();
					CommandLineUtils.restartLongProcess(matchFinderConfig.getReportDestPath(), "run_pov.bat");
				}
				System.exit(-1);				
			}
			catch(Exception e) {
				System.out.println("POV Service failed due to " + e.getMessage());
				e.printStackTrace();
				System.exit(-1);	
			}
			try {
				System.out.println("Generating POV report...");
				MatchFinder finder = new MatchFinder();
				finder.findPerfectMatches(firstObjectId);
				System.out.println("Finding matches finished successfully, please see log for the matches");
			}
			catch(Exception e) {
				System.out.println("POV report generation failed due to " + e.getMessage());
				e.printStackTrace();
				System.exit(-1);	
			}
			try {
				System.out.println("Restarting Tomcat service ");
				String fileName = matchFinderConfig.getReportDestPath() + "\\restart_service.bat";
				String[] commands = {"cmd", "/c", "start", "\"iMatch\"",fileName};
				Runtime.getRuntime().exec(commands);
			}
			catch(Exception e){
				System.out.println("Unable to restart Tomcat service due to " + e.getMessage());
			}
			System.exit(0);
		}	
		else if (cmd.hasOption("verify")) {
			Logger verifyLog = Logger.getLogger("verifylog");
			
			try {
				VerificationSetRunner runner = new VerificationSetRunner();
				LogUtils.info(verifyLog, "Running verification set %s...", DateFormat.getDateInstance().format(new Date()));  
				runner.verify();
				LogUtils.info(verifyLog, "Finished running verification set.");
			}
			catch(Exception e) {
				LogUtils.info(verifyLog, "Verification set failed due to %s",e.getMessage());
				LogUtils.info(verifyLog, "%s",e.toString());
			}
			
		}		
		else if(cmd.hasOption("createIndexTable")) {
			MultitenantRuntimeInfo.init(null);
			DOMConfigurator.configure(RuntimeInfo.getCurrentInfo().getRealPath("/{SolutionConfigFolder}/log4j-console.xml"));
			try {
				RuntimeInfo.getCurrentInfo().getBean("_no_name_");
			}
			catch(Exception e){}
			try {
				//TODO multitenant - set solution name
				RuntimeInfo.getCurrentInfo().startThread(new MultitenantThreadInfo("MobileEyeSite"));
				ProcessedTicketDBase.createTable();
				System.out.println("Solution table generated successfully");
				RuntimeInfo.getCurrentInfo().finishThread();
				System.exit(0);
			}
			catch(Exception e) {
				System.out.println("Solution table generation failed due to " + e.getMessage());
				System.exit(-1);			
			}
		}
		else if(cmd.hasOption("addStemmingException")) {
			String[] optionValues = cmd.getOptionValues("addStemmingException");
			if(optionValues.length == 0) {
				System.out.println("usage: addStemmingException term%%stemm");
				return;
			}
			String tmpText= optionValues[0].toLowerCase();
			String[] tmpTextParts = tmpText.split("%%");
			if(tmpTextParts.length != 2) {
				System.out.println("usage: addStemmingException term%%stemm");
				return;				
			}
			String text = tmpTextParts[0];
			String stemm = tmpTextParts[1];
			ConsoleAppRuntimeInfo.init(null);
			DOMConfigurator.configure(RuntimeInfo.getCurrentInfo().getRealPath("/{SolutionConfigFolder}/log4j-console.xml"));
			try {
				RuntimeInfo.getCurrentInfo().getBean("_no_name_");
			}
			catch(Exception e){}
			try {
				TechnicalDictionary dict = (TechnicalDictionary)RuntimeInfo.getCurrentInfo().getBean("dictionary");
				dict.loadDictionary();
				String oldStemming = PorterStemmer.stem(text);
				boolean added = PorterStemmer.addStemmingException(text, stemm);
				if(!added) {
					System.out.println("Stemming exception for " + text + " already exists");
					System.exit(0);					
				}
				for(TechnicalDictionaryTerm term : dict.getAllTerms(false)) {
					if(term.getTermText().startsWith(text + " ") ||
					   term.getTermText().endsWith(" " + text) ||
					   term.getTermText().contains(" " + text + " ")) {
							term.notifyStemmingException(oldStemming, stemm);
					}
				}
				dict.save();
				System.out.println("Stemming exception added successfully");
				System.exit(0);
			}
			catch(Exception e) {
				System.out.println("Stemming exception addition failed due to " + e.getMessage());
				System.exit(-1);			
			}
		}		
		else  if(cmd.hasOption("snippet")) {
			System.out.println("Testing snippet...");
			
				try 
				{
					MultitenantRuntimeInfo.init(new FictiveServletCtxt("C:\\temp\\VirtualAgent"));
					RuntimeInfo.getCurrentInfo().startThread(new MultitenantThreadInfo("ua-1234567-8"));
					DOMConfigurator.configure(RuntimeInfo.getCurrentInfo().getRealPath("/{SolutionConfigFolder}/log4j-console.xml"));
					try 
					{
						RuntimeInfo.getCurrentInfo().getBean("_no_name_");
					}
					catch(Exception e){}
					try
					{
						// index some content
						/*{
							InMemoryTicket indexticket = new InMemoryTicket("cases","isolve title","softlib ltd and isolve");
							LuceneIndexer indexer = (LuceneIndexer)RuntimeInfo.getCurrentInfo().getBean("lucene.indexer");
							indexer.startBatch();
							ITicketProcessor ticketProcessor = (ITicketProcessor) RuntimeInfo.getCurrentInfo().getBean("ticketProcessor");
							try {
								IProcessedTicket processedTicket = ticketProcessor.processTicket(TicketProcessor.StepsMode.Index,
										indexticket,MatchMode.all,null,false);
								indexer.index(processedTicket);
							} catch (MatcherException e) {
								Assert.fail(e.getMessage());
							}
							indexer.flush();
							
						}*/
						
						InMemoryTicket ticket = new InMemoryTicket("vaObject","","iSolve support other languages");
						Matcher matcher = (Matcher)RuntimeInfo.getCurrentInfo().getBean("matcher");
						matcher.findMatches(ticket);						
					}
					catch(Exception e)
					{
						System.out.println("Exception...");
					}
					RuntimeInfo.getCurrentInfo().startThread(new MultitenantThreadInfo("EZLegacy"));
					try
					{						
						InMemoryTicket ticket = new InMemoryTicket("vaObject","","ca");
						Matcher matcher = (Matcher)RuntimeInfo.getCurrentInfo().getBean("matcher");
						matcher.findMatches(ticket);						
					}
					catch(Exception e)
					{
						System.out.println("Exception...");
					}
				}
				finally
				{
					
				}
		}
		else if(cmd.hasOption("testProcessedTicket")) {
			for(int i=0; i<2; ++i) {
			String iMatchSolutionsFolder = "c:\\temp\\VirtualAgent";
			MultitenantRuntimeInfo.setRootDir(iMatchSolutionsFolder);
			MultitenantRuntimeInfo.init(null);			
			RuntimeInfo.getCurrentInfo().startThread(new MultitenantThreadInfo("UA-1234567-8"));
			DOMConfigurator.configure(RuntimeInfo.getCurrentInfo().getRealPath("/{SolutionConfigFolder}/log4j-console.xml"));
			try 
			{
				RuntimeInfo.getCurrentInfo().getBean("_no_name_");
			}
			catch(Exception e){}
			TechnicalDictionary dict = (TechnicalDictionary)RuntimeInfo.getCurrentInfo().getBean("dictionary");
			try {
				List<IProcessedTicket> tickets = ProcessedTicketDBase.getAllTickets(dict);
				RuntimeInfo.getCurrentInfo().destroy();				
			} catch (MatcherException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
		}

	}	
};
