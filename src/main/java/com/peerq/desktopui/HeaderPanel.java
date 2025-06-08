package com.peerq.desktopui;

import javax.swing.*;
import java.awt.*;

public class HeaderPanel extends JPanel {
    private final PeerQMainFrame mainFrame;
    
    public HeaderPanel(PeerQMainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(1200, 60));
        setBackground(new Color(255, 255, 255));
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));
        
        // Create header container
        JPanel headerContainer = new JPanel(new BorderLayout());
        headerContainer.setBackground(new Color(255, 255, 255));
        
        // Logo
        JLabel logo = new JLabel("PeerQ");
        logo.setFont(new Font("Arial", Font.BOLD, 24));
        logo.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        
        // Navigation
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        navPanel.setBackground(new Color(255, 255, 255));
        
        JButton homeBtn = new JButton("Home");
        JButton askQuestionBtn = new JButton("Ask Question");
        
        homeBtn.addActionListener(e -> mainFrame.showQuestionList());
        askQuestionBtn.addActionListener(e -> mainFrame.showAskQuestion());
        
        navPanel.add(homeBtn);
        navPanel.add(askQuestionBtn);
        
        // Search bar
        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        searchPanel.setBackground(new Color(255, 255, 255));
        
        JTextField searchField = new JTextField(30);
        searchField.setPreferredSize(new Dimension(300, 35));
        searchField.putClientProperty("JTextField.placeholderText", "Search questions, topics, or users...");
        
        JButton searchFiltersBtn = new JButton("Filters");
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchFiltersBtn, BorderLayout.EAST);
        
        // Auth section
        JPanel authPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        authPanel.setBackground(new Color(255, 255, 255));
        
        JButton loginBtn = new JButton("Log In");
        JButton registerBtn = new JButton("Register");
        
        loginBtn.addActionListener(e -> mainFrame.showAuth());
        registerBtn.addActionListener(e -> mainFrame.showAuth());
        
        authPanel.add(loginBtn);
        authPanel.add(registerBtn);
        
        // Add components to header container
        headerContainer.add(logo, BorderLayout.WEST);
        headerContainer.add(navPanel, BorderLayout.CENTER);
        headerContainer.add(searchPanel, BorderLayout.EAST);
        headerContainer.add(authPanel, BorderLayout.SOUTH);
        
        add(headerContainer, BorderLayout.CENTER);
    }
} 