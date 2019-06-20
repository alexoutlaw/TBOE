package edu.tbo.web.models;

public class UserModel {
	private String userId, password, role, edName;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getEdName() {
		return edName;
	}

	public void setEdName(String edName) {
		this.edName = edName;
	}
}
