package com.peerq.web;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.peerq.dao.AnswerDAO;
import com.peerq.dao.QuestionDAO;
import com.peerq.model.Answer;
import com.peerq.model.Question;
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
import java.util.List;

/**
 * Servlet to handle question operations (create, view, answer)
 */
public class QuestionServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final Gson gson = new Gson();
    private final QuestionDAO questionDAO = new QuestionDAO();
    private final AnswerDAO answerDAO = new AnswerDAO();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        
        // Get all questions
        if (pathInfo == null || pathInfo.equals("/")) {
            try {
                List<Question> questions = questionDAO.getAllQuestions();
                sendJsonResponse(response, HttpServletResponse.SC_OK, gson.toJson(questions));
            } catch (Exception e) {
                e.printStackTrace();
                sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error: " + e.getMessage());
            }
            return;
        }
        
        // Get a specific question
        try {
            // Extract question ID from path
            int questionId = Integer.parseInt(pathInfo.substring(1));
            
            // Get question
            Question question = questionDAO.getQuestionById(questionId);
            
            if (question != null) {
                // Get answers for this question
                List<Answer> answers = answerDAO.getAnswersByQuestionId(questionId);
                
                // Build response
                JsonObject responseBody = new JsonObject();
                responseBody.addProperty("success", true);
                responseBody.add("question", gson.toJsonTree(question));
                responseBody.add("answers", gson.toJsonTree(answers));
                
                sendJsonResponse(response, HttpServletResponse.SC_OK, gson.toJson(responseBody));
            } else {
                sendError(response, HttpServletResponse.SC_NOT_FOUND, "Question not found");
            }
        } catch (NumberFormatException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid question ID");
        } catch (Exception e) {
            e.printStackTrace();
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error: " + e.getMessage());
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Check if user is logged in
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "You must be logged in to perform this action");
            return;
        }
        
        User currentUser = (User) session.getAttribute("user");
        String pathInfo = request.getPathInfo();
        
        // Creating a new question
        if (pathInfo == null || pathInfo.equals("/")) {
            createQuestion(request, response, currentUser);
        } 
        // Adding an answer to a question
        else if (pathInfo.matches("/\\d+/answers")) {
            createAnswer(request, response, currentUser, pathInfo);
        }
        else {
            sendError(response, HttpServletResponse.SC_NOT_FOUND, "Endpoint not found");
        }
    }
    
    /**
     * Handle creating a new question
     */
    private void createQuestion(HttpServletRequest request, HttpServletResponse response, User currentUser)
            throws IOException {
        
        // Parse request body
        BufferedReader reader = request.getReader();
        JsonObject requestBody = gson.fromJson(reader, JsonObject.class);
        
        // Extract question details
        String title = requestBody.get("title").getAsString();
        String body = requestBody.get("body").getAsString();
        String category = requestBody.get("category").getAsString();
        String tags = requestBody.has("tags") ? requestBody.get("tags").getAsString() : "";
        boolean isAnonymous = requestBody.has("isAnonymous") && requestBody.get("isAnonymous").getAsBoolean();
        
        // Create new question
        Question newQuestion = new Question();
        newQuestion.setTitle(title);
        newQuestion.setBody(body);
        newQuestion.setCategory(category);
        newQuestion.setTags(tags);
        newQuestion.setUserId(currentUser.getId());
        newQuestion.setAnonymous(isAnonymous);
        
        try {
            // Save question to database
            boolean success = questionDAO.createQuestion(newQuestion);
            
            if (success) {
                // Send success response
                JsonObject responseBody = new JsonObject();
                responseBody.addProperty("success", true);
                responseBody.addProperty("message", "Question created successfully");
                responseBody.addProperty("questionId", newQuestion.getId());
                
                sendJsonResponse(response, HttpServletResponse.SC_CREATED, gson.toJson(responseBody));
            } else {
                sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to create question");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error: " + e.getMessage());
        }
    }
    
    /**
     * Handle creating a new answer to a question
     */
    private void createAnswer(HttpServletRequest request, HttpServletResponse response, User currentUser, String pathInfo)
            throws IOException {
        
        try {
            // Extract question ID from path
            int questionId = Integer.parseInt(pathInfo.split("/")[1]);
            
            // Check if question exists
            Question question = questionDAO.getQuestionById(questionId);
            if (question == null) {
                sendError(response, HttpServletResponse.SC_NOT_FOUND, "Question not found");
                return;
            }
            
            // Parse request body
            BufferedReader reader = request.getReader();
            JsonObject requestBody = gson.fromJson(reader, JsonObject.class);
            
            // Extract answer details
            String content = requestBody.get("content").getAsString();
            boolean isAnonymous = requestBody.has("isAnonymous") && requestBody.get("isAnonymous").getAsBoolean();
            
            // Create new answer
            Answer newAnswer = new Answer();
            newAnswer.setQuestionId(questionId);
            newAnswer.setUserId(currentUser.getId());
            newAnswer.setContent(content);
            newAnswer.setAnonymous(isAnonymous);
            
            // Save answer to database
            boolean success = answerDAO.createAnswer(newAnswer);
            
            if (success) {
                // Send success response
                JsonObject responseBody = new JsonObject();
                responseBody.addProperty("success", true);
                responseBody.addProperty("message", "Answer submitted successfully");
                responseBody.addProperty("answerId", newAnswer.getId());
                
                sendJsonResponse(response, HttpServletResponse.SC_CREATED, gson.toJson(responseBody));
            } else {
                sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to submit answer");
            }
        } catch (NumberFormatException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid question ID");
        } catch (SQLException e) {
            e.printStackTrace();
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error: " + e.getMessage());
        }
    }
    
    /**
     * Utility method to send JSON responses
     */
    private void sendJsonResponse(HttpServletResponse response, int status, String jsonContent) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.print(jsonContent);
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