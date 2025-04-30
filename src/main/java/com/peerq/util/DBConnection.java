package com.peerq.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Database connection utility class that manages JDBC connections to PostgreSQL.
 * Implements connection pooling for better performance.
 */
public class DBConnection {
    // Database configuration from environment variables
    private static final String JDBC_DRIVER = "org.postgresql.Driver";
    private static final String DB_URL = System.getenv("DATABASE_URL");
    private static final String USER = System.getenv("PGUSER");
    private static final String PASSWORD = System.getenv("PGPASSWORD");
    private static final String HOST = System.getenv("PGHOST");
    private static final String PORT = System.getenv("PGPORT");
    private static final String DATABASE = System.getenv("PGDATABASE");
    
    // Connection pool properties
    private static final int POOL_SIZE = 5;
    private static final BlockingQueue<Connection> connectionPool = new ArrayBlockingQueue<>(POOL_SIZE);
    private static boolean initialized = false;
    
    /**
     * Initializes the connection pool by creating and adding connections
     */
    private static synchronized void initializePool() {
        if (initialized) {
            return;
        }
        
        try {
            // Register JDBC driver
            Class.forName(JDBC_DRIVER);
            
            // Create connection properties
            Properties props = new Properties();
            props.setProperty("user", USER);
            props.setProperty("password", PASSWORD);
            
            String url = DB_URL;
            if (url == null || url.isEmpty()) {
                // Build URL from components if DATABASE_URL is not available
                url = "jdbc:postgresql://" + HOST + ":" + PORT + "/" + DATABASE;
            }
            
            System.out.println("Connecting to database at: " + url);
            
            // Initialize the connection pool
            for (int i = 0; i < POOL_SIZE; i++) {
                Connection conn = DriverManager.getConnection(url, props);
                connectionPool.add(conn);
            }
            
            initialized = true;
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found: " + e.getMessage());
            throw new RuntimeException("JDBC Driver not found", e);
        } catch (SQLException e) {
            System.err.println("Error initializing database connection pool: " + e.getMessage());
            throw new RuntimeException("Database connection failed", e);
        }
    }
    
    /**
     * Gets a connection from the pool or creates a new one if pool is empty
     * @return a database connection
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        if (!initialized) {
            initializePool();
        }
        
        try {
            // Try to get a connection from the pool
            Connection conn = connectionPool.poll();
            
            // If pool is empty, create a new connection
            if (conn == null || conn.isClosed()) {
                Properties props = new Properties();
                props.setProperty("user", USER);
                props.setProperty("password", PASSWORD);
                
                String url = DB_URL;
                if (url == null || url.isEmpty()) {
                    // Build URL from components if DATABASE_URL is not available
                    url = "jdbc:postgresql://" + HOST + ":" + PORT + "/" + DATABASE;
                }
                
                return DriverManager.getConnection(url, props);
            }
            
            // Test connection validity
            if (!conn.isValid(1)) {
                conn.close();
                return getConnection();
            }
            
            return conn;
        } catch (SQLException e) {
            System.err.println("Error getting database connection: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Returns a connection to the pool
     * @param conn the connection to return
     */
    public static void releaseConnection(Connection conn) {
        if (conn == null) {
            return;
        }
        
        try {
            // Only add back connections that are valid and not closed
            if (!conn.isClosed() && conn.isValid(1)) {
                // If pool is full, just close the connection
                if (!connectionPool.offer(conn)) {
                    conn.close();
                }
            } else {
                // Close invalid connections
                conn.close();
            }
        } catch (SQLException e) {
            System.err.println("Error releasing connection: " + e.getMessage());
            try {
                conn.close();
            } catch (SQLException ex) {
                // Ignore
            }
        }
    }
    
    /**
     * Closes all connections in the pool
     */
    public static void closeAllConnections() {
        for (Connection conn : connectionPool) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
        connectionPool.clear();
        initialized = false;
    }
}
