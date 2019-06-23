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
public class CandidateDAO {

	@Autowired
	DatabaseManager database;
	
	public List<CandidateModel> findCandidates(int leftId, int limit, Connection conn) throws SQLException {
		List<CandidateModel> models = new ArrayList<>();
		String sql = "SELECT b.id as 'body_id', b.name, s.id as 'system_id', s.x, s.y, s.z "
				+ "FROM "+database.DATA_INSTANCE+".bodies b "
				+ "INNER JOIN "+database.DATA_INSTANCE+".systems s on b.systemId = s.id and s.is_populated = 0 "
				+ "LEFT OUTER JOIN "+database.APP_INSTANCE+".candidates c on c.system_id = b.systemId and c.body_id = b.id "
				+ "WHERE c.body_id is null and left(b.id, 1) = "+leftId+" and b.subType like '%Gas Giant%' "
				+ "LIMIT " + limit;
		
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		
		if(rs.next()) {
			CandidateModel model = new CandidateModel();
			model.setSystemId(rs.getInt("system_id"));
			model.setBodyId(rs.getInt("body_id"));
			model.setDisplayName(rs.getString("name"));
			model.setX(rs.getFloat("x"));
			model.setY(rs.getFloat("y"));
			model.setZ(rs.getFloat("z"));
			models.add(model);
		}
		
		return models;
	}
	
	public void addCandidate(List<CandidateModel> models, Connection conn) throws SQLException {
		String sql = "insert into "+database.APP_INSTANCE+".candidates (system_id, body_id, name, x, y, z, criteria) "
				+ "values (?,?,?,?,?,?,?)";
		
		PreparedStatement stmt = conn.prepareStatement(sql);
		
		for(CandidateModel model : models) {
			stmt.setInt(1, model.getSystemId());
			stmt.setInt(2, model.getBodyId());
			stmt.setString(3, model.getDisplayName());
			stmt.setFloat(4, model.getX());
			stmt.setFloat(5, model.getY());
			stmt.setFloat(6, model.getZ());
			stmt.setString(7, model.getCriteria());
			
			stmt.addBatch();
		}
		
		stmt.executeBatch();
	}
	
	public List<CandidateModel> listFreeCandidates(int limit) throws SQLException {
		List<CandidateModel> models = new ArrayList<>();
		String sql = "select * from tboe.candidates c "
				+ "left outer join tboe.users_candidates uc on uc.body_id = c.body_id and uc.system_id = c.system_id "
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
				model.setCriteria(rs.getString("criteria"));
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
				+ "join tboe.users_candidates uc on uc.body_id = c.body_id and uc.system_id = c.system_id "
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
				model.setCriteria(rs.getString("criteria"));
				models.add(model);
			}
		}
		finally {
			if(conn != null) conn.close();
		}
		
		return models;
	}
	
	public void addUserCandidate(int systemId, int bodyId, String userId) throws SQLException {
		String sql = "insert into tboe.users_candidates (system_id, body_id, user_id) values (?,?,?)";
		
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
		String sql = "delete from tboe.users_candidates where system_id = ? and body_id = ? and user_id = ?";
		
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
