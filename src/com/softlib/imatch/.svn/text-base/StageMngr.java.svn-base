package com.softlib.imatch;

public class StageMngr {

	public enum Stage {
		Extract,
		PostExtract,
		Index,
	};

	private Stage stage;
	
	static private StageMngr instance;
	
	private StageMngr() {
	}
	
	static public StageMngr instance() {
		if (instance==null)
			instance = new StageMngr();
		return instance;
	}
	
	public boolean isStage(Stage stage) {
		return this.stage == stage;
	}

	public Stage getStage() {
		return stage;
	}

	public String getStageStr() {
		if (stage==null)
			return "";
		return stage.toString();
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}	

};
