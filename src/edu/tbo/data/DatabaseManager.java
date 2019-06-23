package edu.tbo.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import edu.tbo.SpringAppInitalizer;

@Component
public class DatabaseManager {
	static String url = "jdbc:mysql://";
	static String instance = "";
	static String username = "";
	static String password = "";
	
	@Autowired
	public DatabaseManager(ServletContext cxt) throws SQLException {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (Exception e) {
			throw new SQLException("JDBC Driver Not Found");
		}
		
		Properties jdbcConfig = SpringAppInitalizer.readConfig("/WEB-INF/jdbc.xml");

        url = "jdbc:mysql://" + jdbcConfig.getProperty("path") + "?useLegacyDatetimeCode=false&serverTimezone=UTC";
        instance = jdbcConfig.getProperty("instance");
        username = jdbcConfig.getProperty("username");
        password = jdbcConfig.getProperty("password");
	}
	
	public Connection getConnection() throws SQLException {
		DriverManager.registerDriver(new com.mysql.jdbc.Driver ());
		//System.out.println("Attempting JDBC Connection: " + (username != null ? username : "<no user>") + "@" + url);
		Connection conn = DriverManager.getConnection(url, username, password);
		conn.setCatalog(instance);
		return conn;
	}
	
	public String testConnection() {
		String result = "";

		try (Connection connection = DriverManager.getConnection(url, username, password)) {
			result = "Database connected!";
		} catch (SQLException e) {
			result = "Cannot connect the database!";
		}
		
		return result;
	}	
}
