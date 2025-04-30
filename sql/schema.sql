-- PeerQ Database Schema

-- Drop tables if they exist (order matters for foreign key constraints)
DROP TABLE IF EXISTS votes;
DROP TABLE IF EXISTS answers;
DROP TABLE IF EXISTS questions;
DROP TABLE IF EXISTS users;

-- Create users table
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,  -- In a production environment, use proper password hashing
    reputation INT DEFAULT 0,
    is_admin BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create questions table
CREATE TABLE questions (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    body TEXT NOT NULL,
    tags VARCHAR(255),
    user_id INT NOT NULL,
    is_solved BOOLEAN DEFAULT FALSE,
    category VARCHAR(50) NOT NULL,
    is_anonymous BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create answers table
CREATE TABLE answers (
    id SERIAL PRIMARY KEY,
    question_id INT NOT NULL,
    user_id INT NOT NULL,
    content TEXT NOT NULL,
    upvotes INT DEFAULT 0,
    is_accepted BOOLEAN DEFAULT FALSE,
    is_anonymous BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create votes table
CREATE TABLE votes (
    id SERIAL PRIMARY KEY,
    answer_id INT NOT NULL,
    user_id INT NOT NULL,
    vote_type VARCHAR(10) NOT NULL CHECK (vote_type IN ('upvote', 'downvote')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (answer_id) REFERENCES answers(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE (answer_id, user_id)  -- Prevent duplicate votes from same user
);

-- Create an admin user (password: admin123)
INSERT INTO users (name, email, password, reputation, is_admin) 
VALUES ('Admin User', 'admin@college.edu', 'admin123', 100, TRUE);

-- Create some sample categories if not using dynamic categories
-- This could be moved to a separate categories table in a more complex schema
-- INSERT INTO categories (name) VALUES 
-- ('Academics'), ('Clubs'), ('Dorm Life'), ('Events'), ('General'), ('Career');

-- Add indexes for better performance
CREATE INDEX idx_question_user ON questions(user_id);
CREATE INDEX idx_question_category ON questions(category);
CREATE INDEX idx_answer_question ON answers(question_id);
CREATE INDEX idx_answer_user ON answers(user_id);
CREATE INDEX idx_votes_answer ON votes(answer_id);
CREATE INDEX idx_votes_user ON votes(user_id);
