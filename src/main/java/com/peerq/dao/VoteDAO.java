package com.peerq.dao;

import com.peerq.model.Vote;
import com.peerq.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Vote-related database operations.
 */
public class VoteDAO {
    
    /**
     * Adds a vote to an answer
     * 
     * @param vote The Vote object to add
     * @return The ID of the newly added vote, or -1 if the operation failed
     */
    public int addVote(Vote vote) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            
            // Check if user has already voted on this answer
            String checkSql = "SELECT id, vote_type FROM votes WHERE answer_id = ? AND user_id = ?";
            pstmt = conn.prepareStatement(checkSql);
            pstmt.setInt(1, vote.getAnswerId());
            pstmt.setInt(2, vote.getUserId());
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                // User has already voted - return the existing vote ID
                return rs.getInt("id");
            }
            
            // Close previous resources
            rs.close();
            pstmt.close();
            
            // Insert the new vote
            String sql = "INSERT INTO votes (answer_id, user_id, vote_type) VALUES (?, ?, ?)";
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, vote.getAnswerId());
            pstmt.setInt(2, vote.getUserId());
            pstmt.setString(3, vote.getVoteType());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating vote failed, no rows affected.");
            }
            
            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new SQLException("Creating vote failed, no ID obtained.");
            }
            
        } catch (SQLException e) {
            System.err.println("Error adding vote: " + e.getMessage());
            return -1;
        } finally {
            // Close resources
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) DBConnection.releaseConnection(conn);
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }
    
    /**
     * Checks if a user has already voted on a specific answer
     * 
     * @param answerId The ID of the answer
     * @param userId The ID of the user
     * @return true if the user has already voted, false otherwise
     */
    public boolean hasUserVoted(int answerId, int userId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            
            String sql = "SELECT id FROM votes WHERE answer_id = ? AND user_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, answerId);
            pstmt.setInt(2, userId);
            rs = pstmt.executeQuery();
            
            return rs.next(); // Returns true if a vote exists
            
        } catch (SQLException e) {
            System.err.println("Error checking if user voted: " + e.getMessage());
            return false;
        } finally {
            // Close resources
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) DBConnection.releaseConnection(conn);
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }
    
    /**
     * Removes a vote from an answer
     * 
     * @param answerId The ID of the answer
     * @param userId The ID of the user
     * @return true if successful, false otherwise
     */
    public boolean removeVote(int answerId, int userId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBConnection.getConnection();
            
            String sql = "DELETE FROM votes WHERE answer_id = ? AND user_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, answerId);
            pstmt.setInt(2, userId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error removing vote: " + e.getMessage());
            return false;
        } finally {
            // Close resources
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) DBConnection.releaseConnection(conn);
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }
    
    /**
     * Gets all votes for a specific answer
     * 
     * @param answerId The ID of the answer
     * @return List of votes for the answer
     */
    public List<Vote> getVotesByAnswerId(int answerId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Vote> votes = new ArrayList<>();
        
        try {
            conn = DBConnection.getConnection();
            
            String sql = "SELECT * FROM votes WHERE answer_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, answerId);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Vote vote = new Vote();
                vote.setId(rs.getInt("id"));
                vote.setAnswerId(rs.getInt("answer_id"));
                vote.setUserId(rs.getInt("user_id"));
                vote.setVoteType(rs.getString("vote_type"));
                vote.setCreatedAt(rs.getString("created_at"));
                
                votes.add(vote);
            }
            
            return votes;
            
        } catch (SQLException e) {
            System.err.println("Error getting votes by answer ID: " + e.getMessage());
            return votes;
        } finally {
            // Close resources
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) DBConnection.releaseConnection(conn);
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }
}
