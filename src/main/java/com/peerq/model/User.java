package com.peerq.model;

import java.io.Serializable;

/**
 * User model class representing a user in the PeerQ system.
 * Implements Serializable for object serialization.
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private String name;
    private String email;
    private String password;
    private int reputation;
    private boolean isAdmin;
    private String createdAt;
    
    /**
     * Default constructor
     */
    public User() {
    }
    
    /**
     * Constructor with essential user information
     * 
     * @param name User's name
     * @param email User's email
     * @param password User's password
     */
    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.reputation = 0;
        this.isAdmin = false;
    }
    
    /**
     * Full constructor with all fields
     * 
     * @param id User ID
     * @param name User's name
     * @param email User's email
     * @param password User's password
     * @param reputation User's reputation score
     * @param isAdmin Administrator status
     * @param createdAt Account creation timestamp
     */
    public User(int id, String name, String email, String password, int reputation, boolean isAdmin, String createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.reputation = reputation;
        this.isAdmin = isAdmin;
        this.createdAt = createdAt;
    }
    
    // Getters and setters
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public int getReputation() {
        return reputation;
    }
    
    public void setReputation(int reputation) {
        this.reputation = reputation;
    }
    
    public boolean isAdmin() {
        return isAdmin;
    }
    
    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    /**
     * Returns a string representation of the User object
     */
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", reputation=" + reputation +
                ", isAdmin=" + isAdmin +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}
