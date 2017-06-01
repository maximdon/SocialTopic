package com.softlib.imatch.dbintegration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool.Config;
import org.apache.log4j.Logger;

import com.softlib.imatch.ITicket;
import com.softlib.imatch.ITicketRetrievedCallback;
import com.softlib.imatch.MatcherException;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.common.cache.ICacheManager;
import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.connectors.BaseTicketProvider;
import com.softlib.imatch.enums.MatchErrorCodes;
import com.softlib.imatch.matcher.Statement;
import com.softlib.imatch.matcher.TicketingSystemIntegrationConfig;

/**
 * This class is responsible for retrieving ticket data from the underlined
 * ticketing system when the integration type is Database.
 * 
 * @author Maxim Donde
 * 
 */
public class DBTicketProvider extends BaseTicketProvider {
	private DataSource ticketingSystemDS;
	private ICacheManager<ITicket> cache;
	private static Object lock = new Object();
	private final static Logger log = Logger.getLogger(DBTicketProvider.class);

	public DBTicketProvider(String objectId, TicketingSystemIntegrationConfig config) {
		super(objectId, config);
		// Just to load all jdbc drivers
		// Note, the DBTicketProvider itself is not Spring bean, so we can
		// invoke getBean in constructor
		RuntimeInfo.getCurrentInfo().getBean("jdbcDriverLoader");
		// Create poolable datasource using apache-commons pooling.
		// Note, we don't use standard apache tomcat pooling since this pool is
		// based on JNDI configuration which is not suitable for us
		// Also note, apache tomcat internally uses the same apache-commons
		// pooling mechanism as we use there
		
		String connectionStr = config.getDbSettings().getParams().get("connectionString");
		ticketingSystemDS = setupDataSource(connectionStr);
	}

	/**
	 * Returns the ticket with given id
	 * 
	 * @throws MatcherException
	 */
	public ITicket get(String id) throws MatcherException {
		//We can't use cache for tickets to find since their SQL is different from tickets to display
		//We prefer to cache tickets for display
		//TODO think here, probably we need two caches for tickets for display and for tickets for match
		if(!validateTicketId(id))
			throw new MatcherException("Invalid ticket id supplied", MatchErrorCodes.InvalidId);

		return get(id, config.getSingleTicketStatement(), false);
	}

	/**
	 * Returns the ticket with given id
	 * 
	 * @throws MatcherException
	 */
	public ITicket getForDisplay(String id) throws MatcherException {
		Statement displayStmt = config.getDisplayTicketStatement();
		if (displayStmt == null)
			return get(id);
		return get(id, displayStmt, true);
	}

	/**
	 * Returns the ticket with given id
	 * 
	 * @throws MatcherException
	 */
	@SuppressWarnings("unchecked")
	private ITicket get(String id, Statement statement, boolean useCache) throws MatcherException {
		synchronized (this) {
			if (cache == null)
				cache = (ICacheManager<ITicket>) RuntimeInfo
						.getCurrentInfo().getBean("cacheManager");
		}
		ITicket ticket = null;
		if(useCache) {
			ticket = cache.get(id,objectId);
			if (ticket != null) {
				LogUtils.debug(log, "Return cached instance for ticket %s", id);
				return ticket;
			}
		}
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = ticketingSystemDS.getConnection();
			List<Object> statementParams = new ArrayList<Object>();
			if (id != null)
				statementParams.add(id);
			ps = buildDBStatement(conn, statement, statementParams, false);
			rs = ps.executeQuery();
			GetCallback callback = new GetCallback();
			if (statement.getConcatFields().length <= 0)
				processDBStatement(rs, statement.getCompleter(), callback);
			else
				processDBStatementConcatMode(rs, statement.getCompleter(), statement.getConcatFields(), callback);
			ticket = callback.getTicket();
			if(ticket != null) {
				if(useCache)
					cache.put(objectId, id, ticket);
			}
			else {
				LogUtils.warn(log, "Unable to find ticket %s", id);
			}
		} catch (SQLException e) {
			throw new MatcherException(
					"Unable to retrieve ticket data, reason " + e.getMessage(),
					e);
		}
		finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					// Do nothing
				}
			if (ps != null)
				try {
					ps.close();
				} catch (SQLException e) {
					// Do nothing
				}
			if (conn != null)
				try {
					conn.close();
				} catch (SQLException e) {
					// Do nothing
				}
		}
		return ticket;
	}

	/**
	 * Returns number of all eligible tickets
	 */
	public int getAllTicketsCount() throws MatcherException {
		Connection conn = null;
		PreparedStatement ps = null;
		int ticketsCount = -1;
		try {
			conn = ticketingSystemDS.getConnection();
			Statement statement = config.getAllTicketsCountStatement();
			List<Object> statementParams = new ArrayList<Object>();
			ps = buildDBStatement(conn, statement, statementParams, true);
			ticketsCount = executeSingleValueDBStatement(ps);
		} catch (SQLException e) {
			LogUtils.error(log, "Unable to retrieve tickets count %s", e
					.getMessage());
		} catch (Exception e) {
			LogUtils.error(log, "Unable to retrieve tickets count %s", e
					.getMessage());
		} finally {
			if (ps != null)
				try {
					ps.close();
				} catch (SQLException e) {
					// Do nothing
				}
			if (conn != null)
				try {
					conn.close();
				} catch (SQLException e) {
					// Do nothing
				}
		}

		return ticketsCount;
	}

	/**
	 * Returns all tickets in chunks of up to bufferSize.
	 * Remains the cursor open to allow additional calls for getAllTickets to retrieve additional tickets
	 */
	public void getAllTickets(ITicketRetrievedCallback callback) throws MatcherException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Statement statement = config.getAllTicketsStatement();
		synchronized (lock) {
			try {				
				conn = ticketingSystemDS.getConnection();
				List<Object> statementParams = new ArrayList<Object>();
				ps = buildDBStatement(conn, statement, statementParams, true);
				rs = ps.executeQuery();
				if (statement.getConcatFields().length <= 0)
					processDBStatement(rs, statement.getCompleter(), callback);
				else
					processDBStatementConcatMode(rs, statement
							.getCompleter(), statement.getConcatFields(), callback);	
			} catch (SQLException e) {
				throw new MatcherException("Unable to retrieve ticket data, reason " + e.getMessage(), e);
			} 
			finally {
				if (rs != null)
					try {
						rs.close();
					} catch (SQLException e) {
						// Do nothing
					}
					if (ps != null)
						try {
							ps.close();
						} catch (SQLException e) {
							// Do nothing
						}
					if (conn != null)
						try {
							conn.close();
						} catch (SQLException e) {
							// Do nothing
						}
				}
			}
	}

	/**
	 * Returns all tickets in chunks of up to bufferSize that was changed since lastRunTime.
	 * Remains the cursor open to allow additional calls for getChangedTickets to retrieve additional tickets
	 */
	public void getChangedTickets(long lastRunTime, ITicketRetrievedCallback callback)
			throws MatcherException {
		if(lastRunTime < 0) {
			//Special case, retrieve all tickets
			LogUtils.debug(log, "Retrieving all tickets");		
		    getAllTickets(callback);
		    return;
		}
		LogUtils.debug(log, "Retrieving changes since %s", lastRunTime);
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Statement statement = config.getChangedTicketsStatement();
		synchronized (lock) {
			try {
				conn = ticketingSystemDS.getConnection();
				List<Object> statementParams = new ArrayList<Object>();
				statementParams.add(lastRunTime);
				ps = buildDBStatement(conn, statement, statementParams, true);
				rs = ps.executeQuery();
				if (statement.getConcatFields().length <= 0)
					processDBStatement(rs, statement.getCompleter(), callback);
				else
					processDBStatementConcatMode(rs, statement
							.getCompleter(), statement.getConcatFields(), callback);
//				LogUtils.info(log, "%d tickets retrieved from the database",
//						tickets.size());
			} catch (SQLException e) {
				throw new MatcherException(
						"Unable to retrieve ticket data, reason "
								+ e.getMessage(), e);
			} 
			finally {
				if (rs != null)
					try {
						rs.close();
					} catch (SQLException e) {
						// Do nothing
					}
				if (ps != null)
					try {
						ps.close();
					} catch (SQLException e) {
						// Do nothing
					}
				if (conn != null)
					try {
						conn.close();
					} catch (SQLException e) {
						// Do nothing
					}
				}
			}
	}

	private void processDBStatement(ResultSet rs, DBTicketCompleter completer, ITicketRetrievedCallback callback)
		throws SQLException
	{
		ResultSetMetaData metadata = rs.getMetaData();
		int columnCount = metadata.getColumnCount();
		int recordCount = 0;
		while (rs.next()) {
			DBTicket ticket = new DBTicket(objectId);
			for (int i = 1; i <= columnCount; ++i) {
				String columnName = metadata.getColumnName(i);
				String columnType = metadata.getColumnTypeName(i);
				Object columnValue;
				if (columnType.equals("CLOB")) {
					columnValue = rs.getString(i);
				}
				else if (columnType.equals("TIMESTAMP")) {
					columnValue = rs.getDate(i);
				}
				else
					columnValue =  rs.getObject(i);
				ticket.setField(columnName, columnValue);
			}		
			completer.complete(ticket);
			callback.ticketRetrieved(ticket);
			if (recordCount % 100 == 0)
				LogUtils.debug(log, "%d tickets retrieved", recordCount);
			recordCount++;			
		}
		LogUtils.info(log, "Total %d tickets retrieved from the database", recordCount);
	}

	private void processDBStatementConcatMode(ResultSet rs, DBTicketCompleter completer, String[] concatFields, ITicketRetrievedCallback callback)
		throws SQLException
	{
		ResultSetMetaData metadata = rs.getMetaData();
		int columnCount = metadata.getColumnCount();
		DBTicket currentTicket = null;
		List<String> concatFieldsList = Arrays.asList(concatFields);
		int recordCount = 0;
		while (rs.next()) {
			String idFieldName = config.getFieldMapping().getIdFieldName();
			String id = rs.getString(idFieldName);
			if (currentTicket == null) {
				currentTicket = new DBTicket(objectId);
				recordCount++;
			} else {
				if (!(currentTicket.getId().equals(id))) {
					completer.complete(currentTicket);
					callback.ticketRetrieved(currentTicket);
					currentTicket = null;
					currentTicket = new DBTicket(objectId);
					recordCount++;
					if (recordCount % 100 == 0)
						LogUtils.debug(log, "%d tickets retrieved", recordCount);
				}
			}
			for (int i = 1; i <= columnCount; ++i) {
				String columnName = metadata.getColumnName(i);
				String columnType = metadata.getColumnTypeName(i);
				Object columnValue;
				if (columnType.equals("CLOB")) {
					columnValue = rs.getString(i);
				}
				else if (columnType.equals("TIMESTAMP")) {
					columnValue = rs.getDate(i);
				}
				else
					columnValue =  rs.getObject(i);
				if (concatFieldsList.contains(columnName)) {
					Object currValObj = currentTicket.getField(columnName);
					if (currValObj != null) {
						String currVal = currValObj.toString();
						currentTicket.setField(columnName, concatField(currVal, columnValue));
					} else
						currentTicket.setField(columnName, columnValue);
				} else
					currentTicket.setField(columnName, columnValue);
			}
		}
		// Add the last ticket
		if(currentTicket != null) {
			completer.complete(currentTicket);
			callback.ticketRetrieved(currentTicket);
		}
	}
	
	private int executeSingleValueDBStatement(PreparedStatement ps)
	throws SQLException {
		ResultSet rs = ps.executeQuery();
		int ticketsCount = 0;

		while (rs.next()) {
			ticketsCount = rs.getInt(1);
		}

		rs.close();

		return ticketsCount;
	}

	private PreparedStatement buildDBStatement(Connection conn,
			Statement statement, List<Object> statementParams, boolean isLogInfo)
			throws SQLException {
		String statementStr;
		if (statement.getAnalyzerName() != null) {
			StatementAnalyzer queryAnalyzer = (StatementAnalyzer) RuntimeInfo
					.getCurrentInfo().getBean(statement.getAnalyzerName());
			statementStr = queryAnalyzer.analyzeQuery(statement
					.getStatementString(), statementParams);
		} else {
			statementStr = statement.getStatementString();
		}
		PreparedStatement ps = conn.prepareStatement(statementStr);
		for (int i = 0; i < statementParams.size(); ++i) {
			ps.setString(i + 1, statementParams.get(i).toString());
		}	
		if(isLogInfo)
			LogUtils.info(log,
				"Executing database statement %s with parameters %s",
				statementStr, statementParams);		
		return ps;
	}

	private DataSource setupDataSource(String connectURI) {
		GenericObjectPool connectionPool = new GenericObjectPool(null);
		Config poolConfig = new Config();
		Set<Entry<String, String>> poolConfigParams = config
				.getDbSettings().getParams().entrySet();
		for (Entry<String, String> poolConfigParam : poolConfigParams) {
			if (poolConfigParam.getKey().equals("maxActive"))
				poolConfig.maxActive = Integer.parseInt(poolConfigParam
						.getValue());
			if (poolConfigParam.getKey().equals("maxIdle"))
				poolConfig.maxIdle = Integer.parseInt(poolConfigParam
						.getValue());
			if (poolConfigParam.getKey().equals("maxWait"))
				poolConfig.maxWait = Integer.parseInt(poolConfigParam
						.getValue());
		}
		connectionPool.setConfig(poolConfig);
		ConnectionFactory connectionFactory = null;
		if(isOdbcConnectString(connectURI)) {
			String[] odbcConnectStringParams = parseOdbcConnectString(connectURI);
			String actualConnectString = odbcConnectStringParams[0];
			String username = odbcConnectStringParams[1];
			String password = odbcConnectStringParams[2];
			connectionFactory = new DriverManagerConnectionFactory(actualConnectString, username, password);
		}
		else
			connectionFactory = new DriverManagerConnectionFactory(connectURI, null);
		PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(
				connectionFactory, connectionPool, null, null, false, true);
		PoolingDataSource dataSource = new PoolingDataSource(connectionPool);
		return dataSource;
	}

	private String[] parseOdbcConnectString(String connectURI) {
		int delimiterIdx = connectURI.indexOf(';');
		String connectString = connectURI.substring(0, delimiterIdx);
		String userPwdString = connectURI.substring(delimiterIdx + 1);
		int delimiterIdxUser = userPwdString.indexOf('/');
		String[] result = new String[3];
		result[0] = connectString;
		result[1] = userPwdString.substring(0, delimiterIdxUser);
		result[2] = userPwdString.substring(delimiterIdxUser + 1);
		return result;
	}

	private boolean isOdbcConnectString(String connectURI) {
		return connectURI.startsWith("jdbc:odbc:");
	}
}
