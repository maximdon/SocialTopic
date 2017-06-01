package com.softlib.imatch.relations;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.dictionary.TechnicalTermSource;
import com.softlib.imatch.ticketprocessing.ExtractVersionsByNLP;
import com.softlib.imatch.ticketprocessing.VersionsTechToknes;

public class VersionAlgorithm implements IRelationAlgorithm{

	private static Logger log = Logger.getLogger(VersionAlgorithm.class);

	private final static int VersionRelationAlg = 16;

	//Versions relations
	//Versions relations are also built in 2 steps, first we populate the map below with list of potential relations
	//In the second step we relate all undoubt relations
	
	//This map contains potential pairs of NLP Version term and (regex) Versions term
	//We don't relate these 2 terms as we found them to prevent doubt relations to be added
	Map<TechnicalDictionaryTerm, TechnicalDictionaryTerm> versionsTerms = new HashMap<TechnicalDictionaryTerm, TechnicalDictionaryTerm>();

	
	public VersionAlgorithm() {
		super();
	}


	public boolean relate(RelationAlgorithmContext context, TechnicalDictionaryTerm term, String[] termParts) {

		int supportedRelationsAlgs = term.getTermSource().getSupportedRelationsAlgs();
		if((supportedRelationsAlgs & VersionRelationAlg) == VersionRelationAlg) {

			TechnicalTermSource nlpVersionSource = context.dictionary.getSource(ExtractVersionsByNLP.FIELD_NAME);
			TechnicalTermSource regexVersionSource = context.dictionary.getSource(VersionsTechToknes.FIELD_NAME);
			if(term.getTermSource() != null && term.getTermSource().equals(nlpVersionSource)) {
				int spaceIdx = term.getTermText().lastIndexOf(' ');
				if(spaceIdx > -1) {
					String verToken = term.getTermText().substring(spaceIdx + 1);
					String[] possibleExistingTerms = new String[] {verToken, verToken + ".0", verToken + ".00"};
					for(String possibleExistingTerm : possibleExistingTerms) {
						TechnicalDictionaryTerm relatedVersionTerm = context.dictionary.get(possibleExistingTerm);
						if(relatedVersionTerm == null || !(relatedVersionTerm.getTermSource().equals(regexVersionSource) || relatedVersionTerm.getTermSource().equals(nlpVersionSource)))
							continue;
						if(versionsTerms.containsKey(relatedVersionTerm)) {
							TechnicalDictionaryTerm knownRelation = versionsTerms.get(relatedVersionTerm);
							if(!knownRelation.equals(term))
								//Ambiguity found, remove this term from the list of possible relations
								versionsTerms.remove(relatedVersionTerm);
						}
						else if(relatedVersionTerm != null && !term.equals(relatedVersionTerm)) {
							versionsTerms.put(relatedVersionTerm, term);
						}
					}
				}
			}
		}
		return false;
	}
	
	public void finish(RelationAlgorithmContext context) {
		for(Entry<TechnicalDictionaryTerm, TechnicalDictionaryTerm> versionSynonym: versionsTerms.entrySet()) {
			try {
				TechnicalDictionaryTerm relatedTerm = versionSynonym.getKey();
				TechnicalDictionaryTerm versionTerm = versionSynonym.getValue();
				context.relation.relateWithContaining(versionTerm, relatedTerm, "Versions");
			}
			catch (Exception e) {
				e.printStackTrace();
				LogUtils.error(log,"buildRelations.versionSynonym(%s): Exception = %s",versionSynonym,e.getMessage());
			}		
		}
	}

}
