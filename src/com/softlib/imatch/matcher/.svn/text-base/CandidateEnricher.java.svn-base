package com.softlib.imatch.matcher;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.softlib.imatch.BaseTicket;
import com.softlib.imatch.ITicket;
import com.softlib.imatch.ITicketProvider;
import com.softlib.imatch.InMemoryTicket;
import com.softlib.imatch.MatcherException;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.TicketProviderFactory;
import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.common.StringUtils;
import com.softlib.imatch.common.TracerFile;
import com.softlib.imatch.common.TracerFileLast;
import com.softlib.imatch.common.configuration.IConfigurationResource;
import com.softlib.imatch.common.configuration.IConfigurationResourceLoader;
import com.softlib.imatch.dbintegration.DBTicket;
import com.softlib.imatch.dbintegration.DBUtils;
import com.softlib.imatch.density.DensityCalculation;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.distance.PositionsByTerm;
import com.softlib.imatch.distance.TermsByPositions;
import com.softlib.imatch.ticketprocessing.IProcessedTicket;
import com.softlib.imatch.ticketprocessing.ProcessedTicket;
import com.softlib.imatch.ticketprocessing.IProcessedTicket.MatchMode;

public class CandidateEnricher implements ISingleCandidateProcessor {
	
	static final String ORIGINAL_OBJECT_ID = "_original_object_id_";
	static final String ORIGINAL_ID = "_original_id_";

	private static final Logger log = Logger.getLogger(CandidateEnricher.class);
	
	static private TracerFile distanceFile = 
		TracerFileLast.create(TracerFileLast.Distance,"current",false);
	static IProcessedTicket last;

	static TermsDistanceConfig termsDistanceConfig;
	
	public void processCandidate(MatchCandidate candidate) throws MatcherException {
		if(candidate == null || candidate.getCandidateData() == null || candidate.getCandidateData().getId() == null)
			return;
		
		//TODO allow enrichment all candidates in single database query
		String originalTicketId = (String)candidate.getCandidateData().getField(ORIGINAL_ID);
		String originalObjecttId = (String)candidate.getCandidateData().getField(ORIGINAL_OBJECT_ID);
		String ticketId = candidate.getCandidateData().getId();
		String objectId = candidate.getOriginObjectId();
		if(!StringUtils.isEmpty(originalTicketId))
			ticketId = originalTicketId;
		if(!StringUtils.isEmpty(originalObjecttId))
			objectId = originalObjecttId;		
		ITicketProvider ticketProvider = (ITicketProvider) RuntimeInfo.getCurrentInfo().getBean(TicketProviderFactory.getProviderId(objectId));
		ITicket ticket = null;
		ticket = ticketProvider.getForDisplay(ticketId);
		if(ticket == null) {
			//The candidate ticket was not found, create new fictive ticket for display
			ticket = new DBTicket(candidate.getOriginObjectId());
			((DBTicket)ticket).setId(ticketId);
			Iterator<String> it = ticket.getFieldsConfig().getTitleFields().iterator();
			String titleFieldName = it.next();
			((DBTicket)ticket).setField(titleFieldName, "Unknown ticket");
		}
		if(originalObjecttId != null) {
			LogUtils.debug(log, "Original %s, id %s, title %s", originalObjecttId, ticket.getId(), ticket.getTitle());
			//Convert the newly retrieved ticket to the correct object id.
			BaseTicket duplicateTicket = new DBTicket(candidate.getOriginObjectId());
			BaseTicket originalTicket = (BaseTicket)ticket;
			duplicateTicket.setId(ticketId);
			for(String fieldName : originalTicket.getFields())
				duplicateTicket.setField(fieldName, originalTicket.getField(fieldName));
			
			ticket = duplicateTicket;
		}
		ProcessedTicket sourceTicket = 
			((ProcessedTicket)candidate.getSourceProcessedTicket());
		
		if (termsDistanceConfig==null) {
			IConfigurationResourceLoader loader = (IConfigurationResourceLoader) RuntimeInfo.getCurrentInfo().getBean("xmlConfigurationResourceLoader");
			IConfigurationResource resource = loader.loadResource("xml:///{SolutionConfigFolder}/matcher.xml;//termsDistance");
			termsDistanceConfig = (TermsDistanceConfig) resource.getCustomConfiguration(TermsDistanceConfig.class);
			termsDistanceConfig.init();
		}
		
		Boolean activeInMatch = termsDistanceConfig.getActiveInMatch();
		Boolean activeInRematch = termsDistanceConfig.getActiveInRematch();
		MatchMode matchMode = sourceTicket.getMatchMode();

		if (activeInMatch && matchMode==MatchMode.match ||
			activeInRematch && matchMode==MatchMode.rematch) {

			int maxImportantTerms = termsDistanceConfig.getMaxImportantTerms();
			List<TechnicalDictionaryTerm> mostImportentTerms = 
				sourceTicket.getImportantTerms(maxImportantTerms);
			
			ITicketFieldsNames fieldsNamesConfig = ticket.getFieldsConfig();
//			fieldsNamesConfig.setObjectId(sourceTicket.getOriginObjectId());
			Set<String> fieldsNames = fieldsNamesConfig.getAllFields(matchMode);

			String text = DBUtils.concatFields(fieldsNames, ticket);
			
			TermsByPositions termsByPositions = new TermsByPositions();
			termsByPositions.process(mostImportentTerms,text);
			DensityCalculation densityCalculation = 
				new DensityCalculation(termsByPositions.getPositionsByRelationship());
			
			int reduceScorePercent = termsDistanceConfig.getReduceScorePercent();
			int distanceByLetters = termsDistanceConfig.getDistanceByLetters();
			int relationshipDensity = 
				densityCalculation.relationshipDensity(mostImportentTerms.size(),distanceByLetters);
			float reducePercent = ((float)reduceScorePercent/100)*relationshipDensity;
			
			if (reducePercent>0) {
				float score = candidate.getScore();
				score = score/100*(100-reducePercent);
				LogUtils.debug(log, "Reducing candidate %s score to %f due to large distance between the terms", candidate, score);
				candidate.setScore(score);
				print(text,termsByPositions,mostImportentTerms,reducePercent,candidate);
			}
		}
		
		LogUtils.debug(log, "Ticket data for ticket %s completed successfully, ticket title is %s", ticketId, ticket.getTitle());
		candidate.setCandidateData(ticket);
	}
	
	public void print(String text,TermsByPositions termsByPositions,
					  List<TechnicalDictionaryTerm> mostImportentTerms,
					  float reducePercent,MatchCandidate candidate) {
		if (!distanceFile.isActive())
			return;
		
		text=termsByPositions.replace("-<",">-",true);

		IProcessedTicket current = candidate.getSourceProcessedTicket();
		if (last==null || !last.equals(current)) {
			last = current;
			distanceFile.clean();
		}
		
		PositionsByTerm positionsByRelationship = 
			termsByPositions.getPositionsByRelationship();
		
		distanceFile.write("[Id   ] "+candidate.getProcessedTicket().getId());
		distanceFile.write("[Value] "+reducePercent);
		distanceFile.write("[Terms] "+mostImportentTerms);
		distanceFile.write("[Map  ] "+positionsByRelationship);
		distanceFile.write("[Text ] "+text);
	}

};
