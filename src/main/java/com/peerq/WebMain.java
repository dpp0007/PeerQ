package com.peerq;

import com.peerq.util.DBConnection;
import com.peerq.web.AuthServlet;
import com.peerq.web.QuestionServlet;
import com.peerq.web.StaticFileServlet;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * Main entry point for the PeerQ web application.
 */
public class WebMain {
    private static final int PORT = 5000;
    
    /**
     * Main method that starts the web application
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        // Initialize database connection
        try {
            // Test database connection
            DBConnection.getConnection().close();
            System.out.println("Database connection successful.");
        } catch (Exception e) {
            System.err.println("Error connecting to database: " + e.getMessage());
            System.exit(1);
        }
        
        // Create and configure the server
        Server server = new Server(PORT);
        
        // Create a servlet context
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        
        // Set up the resource base for static files
        String resourceBase = "";
        try {
            resourceBase = WebMain.class.getClassLoader().getResource("static").toExternalForm();
            context.setResourceBase(resourceBase);
        } catch (Exception e) {
            System.err.println("Warning: Could not find static resources folder. Using current directory.");
            resourceBase = "./src/main/resources/static";
            context.setResourceBase(resourceBase);
        }
        
        // Add servlets
        context.addServlet(new ServletHolder(new AuthServlet()), "/api/auth/*");
        context.addServlet(new ServletHolder(new QuestionServlet()), "/api/questions/*");
        
        // Add default servlet for static files
        ServletHolder defaultServlet = new ServletHolder("default", StaticFileServlet.class);
        defaultServlet.setInitParameter("resourceBase", resourceBase);
        defaultServlet.setInitParameter("dirAllowed", "false");
        context.addServlet(defaultServlet, "/");
        
        server.setHandler(context);
        
        // Start the server
        try {
            server.start();
            System.out.println("Server started on port " + PORT);
            
            // Add shutdown hook to close database connections
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                DBConnection.closeAllConnections();
                System.out.println("Application shutting down, closed database connections.");
            }));
            
            server.join();
        } catch (Exception e) {
            System.err.println("Error starting server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}