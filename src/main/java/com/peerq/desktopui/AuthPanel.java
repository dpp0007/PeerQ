package com.peerq.desktopui;

import javax.swing.*;
import java.awt.*;

public class AuthPanel extends JPanel {
    private final PeerQMainFrame mainFrame;
    private CardLayout cardLayout;
    private JPanel formsContainer;
    
    public AuthPanel(PeerQMainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        
        // Create forms container with card layout
        cardLayout = new CardLayout();
        formsContainer = new JPanel(cardLayout);
        formsContainer.setBackground(new Color(255, 255, 255));
        
        // Create login form
        JPanel loginForm = createLoginForm();
        formsContainer.add(loginForm, "LOGIN");
        
        // Create registration form
        JPanel registerForm = createRegisterForm();
        formsContainer.add(registerForm, "REGISTER");
        
        // Add forms container to panel
        add(formsContainer, BorderLayout.CENTER);
        
        // Show login form by default
        showLoginForm();
    }
    
    private JPanel createLoginForm() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(255, 255, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Title
        JLabel titleLabel = new JLabel("Log In");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);
        
        // Email field
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        JLabel emailLabel = new JLabel("Email");
        panel.add(emailLabel, gbc);
        
        gbc.gridy = 2;
        JTextField emailField = new JTextField(20);
        panel.add(emailField, gbc);
        
        // Password field
        gbc.gridy = 3;
        JLabel passwordLabel = new JLabel("Password");
        panel.add(passwordLabel, gbc);
        
        gbc.gridy = 4;
        JPasswordField passwordField = new JPasswordField(20);
        panel.add(passwordField, gbc);
        
        // Login button
        gbc.gridy = 5;
        JButton loginButton = new JButton("Log In");
        panel.add(loginButton, gbc);
        
        // Register link
        gbc.gridy = 6;
        JLabel registerLabel = new JLabel("Don't have an account? ");
        JButton registerLink = new JButton("Register");
        registerLink.setBorderPainted(false);
        registerLink.setContentAreaFilled(false);
        registerLink.setForeground(new Color(0, 120, 212));
        
        JPanel registerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        registerPanel.setBackground(new Color(255, 255, 255));
        registerPanel.add(registerLabel);
        registerPanel.add(registerLink);
        panel.add(registerPanel, gbc);
        
        // Add action listeners
        registerLink.addActionListener(e -> showRegisterForm());
        loginButton.addActionListener(e -> {
            // TODO: Implement login logic
            mainFrame.showQuestionList();
        });
        
        return panel;
    }
    
    private JPanel createRegisterForm() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(255, 255, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Title
        JLabel titleLabel = new JLabel("Register");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);
        
        // Full Name field
        gbc.gridy = 1;
        JLabel nameLabel = new JLabel("Full Name");
        panel.add(nameLabel, gbc);
        
        gbc.gridy = 2;
        JTextField nameField = new JTextField(20);
        panel.add(nameField, gbc);
        
        // Email field
        gbc.gridy = 3;
        JLabel emailLabel = new JLabel("Galgotias University Email");
        panel.add(emailLabel, gbc);
        
        gbc.gridy = 4;
        JTextField emailField = new JTextField(20);
        panel.add(emailField, gbc);
        
        // Password field
        gbc.gridy = 5;
        JLabel passwordLabel = new JLabel("Password");
        panel.add(passwordLabel, gbc);
        
        gbc.gridy = 6;
        JPasswordField passwordField = new JPasswordField(20);
        panel.add(passwordField, gbc);
        
        // Confirm Password field
        gbc.gridy = 7;
        JLabel confirmPasswordLabel = new JLabel("Confirm Password");
        panel.add(confirmPasswordLabel, gbc);
        
        gbc.gridy = 8;
        JPasswordField confirmPasswordField = new JPasswordField(20);
        panel.add(confirmPasswordField, gbc);
        
        // Register button
        gbc.gridy = 9;
        JButton registerButton = new JButton("Register");
        panel.add(registerButton, gbc);
        
        // Login link
        gbc.gridy = 10;
        JLabel loginLabel = new JLabel("Already have an account? ");
        JButton loginLink = new JButton("Log In");
        loginLink.setBorderPainted(false);
        loginLink.setContentAreaFilled(false);
        loginLink.setForeground(new Color(0, 120, 212));
        
        JPanel loginPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        loginPanel.setBackground(new Color(255, 255, 255));
        loginPanel.add(loginLabel);
        loginPanel.add(loginLink);
        panel.add(loginPanel, gbc);
        
        // Add action listeners
        loginLink.addActionListener(e -> showLoginForm());
        registerButton.addActionListener(e -> {
            // TODO: Implement registration logic
            mainFrame.showQuestionList();
        });
        
        return panel;
    }
    
    public void showLoginForm() {
        cardLayout.show(formsContainer, "LOGIN");
    }
    
    public void showRegisterForm() {
        cardLayout.show(formsContainer, "REGISTER");
    }
} 