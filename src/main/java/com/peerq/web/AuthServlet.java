package com.peerq.web;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.peerq.dao.UserDAO;
import com.peerq.model.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

/**
 * Servlet to handle user authentication (login, registration, logout)
 */
public class AuthServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final Gson gson = new Gson();
    private final UserDAO userDAO = new UserDAO();
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid path");
            return;
        }
        
        // Handle different authentication endpoints
        if (pathInfo.equals("/login")) {
            handleLogin(request, response);
        } else if (pathInfo.equals("/register")) {
            handleRegister(request, response);
        } else if (pathInfo.equals("/logout")) {
            handleLogout(request, response);
        } else {
            sendError(response, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found");
        }
    }
    
    /**
     * Handle user login
     */
    private void handleLogin(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        // Parse request body
        BufferedReader reader = request.getReader();
        JsonObject requestBody = gson.fromJson(reader, JsonObject.class);
        
        // Extract login credentials
        String email = requestBody.get("email").getAsString();
        String password = requestBody.get("password").getAsString();
        
        try {
            // Authenticate user
            User user = userDAO.authenticateUser(email, password);
            
            if (user != null) {
                // Set user in session
                HttpSession session = request.getSession(true);
                session.setAttribute("user", user);
                
                // Send success response
                JsonObject responseBody = new JsonObject();
                responseBody.addProperty("success", true);
                responseBody.addProperty("message", "Login successful");
                responseBody.addProperty("userId", user.getId());
                responseBody.addProperty("userName", user.getName());
                responseBody.addProperty("isAdmin", user.isAdmin());
                
                response.setContentType("application/json");
                PrintWriter out = response.getWriter();
                out.print(gson.toJson(responseBody));
                out.flush();
            } else {
                sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid email or password");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error: " + e.getMessage());
        }
    }
    
    /**
     * Handle user registration
     */
    private void handleRegister(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        // Parse request body
        BufferedReader reader = request.getReader();
        JsonObject requestBody = gson.fromJson(reader, JsonObject.class);
        
        // Extract registration details
        String name = requestBody.get("name").getAsString();
        String email = requestBody.get("email").getAsString();
        String password = requestBody.get("password").getAsString();
        
        // Validate email domain (must be a Galgotias University email)
        if (!email.endsWith("galgotiasuniversity.ac.in")) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Registration requires a Galgotias University email address");
            return;
        }
        
        try {
            // Check if email already exists
            if (userDAO.getUserByEmail(email) != null) {
                sendError(response, HttpServletResponse.SC_CONFLICT, "Email already registered");
                return;
            }
            
            // Create new user
            User newUser = new User();
            newUser.setName(name);
            newUser.setEmail(email);
            newUser.setPassword(password); // In production, this should be hashed
            newUser.setReputation(0);
            newUser.setAdmin(false);
            
            // Save user to database
            boolean success = userDAO.createUser(newUser);
            
            if (success) {
                // Set user in session
                User createdUser = userDAO.getUserByEmail(email);
                HttpSession session = request.getSession(true);
                session.setAttribute("user", createdUser);
                
                // Send success response
                JsonObject responseBody = new JsonObject();
                responseBody.addProperty("success", true);
                responseBody.addProperty("message", "Registration successful");
                responseBody.addProperty("userId", createdUser.getId());
                responseBody.addProperty("userName", createdUser.getName());
                
                response.setContentType("application/json");
                PrintWriter out = response.getWriter();
                out.print(gson.toJson(responseBody));
                out.flush();
            } else {
                sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to create user");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error: " + e.getMessage());
        }
    }
    
    /**
     * Handle user logout
     */
    private void handleLogout(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        // Invalidate session
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        
        // Send success response
        JsonObject responseBody = new JsonObject();
        responseBody.addProperty("success", true);
        responseBody.addProperty("message", "Logout successful");
        
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.print(gson.toJson(responseBody));
        out.flush();
    }
    
    /**
     * Utility method to send error responses
     */
    private void sendError(HttpServletResponse response, int status, String message) throws IOException {
        JsonObject errorResponse = new JsonObject();
        errorResponse.addProperty("success", false);
        errorResponse.addProperty("message", message);
        
        response.setStatus(status);
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.print(gson.toJson(errorResponse));
        out.flush();
    }
}