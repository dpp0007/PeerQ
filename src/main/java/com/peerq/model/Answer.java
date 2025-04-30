package com.peerq.model;

import java.io.Serializable;

/**
 * Answer model class representing an answer to a question in the PeerQ system.
 * Implements Serializable for object serialization.
 */
public class Answer implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private int questionId;
    private int userId;
    private String userName; // Denormalized for display purposes
    private String content;
    private String createdAt;
    private int upvotes;
    private boolean isAccepted;
    private boolean isAnonymous;
    
    /**
     * Default constructor
     */
    public Answer() {
    }
    
    /**
     * Constructor with essential answer information
     * 
     * @param questionId ID of the question being answered
     * @param userId ID of the user providing the answer
     * @param content Answer content
     * @param isAnonymous Whether the answer is posted anonymously
     */
    public Answer(int questionId, int userId, String content, boolean isAnonymous) {
        this.questionId = questionId;
        this.userId = userId;
        this.content = content;
        this.upvotes = 0;
        this.isAccepted = false;
        this.isAnonymous = isAnonymous;
    }
    
    /**
     * Full constructor with all fields
     */
    public Answer(int id, int questionId, int userId, String userName, String content, 
                 String createdAt, int upvotes, boolean isAccepted, boolean isAnonymous) {
        this.id = id;
        this.questionId = questionId;
        this.userId = userId;
        this.userName = userName;
        this.content = content;
        this.createdAt = createdAt;
        this.upvotes = upvotes;
        this.isAccepted = isAccepted;
        this.isAnonymous = isAnonymous;
    }
    
    // Getters and setters
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getQuestionId() {
        return questionId;
    }
    
    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    public int getUpvotes() {
        return upvotes;
    }
    
    public void setUpvotes(int upvotes) {
        this.upvotes = upvotes;
    }
    
    public boolean isAccepted() {
        return isAccepted;
    }
    
    public void setAccepted(boolean isAccepted) {
        this.isAccepted = isAccepted;
    }
    
    public boolean isAnonymous() {
        return isAnonymous;
    }
    
    public void setAnonymous(boolean isAnonymous) {
        this.isAnonymous = isAnonymous;
    }
    
    /**
     * Returns a string representation of the Answer object
     */
    @Override
    public String toString() {
        return "Answer{" +
                "id=" + id +
                ", questionId=" + questionId +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", upvotes=" + upvotes +
                ", isAccepted=" + isAccepted +
                ", isAnonymous=" + isAnonymous +
                '}';
    }
}
