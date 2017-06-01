package com.softlib.imatch.relations;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.language.DoubleMetaphone;

import com.softlib.imatch.common.ITokenizer;
import com.softlib.imatch.common.ProximityDelimeterAwareTokenizer;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;

public class PhoneticAlgorithm implements IRelationAlgorithm {

	private final static int PhoneticsRelationAlg = 4;
    
	private Map<String, TechnicalDictionaryTerm> phoneticSimilarityTerms = new HashMap<String, TechnicalDictionaryTerm> ();
	private DoubleMetaphone dm = new DoubleMetaphone();
	private int minimumWordLengthForPhoneticTest;
	private int longWordLengthForPhoneticTest;
	private double levenshteinMinimalSimilarityPercentForLongWord;
	private double levenshteinMinimalSimilarityPercentForShortWord;
	private ITokenizer splitter = new ProximityDelimeterAwareTokenizer(new char[] {' '});

    
	public PhoneticAlgorithm(int minimumWordLengthForPhoneticTest, int longWordLengthForPhoneticTest, double levenshteinMinimalSimilarityPercentForShortWord, double levenshteinMinimalSimilarityPercentForLongWord) {
		super();
		this.minimumWordLengthForPhoneticTest = minimumWordLengthForPhoneticTest;
		this.longWordLengthForPhoneticTest = longWordLengthForPhoneticTest;
		this.levenshteinMinimalSimilarityPercentForShortWord = levenshteinMinimalSimilarityPercentForShortWord;
		this.levenshteinMinimalSimilarityPercentForLongWord = levenshteinMinimalSimilarityPercentForLongWord;
	    dm.setMaxCodeLen(100);
		
	}
	
	public boolean relate(RelationAlgorithmContext context, TechnicalDictionaryTerm term, String[] termParts) {
		boolean foundRelation = false;
		int supportedRelationsAlgs = term.getTermSource().getSupportedRelationsAlgs();
		if((supportedRelationsAlgs & PhoneticsRelationAlg) == PhoneticsRelationAlg) {
			
			String phoneticBaseTerm = "";
			boolean activatePhonetic = false;
			for (int i=0; i<termParts.length; i++) {
				if ( ((termParts[i]).length() >= minimumWordLengthForPhoneticTest) && ((termParts[i]).matches("[a-zA-Z]*") ) ) {
					phoneticBaseTerm += dm.doubleMetaphone(termParts[i]);
					activatePhonetic = true;
				}
				else {
					phoneticBaseTerm += termParts[i];
				}
			}
			if (activatePhonetic) {
                if (phoneticSimilarityTerms.containsKey(phoneticBaseTerm)) {
                	String[] potentialSimilarTermParts = splitter.split((phoneticSimilarityTerms.get(phoneticBaseTerm)).getTermText());
            		boolean similar = false;
                	if (termParts.length == potentialSimilarTermParts.length) {
                		similar = true;
                		for (int i=0; i < termParts.length; i++) {
                			if ( termParts[i].length() >= minimumWordLengthForPhoneticTest ) {
                				double levenshteinMinimalSimilarityPercent;
                				if (termParts[i].length() >= longWordLengthForPhoneticTest)
                					levenshteinMinimalSimilarityPercent = levenshteinMinimalSimilarityPercentForLongWord;
                				else
                					levenshteinMinimalSimilarityPercent = levenshteinMinimalSimilarityPercentForShortWord;
                					
                				if (Levenshtein(termParts[i],potentialSimilarTermParts[i]) < levenshteinMinimalSimilarityPercent) {
                					similar = false;
                					break;
                				}
                			}
                			else {
                				if ( !( (termParts[i]).equalsIgnoreCase(potentialSimilarTermParts[i]) ) ) {
                					similar = false;
                					break;
                				}
                			}
                		}
                	}
                    if (similar) {
                    	context.relation.relateWithContaining(term, phoneticSimilarityTerms.get(phoneticBaseTerm),"Phonetics");
                    	foundRelation = true;
                    }
                }
                else {
                      phoneticSimilarityTerms.put(phoneticBaseTerm, term);
                }
			}
		}
		return foundRelation;
	}
	public void finish(RelationAlgorithmContext context) {
	}

    public double Levenshtein(final String s1, final String s2)
    {
        double retval = 0.0;
        final int n = s1.length();
        final int m = s2.length();
        if (0 == n)
        {
            retval = m;
        }
        else if (0 == m)
        {
            retval = n;
        }
        else
        {
            retval = 1.0 - (compare(s1, n, s2, m) / (Math.max(n, m)));
        }
        return retval;
    }

    private double compare(final String s1, final int n, 
                           final String s2, final int m)
    {
        int matrix[][] = new int[n + 1][m + 1];
        for (int i = 0; i <= n; i++)
        {
            matrix[i][0] = i;
        }
        for (int i = 0; i <= m; i++)
        {
            matrix[0][i] = i;
        }

        for (int i = 1; i <= n; i++)
        {
            int s1i = s1.codePointAt(i - 1);
            for (int j = 1; j <= m; j++)
            {
                int s2j = s2.codePointAt(j - 1);
                final int cost = s1i == s2j ? 0 : 1;
                matrix[i][j] = min3(matrix[i - 1][j] + 1, 
                                    matrix[i][j - 1] + 1, 
                                    matrix[i - 1][j - 1] + cost);
            }
        }
        return matrix[n][m];
    }

    private int min3(final int a, final int b, final int c)
    {
        return Math.min(Math.min(a, b), c);
    }


	
}
