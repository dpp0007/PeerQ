package com.peerq.desktopui;

import javax.swing.*;
import java.awt.*;

public class QuestionDetailPanel extends JPanel {
    private final PeerQMainFrame mainFrame;
    private JPanel answersContainer;
    
    public QuestionDetailPanel(PeerQMainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(new Color(255, 255, 255));
        
        // Create header with back button
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(255, 255, 255));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JButton backButton = new JButton("← Back to Questions");
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setForeground(new Color(0, 120, 212));
        backButton.addActionListener(e -> mainFrame.showQuestionList());
        
        headerPanel.add(backButton, BorderLayout.WEST);
        
        // Create question content panel
        JPanel questionPanel = createQuestionPanel();
        
        // Create answers container
        answersContainer = new JPanel();
        answersContainer.setLayout(new BoxLayout(answersContainer, BoxLayout.Y_AXIS));
        answersContainer.setBackground(new Color(255, 255, 255));
        
        // Add sample answers
        addSampleAnswers();
        
        // Create answer form
        JPanel answerFormPanel = createAnswerForm();
        
        // Create scroll pane for answers
        JScrollPane scrollPane = new JScrollPane(answersContainer);
        scrollPane.setBorder(null);
        scrollPane.setBackground(new Color(255, 255, 255));
        
        // Add components to panel
        add(headerPanel, BorderLayout.NORTH);
        add(questionPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.CENTER);
        add(answerFormPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createQuestionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(255, 255, 255));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Question title
        JLabel titleLabel = new JLabel("How to prepare for final exams?");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        
        // Question meta
        JPanel metaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        metaPanel.setBackground(new Color(255, 255, 255));
        
        JLabel authorLabel = new JLabel("Posted by John Doe");
        JLabel timeLabel = new JLabel("2 hours ago");
        JLabel categoryLabel = new JLabel("Academics");
        categoryLabel.setForeground(new Color(0, 120, 212));
        
        metaPanel.add(authorLabel);
        metaPanel.add(Box.createHorizontalStrut(10));
        metaPanel.add(timeLabel);
        metaPanel.add(Box.createHorizontalStrut(10));
        metaPanel.add(categoryLabel);
        
        // Question content
        JTextArea contentArea = new JTextArea(
            "I'm struggling with time management for my final exams. " +
            "I have 5 subjects to prepare for and only 2 weeks left. " +
            "Any tips on how to organize my study schedule effectively?"
        );
        contentArea.setWrapStyleWord(true);
        contentArea.setLineWrap(true);
        contentArea.setEditable(false);
        contentArea.setBackground(new Color(255, 255, 255));
        contentArea.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Add components to panel
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(metaPanel, BorderLayout.CENTER);
        panel.add(contentArea, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void addSampleAnswers() {
        // Sample answer 1
        JPanel answerPanel = createAnswerPanel(
            "Jane Smith",
            "2 hours ago",
            "Here's what worked for me:\n\n" +
            "1. Create a study schedule with specific time slots for each subject\n" +
            "2. Take regular breaks (25 minutes study, 5 minutes break)\n" +
            "3. Use active recall techniques instead of passive reading\n" +
            "4. Form study groups for difficult topics\n" +
            "5. Get enough sleep and exercise"
        );
        answersContainer.add(answerPanel);
        
        // Sample answer 2
        answerPanel = createAnswerPanel(
            "Mike Johnson",
            "1 hour ago",
            "I recommend using the Pomodoro Technique. Study for 25 minutes, " +
            "then take a 5-minute break. After 4 pomodoros, take a longer break. " +
            "This helps maintain focus and prevents burnout."
        );
        answersContainer.add(answerPanel);
        
        // Add some spacing between answers
        answersContainer.add(Box.createVerticalStrut(10));
    }
    
    private JPanel createAnswerPanel(String author, String time, String content) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(255, 255, 255));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Author and time
        JPanel metaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        metaPanel.setBackground(new Color(255, 255, 255));
        
        JLabel authorLabel = new JLabel(author);
        JLabel timeLabel = new JLabel(time);
        timeLabel.setForeground(new Color(100, 100, 100));
        
        metaPanel.add(authorLabel);
        metaPanel.add(Box.createHorizontalStrut(10));
        metaPanel.add(timeLabel);
        
        // Answer content
        JTextArea contentArea = new JTextArea(content);
        contentArea.setWrapStyleWord(true);
        contentArea.setLineWrap(true);
        contentArea.setEditable(false);
        contentArea.setBackground(new Color(255, 255, 255));
        contentArea.setFont(new Font("Arial", Font.PLAIN, 14));
        
        panel.add(metaPanel, BorderLayout.NORTH);
        panel.add(contentArea, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createAnswerForm() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(255, 255, 255));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Your Answer");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        JTextArea answerArea = new JTextArea(6, 40);
        answerArea.setWrapStyleWord(true);
        answerArea.setLineWrap(true);
        
        JButton submitButton = new JButton("Submit Answer");
        submitButton.addActionListener(e -> {
            // TODO: Implement answer submission
            JOptionPane.showMessageDialog(this, "Answer submitted successfully!");
        });
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(new JScrollPane(answerArea), BorderLayout.CENTER);
        panel.add(submitButton, BorderLayout.SOUTH);
        
        return panel;
    }
} 