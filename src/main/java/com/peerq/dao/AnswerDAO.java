package com.peerq.dao;

import com.peerq.model.Answer;
import com.peerq.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Answer-related database operations.
 */
public class AnswerDAO {
    
    /**
     * Retrieves all answers for a specific question
     * 
     * @param questionId The ID of the question
     * @return List of answers for the question
     */
    public List<Answer> getAnswersByQuestionId(int questionId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Answer> answers = new ArrayList<>();
        
        try {
            conn = DBConnection.getConnection();
            
            String sql = "SELECT a.*, u.name AS user_name FROM answers a " +
                         "LEFT JOIN users u ON a.user_id = u.id " +
                         "WHERE a.question_id = ? " +
                         "ORDER BY a.upvotes DESC, a.created_at ASC";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, questionId);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Answer answer = extractAnswerFromResultSet(rs);
                answers.add(answer);
            }
            
            return answers;
            
        } catch (SQLException e) {
            System.err.println("Error getting answers by question ID: " + e.getMessage());
            return answers;
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
     * Submits a new answer to a question
     * 
     * @param answer The Answer object to submit
     * @return The ID of the newly submitted answer, or -1 if submission failed
     */
    public int submitAnswer(Answer answer) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            
            String sql = "INSERT INTO answers (question_id, user_id, content, upvotes, is_accepted, is_anonymous) " +
                         "VALUES (?, ?, ?, ?, ?, ?)";
            
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, answer.getQuestionId());
            pstmt.setInt(2, answer.getUserId());
            pstmt.setString(3, answer.getContent());
            pstmt.setInt(4, answer.getUpvotes());
            pstmt.setBoolean(5, answer.isAccepted());
            pstmt.setBoolean(6, answer.isAnonymous());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating answer failed, no rows affected.");
            }
            
            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new SQLException("Creating answer failed, no ID obtained.");
            }
            
        } catch (SQLException e) {
            System.err.println("Error submitting answer: " + e.getMessage());
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
     * Upvotes an answer, incrementing its upvote count and recording the vote
     * 
     * @param answerId The ID of the answer to upvote
     * @param userId The ID of the user doing the upvoting
     * @return true if successful, false otherwise
     */
    public boolean upvoteAnswer(int answerId, int userId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Check if user has already voted on this answer
            String checkSql = "SELECT id FROM votes WHERE answer_id = ? AND user_id = ?";
            pstmt = conn.prepareStatement(checkSql);
            pstmt.setInt(1, answerId);
            pstmt.setInt(2, userId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                // User has already voted on this answer
                conn.rollback();
                return false;
            }
            
            // Close the previous resources
            rs.close();
            pstmt.close();
            
            // Insert vote record
            String voteSql = "INSERT INTO votes (answer_id, user_id, vote_type) VALUES (?, ?, 'upvote')";
            pstmt = conn.prepareStatement(voteSql);
            pstmt.setInt(1, answerId);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
            
            // Close the previous resources
            pstmt.close();
            
            // Update answer upvote count
            String updateSql = "UPDATE answers SET upvotes = upvotes + 1 WHERE id = ?";
            pstmt = conn.prepareStatement(updateSql);
            pstmt.setInt(1, answerId);
            pstmt.executeUpdate();
            
            // Get user ID of answer author to update their reputation
            pstmt.close();
            String getUserSql = "SELECT user_id FROM answers WHERE id = ?";
            pstmt = conn.prepareStatement(getUserSql);
            pstmt.setInt(1, answerId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                int answerUserId = rs.getInt("user_id");
                
                // Close the previous resources
                rs.close();
                pstmt.close();
                
                // Update user reputation (give +10 points for upvote)
                String updateRepSql = "UPDATE users SET reputation = reputation + 10 WHERE id = ?";
                pstmt = conn.prepareStatement(updateRepSql);
                pstmt.setInt(1, answerUserId);
                pstmt.executeUpdate();
            }
            
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            System.err.println("Error upvoting answer: " + e.getMessage());
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                System.err.println("Error rolling back transaction: " + ex.getMessage());
            }
            return false;
        } finally {
            // Close resources
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    DBConnection.releaseConnection(conn);
                }
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }
    
    /**
     * Marks an answer as accepted
     * 
     * @param answerId The ID of the answer to mark as accepted
     * @return true if successful, false otherwise
     */
    public boolean markAnswerAsAccepted(int answerId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Get question ID for this answer
            String getQuestionSql = "SELECT question_id, user_id FROM answers WHERE id = ?";
            pstmt = conn.prepareStatement(getQuestionSql);
            pstmt.setInt(1, answerId);
            rs = pstmt.executeQuery();
            
            if (!rs.next()) {
                conn.rollback();
                return false;
            }
            
            int questionId = rs.getInt("question_id");
            int answerUserId = rs.getInt("user_id");
            
            // Close the previous resources
            rs.close();
            pstmt.close();
            
            // Unmark any previously accepted answers for this question
            String unmarkSql = "UPDATE answers SET is_accepted = false WHERE question_id = ?";
            pstmt = conn.prepareStatement(unmarkSql);
            pstmt.setInt(1, questionId);
            pstmt.executeUpdate();
            
            // Close the previous resource
            pstmt.close();
            
            // Mark the new accepted answer
            String markSql = "UPDATE answers SET is_accepted = true WHERE id = ?";
            pstmt = conn.prepareStatement(markSql);
            pstmt.setInt(1, answerId);
            pstmt.executeUpdate();
            
            // Close the previous resource
            pstmt.close();
            
            // Mark the question as solved
            String solveSql = "UPDATE questions SET is_solved = true WHERE id = ?";
            pstmt = conn.prepareStatement(solveSql);
            pstmt.setInt(1, questionId);
            pstmt.executeUpdate();
            
            // Close the previous resource
            pstmt.close();
            
            // Update user reputation (give +15 points for accepted answer)
            String updateRepSql = "UPDATE users SET reputation = reputation + 15 WHERE id = ?";
            pstmt = conn.prepareStatement(updateRepSql);
            pstmt.setInt(1, answerUserId);
            pstmt.executeUpdate();
            
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            System.err.println("Error marking answer as accepted: " + e.getMessage());
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                System.err.println("Error rolling back transaction: " + ex.getMessage());
            }
            return false;
        } finally {
            // Close resources
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    DBConnection.releaseConnection(conn);
                }
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }
    
    /**
     * Get an answer by its ID
     * 
     * @param answerId The ID of the answer to retrieve
     * @return The Answer object if found, null otherwise
     */
    public Answer getAnswerById(int answerId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            
            String sql = "SELECT a.*, u.name AS user_name FROM answers a " +
                         "LEFT JOIN users u ON a.user_id = u.id " +
                         "WHERE a.id = ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, answerId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractAnswerFromResultSet(rs);
            } else {
                return null;
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting answer by ID: " + e.getMessage());
            return null;
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
     * Helper method to extract an Answer object from a ResultSet
     * 
     * @param rs The ResultSet to extract from
     * @return An Answer object populated with data from the ResultSet
     * @throws SQLException If a database access error occurs
     */
    private Answer extractAnswerFromResultSet(ResultSet rs) throws SQLException {
        Answer answer = new Answer();
        answer.setId(rs.getInt("id"));
        answer.setQuestionId(rs.getInt("question_id"));
        answer.setUserId(rs.getInt("user_id"));
        answer.setUserName(rs.getString("user_name"));
        answer.setContent(rs.getString("content"));
        answer.setCreatedAt(rs.getString("created_at"));
        answer.setUpvotes(rs.getInt("upvotes"));
        answer.setAccepted(rs.getBoolean("is_accepted"));
        answer.setAnonymous(rs.getBoolean("is_anonymous"));
        
        return answer;
    }
}
