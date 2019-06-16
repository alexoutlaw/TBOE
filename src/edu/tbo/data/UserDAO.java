package edu.tbo.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import edu.tbo.web.models.UserModel;

@Repository
public class UserDAO {
	
	@Autowired
	DatabaseManager dataManager;
	
	public void addUser(String userId, String password, String role) throws SQLException {
		String userSql = "INSERT INTO tboe.users (user_id, password, role) VALUES(?, ?, ?)";
		
		Connection connection = null;
		try {
			connection = dataManager.getConnection();
			PreparedStatement userStmt = connection.prepareStatement(userSql);
			userStmt.setString(1, userId);
			userStmt.setString(2, password);
			userStmt.setString(2, role);
			userStmt.executeUpdate(userSql);
		}
		finally {
			if(connection != null) connection.close();
		}
	}
	
	public UserModel getUser(String userId) throws SQLException {
		UserModel user = null;
		String sql = "SELECT * FROM tboe.users "
				+ "WHERE user_id='"+userId+"'";
		
		Connection connection = null;
		try {
			connection = dataManager.getConnection();
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			
			if(rs.next()) {
				user = populateUser(rs);
			}
			
			connection.close();
			return user;
		}
		catch(SQLException ex) {
			throw ex;
		}
	}
	
	private UserModel populateUser(ResultSet rs) throws SQLException {
		UserModel user = new UserModel();
		user.setUserId(rs.getString("user_id"));
		user.setRole(rs.getString("role"));
		return user;
	}
}
