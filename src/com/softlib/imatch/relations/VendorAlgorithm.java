package com.softlib.imatch.relations;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.dictionary.VendorList;
import com.softlib.imatch.dictionary.Wordnet;

public class VendorAlgorithm implements IRelationAlgorithm {

	private static Logger log = Logger.getLogger(VendorAlgorithm.class);

	private final static int VendorRelationAlg = 2;
	//Vendors are also built in 2 steps, first we populate the map below with list of potential relations
	//In the second step we relate all undoubt relations
	
	//This map contains potential pairs of related terms vendor term and related term without vendor.
	//We don't relate these 2 terms as we found them to prevent doubt relations to be added
	VendorList vendorList = VendorList.getInstance();
	Map<TechnicalDictionaryTerm, TechnicalDictionaryTerm> vendorsTerms = new HashMap<TechnicalDictionaryTerm, TechnicalDictionaryTerm>();


	public VendorAlgorithm() {
		super();
	}

	public boolean relate(RelationAlgorithmContext context, TechnicalDictionaryTerm term, String[] termParts) {
		//Handle vendors
		int supportedRelationsAlgs = term.getTermSource().getSupportedRelationsAlgs();

		String vendor = vendorList.getTermVendor(term.getTermText());
		if(vendor != null &&
			term.getTermText().trim().length() > vendor.length() &&
			(supportedRelationsAlgs & VendorRelationAlg) == VendorRelationAlg) 
		{
			String termWithoutVendor = term.getTermText().substring(vendor.length() + 1);
			if(!Wordnet.getInstance().containsWord(termWithoutVendor)) {
				TechnicalDictionaryTerm relatedVendorTerm = context.dictionary.get(termWithoutVendor);
				if(relatedVendorTerm != null && vendorsTerms.containsKey(relatedVendorTerm)) {
					TechnicalDictionaryTerm knownVendor = vendorsTerms.get(relatedVendorTerm);
					if(!knownVendor.equals(term))
						//Ambiguity found, remove this term from the list of possible relations
						vendorsTerms.remove(relatedVendorTerm);
				}
				else if(relatedVendorTerm != null && !term.equals(relatedVendorTerm)) {
					vendorsTerms.put(relatedVendorTerm, term);
				}
			}
		}
		return false;
	}
	public void finish(RelationAlgorithmContext context) {
		for(Entry<TechnicalDictionaryTerm, TechnicalDictionaryTerm> vendorSynonym: vendorsTerms.entrySet()) {
			try {
				TechnicalDictionaryTerm relatedTerm = vendorSynonym.getKey();
				TechnicalDictionaryTerm vendorTerm = vendorSynonym.getValue();
				context.relation.relateWithContaining(vendorTerm, relatedTerm, "Vendors");
			}
			catch (Exception e) {
				e.printStackTrace();
				LogUtils.error(log,"buildRelations.vendorSynonym($s): Exception = $s",vendorSynonym,e.getMessage());
			}		
		}
	}

}