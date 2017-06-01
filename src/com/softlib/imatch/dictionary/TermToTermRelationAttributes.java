package com.softlib.imatch.dictionary;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.Session;

import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.common.HibernateUtils;
import com.softlib.imatch.common.SessionMode;

public class TermToTermRelationAttributes 
{
	private TechnicalDictionaryTerm term1;
	private TechnicalDictionaryTerm term2;
	
	public static boolean isReliable(TechnicalDictionaryTerm term1, TechnicalDictionaryTerm term2)
	{
		return new TermToTermRelationAttributes(term1, term2).isReliable() || 
			   new TermToTermRelationAttributes(term2, term1).isReliable();
	}
	
	public static void setReliable(TechnicalDictionaryTerm term1, TechnicalDictionaryTerm term2, boolean isReliable)
	{
		new TermToTermRelationAttributes(term1, term2).setReliable(isReliable);
		new TermToTermRelationAttributes(term2, term1).setReliable(isReliable);
	}
	
	private TermToTermRelationAttributes(TechnicalDictionaryTerm term1, TechnicalDictionaryTerm term2)
	{
		this.term1 = term1;
		this.term2 = term2;
	}
	
	@SuppressWarnings("deprecation")
	public boolean isReliable() {
		Session session = RuntimeInfo.getCurrentInfo().getHibernate().acquireSession(SessionMode.READONLY);
		ResultSet rs;
		boolean reliable = false;
		boolean isReliableSet = false;
		try {
		    Connection connection = session.connection();
		    PreparedStatement stmt = connection.prepareStatement("select is_reliable, is_reliable_set from dictionary_terms_relations where term1_id=? and term2_id=?");
			stmt.setInt(1, term1.getTermId());
			stmt.setInt(2, term2.getTermId());
			rs = stmt.executeQuery();
			if(rs.next()) {
				isReliableSet = rs.getBoolean(2);
				if(isReliableSet)
					reliable = rs.getBoolean(1);
			}
		} catch (SQLException e) {
			//No value was set, apply 
		}		
		finally {
			RuntimeInfo.getCurrentInfo().getHibernate().releaseSession(session);
		}
		if(!isReliableSet)
			reliable = calculateReliabilityRules();
		return reliable;
	}

	private boolean calculateReliabilityRules() 
	{
		boolean result = true;
		if (term1.getTermSource().getSourceId()== TechnicalTermSource.SOFTLIB_TERMS_ID || 
			term2.getTermSource().getSourceId()== TechnicalTermSource.SOFTLIB_TERMS_ID) 
			result = false;
		if (term1.getTermSource().getsourceName().equals("NLP Version Tokens") &&
			term2.getTermSource().getsourceName().equals("NLP Version Tokens Split")) 
			result = false;
		return result;
	}
	@SuppressWarnings("deprecation")
	public void setReliable(boolean isReliable) {	
		Session session = RuntimeInfo.getCurrentInfo().getHibernate().acquireSession(SessionMode.READ_WRITE);
		try {
		    Connection connection = session.connection();
		    PreparedStatement stmt = connection.prepareStatement("update dictionary_terms_relations set is_reliable = ?, is_reliable_set = 1 where term1_id=? and term2_id=?");
			stmt.setBoolean(1, isReliable);
			stmt.setInt(2, term1.getTermId());
			stmt.setInt(3, term2.getTermId());
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		finally {
			RuntimeInfo.getCurrentInfo().getHibernate().releaseSession(session);
		}
	}
}
