package com.peerq.desktopui;

import javax.swing.*;
import java.awt.*;

public class QuestionListPanel extends JPanel {
    private final PeerQMainFrame mainFrame;
    private JPanel questionsContainer;
    
    public QuestionListPanel(PeerQMainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(new Color(255, 255, 255));
        
        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(255, 255, 255));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Recent Questions");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        
        // Category filter
        String[] categories = {"All Categories", "Academics", "Campus Life", "Career", "Technology", "Miscellaneous"};
        JComboBox<String> categoryFilter = new JComboBox<>(categories);
        categoryFilter.setPreferredSize(new Dimension(150, 30));
        
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controlsPanel.setBackground(new Color(255, 255, 255));
        controlsPanel.add(categoryFilter);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(controlsPanel, BorderLayout.EAST);
        
        // Create questions container
        questionsContainer = new JPanel();
        questionsContainer.setLayout(new BoxLayout(questionsContainer, BoxLayout.Y_AXIS));
        questionsContainer.setBackground(new Color(255, 255, 255));
        
        // Add sample questions (replace with actual data later)
        addSampleQuestions();
        
        // Create scroll pane for questions
        JScrollPane scrollPane = new JScrollPane(questionsContainer);
        scrollPane.setBorder(null);
        scrollPane.setBackground(new Color(255, 255, 255));
        
        // Add components to panel
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void addSampleQuestions() {
        // Sample question 1
        JPanel questionPanel = createQuestionPanel(
            "How to prepare for final exams?",
            "I'm struggling with time management for my final exams. Any tips?",
            "Academics",
            "5 answers",
            "2 hours ago"
        );
        questionsContainer.add(questionPanel);
        
        // Sample question 2
        questionPanel = createQuestionPanel(
            "Best places to study on campus?",
            "Looking for quiet places to study during exam season.",
            "Campus Life",
            "3 answers",
            "5 hours ago"
        );
        questionsContainer.add(questionPanel);
        
        // Add some spacing between questions
        questionsContainer.add(Box.createVerticalStrut(10));
    }
    
    private JPanel createQuestionPanel(String title, String preview, String category, String answers, String time) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(255, 255, 255));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Title and preview
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(255, 255, 255));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        JLabel previewLabel = new JLabel(preview);
        previewLabel.setForeground(new Color(100, 100, 100));
        
        contentPanel.add(titleLabel, BorderLayout.NORTH);
        contentPanel.add(previewLabel, BorderLayout.CENTER);
        
        // Meta information
        JPanel metaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        metaPanel.setBackground(new Color(255, 255, 255));
        
        JLabel categoryLabel = new JLabel(category);
        categoryLabel.setForeground(new Color(0, 120, 212));
        
        JLabel answersLabel = new JLabel(answers);
        answersLabel.setForeground(new Color(100, 100, 100));
        
        JLabel timeLabel = new JLabel(time);
        timeLabel.setForeground(new Color(100, 100, 100));
        
        metaPanel.add(categoryLabel);
        metaPanel.add(Box.createHorizontalStrut(10));
        metaPanel.add(answersLabel);
        metaPanel.add(Box.createHorizontalStrut(10));
        metaPanel.add(timeLabel);
        
        contentPanel.add(metaPanel, BorderLayout.SOUTH);
        
        // Add click listener to show question detail
        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mainFrame.showQuestionDetail();
            }
        });
        
        panel.add(contentPanel, BorderLayout.CENTER);
        return panel;
    }
} 