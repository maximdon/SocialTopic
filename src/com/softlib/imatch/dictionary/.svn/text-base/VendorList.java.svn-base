package com.softlib.imatch.dictionary;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.common.HibernateUtils;
import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.common.SessionMode;

public class VendorList {

	private static VendorList instance = null;
	private static Logger log = Logger.getLogger(VendorList.class);	

	private boolean loaded = false;
	private Set<String> vendors = new HashSet<String>();
	
	private VendorList() {
		
	}
	
	public static VendorList getInstance() {
		if(instance == null) {
			instance = new VendorList();
			instance.load();
		}
		return instance;
	}
	
	public void addVendor(Vendor vendor) {
		vendors.add(vendor.getVendorText().toLowerCase());
	}	
	
	private void load()
	{
		Session session = RuntimeInfo.getCurrentInfo().getHibernate().acquireSession(SessionMode.READONLY);
		try {
			load(session);
		}
		finally {
			RuntimeInfo.getCurrentInfo().getHibernate().releaseSession(session);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void load(Session session)
	{
		if(loaded)
			return;		
		LogUtils.debug(log, "Loading vendor dictionary");
		Query query = session.createQuery("from Vendor");
		try {
			List<Vendor> vendorsList = query.list();
			for(Vendor vendor : vendorsList) {
				addVendor(vendor);
			}
		}
		catch(org.hibernate.MappingException me) {
			//This exception is thrown when the dictionary is empty. Skip it
		}
		LogUtils.debug(log, "Dictionary loaded successfully, %d vendors found", getVendorsCount());
		loaded = true;
	}

	public boolean contains(String vendorName) {
		return vendors.contains(vendorName.toLowerCase());
	}
	
	public int getVendorsCount()
	{
		return vendors.size();
	}
	
	public String getTermVendor(String termText)  {
		for(String vendor : vendors) {
			if(termText.equals(vendor) || termText.startsWith(vendor + " "))
				return vendor;
		}
		return null;
	}
}
