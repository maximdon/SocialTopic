package com.softlib.imatch.dbintegration;

import java.util.List;

public class DriverLoader 
{
	private List<String> drivers;
	public void setSupportedDrivers(List<String> drivers)
	{
		//TODO add jars for all common drivers
		this.drivers = drivers;
		try {
			for(String driver : drivers)
				Class.forName(driver);
		}
		catch(ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
	public List<String> getSupportedDrivers()
	{
		return drivers;
	}
}
