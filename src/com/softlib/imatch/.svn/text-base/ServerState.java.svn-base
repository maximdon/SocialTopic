package com.softlib.imatch;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Represents persistent server state as part of the RuntimeInfo.
 * The state is loaded / stored in the internal repository INTERNAL_STATE table
 * @see RuntimeInfo
 * @author Maxim Donde
 *
 */
@Entity
@Table(name="INTERNAL_STATE")
public class ServerState
{
	public static final int DEFAULT_SERVER_ID = 1;

	@Id
	@Column(name="server_id")
	private int serverId = DEFAULT_SERVER_ID;
	
	@Id
	@Column(name="object_id")
	private String objectId;

	
	@Column(name="last_index_run")
	private long lastIndexRun = -1;

	@Column(name="last_extract_run")
	private long lastExtractRun = -1;

	@Column(name="last_post_extract_run")
	private long lastPostExtractRun = -1;


	@Column(name="index_recovery_position")
	private double indexRecoveryPosition = -1;

	@Column(name="extract_recovery_position")
	private double extractRecoveryPosition = -1;

	@Column(name="post_extract_recovery_position")
	private double postExtractRecoveryPosition = -1;

	@Column(name="relations_recovery_position")
	private double relationsRecoveryPosition = -1;
	
	public ServerState() {	
	}
	
	public ServerState(String objectId) {
		this.objectId = objectId;
	}
	
	public void setServerId(int serverId) {
		this.serverId = serverId;
	}

	public int getServerId() {
		return serverId;
	}

	public void setLastIndexRun(long lastIndexRun) {
		this.lastIndexRun = lastIndexRun;
	}

	public String getObjectId() {
		return objectId;
	}

	public long getLastIndexRun() {
		return lastIndexRun;
	}
		
	public void setLastExtractRun(long lastExtractRun) {
		this.lastExtractRun = lastExtractRun;
	}

	public long getLastExtractRun() {
		return lastExtractRun;
	}

	public void setIndexRecoveryPosition(double recoveryPosition) {
		indexRecoveryPosition = recoveryPosition;
	}
	
	public double getIndexRecoveryPosition() {
		return indexRecoveryPosition;
	}

	public void setExtractRecoveryPosition(double recoveryPosition) {
		extractRecoveryPosition = recoveryPosition;
	}
	
	public double getExtractRecoveryPosition() {
		return extractRecoveryPosition;
	}
	
	public double getPostExtractRecoveryPosition() {
		return postExtractRecoveryPosition;
	}

	public void setPostExtractRecoveryPosition(double postExtractRecoveryPosition) {
		this.postExtractRecoveryPosition = postExtractRecoveryPosition;
	}

	public long getLastPostExtractRun() {
		return lastPostExtractRun;
	}

	public void setLastPostExtractRun(long lastPostExtractRun) {
		this.lastPostExtractRun = lastPostExtractRun;
	}
	
	public double getRelationsRecoveryPosition() {
		return relationsRecoveryPosition;
	}

	public void setRelationsRecoveryPosition(double relationsRecoveryPosition) {
		this.relationsRecoveryPosition = relationsRecoveryPosition;
	}

	public ServerState clone() {
		ServerState rc = new ServerState(objectId);

		rc.setExtractRecoveryPosition(extractRecoveryPosition);
		rc.setPostExtractRecoveryPosition(postExtractRecoveryPosition);
		rc.setIndexRecoveryPosition(indexRecoveryPosition);

		rc.setLastExtractRun(lastExtractRun);
		rc.setLastPostExtractRun(lastPostExtractRun);
		rc.setLastIndexRun(lastIndexRun);
		rc.setRelationsRecoveryPosition(relationsRecoveryPosition);

		rc.setServerId(serverId);
		return rc;
	}
	
	
};
