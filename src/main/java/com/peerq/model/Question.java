package com.peerq.model;

import java.io.Serializable;

/**
 * Question model class representing a question in the PeerQ system.
 * Implements Serializable for object serialization.
 */
public class Question implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private String title;
    private String body;
    private String tags;
    private int userId;
    private String userName; // Denormalized for display purposes
    private String createdAt;
    private boolean isSolved;
    private int answerCount; // Denormalized count for display purposes
    private String category;
    private boolean isAnonymous;
    
    /**
     * Default constructor
     */
    public Question() {
    }
    
    /**
     * Constructor with essential question information
     * 
     * @param title Question title
     * @param body Question content
     * @param tags Associated tags (comma-separated)
     * @param userId ID of the user asking the question
     * @param category Question category
     * @param isAnonymous Whether the question is posted anonymously
     */
    public Question(String title, String body, String tags, int userId, String category, boolean isAnonymous) {
        this.title = title;
        this.body = body;
        this.tags = tags;
        this.userId = userId;
        this.isSolved = false;
        this.answerCount = 0;
        this.category = category;
        this.isAnonymous = isAnonymous;
    }
    
    /**
     * Full constructor with all fields
     */
    public Question(int id, String title, String body, String tags, int userId, String userName, 
                   String createdAt, boolean isSolved, int answerCount, String category, boolean isAnonymous) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.tags = tags;
        this.userId = userId;
        this.userName = userName;
        this.createdAt = createdAt;
        this.isSolved = isSolved;
        this.answerCount = answerCount;
        this.category = category;
        this.isAnonymous = isAnonymous;
    }
    
    // Getters and setters
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getBody() {
        return body;
    }
    
    public void setBody(String body) {
        this.body = body;
    }
    
    public String getTags() {
        return tags;
    }
    
    public void setTags(String tags) {
        this.tags = tags;
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
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    public boolean isSolved() {
        return isSolved;
    }
    
    public void setSolved(boolean isSolved) {
        this.isSolved = isSolved;
    }
    
    public int getAnswerCount() {
        return answerCount;
    }
    
    public void setAnswerCount(int answerCount) {
        this.answerCount = answerCount;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public boolean isAnonymous() {
        return isAnonymous;
    }
    
    public void setAnonymous(boolean isAnonymous) {
        this.isAnonymous = isAnonymous;
    }
    
    /**
     * Returns a string representation of the Question object
     */
    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", tags='" + tags + '\'' +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", isSolved=" + isSolved +
                ", answerCount=" + answerCount +
                ", category='" + category + '\'' +
                ", isAnonymous=" + isAnonymous +
                '}';
    }
    
    /**
     * Returns a shortened version of the body for preview purposes
     * 
     * @param maxLength Maximum length of the preview
     * @return Shortened body text
     */
    public String getBodyPreview(int maxLength) {
        if (body.length() <= maxLength) {
            return body;
        }
        return body.substring(0, maxLength - 3) + "...";
    }
}
