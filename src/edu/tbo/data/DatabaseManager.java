package edu.tbo.data;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

@Component
public class DatabaseManager {
	static String url = "jdbc:mysql://";
	static String username = "";
	static String password = "";
	
	@Autowired
	public DatabaseManager(ServletContext cxt) throws SQLException {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (Exception e) {
			throw new SQLException("JDBC Driver Not Found");
		}
		
		try {			
			Document configXml = readConfig(cxt);

	        url = "jdbc:mysql://" + configXml.getElementsByTagName("path").item(0).getTextContent() + "?useLegacyDatetimeCode=false&serverTimezone=UTC";
	        username = configXml.getElementsByTagName("username").item(0).getTextContent();
	        password = configXml.getElementsByTagName("password").item(0).getTextContent();
		}
		catch(Exception ex) {
			throw new SQLException("Unable to set JDBC Config");
		}
	}
	
	private Document readConfig(ServletContext cxt) throws SQLException {
		try {
			InputStream stream = cxt.getResourceAsStream("/WEB-INF/jdbc.xml");
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder builder = factory.newDocumentBuilder();
	        Document doc = builder.parse(stream);
	        return doc;
		}
		catch(Exception ex) {
			throw new SQLException("Unable to read JDBC Config");
		}
	}
	
	public Connection getConnection() throws SQLException {
		DriverManager.registerDriver(new com.mysql.jdbc.Driver ());
		//System.out.println("Attempting JDBC Connection: " + (username != null ? username : "<no user>") + "@" + url);
		Connection conn = DriverManager.getConnection(url, username, password);
		conn.setCatalog("osdb");
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
