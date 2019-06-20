package edu.tbo.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.jasypt.util.password.StrongPasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import edu.tbo.SpringAppInitalizer;
import edu.tbo.web.models.UserModel;

@Repository
public class UserDAO {
	
	@Autowired
	DatabaseManager dataManager;
	
	public void addUser(String userId, String password, String role, String edName) throws SQLException {
		String userSql = "INSERT INTO tboe.users (user_id, password_enc, role, ed_name) VALUES(?, ?, ?, ?)";
		
		Connection connection = null;
		try {
			connection = dataManager.getConnection();
			PreparedStatement userStmt = connection.prepareStatement(userSql);
			userStmt.setString(1, userId);
			userStmt.setString(2, encrypt(password));
			userStmt.setString(3, role);
			userStmt.setString(4, edName);
			userStmt.executeUpdate(userSql);
		}
		finally {
			if(connection != null) connection.close();
		}
	}

	public boolean doesUserExist(String userId) throws SQLException {
		String sql = "SELECT * FROM tboe.users "
				+ "WHERE user_id='"+userId+"'";
		
		Connection connection = null;
		try {
			connection = dataManager.getConnection();
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			return rs.next();
		}
		finally {
			if(connection != null) connection.close();
		}
	}
	
	public UserModel getUser(String userId, String password) throws SQLException {
		UserModel user = null;
		String sql = "SELECT * FROM tboe.users "
				+ "WHERE user_id='"+userId+"'";
		
		Connection connection = null;
		try {
			connection = dataManager.getConnection();
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			
			if(rs.next()) {
				if(!checkEncrypted(rs.getString("password_enc"), password))
					throw new RuntimeException("User Information Not Found");
					
				user = populateUser(rs);
			}
			
			connection.close();
			return user;
		}
		finally {
			if(connection != null) connection.close();
		}
	}
	
	private UserModel populateUser(ResultSet rs) throws SQLException {
		UserModel user = new UserModel();
		user.setUserId(rs.getString("user_id"));
		user.setRole(rs.getString("role"));
		user.setEdName(rs.getString("ed_name"));
		return user;
	}
	
	
	private String encrypt(String raw) {
		Properties encConfig = SpringAppInitalizer.readConfig("/WEB-INF/enc.xml");
		String salt = encConfig.getProperty("salt");
		
		StrongPasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();
		return passwordEncryptor.encryptPassword(raw+salt);
	}
	
	private boolean checkEncrypted(String enc, String check) {
		Properties encConfig = SpringAppInitalizer.readConfig("/WEB-INF/enc.xml");
		String salt = encConfig.getProperty("salt");
		
		StrongPasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();
		return passwordEncryptor.checkPassword(check+salt, enc);
	}
}
