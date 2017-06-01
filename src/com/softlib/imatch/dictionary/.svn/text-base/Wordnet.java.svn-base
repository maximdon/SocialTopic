package com.softlib.imatch.dictionary;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.common.LogUtils;

import edu.northwestern.at.utils.corpuslinguistics.lemmatizer.DefaultLemmatizer;
import edu.northwestern.at.utils.corpuslinguistics.lemmatizer.Lemmatizer;

public class Wordnet {
	
	private final static String sql = "SELECT wordid FROM word WHERE lemma in (?, ?, ?)";

	private static Logger log = Logger.getLogger(Wordnet.class);
	private static Wordnet instance = null;
	private static Object lock = new Object();
	private Connection connection;
	private boolean isDisabled = false;
	private Lemmatizer lemmatizer = null;
	private Map<String, String> lemmaExceptions = new HashMap<String, String>();
	
	private final static Set<String> stopWords = new HashSet<String>(Arrays.asList(new String[] {"a", "about", "above", "across", "after", "afterwards", "again", "against", "all", "almost", "alone", "along", "already", "also","although","always","am","among", "amongst", "amoungst", "amount",  "an", "and", "another", "any","anyhow","anyone","anything","anyway", "anywhere", "are", "around", "as",  "at", 
			   "back","be","became", "because","become","becomes", "becoming", "been", "before", "beforehand", "behind", "being", "below", "beside", "besides", "between", "beyond", "bill", "both", "bottom","but", "by", 
			   "call", "can", "cannot", "cant", "co", "con", "could", "couldnt", "cry", 
			   "de", "describe", "do", "did", "does", "does not", "done", "down", "due", "during", 
			   "each", "eg", "eight", "either", "else", "elsewhere", "enough", "etc", "even", "ever", "every", "everyone", "everything", "everywhere", 
			   "for", "from", "get", 
			   "give", "go", "got", 
			   "had", "has", "hasnt", "have", "he", "hence", "her", "here", "hereafter", "hereby", "herein", "hereupon", "hers", "herself", "him", "himself", "his", "how", "however", 
			   "i", "i.e.", "if", "in", "inc", "indeed", "into", "is", "it", "its", "itself", 
			   "latter", "latterly", "least", "ltd",
			   "made", "many", "may", "me", "meanwhile", "might", "mill", "mine", "more", "moreover", "most", "mostly", "much", "must", "my", "myself",
			   "namely", "neither", "never", "nevertheless", "no", "nobody", "none", "noone", "nor", "not", "nothing", "now", "nowhere",
			   "of", "off", "often", "on", "once", "one", "only", "onto", "or", "other", "others", "otherwise", "our", "ours", "ourselves", "out", "over", 
			   "per", "perhaps", "please", "put", 
			   "rather", "re", 
			   "same", "see", "seem", "seemed", "seeming", "seems", "serious", "several", "she", "should", "show", "side", "since", "sincere", "six", "sixty", "so", "some", "somehow", "someone", "something", "sometime", "sometimes", "somewhere", "still", "such", 
			   "than", "that", "the", "their", "them", "themselves", "then", "thence", "there", "thereafter", "thereby", "therefore", "therein", "thereupon", "these", "they", "thickv", "thin", "this", "those", "though", "through", "throughout", "thru", "thus", "to", "together", "too", "top", "toward", "towards", 
			   "un", "under", "until", "up", "upon", "us", 
			   "very", "via", 
			   "was", "we", "well", "were", "what", "whatever", "when", "whence", "whenever", "where", "whereafter", "whereas", "whereby", "wherein", "whereupon", "wherever", "whether", "which", "while", "whither", "who", "whoever", "whole", "whom", "whose", "why", "will", "with", "within", "without", "would", 
			   "yet", "you", "your", "yours", "yourself", "yourselves"}));
	
	private Wordnet() 
	{
		try { 
			this.connection = DriverManager.getConnection("jdbc:sqlite:" + RuntimeInfo.getCurrentInfo().getRealPath("/wordnet30.sqlite"));
			lemmatizer = new DefaultLemmatizer();
		}
		catch(Exception e) {
			LogUtils.error(log, "Unable to open sql connection to wordnet, the check will be disabled");
			disableCheck();
		}
		lemmaExceptions.put("status", "status");
		lemmaExceptions.put("data", "data");
	}
	
	public static Wordnet getInstance() {
		synchronized (lock) {
			if(instance == null) {
				instance = new Wordnet();
			}			
		}
		return instance;
	}
	
	public void disableCheck() {
		isDisabled  = true;
	}
	
	public WordnetWord findWord(String word, String pos)
	{	
		if(isDisabled)
			return null;
		word = word.trim().toLowerCase();
		word = TechnicalDictionaryKey.cleanText(word);
		if (isStopWord(word))
			return new WordnetWord(-1, word, word);
		String lemma = null;
		if(lemmaExceptions.containsKey(word))
			lemma = lemmaExceptions.get(word);
		else if(lemmatizer != null){
			if(pos != null)
				try {
					lemma = lemmatizer.lemmatize(word, toLemmatizerPOSClass(pos));
				}
				catch(Exception e) {
					LogUtils.warn(log, "Unable to lemmatize word %s", word);
				}
			else
				try {
					lemma = lemmatizer.lemmatize(word);
				}
				catch (Exception e) {
					LogUtils.warn(log, "Unable to lemmatize word %s", word);				
				}
		}
		if(lemma == null || lemma.equals("*"))
			lemma = word;		
		
		PreparedStatement stmt = null;
		ResultSet rs;
		int wordid = -1;
		synchronized (this) {
			try {
				stmt = connection.prepareStatement(sql); 
				stmt.setString(1, lemma);
				stmt.setString(2, word);
				stmt.setString(3, PorterStemmer.stem(word));
				
				rs = stmt.executeQuery();
				if(rs.next())
					wordid = rs.getInt(1);
			} catch (SQLException e) {
				LogUtils.error(log, "Unable to query wordnet, reason %s", e.getMessage());
				return null;
			}
			finally {
				if(stmt != null)
					try {
						stmt.close();
					} catch (SQLException e) {
						//Do nothing
					}
			}
		}
		if(wordid == -1)
			return null;
		else
			return new WordnetWord(wordid, word, lemma);
	}
	
	private String toLemmatizerPOSClass(String pos) {
		if(pos.startsWith("NN"))
			return "noun";
		else if(pos.startsWith("VB"))
			return "verb";
		else if(pos.startsWith("JJ"))
			return "adjective";
		//UNKNOWN pos, use default
		return null;
	}

	public boolean containsWord(String word) 
	{	
		return findWord(word, null) != null;
	}
	
	public boolean isStopWord(String word)
	{
		return stopWords.contains(word.trim().toLowerCase());
	}
}
