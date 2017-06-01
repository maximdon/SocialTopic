package com.softlib.taxonomytool.engine;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.softlib.imatch.ITicket;
import com.softlib.imatch.ITicketProvider;
import com.softlib.imatch.ITicketRetrievedCallback;
import com.softlib.imatch.MatcherException;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.TicketProviderFactory;
import com.softlib.imatch.common.configuration.IConfigurationObject;
import com.softlib.imatch.common.configuration.IConfigurationResource;
import com.softlib.imatch.common.configuration.IConfigurationResourceLoader;
import com.softlib.imatch.matcher.TicketingSystemIntegrationConfig;
import com.softlib.imatch.nlp.NLP;
import com.softlib.imatch.ticketprocessing.IProcessedTicket.MatchMode;
import com.softlib.taxonomytool.model.Corpus;

public class Engine implements ITicketRetrievedCallback {
	private NLP nlp = new NLP();
	private Corpus corpus = new Corpus();
	private Pattern pattern = Pattern.compile("^[\\w-]+$");
	private String objectId;
	private String targetDatabaseConnectionString;
	
	ITicketProvider ticketProvider;
	static public Connection targetDatabaseConnection = null;
	static public PreparedStatement insertWord;
	static public PreparedStatement insertWordInstance;
	static public PreparedStatement selectWordByStem;
	Connection conn = null;
	int ticketCounters = 0;
	
	public String getObjectId() {
		return objectId;
	}
	
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	
	public Engine(String targetDatabaseConnectionString)
	{
		this.targetDatabaseConnectionString = targetDatabaseConnectionString;
	}
	
	public void start()
	{	
		try {
			//String id = null;
			//String lastID = null;
			//StringBuilder data = new StringBuilder();
			
			ticketProvider = (ITicketProvider) RuntimeInfo.getCurrentInfo().getBean(TicketProviderFactory.getProviderId(this.objectId));
			
			
			//Class.forName("org.sqlite.JDBC");
			//Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			//sqliteConn = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\Yaniv\\Workspaces\\MyEclipse 8.6\\TaxonomyTool\\frequencies.db");
			targetDatabaseConnection = DriverManager.getConnection(this.targetDatabaseConnectionString);
			//Statement sqliteStmt = sqliteConn.createStatement();
			Engine.targetDatabaseConnection.setAutoCommit(false);
			insertWord = targetDatabaseConnection.prepareStatement("insert into Words (Word, StemmedWord, WordLevel) values (?, ?, ?);", Statement.RETURN_GENERATED_KEYS);
			insertWordInstance = targetDatabaseConnection.prepareStatement("insert into WordInstances (TicketID, SentenceIndex, StartWordIndexInSentence, EndWordIndexInSentence, StartWordIndexInTicket , EndWordIndexInTicket, POS, WordID) values (?, ?, ?, ?, ?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS);
			selectWordByStem =  targetDatabaseConnection.prepareStatement("select id from words where StemmedWord = ?");
			//System.out.println(String.format("running in %s mode", SQLiteJDBCLoader.isNativeMode() ? "native" : "pure-java"));
			
			ticketProvider.getAllTickets(this);
			//conn = DriverManager.getConnection("jdbc:sqlserver://192.168.10.25;databaseName=AttunityEResolve;user=sa;password=Softlib_2;");
			//Statement stmt = conn.createStatement();
						 
			//String qry = "select iss.IssueID, Subject, Detail, Solution, act.ActDescription as ActDesc from Issues iss Left Outer Join Activities act ON iss.IssueID = act.IssueID Where iss.IssueID > 11000 and Status not like '%Invalid/Dup%' ORDER BY iss.IssueID DESC";
			//String qry = "select iss.IssueID, Subject, Detail, Solution, act.ActDescription as ActDesc from Issues iss Left Outer Join Activities act ON iss.IssueID = act.IssueID Where Status not like '%Invalid/Dup%' ORDER BY iss.IssueID DESC";
			//ResultSet rs = stmt.executeQuery(qry);
			/*
			
			while (rs.next()) {
				id = rs.getString("IssueID");
				System.out.println("IssueID " + rs.getString("IssueID"));
				
				if (!id.equals(lastID))
				{
					
					if (data.length() > 0)
					{
						//System.out.println(data.toString());
						this.process(lastID, data.toString());
					}
					
					lastID = id;
					
					data.delete(0, data.length());
					data.append(rs.getString("Subject"));
					data.append("\n\n");
					data.append(rs.getString("Detail"));
					data.append("\n\n");
					data.append(rs.getString("Solution"));
					
					ticketCounters++;
					
					if (ticketCounters % 1000 == 0)
						sqlServerConn.commit();
				}
				
				data.append(rs.getString("ActDesc"));
			}
			*/
			//this.process(id, data.toString());
			
			targetDatabaseConnection.commit();
			
			System.out.print("DONE.");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MatcherException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			if (conn != null)
			{
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if (targetDatabaseConnection != null) {
				try {
					targetDatabaseConnection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}			
		}
	}
	
	public void process(String ticketID, String text)
	{
		int sentenceIndex = 0;
		int wordIndexInTicket = 0;
		String[] sentences = nlp.extractSentences(text);
		
		for (String sentence : sentences) {
			String[] tokens = nlp.tokenize(sentence);
			String[] tags = nlp.tag(sentence);
			
			for (int wordIndexInSentence = 0; wordIndexInSentence < tags.length; wordIndexInSentence++) {
				Matcher matcher = pattern.matcher(tokens[wordIndexInSentence]);
				if (matcher.matches())
				{
					corpus.addWord(tokens[wordIndexInSentence].toLowerCase(), tags[wordIndexInSentence], ticketID, sentenceIndex, wordIndexInSentence, wordIndexInTicket);
				}
				
				wordIndexInTicket++;
			}			
			
			sentenceIndex++;			
		}
		
		try {
			insertWordInstance.executeBatch();
			
			ticketCounters++;
			
			if (ticketCounters % 1000 == 0)
				targetDatabaseConnection.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void ticketRetrieved(ITicket ticket) {
		process(ticket.getId(), ticket.getBody(MatchMode.all));		
	}
}
