package com.peerq.dao;

import com.peerq.model.Question;
import com.peerq.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Question-related database operations.
 */
public class QuestionDAO {
    
    /**
     * Adds a new question to the database
     * 
     * @param question The Question object to add
     * @return The ID of the newly added question, or -1 if the operation failed
     */
    public int addQuestion(Question question) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            
            String sql = "INSERT INTO questions (title, body, tags, user_id, is_solved, category, is_anonymous) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)";
            
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, question.getTitle());
            pstmt.setString(2, question.getBody());
            pstmt.setString(3, question.getTags());
            pstmt.setInt(4, question.getUserId());
            pstmt.setBoolean(5, question.isSolved());
            pstmt.setString(6, question.getCategory());
            pstmt.setBoolean(7, question.isAnonymous());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating question failed, no rows affected.");
            }
            
            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new SQLException("Creating question failed, no ID obtained.");
            }
            
        } catch (SQLException e) {
            System.err.println("Error adding question: " + e.getMessage());
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
     * Retrieves all questions from the database, with user names
     * 
     * @return List of all questions
     */
    public List<Question> getAllQuestions() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Question> questions = new ArrayList<>();
        
        try {
            conn = DBConnection.getConnection();
            
            String sql = "SELECT q.*, u.name AS user_name, " +
                         "(SELECT COUNT(*) FROM answers a WHERE a.question_id = q.id) AS answer_count " +
                         "FROM questions q " +
                         "LEFT JOIN users u ON q.user_id = u.id " +
                         "ORDER BY q.created_at DESC";
            
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Question question = extractQuestionFromResultSet(rs);
                questions.add(question);
            }
            
            return questions;
            
        } catch (SQLException e) {
            System.err.println("Error getting all questions: " + e.getMessage());
            return questions;
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
     * Retrieves questions posted by a specific user
     * 
     * @param userId The ID of the user whose questions to retrieve
     * @return List of questions posted by the user
     */
    public List<Question> getQuestionsByUserId(int userId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Question> questions = new ArrayList<>();
        
        try {
            conn = DBConnection.getConnection();
            
            String sql = "SELECT q.*, u.name AS user_name, " +
                         "(SELECT COUNT(*) FROM answers a WHERE a.question_id = q.id) AS answer_count " +
                         "FROM questions q " +
                         "LEFT JOIN users u ON q.user_id = u.id " +
                         "WHERE q.user_id = ? " +
                         "ORDER BY q.created_at DESC";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Question question = extractQuestionFromResultSet(rs);
                questions.add(question);
            }
            
            return questions;
            
        } catch (SQLException e) {
            System.err.println("Error getting questions by user ID: " + e.getMessage());
            return questions;
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
     * Retrieves all unanswered questions from the database
     * 
     * @return List of unanswered questions
     */
    public List<Question> getUnansweredQuestions() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Question> questions = new ArrayList<>();
        
        try {
            conn = DBConnection.getConnection();
            
            String sql = "SELECT q.*, u.name AS user_name, 0 AS answer_count " +
                         "FROM questions q " +
                         "LEFT JOIN users u ON q.user_id = u.id " +
                         "LEFT JOIN answers a ON q.id = a.question_id " +
                         "WHERE a.id IS NULL " +
                         "ORDER BY q.created_at DESC";
            
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Question question = extractQuestionFromResultSet(rs);
                questions.add(question);
            }
            
            return questions;
            
        } catch (SQLException e) {
            System.err.println("Error getting unanswered questions: " + e.getMessage());
            return questions;
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
     * Gets a question by its ID
     * 
     * @param questionId The ID of the question to retrieve
     * @return The Question object if found, null otherwise
     */
    public Question getQuestionById(int questionId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            
            String sql = "SELECT q.*, u.name AS user_name, " +
                         "(SELECT COUNT(*) FROM answers a WHERE a.question_id = q.id) AS answer_count " +
                         "FROM questions q " +
                         "LEFT JOIN users u ON q.user_id = u.id " +
                         "WHERE q.id = ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, questionId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractQuestionFromResultSet(rs);
            } else {
                return null;
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting question by ID: " + e.getMessage());
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
     * Marks a question as solved
     * 
     * @param questionId The ID of the question to mark as solved
     * @return true if successful, false otherwise
     */
    public boolean markQuestionAsSolved(int questionId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBConnection.getConnection();
            
            String sql = "UPDATE questions SET is_solved = true WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, questionId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error marking question as solved: " + e.getMessage());
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
     * Retrieves questions by category
     * 
     * @param category The category to filter by
     * @return List of questions in the specified category
     */
    public List<Question> getQuestionsByCategory(String category) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Question> questions = new ArrayList<>();
        
        try {
            conn = DBConnection.getConnection();
            
            String sql = "SELECT q.*, u.name AS user_name, " +
                         "(SELECT COUNT(*) FROM answers a WHERE a.question_id = q.id) AS answer_count " +
                         "FROM questions q " +
                         "LEFT JOIN users u ON q.user_id = u.id " +
                         "WHERE q.category = ? " +
                         "ORDER BY q.created_at DESC";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, category);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Question question = extractQuestionFromResultSet(rs);
                questions.add(question);
            }
            
            return questions;
            
        } catch (SQLException e) {
            System.err.println("Error getting questions by category: " + e.getMessage());
            return questions;
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
     * Search questions by keyword in title, body, or tags
     * 
     * @param keyword The search keyword
     * @return List of matching questions
     */
    public List<Question> searchQuestions(String keyword) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Question> questions = new ArrayList<>();
        
        try {
            conn = DBConnection.getConnection();
            
            String sql = "SELECT q.*, u.name AS user_name, " +
                         "(SELECT COUNT(*) FROM answers a WHERE a.question_id = q.id) AS answer_count " +
                         "FROM questions q " +
                         "LEFT JOIN users u ON q.user_id = u.id " +
                         "WHERE q.title LIKE ? OR q.body LIKE ? OR q.tags LIKE ? " +
                         "ORDER BY q.created_at DESC";
            
            pstmt = conn.prepareStatement(sql);
            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Question question = extractQuestionFromResultSet(rs);
                questions.add(question);
            }
            
            return questions;
            
        } catch (SQLException e) {
            System.err.println("Error searching questions: " + e.getMessage());
            return questions;
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
     * Helper method to extract a Question object from a ResultSet
     * 
     * @param rs The ResultSet to extract from
     * @return A Question object populated with data from the ResultSet
     * @throws SQLException If a database access error occurs
     */
    private Question extractQuestionFromResultSet(ResultSet rs) throws SQLException {
        Question question = new Question();
        question.setId(rs.getInt("id"));
        question.setTitle(rs.getString("title"));
        question.setBody(rs.getString("body"));
        question.setTags(rs.getString("tags"));
        question.setUserId(rs.getInt("user_id"));
        question.setUserName(rs.getString("user_name"));
        question.setCreatedAt(rs.getString("created_at"));
        question.setSolved(rs.getBoolean("is_solved"));
        question.setAnswerCount(rs.getInt("answer_count"));
        question.setCategory(rs.getString("category"));
        question.setAnonymous(rs.getBoolean("is_anonymous"));
        
        return question;
    }
}
