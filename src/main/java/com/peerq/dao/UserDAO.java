package com.peerq.dao;

import com.peerq.model.User;
import com.peerq.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for User-related database operations.
 */
public class UserDAO {
    
    /**
     * Registers a new user in the database
     * 
     * @param user The User object to register
     * @return The ID of the newly registered user, or -1 if registration failed
     */
    public int registerUser(User user) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            
            // Check if email already exists
            pstmt = conn.prepareStatement("SELECT id FROM users WHERE email = ?");
            pstmt.setString(1, user.getEmail());
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                // Email already registered
                return -1;
            }
            
            // Close previous resources
            rs.close();
            pstmt.close();
            
            // Insert new user
            String sql = "INSERT INTO users (name, email, password, reputation, is_admin) VALUES (?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPassword()); // In a real app, this should be hashed
            pstmt.setInt(4, user.getReputation());
            pstmt.setBoolean(5, user.isAdmin());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }
            
            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new SQLException("Creating user failed, no ID obtained.");
            }
            
        } catch (SQLException e) {
            System.err.println("Error registering user: " + e.getMessage());
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
     * Validates user login credentials
     * 
     * @param email User's email
     * @param password User's password
     * @return User object if login is successful, null otherwise
     */
    public User validateLogin(String email, String password) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            
            String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            pstmt.setString(2, password); // In a real app, should compare with hashed password
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                // Login successful, create and return user object
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setReputation(rs.getInt("reputation"));
                user.setAdmin(rs.getBoolean("is_admin"));
                user.setCreatedAt(rs.getString("created_at"));
                
                return user;
            } else {
                // Login failed
                return null;
            }
            
        } catch (SQLException e) {
            System.err.println("Error validating login: " + e.getMessage());
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
     * Retrieves a user by their ID
     * 
     * @param id The user ID to look up
     * @return User object if found, null otherwise
     */
    public User getUserById(int id) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            
            String sql = "SELECT * FROM users WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setReputation(rs.getInt("reputation"));
                user.setAdmin(rs.getBoolean("is_admin"));
                user.setCreatedAt(rs.getString("created_at"));
                
                return user;
            } else {
                return null;
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting user by ID: " + e.getMessage());
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
     * Updates a user's reputation score
     * 
     * @param userId User ID
     * @param reputationChange Amount to change reputation by (positive or negative)
     * @return true if successful, false otherwise
     */
    public boolean updateReputation(int userId, int reputationChange) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBConnection.getConnection();
            
            String sql = "UPDATE users SET reputation = reputation + ? WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, reputationChange);
            pstmt.setInt(2, userId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating reputation: " + e.getMessage());
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
     * Retrieves all users from the database
     * 
     * @return List of all users
     */
    public List<User> getAllUsers() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<User> users = new ArrayList<>();
        
        try {
            conn = DBConnection.getConnection();
            
            String sql = "SELECT * FROM users ORDER BY reputation DESC";
            pstmt = conn.prepareStatement(sql);
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setReputation(rs.getInt("reputation"));
                user.setAdmin(rs.getBoolean("is_admin"));
                user.setCreatedAt(rs.getString("created_at"));
                
                users.add(user);
            }
            
            return users;
            
        } catch (SQLException e) {
            System.err.println("Error getting all users: " + e.getMessage());
            return users;
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
     * Authenticates a user with email and password
     * 
     * @param email User's email
     * @param password User's password
     * @return User object if authentication successful, null otherwise
     * @throws SQLException If a database error occurs
     */
    public User authenticateUser(String email, String password) throws SQLException {
        // This method is similar to validateLogin but throws exceptions
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            
            String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            pstmt.setString(2, password); // In production, this should compare with hashed password
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                // Authentication successful, create and return user object
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setReputation(rs.getInt("reputation"));
                user.setAdmin(rs.getBoolean("is_admin"));
                user.setCreatedAt(rs.getString("created_at"));
                
                return user;
            } else {
                // Authentication failed
                return null;
            }
            
        } finally {
            // Close resources
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) DBConnection.releaseConnection(conn);
        }
    }
    
    /**
     * Retrieve a user by their email address
     * 
     * @param email The email to look up
     * @return User object if found, null otherwise
     * @throws SQLException If a database error occurs
     */
    public User getUserByEmail(String email) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            
            String sql = "SELECT * FROM users WHERE email = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setReputation(rs.getInt("reputation"));
                user.setAdmin(rs.getBoolean("is_admin"));
                user.setCreatedAt(rs.getString("created_at"));
                
                return user;
            } else {
                return null;
            }
            
        } finally {
            // Close resources
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) DBConnection.releaseConnection(conn);
        }
    }
    
    /**
     * Creates a new user in the database
     * 
     * @param user The User object to create
     * @return true if successful, false otherwise
     * @throws SQLException If a database error occurs
     */
    public boolean createUser(User user) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBConnection.getConnection();
            
            String sql = "INSERT INTO users (name, email, password, reputation, is_admin) VALUES (?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPassword()); // In production, this should be hashed
            pstmt.setInt(4, user.getReputation());
            pstmt.setBoolean(5, user.isAdmin());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } finally {
            // Close resources
            if (pstmt != null) pstmt.close();
            if (conn != null) DBConnection.releaseConnection(conn);
        }
    }
}
