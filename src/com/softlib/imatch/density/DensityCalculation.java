package com.softlib.imatch.density;

import java.util.ArrayList;
import java.util.List;

import com.softlib.imatch.common.ObjectPerListCombinations;
import com.softlib.imatch.common.SubListCombinations;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.distance.PositionsByTerm;
import com.softlib.imatch.distance.TermPosition;

public class DensityCalculation {

	private PositionsByTerm positionsByRelationship = new PositionsByTerm();

	public DensityCalculation(PositionsByTerm positionsByRelationship) {
		super();
		this.positionsByRelationship = positionsByRelationship;
	}

	private PositionsByTerm getSubMapFromCombination(PositionsByTerm map,
			List<TechnicalDictionaryTerm> combination) {
		PositionsByTerm rc = new PositionsByTerm();
		for (TechnicalDictionaryTerm term : combination) {
			rc.getMap().put(term, map.getMap().get(term));
		}
		return rc;
	}

	private int getDistance(List<TermPosition> positions) {
		int maxIdx = -1;
		int minIdx = -1;
		for(TermPosition position : positions ) {
			Integer startPos = position.getStart();
			if (startPos>maxIdx)
				maxIdx = startPos;
			if (minIdx==-1 || startPos<minIdx)
				minIdx = startPos;
		}
		return maxIdx-minIdx;
	}

	private int inDistance(int combinationSize,int returnValue,int distance) {
		List<TechnicalDictionaryTerm> terms = 
			new ArrayList<TechnicalDictionaryTerm>(positionsByRelationship.getMap().keySet());
		SubListCombinations<TechnicalDictionaryTerm> subListCombinations = 
			new SubListCombinations<TechnicalDictionaryTerm>(terms,combinationSize);
		List<TechnicalDictionaryTerm> combination = subListCombinations.getNextCombination();
		while (!combination.isEmpty()) {
			PositionsByTerm subMap =
				getSubMapFromCombination(positionsByRelationship,combination);
			if(inDistance(subMap,distance))
				return returnValue;
			combination = subListCombinations.getNextCombination();
		}
		return -1;
	}
	

	private boolean inDistance(PositionsByTerm map,int maxDistance) {

		List<List<TermPosition>> lists = new ArrayList<List<TermPosition>>();
		for (TechnicalDictionaryTerm term : map.getMap().keySet()) 
			lists.add(map.getMap().get(term));
		ObjectPerListCombinations<TermPosition> perListCombinations =
			new ObjectPerListCombinations<TermPosition>(lists);
		
		List<TermPosition> combination = perListCombinations.getNextCombination();
		int minDistance = 0;
		while (!combination.isEmpty()) {
			int distance = getDistance(combination);
			if (minDistance==0 || distance<minDistance)
				minDistance=distance;
			combination = perListCombinations.getNextCombination();
		}
		return minDistance<maxDistance;
	}

	public int relationshipDensity(int numTerms,int distance) {
		if (numTerms<2) 
			return 0;
		int rc;
		int returnValue = 0;
		for (int combSize=numTerms ; combSize>1 ; combSize--) {
			if( (rc=inDistance(combSize,returnValue,distance))!=-1)
				return rc;
			returnValue += (100/(numTerms-1)); 
		}
		return 100;
	}

};
