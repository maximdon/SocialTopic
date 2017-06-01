package com.softlib.imatch.ticketprocessing;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;

import com.softlib.imatch.InMemoryTicket;
import com.softlib.imatch.MatcherException;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.StageMngr;
import com.softlib.imatch.StageMngr.Stage;
import com.softlib.imatch.common.HibernateUtils;
import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.common.Pair;
import com.softlib.imatch.common.SessionMode;
import com.softlib.imatch.common.configuration.IConfigurationObject;
import com.softlib.imatch.common.configuration.IConfigurationResource;
import com.softlib.imatch.common.configuration.IConfigurationResourceLoader;
import com.softlib.imatch.dictionary.ITechnicalDictionary;
import com.softlib.imatch.dictionary.TechnicalDictionary;
import com.softlib.imatch.dictionary.TechnicalDictionaryKey;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.dictionary.TechnicalTermSource;
import com.softlib.imatch.matcher.SearcherConfiguration;
import com.softlib.imatch.matcher.TicketingSystemFieldMapping;
import com.softlib.imatch.matcher.TicketingSystemIntegrationConfig;
import com.softlib.imatch.ticketprocessing.IProcessedTicket.MatchMode;

public class ProcessedTicketDBase {

	private final static Logger log = Logger.getLogger(ProcessedTicketDBase.class);

	public final static String TABLE = "PROCESSED_TICKETS";
	public final static String NUMERIC_ID_FIELD = "numeric_ticket_id";
	public final static String ID_FIELD = "ticket_id";

	static private Connection conn = null;
	static private Session session = null;

	@SuppressWarnings("deprecation")
	static public void createTable() {
		try {
			Session session = RuntimeInfo.getCurrentInfo().getHibernate().acquireSession(SessionMode.READ_WRITE);
			Connection conn = session.connection();
			create(conn);
			RuntimeInfo.getCurrentInfo().getHibernate().releaseSession(session);
		}
		catch (Exception e) {
			LogUtils.error(log, "createTable() %s", e.getMessage());
			e.printStackTrace();
		}		
	}
	
	@SuppressWarnings("deprecation")
	static public boolean startSession() {		
		try {
			session = RuntimeInfo.getCurrentInfo().getHibernate().acquireSession(SessionMode.READ_WRITE);
			conn = session.connection();
			conn.setAutoCommit(false);
		}
		catch (Exception e) {
			LogUtils.error(log, "startSession() %s", e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	static public boolean endSession() {
		if (conn==null || session==null) {
			LogUtils.error(log, "endSession(not ready)");
			return false;
		}
		try {
			conn.commit();
			conn.setAutoCommit(true);
			RuntimeInfo.getCurrentInfo().getHibernate().releaseSession(session);
			conn = null;
			session = null;
		}
		catch (Exception e) {
			LogUtils.error(log, "endSession() %s", e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	static public boolean write(String id,List<Pair<String,String>> values) {
		if (conn==null || session==null) {
			LogUtils.error(log, "write(not ready)");
			return false;
		}
		String sql = getInsertSQL(id,values);
		try {
			conn.createStatement().executeUpdate(sql);
		} catch (SQLException ie) {
			try {
				if (!values.isEmpty()) {
					sql = getUpdateSQL(id,values);
					conn.createStatement().executeUpdate(sql);
				}
			} catch (SQLException ue) {
				LogUtils.error(log, "endSession() %s", ue.getMessage());
				ue.printStackTrace();
				return false;
			}
		}
		return true;
	}

	static private String getValue(String str) {
		String value = str.replaceAll("'", "''");
		return "'" + value + "'";
	}
	
	static private long toNumericId(String id) {
		BigInteger result;
		try {
			result = new BigInteger(id);
		}
		catch(NumberFormatException ne) {
			result = new BigInteger(id.getBytes());
		}
		return result.longValue();
	}

	//	UPDATE Table SET column1=value,...
	static private String getUpdateSQL(String id,List<Pair<String,String>> values) {
		String rc = "update " + TABLE + " set ";

		boolean first = true;
		for (Pair<String,String> value : values) {
			if (first)
				first = false;
			else
				rc = rc + ",";
			rc = rc + value.getLeft() + "=" + 
					  getValue(value.getRight());
		}

		rc = rc + " where " + NUMERIC_ID_FIELD + "=" + toNumericId(id);
		return rc;
	}

	//	INSERT INTO Table (column1,...) VALUES (value1,...)
	static private String getInsertSQL(String id,List<Pair<String,String>> values) {
		String headerStr = "insert into " + TABLE + " ";
		String columnStr = NUMERIC_ID_FIELD + "," + ID_FIELD;
		String valuesStr = toNumericId(id) + "," + getValue(id);

		for (Pair<String,String> value : values) {
			columnStr = columnStr + "," + value.getLeft();
			valuesStr = valuesStr + "," + getValue(value.getRight());
		}

		return headerStr +"("+ columnStr + ") values (" + valuesStr +")";
	}
	
	static private void create(Connection conn) {
		String sql = getCreateSQL();
		try {
			conn.createStatement().executeUpdate(sql);
		} catch (SQLException ie) {
			LogUtils.error(log, "create() %s", ie.getMessage());
			ie.printStackTrace();
		}
	}

	static private String getCreateSQL() {
		String headerStr = "create table " + TABLE + " ";
		String columnsStr = " ["+NUMERIC_ID_FIELD+"] NUMERIC PRIMARY KEY NULL, [" + ID_FIELD + "] NVARCHAR(300) NULL";
		List<String> stageFieldNames = new ArrayList<String>();

		for (StageMngr.Stage stage : StageMngr.Stage.values())
			stageFieldNames.add(stage.name());
		
		for (String objectId : getObjectIDs()) {
			for(String fieldName : getFieldsNames(objectId)) {
				stageFieldNames.add(ProcessedTicketWrite.generateFieldName(Stage.Extract, objectId, fieldName));
				stageFieldNames.add(ProcessedTicketWrite.generateFieldName(Stage.PostExtract, objectId, fieldName));
			}
		}
		for (String fieldName : stageFieldNames)
			columnsStr = columnsStr + ", [" +fieldName+"] NVARCHAR(2000) NULL ";

		return headerStr + "(" + columnsStr + ")";
	}

	static private Set<String> getObjectIDs() {
		IConfigurationResourceLoader loader = (IConfigurationResourceLoader)RuntimeInfo.getCurrentInfo().getBean("xmlConfigurationResourceLoader");
		IConfigurationResource resource = loader.loadResource("xml:///{SolutionConfigFolder}/matcher.xml;//searcher");
		IConfigurationObject config = resource.getConfigurationObject(SearcherConfiguration.class);
		Set<String> objectIdList = config.getAllObjects();
		return objectIdList;
	}

	static private List<String> getFieldsNames(String objectId) {
		IConfigurationResourceLoader loader = (IConfigurationResourceLoader)RuntimeInfo.getCurrentInfo().getBean("xmlConfigurationResourceLoader");
		IConfigurationResource resource = loader.loadResource("xml:///{SolutionConfigFolder}/matcher.xml;//ticketingSystemIntegration");
		IConfigurationObject config = resource.getConfigurationObject(TicketingSystemIntegrationConfig.class);

		TicketingSystemIntegrationConfig ticketingSystemConfig = 
			(TicketingSystemIntegrationConfig)config.getUnderlinedObject(objectId);		

		List<String> bodyFieldName = ticketingSystemConfig.getBodyFields().getBodyFields(MatchMode.all);
		TicketingSystemFieldMapping ticketingSystemFieldMapping = ticketingSystemConfig.getFieldMapping();
		String titleFieldName = ticketingSystemFieldMapping.getTitleFieldName();

		List<String> rc = new ArrayList<String>();
		rc.addAll(bodyFieldName);
		rc.add(titleFieldName);

		return rc;
	}
	
	
	static private Pair<String,String> getPair(String eField,String pField) {
		if (eField==null)
			eField = "";
		if (pField==null)
			pField = "";
		return new Pair<String,String>(eField,pField);
	}
	
	static public Map<String,Pair<String,String>> getResultMap(String objectId, String ticketId,Set<String> fieldsNames)  {
		Map<String,Pair<String,String>> rc = new HashMap<String,Pair<String,String>>();
		ResultSet result = null; 
		try {
			result = getResult(ticketId);
			if (result!=null && result.next()) {
				long id = result.getLong(NUMERIC_ID_FIELD);
				if (id == toNumericId(ticketId)) {
					for (String fieldsName : fieldsNames) {
						String eField = result.getString(ProcessedTicketWrite.generateFieldName(Stage.Extract, objectId, fieldsName));
						String pField = result.getString(ProcessedTicketWrite.generateFieldName(Stage.PostExtract, objectId, fieldsName));
						rc.put(fieldsName,getPair(eField,pField));
					}
				}
			}
			else
				if(StageMngr.instance().isStage(Stage.Index))
					LogUtils.warn(log, "No extraction information for ticket %s", ticketId);
		}
		catch (Exception e) {
			LogUtils.error(log, "Unexpected error during retrieving ticket map for id %s, internal error is %s", ticketId, e.getMessage());
		}
		
		getResultEnd(result);
		
		return rc;
	}

	
	static public String getField(String ticketId,String fieldName)  {
		ResultSet result = null;
		String stageValue = "";
		try {
			result = getResult(ticketId);
			if (result!=null && result.next()) {
				long id = result.getLong(NUMERIC_ID_FIELD);
				if (id == toNumericId(ticketId))
					stageValue = result.getString(fieldName);
			}
			else
				if(StageMngr.instance().isStage(Stage.Index))
					LogUtils.warn(log, "No extraction information for ticket %s", ticketId);
		}
		catch (Exception e) {			
			LogUtils.error(log, "Unexpected error during retrieving field %s for id %s, internal error is %s", fieldName, ticketId, e.getMessage());
		}
		
		getResultEnd(result);
		
		return stageValue;
	}

	@SuppressWarnings("deprecation")
	static private ResultSet getResult(String ticketId) throws HibernateException, SQLException  {
		String sql = "select * from " + TABLE + " where " + NUMERIC_ID_FIELD + "=?";

		ResultSet rs=null;
		session = RuntimeInfo.getCurrentInfo().getHibernate().acquireSession(SessionMode.READONLY);
		PreparedStatement stmt = session.connection().prepareStatement(sql);
		stmt.setLong(1, toNumericId(ticketId));
		rs = stmt.executeQuery(); 
			
		return rs;
	}

	static private void getResultEnd(ResultSet rs)  {
		if(rs != null) {
			try {				
				rs.getStatement().close();
			} 
			catch (SQLException e) {
				LogUtils.error(log, "getResultEnd() %s", e.getMessage());
			}
		}
		RuntimeInfo.getCurrentInfo().getHibernate().releaseSession(session);
		session = null;
	}
	
	/**
	 * Reconstructs all processed tickets based on PROCESSED_TICKETS information.
	 * Uses the given dictionary to verify the saved term is still valid
	 * @param dictionary
	 * @return
	 * @throws MatcherException
	 */
	static public List<IProcessedTicket> getAllTickets(final TechnicalDictionary dictionary) throws MatcherException
	{		
		session = RuntimeInfo.getCurrentInfo().getHibernate().acquireSession(SessionMode.READONLY);
		final List<IProcessedTicket> result = new ArrayList<IProcessedTicket>();
		try {
			session.doWork(new Work() {				
				@Override
				public void execute(Connection connection) throws SQLException {
					String sql = "SELECT * FROM " + TABLE;
					Statement stmt = null;
					ResultSet rs = null;
					try { 
						stmt = connection.createStatement();
						rs = stmt.executeQuery(sql);
						List<String> fieldNames = getStageFieldsNames();
						IScoreCalculator scoreCalculator = (IScoreCalculator) RuntimeInfo.getCurrentInfo().getBean("scoreCalculator");
						ResultSetMetaData meta = rs.getMetaData();
						int numCol = meta.getColumnCount();
						List<String> retrievedColumns = new ArrayList<String>();
						for (int i = 1; i < numCol + 1; i++) {
							retrievedColumns.add(meta.getColumnName(i));
						}
						while(rs.next()) {
							String ticketId = rs.getString(ID_FIELD);
							InMemoryTicket baseTicket = null;
							ProcessedTicket ticket = null;
							for(String fieldName : fieldNames) {
								if(!ProcessedTicketWrite.isDataField(fieldName) || !retrievedColumns.contains(fieldName))						
									//Skip all special fields
									continue;
								String fieldTermsStr = rs.getString(fieldName);
								if(fieldTermsStr == null)
									continue;
								//Each field name consists of 3 parts : stage, objectId, original field name
								String[] fieldNameParts = ProcessedTicketWrite.parseFieldName(fieldName);
								if(baseTicket == null) {
									baseTicket = new InMemoryTicket(fieldNameParts[1], ticketId, "", "");
									ticket = new ProcessedTicket(baseTicket, scoreCalculator);
								}
								ticket.startSession(fieldNameParts[2], ticketId, TechnicalTermSource.NLP_VERSION_SOURCE_NAME);
								List<String> fieldTerms = ProcessedTicketWrite.parseField(fieldTermsStr);
								for(String fieldTermStr : fieldTerms) {
									TechnicalDictionaryTerm term = dictionary.get(new TechnicalDictionaryKey(fieldTermStr, false));
									if(term != null)
										ticket.addTerm(term.getTermKey());
								}
								ticket.endSession(0, null, false);
							}
							if(ticket != null)
								result.add(ticket);
						}
					}
					finally {
						if(stmt != null)
							try {
								stmt.close();
							} catch (SQLException e1) {
								//Do nothing
							}
					}					
				}
			});
		}
		catch(Exception e) {
			throw new MatcherException("Unable to retrieve processed tickets, underline error " + e.getMessage(), e);
		}
		finally {
			RuntimeInfo.getCurrentInfo().getHibernate().releaseSession(session);
		}
		return result;
	}
	
	private static List<String> getStageFieldsNames() {
		List<String> stageFieldNames = new ArrayList<String>();

		for (StageMngr.Stage stage : StageMngr.Stage.values())
			stageFieldNames.add(stage.name());
		
		for (String objectId : getObjectIDs()) {
			for(String fieldName : getFieldsNames(objectId)) {
				stageFieldNames.add(ProcessedTicketWrite.generateFieldName(Stage.Extract, objectId, fieldName));
				stageFieldNames.add(ProcessedTicketWrite.generateFieldName(Stage.PostExtract, objectId, fieldName));
			}
		}
		return stageFieldNames;
	}
};
