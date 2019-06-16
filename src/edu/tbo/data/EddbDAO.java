package edu.tbo.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import edu.tbo.web.models.CandidateModel;

@Repository
public class EddbDAO {

	@Autowired
	DatabaseManager database;
	
	public List<CandidateModel> findCandidates(int limit) throws SQLException {
		List<CandidateModel> models = new ArrayList<>();
		String sql = "select b.system_id, b.id, b.name from tboe.bodies b "
				+ "left outer join tboe.candidates c on c.system_id = b.system_id and c.body_id = b.id "
				+ "where c.body_id is null "
				+ "limit " + limit;
		
		Connection conn = null;
		try {
			conn = database.getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			
			if(rs.next()) {
				CandidateModel model = new CandidateModel();
				model.setSystemId(rs.getInt("system_id"));
				model.setBodyId(rs.getInt("id"));
				model.setDisplayName(rs.getString("name"));
				models.add(model);
			}
		}
		finally {
			if(conn != null) conn.close();
		}
		
		return models;
	}
	
	public void addCandidate(CandidateModel model) throws SQLException {
		String sql = "insert into tboe.candidates (system_id, body_id, name) values (?,?,?)";
		
		Connection conn = null;
		try {
			conn = database.getConnection();
			PreparedStatement stmt = conn.prepareStatement(sql);
			
			stmt.setInt(1, model.getSystemId());
			stmt.setInt(2, model.getBodyId());
			stmt.setString(3, model.getDisplayName());
			
			stmt.executeUpdate();
		}
		finally {
			if(conn != null) conn.close();
		}
	}
	
	public List<CandidateModel> listFreeCandidates(int limit) throws SQLException {
		List<CandidateModel> models = new ArrayList<>();
		String sql = "select * from tboe.candidates c "
				+ "left outer join tboe.user_candidates uc on uc.body_id = c.body_id and uc.system_id = c.system_id "
				+ "where uc.body_id is null "
				+ "limit " + limit;
		
		Connection conn = null;
		try {
			conn = database.getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			
			while(rs.next()) {
				CandidateModel model = new CandidateModel();
				model.setSystemId(rs.getInt("system_id"));
				model.setBodyId(rs.getInt("body_id"));
				model.setDisplayName(rs.getString("name"));
				models.add(model);
			}
		}
		finally {
			if(conn != null) conn.close();
		}
		
		return models;
	}
	
	public List<CandidateModel> listUserCandidates(String userId, int limit) throws SQLException {
		List<CandidateModel> models = new ArrayList<>();
		String sql = "select * from tboe.candidates c "
				+ "join tboe.user_candidates uc on uc.body_id = c.body_id and uc.system_id = c.system_id "
				+ "where uc.user_id = ? "
				+ "limit " + limit;
		
		Connection conn = null;
		try {
			conn = database.getConnection();
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, userId);
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next()) {
				CandidateModel model = new CandidateModel();
				model.setSystemId(rs.getInt("system_id"));
				model.setBodyId(rs.getInt("body_id"));
				model.setDisplayName(rs.getString("name"));
				models.add(model);
			}
		}
		finally {
			if(conn != null) conn.close();
		}
		
		return models;
	}
	
	public void addUserCandidate(int systemId, int bodyId, String userId) throws SQLException {
		String sql = "insert into tboe.user_candidates (system_id, body_id, user_id) values (?,?,?)";
		
		Connection conn = null;
		try {
			conn = database.getConnection();
			PreparedStatement stmt = conn.prepareStatement(sql);
			
			stmt.setInt(1, systemId);
			stmt.setInt(2, bodyId);
			stmt.setString(3, userId);
			
			stmt.executeUpdate();
		}
		finally {
			if(conn != null) conn.close();
		}
	}
	
	public void removeUserCandidate(int systemId, int bodyId, String userId) throws SQLException {
		String sql = "delete from tboe.user_candidates where system_id = ? and body_id = ? and user_id = ?";
		
		Connection conn = null;
		try {
			conn = database.getConnection();
			PreparedStatement stmt = conn.prepareStatement(sql);
			
			stmt.setInt(1, systemId);
			stmt.setInt(2, bodyId);
			stmt.setString(3, userId);
			
			stmt.executeUpdate();
		}
		finally {
			if(conn != null) conn.close();
		}
	}
}
