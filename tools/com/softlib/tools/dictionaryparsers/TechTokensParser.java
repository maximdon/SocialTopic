package com.softlib.tools.dictionaryparsers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Session;

import com.softlib.imatch.ITicket;
import com.softlib.imatch.MatcherException;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.ServerState;
import com.softlib.imatch.common.ITokenizer;
import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.common.ProximityDelimeterAwareTokenizer;
import com.softlib.imatch.common.configuration.ConfigurationObject;
import com.softlib.imatch.common.configuration.IConfigurationObject;
import com.softlib.imatch.common.configuration.IConfigurationResource;
import com.softlib.imatch.common.configuration.IConfigurationResourceLoader;
import com.softlib.imatch.common.progressnotifier.DefaultProgressNotifier;
import com.softlib.imatch.common.progressnotifier.IProgressNotificationListener;
import com.softlib.imatch.common.progressnotifier.IProgressNotifier;
import com.softlib.imatch.dictionary.ITechnicalDictionary;
import com.softlib.imatch.dictionary.ITechnicalTermsContainer;
import com.softlib.imatch.dictionary.SynonymsRelation;
import com.softlib.imatch.dictionary.TechnicalDictionary;
import com.softlib.imatch.dictionary.TechnicalDictionaryFrequency;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.dictionary.TechnicalTermSource;
import com.softlib.imatch.matcher.ITicketFieldsNames;
import com.softlib.imatch.matcher.SearcherConfiguration;
import com.softlib.imatch.relations.IRelationAlgorithm;
import com.softlib.imatch.relations.RelationAlgorithmContext;
import com.softlib.imatch.ticketprocessing.ITicketProcessStep;
import com.softlib.imatch.ticketprocessing.StepContext;
import com.softlib.imatch.ticketprocessing.IProcessedTicket.MatchMode;

public class TechTokensParser implements IDictionaryParser, IProgressNotifier
{
	
	public static final String IT = "IT";
	
	private static final String NAMES_TERMS = "Names Terms";
	private static final String SPLIT_TERMS = "Split Terms";

	private static Logger log = Logger.getLogger(TechTokensParser.class);
	private List<ITicketProcessStep> processSteps;
	private int leftMargin;
	private int rightMargin; 
	private float leftMarginPercent;
	private float rightMarginPercent;
	private int	minSynCount;
	
	private IProgressNotifier notifier = new DefaultProgressNotifier();
	
	private TechnicalDictionary dictionary;

	private List<IRelationAlgorithm> relationAlgorithms;
	
	public TechTokensParser(int leftMargin, int rightMargin, float leftMarginPercent, float rightMarginPercent, boolean splitMultiTerms,int minSynCount) {
		this.leftMargin = leftMargin;
		this.rightMargin = rightMargin;
		this.leftMarginPercent = leftMarginPercent;
		this.rightMarginPercent = rightMarginPercent;
		this.minSynCount = minSynCount;
	}
	
	public void setSteps(List<ITicketProcessStep> steps) {
		this.processSteps = steps;
	}
	
	public void setRelationAlgorithms(List<IRelationAlgorithm> relationAlgorithms) {
		this.relationAlgorithms = relationAlgorithms;
	}

	public void init() throws MatcherException {
		getDictionary().addSource(SPLIT_TERMS);
		getDictionary().addSource(NAMES_TERMS);		
	}
		
	private ITechnicalTermsContainer getTermsContainer(ITicket ticket) throws MatcherException {
		getDictionary();
		return new DualTermsContainer(dictionary,ticket);
	}	
	
	private ITechnicalTermsContainer getDictionary() throws MatcherException {
		synchronized (this) {
			if (dictionary != null)
				return dictionary;
			dictionary = (TechnicalDictionary) RuntimeInfo.getCurrentInfo().getBean("dictionary");
			TechnicalDictionaryFrequency frequency =
				new TechnicalDictionaryFrequency(leftMargin,rightMargin,leftMarginPercent,rightMarginPercent);
			dictionary.setFrequency(frequency);
			return dictionary;			
		}
	}
	
	public void end() {
		for(ITicketProcessStep step : processSteps)
			step.end();		
	}
	
	//TODO this code is the same as in TicketProcessor, reuse
	public void parse(ITicket ticket) throws IOException, SQLException, MatcherException {
		
		ITechnicalTermsContainer termsContainer = getTermsContainer(ticket);
		
		ITicketFieldsNames fieldsNamesConfig = ticket.getFieldsConfig();
		Set<String> fieldsNames = fieldsNamesConfig.getAllFields(MatchMode.all);
			
		Map<String,StepContext> stepContextByFieldName = new HashMap<String, StepContext>();
		for (String fieldName : fieldsNames) {
			StepContext stepContext = new StepContext(ticket.getId()); 
			stepContextByFieldName.put(fieldName, stepContext);
		}
		
		for(ITicketProcessStep step : processSteps) {
			for (String fieldName : fieldsNames) {
				try {
					if(ticket.getField(fieldName) == null)
						continue;
					step.run(fieldName,ticket,termsContainer, stepContextByFieldName.get(fieldName));
				}
				catch(ConcurrentModificationException e) {
					//TODO patch, this exception should be solved
					LogUtils.error(log, "ConcurrentModificationException for ticket %s on field %s for step %s", ticket, fieldName, step.getStepName());
				}
			}
		}
		
		termsContainer.finish();
	}
	
	public void buildRelations(ITechnicalDictionary dictionary, Session session)
			throws IOException, SQLException {
			
		SynonymsRelation relation = new SynonymsRelation(dictionary);
		relation.enableTraceForRule("Phonetics");
		relation.enableTraceForRule("Acronym");

		ITokenizer splitter = new ProximityDelimeterAwareTokenizer(new char[] {' '});
		Iterator<TechnicalDictionaryTerm> termsIterator = dictionary.termsIterator();
					
		RelationAlgorithmContext relationContext = new RelationAlgorithmContext();
		relationContext.dictionary = dictionary;
		relationContext.relation = relation;
		int count = 0;
		int totalCount = dictionary.getTermsCount(); 
		IConfigurationResourceLoader loader = (IConfigurationResourceLoader)RuntimeInfo.getCurrentInfo().getBean("xmlConfigurationResourceLoader");
		IConfigurationResource resource = loader.loadResource("xml:///{SolutionConfigFolder}/matcher.xml;//searcher");
		IConfigurationObject config = resource.getConfigurationObject(SearcherConfiguration.class);
		//The object id is not important in this case, always use the first one
		String objectId = config.getFirstObjectId();
		ServerState state = RuntimeInfo.getCurrentInfo().getInternalState(objectId);
		LogUtils.info(log, "Building relations, previous recovery position is %d", (int)state.getRelationsRecoveryPosition());
		while(termsIterator.hasNext()) {
			TechnicalDictionaryTerm existingTerm = termsIterator.next();
			count ++;
			if(count < state.getRelationsRecoveryPosition())
				continue;
			if(count % 1000 == 0) {
				LogUtils.info(log, "Building relations, till now %d from %d terms processed", count, totalCount);
				((TechnicalDictionary)dictionary).save();
				state.setRelationsRecoveryPosition(count);
				RuntimeInfo.getCurrentInfo().saveInternalState(objectId, session);
				notifyProgress(2, "Another 1000 terms processed", count, totalCount - count);
			}
			//TODO patch, currently 1 frequency terms relations are not in use. 
			//Once we enable these relations we need to find better solution here
			if(existingTerm.getFrequency() == 1)
				continue;
			String termText = existingTerm.getTermText();
			String[] termParts;
			
			try {
				termParts = splitter.split(termText);
				for (int i=0; i < relationAlgorithms.size(); i++) {
					relationAlgorithms.get(i).relate(relationContext, existingTerm, termParts);
				}
			}
			catch (Exception e) {
				e.printStackTrace();
				LogUtils.error(log,"buildRelations(%s) : Exception = %s",termText,e.getMessage());
			}
		}
		for (int i=0; i < relationAlgorithms.size(); i++) {
			relationAlgorithms.get(i).finish(relationContext);
		}
		state.setRelationsRecoveryPosition(-1);
		RuntimeInfo.getCurrentInfo().saveInternalState(objectId, session);
		LogUtils.debug(log, "All relations added successfully");
	}

	public Collection<TechnicalTermSource> getSources() {
		// TODO Auto-generated method stub
		return null;
	}

	//Progress Notification interface
	@Override
	public void notifyProgress(int level, String message, int processedCount,
			int remainingCount) {
		notifier.notifyProgress(level, message, processedCount, remainingCount);
	}

	@Override
	public void registerProgressNotificationListener(
			IProgressNotificationListener listener) {
		notifier.registerProgressNotificationListener(listener);
	}

	@Override
	public void unregisterProgressNotificationListener(
			IProgressNotificationListener listener) {
		notifier.unregisterProgressNotificationListener(listener);
	}
};
