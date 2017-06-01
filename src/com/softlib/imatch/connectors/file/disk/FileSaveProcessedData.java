package com.softlib.imatch.connectors.file.disk;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.softlib.imatch.StageMngr;
import com.softlib.imatch.common.Pair;
import com.softlib.imatch.ticketprocessing.ProcessedTicketDBase;

public class FileSaveProcessedData {
	
	private final static String FIELD_DONE = "done";

	private Set<String> notSavedFileNames = new HashSet<String>();

	public void add(String fileName) {
		notSavedFileNames.add(fileName);
	}
	
	public boolean isExist(String ticketId) {
		String fieldData = ProcessedTicketDBase.getField(ticketId,StageMngr.instance().getStageStr());
		if (fieldData!=null && fieldData.equals(FIELD_DONE))
			return true;
		return false;
	}
	
	public void save() {
		ProcessedTicketDBase.startSession();
		for (String notSavedFileName : notSavedFileNames) {
			List<Pair<String,String>> values = new ArrayList<Pair<String,String>>();
			Pair<String,String> value = 
				new Pair<String,String>(StageMngr.instance().getStageStr(),FIELD_DONE);
			values.add(value);
			ProcessedTicketDBase.write(notSavedFileName,values);
		}
		ProcessedTicketDBase.endSession();
		notSavedFileNames.clear();
	}

};
