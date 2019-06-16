package edu.tbo.web.models;

public class CandidateModel {
	private int systemId, bodyId;
	private String displayName;
	
	public int getSystemId() {
		return systemId;
	}
	public void setSystemId(int systemId) {
		this.systemId = systemId;
	}
	public int getBodyId() {
		return bodyId;
	}
	public void setBodyId(int bodyId) {
		this.bodyId = bodyId;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
}
