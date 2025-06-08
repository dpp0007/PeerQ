package com.peerq.desktopui;

import javax.swing.*;
import java.awt.*;

public class PeerQMainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private HeaderPanel headerPanel;
    
    // Panels for different sections
    private QuestionListPanel questionListPanel;
    private QuestionDetailPanel questionDetailPanel;
    private AskQuestionPanel askQuestionPanel;
    private AuthPanel authPanel;
    
    public PeerQMainFrame() {
        setTitle("PeerQ - College Community Discourse Platform");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        // Initialize layout
        setLayout(new BorderLayout());
        
        // Create header panel
        headerPanel = new HeaderPanel(this);
        add(headerPanel, BorderLayout.NORTH);
        
        // Initialize main content panel
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(new Color(255, 255, 255));
        
        // Initialize panels
        questionListPanel = new QuestionListPanel(this);
        questionDetailPanel = new QuestionDetailPanel(this);
        askQuestionPanel = new AskQuestionPanel(this);
        authPanel = new AuthPanel(this);
        
        // Add panels to card layout
        mainPanel.add(questionListPanel, "QUESTION_LIST");
        mainPanel.add(questionDetailPanel, "QUESTION_DETAIL");
        mainPanel.add(askQuestionPanel, "ASK_QUESTION");
        mainPanel.add(authPanel, "AUTH");
        
        // Add main panel to frame
        add(mainPanel, BorderLayout.CENTER);
        
        // Show question list by default
        showQuestionList();
    }
    
    public void showQuestionList() {
        cardLayout.show(mainPanel, "QUESTION_LIST");
    }
    
    public void showQuestionDetail() {
        cardLayout.show(mainPanel, "QUESTION_DETAIL");
    }
    
    public void showAskQuestion() {
        cardLayout.show(mainPanel, "ASK_QUESTION");
    }
    
    public void showAuth() {
        cardLayout.show(mainPanel, "AUTH");
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new PeerQMainFrame().setVisible(true);
        });
    }
} 