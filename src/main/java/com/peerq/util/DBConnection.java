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
    
    // Connection pool properties
    private static final int POOL_SIZE = 5;
    private static final BlockingQueue<Connection> connectionPool = new ArrayBlockingQueue<>(POOL_SIZE);
    private static boolean initialized = false;
    
    /**
     * Build a JDBC connection URL from the environment variables
     */
    private static String buildConnectionUrl() {
        try {
            // Get database URL from environment
            String dbUrl = System.getenv("DATABASE_URL");
            
            if (dbUrl != null && !dbUrl.isEmpty()) {
                // Database URL in format postgresql://username:password@host:port/database?params
                if (dbUrl.startsWith("postgresql://")) {
                    // Extract credentials and host info
                    String noPrefix = dbUrl.substring("postgresql://".length());
                    int atIndex = noPrefix.indexOf('@');
                    
                    if (atIndex > 0) {
                        // Get credentials part (username:password)
                        String credentials = noPrefix.substring(0, atIndex);
                        // Get host part (host:port/database?params)
                        String hostPart = noPrefix.substring(atIndex + 1);
                        
                        // Extract username and password
                        String[] credentialParts = credentials.split(":");
                        String username = credentialParts[0];
                        String password = credentialParts.length > 1 ? credentialParts[1] : "";
                        
                        return "jdbc:postgresql://" + hostPart;
                    }
                }
            }
            
            // If DATABASE_URL is not available or not in the expected format,
            // build from individual environment variables
            String host = System.getenv("PGHOST");
            String port = System.getenv("PGPORT");
            String database = System.getenv("PGDATABASE");
            
            if (host != null && port != null && database != null) {
                return "jdbc:postgresql://" + host + ":" + port + "/" + database;
            }
            
            throw new IllegalStateException("Cannot build database URL: missing environment variables");
        } catch (Exception e) {
            System.err.println("Error building database URL: " + e.getMessage());
            throw new RuntimeException("Failed to build database connection URL", e);
        }
    }
    
    /**
     * Get database credentials from environment variables
     */
    private static Properties getConnectionProps() {
        Properties props = new Properties();
        
        // Get database URL from environment
        String dbUrl = System.getenv("DATABASE_URL");
        
        if (dbUrl != null && !dbUrl.isEmpty() && dbUrl.startsWith("postgresql://")) {
            // Extract credentials from URL
            String noPrefix = dbUrl.substring("postgresql://".length());
            int atIndex = noPrefix.indexOf('@');
            
            if (atIndex > 0) {
                // Get credentials part (username:password)
                String credentials = noPrefix.substring(0, atIndex);
                
                // Extract username and password
                String[] credentialParts = credentials.split(":");
                String username = credentialParts[0];
                String password = credentialParts.length > 1 ? credentialParts[1] : "";
                
                props.setProperty("user", username);
                props.setProperty("password", password);
                return props;
            }
        }
        
        // If we can't extract from DATABASE_URL, use individual environment variables
        String username = System.getenv("PGUSER");
        String password = System.getenv("PGPASSWORD");
        
        if (username != null) {
            props.setProperty("user", username);
        }
        
        if (password != null) {
            props.setProperty("password", password);
        }
        
        return props;
    }
    
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
            
            // Build the connection URL
            String url = buildConnectionUrl();
            System.out.println("Connecting to database with URL: " + url);
            
            // Get connection properties (credentials)
            Properties props = getConnectionProps();
            
            // Initialize the connection pool
            for (int i = 0; i < POOL_SIZE; i++) {
                Connection conn = DriverManager.getConnection(url, props);
                connectionPool.add(conn);
            }
            
            initialized = true;
            System.out.println("Database connection pool initialized successfully.");
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
                String url = buildConnectionUrl();
                Properties props = getConnectionProps();
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