package com.softlib.imatch.test.isolveindex;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.xml.DOMConfigurator;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;
import org.hibernate.Session;
import org.junit.Test;

import com.softlib.imatch.ConsoleAppRuntimeInfo;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.common.HibernateUtils;
import com.softlib.imatch.common.SessionMode;
import com.softlib.imatch.matcher.lucene.LuceneTicketsRepository;

public class LuceneSearchProcessedTicketTest {

	private LuceneTicketsRepository repository;
	
	private void init() {
		ConsoleAppRuntimeInfo.init(null);
		DOMConfigurator.configure(RuntimeInfo.getCurrentInfo().getRealPath("/{SolutionConfigFolder}/log4j-console.xml"));
		try {
			RuntimeInfo.getCurrentInfo().getBean("_n_");				
		}
		catch(Exception e){}
		repository = (LuceneTicketsRepository)RuntimeInfo.getCurrentInfo().getBean("ticketsRepository");
	}
	
	//in HibernateUtils.java : change line :
   	//dbFilePath = RuntimeInfo.getCurrentInfo().getRealPath("/{SolutionDBFolder}/iddocs.db");
    
	@Test
	public void test() { 
		try {
			init();
			
			List<String> ordIds;
			
			System.out.println("===============================");
			ordIds = new ArrayList<String>();
			ordIds = checkString("attunity AND studio AND 5.0.1",ordIds);
					 checkString("\"attunity studio 5.0.1\"",ordIds);
					 checkString("\"attunity studio\" AND 5.0.1",ordIds);			
			System.out.println("===============================");
	
			ordIds = new ArrayList<String>();
			ordIds = checkString("install AND attunity AND jdbc AND driver",ordIds);
					 checkString("install^1.0 AND \"attunity jdbc driver\"^3.5",ordIds);
				
			System.out.println("===============================");
			ordIds = new ArrayList<String>();
			ordIds = checkString("install attunity jdbc driver",ordIds);
					 checkString("install^1.0 \"attunity jdbc driver\"^3.5",ordIds);
				
			System.out.println("===============================");

			ordIds = new ArrayList<String>();
			ordIds = checkString("fix AND attunity AND jdbc AND driver",ordIds);
				     checkString("fix^0.2 AND \"attunity jdbc driver\"^3.5",ordIds);

			System.out.println("===============================");
			ordIds = new ArrayList<String>();
			ordIds = checkString("fix attunity jdbc driver",ordIds);
				     checkString("fix^0.2 \"attunity jdbc driver\"^3.5",ordIds);

			System.out.println("===============================");

			ordIds = new ArrayList<String>();
			ordIds = checkString("attunity AND support AND jdbc AND driver",ordIds);
					 checkString("attunity^0.2 AND support^0.2 AND (\"attunity jdbc driver\"^3.5 OR \"jdbc driver\"^3.5)",ordIds);
			System.out.println("===============================");

			ordIds = new ArrayList<String>();
			ordIds = checkString("attunity support jdbc driver",ordIds);
					 checkString("attunity^0.2 support^0.2 (\"attunity jdbc driver\"^3.5 OR \"jdbc driver\"^3.5)",ordIds);
			System.out.println("===============================");
					 		 
			ordIds = new ArrayList<String>();
			ordIds = checkString("installed AND license AND valid",ordIds);
					 checkString("\"the installed license is not valid\"",ordIds);					 
		     System.out.println("===============================");
			 		 
			ordIds = new ArrayList<String>();
			ordIds = checkString("odbc AND configuration AND problem",ordIds);
					 checkString("odbc^3.5 AND configuration^1 AND problem^0.2",ordIds);					 
			System.out.println("===============================");
			 		 
			ordIds = new ArrayList<String>();
			ordIds = checkString("odbc configuration problem",ordIds);
					 checkString("odbc^3.5 configuration^1 problem^0.2",ordIds);					 
		}
		catch (Exception e) {
			System.out.println("Exception "+e.getMessage());
		}
	}
	private Comparator<String> comparator = new Comparator<String>() {
		public int compare(String str1, String str2) {
			return Integer.parseInt(str1) - Integer.parseInt(str2);
		}
	};

	private void printIds(List<String> ids,String title) {
//		Collections.sort(ids,comparator);
		System.out.println ("   "+title+" : "+ids);
	}
	
	private List<String> checkString(String text,List<String> orgIds) throws Exception {
		
		List<String> itemnums = repository.findIsolve(text);
		List<String> ids = itemnum2Id(itemnums);
		
		List<String> oldIds = new ArrayList<String>();
		List<String> newIds = new ArrayList<String>();
		for (String id : ids) {
			if (orgIds.contains(id))
				oldIds.add(id);
			else
				newIds.add(id);
		}
		List<String> removeIds = new ArrayList<String>();
		for (String id : orgIds) {
			if (!newIds.contains(id) && !oldIds.contains(id))
				removeIds.add(id);
		}

		System.out.println("\nCheckString : { "+text+" }");
		printIds(oldIds,   "org     ids");
		printIds(newIds,   "new     ids");
		printIds(removeIds,"removed ids");
				
		return ids;
	}

	
	static public List<String> itemnum2Id(List<String> itemnums) {
		List<String> rc = new ArrayList<String>();
//		String SQLiteDriver = "org.sqlite.JDBC";
//		String dbFilePath;
//		String connectionUrl;
//		Connection con=null;
//		try {
//			Class.forName(SQLiteDriver);
//			dbFilePath = RuntimeInfo.getCurrentInfo().getRealPath("/{SolutionDBFolder}/iddocs.db");
//			connectionUrl = "jdbc:sqlite:" + dbFilePath;
//			con = DriverManager.getConnection(connectionUrl);
//		} catch (Exception e1) {
//			e1.printStackTrace();
//			rc.add("error");
//			return rc;
//		}
		Session session = RuntimeInfo.getCurrentInfo().getHibernate().acquireSession(SessionMode.READ_WRITE);
		Connection con = session.connection();
		
		for (String itemnum : itemnums) {
			String sql = "select PART_STRING from ID_ITEM_PARTS where PART_ID = "+itemnum;
			try {
				ResultSet resultSet = con.createStatement().executeQuery(sql);
				if (resultSet.next()) {
					String column = resultSet.getString("PART_STRING");
					int idx1 = column.indexOf("|")+1;
					int idx2 = column.indexOf("|",idx1);
					String id = column.substring(idx1,idx2);
					rc.add(id);
				}
			} 
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
		RuntimeInfo.getCurrentInfo().getHibernate().releaseSession(session);
		return rc;
	}
	
	static private StandardAnalyzer iAnalyzer = new StandardAnalyzer(Version.LUCENE_CURRENT, new HashSet<String>());
	static private QueryParser iParser = new QueryParser(Version.LUCENE_CURRENT, "contents", iAnalyzer);

	static public Query buildQuery(String str) {
		Query rc;
		try {
			synchronized (iParser) {
				rc = iParser.parse(str);					
			}
		} catch (ParseException e) {
			throw new RuntimeException("Unexpected error occured: " + e.getMessage());
		}
		return rc;
	}
	

};
