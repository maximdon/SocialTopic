package com.softlib.imatch.ticketprocessing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.softlib.imatch.common.Pair;

public class ProcessedTicketWritter {

	private static ProcessedTicketWritter instance;

	private Map<String,List<Pair<String,String>>> valuesById =
		new HashMap<String,List<Pair<String,String>>>();

	private ProcessedTicketWritter() {
	}

	static public ProcessedTicketWritter getInstance() {
		if (instance==null) { 
			instance = new ProcessedTicketWritter();
			//instance.start();
		}
		return instance;
	}

	synchronized public void save() {
		Map<String,List<Pair<String,String>>> data = getData();
		if (data.isEmpty())
			return;
		ProcessedTicketDBase.startSession();
		for (String id : data.keySet()) {
			List<Pair<String,String>> values = data.get(id);
			ProcessedTicketDBase.write(id,values);
		}			
		ProcessedTicketDBase.endSession();
	}

	synchronized public void addData(String id,List<Pair<String,String>> values) {
		valuesById.put(id,values);
	}
	
	synchronized private Map<String,List<Pair<String,String>>> getData() {
		Map<String,List<Pair<String,String>>> data = valuesById;
		valuesById = new HashMap<String, List<Pair<String,String>>>();
		return data;
	}
	
	public void run() {
		while(true) {
		}
	}


};
