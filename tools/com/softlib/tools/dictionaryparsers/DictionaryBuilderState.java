package com.softlib.tools.dictionaryparsers;

import com.softlib.imatch.ServerState;
import com.softlib.imatch.StageMngr;

public class DictionaryBuilderState {
	
	private final ServerState internalState;

	public DictionaryBuilderState(ServerState internalState) {
		this.internalState = internalState;
	}
	
	
	public ServerState getInternalState() {
		return internalState;
	}

	public double getRecoveryPosition() {
		switch (StageMngr.instance().getStage()) {
			case Extract:
				return internalState.getExtractRecoveryPosition();
			case PostExtract:
				return internalState.getPostExtractRecoveryPosition();
			case Index:
				return internalState.getIndexRecoveryPosition();
			default:
				return -1;
		}
	}

	public void setRecoveryPosition(double recoveryPosition) {
		switch (StageMngr.instance().getStage()) {
			case Extract:
				 internalState.setExtractRecoveryPosition(recoveryPosition);
			break;
			case PostExtract:
				 internalState.setPostExtractRecoveryPosition(recoveryPosition);
			break;
			case Index:
				 internalState.setIndexRecoveryPosition(recoveryPosition);
		    break;
		}
	}

	public long getLastRun() {
		switch (StageMngr.instance().getStage()) {
			case Extract:
				return internalState.getLastExtractRun();
			case PostExtract:
				return internalState.getLastPostExtractRun();
			case Index:
				return internalState.getLastIndexRun();
			default:
				return -1;
		}
	}

	public void setLastRun(long lastRun) {
		switch (StageMngr.instance().getStage()) {
			case Extract:
				 internalState.setLastExtractRun(lastRun);
			break;
			case PostExtract:
				 internalState.setLastPostExtractRun(lastRun);
		    break;
			case Index:
				 internalState.setLastPostExtractRun(lastRun);
			break;
		}
	}

	public void setRelationsRecoveryPosition(double relationsRecoveryPosition) {
		internalState.setRelationsRecoveryPosition(relationsRecoveryPosition);
	}
};
