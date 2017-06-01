package com.softlib.taxonomytool.model;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.softlib.taxonomytool.engine.Engine;
import com.softlib.taxonomytool.tools.PorterStemmer;

public class Corpus {
	private long wordsCount;
	private TObjectIntMap<String> words = new TObjectIntHashMap<String>();
	//private Map<String, Word> words = new HashMap<String, Word>(100000);
	
	public long getWordsCount() {
		return wordsCount;
	}
	public void setWordsCount(long wordsCount) {
		this.wordsCount = wordsCount;
	}
		
	public void addWord(String token, String partOfSpeech, String ticketID, int sentenceIndex, int wordIndexInSentence, int wordIndexInTicket)
	{
		try {
			String stemmedToken = PorterStemmer.stem(token);
			int wordId = 0;
			Word word;
			
			/*
			Engine.selectWordByStem.setString(1, stemmedToken);
			ResultSet rs = Engine.selectWordByStem.executeQuery();
			
			while (rs.next()) {
				wordId = rs.getInt("id");
				break;
			}
			*/
			
			if (words.containsKey(stemmedToken))
				wordId = words.get(stemmedToken); 
			
			if (wordId == 0)
			{			
				Engine.insertWord.setString(1, token);			
				Engine.insertWord.setString(2, stemmedToken);
				Engine.insertWord.setInt(3, 1);
				Engine.insertWord.addBatch();
				
				Engine.insertWord.executeUpdate();
			    //Engine.sqliteConn.setAutoCommit(true);
			    
			    ResultSet resultSet = Engine.insertWord.getGeneratedKeys();
			    
			    while (resultSet.next()) {
			    	wordId = resultSet.getInt(1);
			    	break;					
				}
			    
				word = new Word(token, stemmedToken);
			    word.setWordID(resultSet.getInt(1));
			    words.put(stemmedToken, wordId);
			}
			//else
				//wordId = words.get(stemmedToken);

			Engine.insertWordInstance.setInt(1, Integer.parseInt(ticketID));
			Engine.insertWordInstance.setInt(2, sentenceIndex);
			Engine.insertWordInstance.setInt(3, wordIndexInSentence);
			Engine.insertWordInstance.setInt(4, wordIndexInSentence);
			Engine.insertWordInstance.setInt(5, wordIndexInTicket);
			Engine.insertWordInstance.setInt(6, wordIndexInTicket);
			Engine.insertWordInstance.setString(7, partOfSpeech);
			Engine.insertWordInstance.setInt(8, wordId);
			Engine.insertWordInstance.addBatch();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//wordsCount++;	
		//word.addTicket(ticketID, sentenceIndex, wordIndexInSentence, wordIndexInTicket);
	}
}
