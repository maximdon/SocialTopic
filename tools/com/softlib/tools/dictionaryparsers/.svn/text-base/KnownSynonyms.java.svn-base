package com.softlib.tools.dictionaryparsers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Session;

import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.common.SessionMode;

public class KnownSynonyms
{
	private static Map<String, String> synonymsMap = new HashMap<String, String>();
	private static Logger log = Logger.getLogger(KnownSynonyms.class);
	
	static {
		loadMap();
	}
	
	public static Map<String, String> getMap() {
		return synonymsMap;
	}

	private static void loadMap() {
		Session session = RuntimeInfo.getCurrentInfo().getHibernate().acquireSession(SessionMode.READONLY);
		Connection connection = session.connection();
		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT Abbreviation, Synonym FROM KNOWN_SYNONYMS");
			while(rs.next()) {				
				String key = rs.getString(1);
				String value = rs.getString(2);
				synonymsMap.put(key, value);
			}
			LogUtils.debug(log, "%d known synonyms found", synonymsMap.size());
		} catch (SQLException e) {
			LogUtils.error(log, "Unable to load known synonyms, reason %s", e.getMessage());
		}		
		finally {
			RuntimeInfo.getCurrentInfo().getHibernate().releaseSession(session);
		}
	}
}
