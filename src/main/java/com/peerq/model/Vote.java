package com.peerq.model;

import java.io.Serializable;

/**
 * Vote model class representing a user's vote on an answer in the PeerQ system.
 * Implements Serializable for object serialization.
 */
public class Vote implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private int answerId;
    private int userId;
    private String voteType; // "upvote" or "downvote"
    private String createdAt;
    
    /**
     * Default constructor
     */
    public Vote() {
    }
    
    /**
     * Constructor with essential vote information
     * 
     * @param answerId ID of the answer being voted on
     * @param userId ID of the user voting
     * @param voteType Type of vote ("upvote" or "downvote")
     */
    public Vote(int answerId, int userId, String voteType) {
        this.answerId = answerId;
        this.userId = userId;
        this.voteType = voteType;
    }
    
    /**
     * Full constructor with all fields
     */
    public Vote(int id, int answerId, int userId, String voteType, String createdAt) {
        this.id = id;
        this.answerId = answerId;
        this.userId = userId;
        this.voteType = voteType;
        this.createdAt = createdAt;
    }
    
    // Getters and setters
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getAnswerId() {
        return answerId;
    }
    
    public void setAnswerId(int answerId) {
        this.answerId = answerId;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getVoteType() {
        return voteType;
    }
    
    public void setVoteType(String voteType) {
        this.voteType = voteType;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    /**
     * Returns a string representation of the Vote object
     */
    @Override
    public String toString() {
        return "Vote{" +
                "id=" + id +
                ", answerId=" + answerId +
                ", userId=" + userId +
                ", voteType='" + voteType + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}
