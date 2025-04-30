package com.peerq.ui;

import com.peerq.dao.UserDAO;
import com.peerq.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Login form for the PeerQ application.
 */
public class LoginForm extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel registerLink;
    private JLabel errorLabel;
    
    /**
     * Constructor that initializes the login form
     */
    public LoginForm() {
        initializeUI();
    }
    
    /**
     * Sets up the UI components
     */
    private void initializeUI() {
        // Basic window setup
        setTitle(UIConstants.APP_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 450);
        setLocationRelativeTo(null); // Center on screen
        setResizable(false);
        
        // Main content panel with padding
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(UIConstants.BG_COLOR);
        contentPanel.setBorder(UIConstants.PANEL_PADDING);
        
        // App title/logo
        JLabel titleLabel = new JLabel(UIConstants.APP_TITLE);
        titleLabel.setFont(UIConstants.TITLE_FONT);
        titleLabel.setForeground(UIConstants.PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("College Community Platform");
        subtitleLabel.setFont(UIConstants.REGULAR_FONT);
        subtitleLabel.setForeground(UIConstants.LIGHT_TEXT_COLOR);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Card panel for the login form
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBackground(UIConstants.CARD_BG_COLOR);
        cardPanel.setBorder(UIConstants.CARD_BORDER);
        cardPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Login title
        JLabel loginTitle = new JLabel("Log In");
        loginTitle.setFont(UIConstants.SUBTITLE_FONT);
        loginTitle.setForeground(UIConstants.TEXT_COLOR);
        loginTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Error label (initially hidden)
        errorLabel = new JLabel(" ");
        errorLabel.setFont(UIConstants.SMALL_FONT);
        errorLabel.setForeground(UIConstants.ERROR_COLOR);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Email field
        JPanel emailPanel = new JPanel(new BorderLayout());
        emailPanel.setBackground(UIConstants.CARD_BG_COLOR);
        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(UIConstants.REGULAR_FONT);
        emailLabel.setForeground(UIConstants.TEXT_COLOR);
        emailField = new JTextField(20);
        emailField.setFont(UIConstants.REGULAR_FONT);
        emailField.setBorder(UIConstants.FIELD_BORDER);
        emailField.setPreferredSize(new Dimension(emailField.getPreferredSize().width, UIConstants.FORM_FIELD_HEIGHT));
        emailPanel.add(emailLabel, BorderLayout.NORTH);
        emailPanel.add(emailField, BorderLayout.CENTER);
        emailPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, emailPanel.getPreferredSize().height));
        
        // Password field
        JPanel passwordPanel = new JPanel(new BorderLayout());
        passwordPanel.setBackground(UIConstants.CARD_BG_COLOR);
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(UIConstants.REGULAR_FONT);
        passwordLabel.setForeground(UIConstants.TEXT_COLOR);
        passwordField = new JPasswordField(20);
        passwordField.setFont(UIConstants.REGULAR_FONT);
        passwordField.setBorder(UIConstants.FIELD_BORDER);
        passwordField.setPreferredSize(new Dimension(passwordField.getPreferredSize().width, UIConstants.FORM_FIELD_HEIGHT));
        passwordPanel.add(passwordLabel, BorderLayout.NORTH);
        passwordPanel.add(passwordField, BorderLayout.CENTER);
        passwordPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, passwordPanel.getPreferredSize().height));
        
        // Login button
        loginButton = new JButton("Log In");
        loginButton.setFont(UIConstants.BUTTON_FONT);
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(UIConstants.PRIMARY_COLOR);
        loginButton.setBorder(UIConstants.BUTTON_BORDER);
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Button hover effect
        loginButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                loginButton.setBackground(UIConstants.PRIMARY_COLOR.darker());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                loginButton.setBackground(UIConstants.PRIMARY_COLOR);
            }
        });
        
        // Register link
        JPanel registerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        registerPanel.setBackground(UIConstants.CARD_BG_COLOR);
        registerPanel.add(new JLabel("Don't have an account?"));
        registerLink = new JLabel("Register");
        registerLink.setFont(UIConstants.REGULAR_FONT);
        registerLink.setForeground(UIConstants.PRIMARY_COLOR);
        registerLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerPanel.add(registerLink);
        
        // Add components to the card
        cardPanel.add(loginTitle);
        cardPanel.add(Box.createVerticalStrut(UIConstants.STANDARD_PADDING));
        cardPanel.add(errorLabel);
        cardPanel.add(Box.createVerticalStrut(UIConstants.SMALL_PADDING));
        cardPanel.add(emailPanel);
        cardPanel.add(Box.createVerticalStrut(UIConstants.STANDARD_PADDING));
        cardPanel.add(passwordPanel);
        cardPanel.add(Box.createVerticalStrut(UIConstants.LARGE_PADDING));
        cardPanel.add(loginButton);
        cardPanel.add(Box.createVerticalStrut(UIConstants.STANDARD_PADDING));
        cardPanel.add(registerPanel);
        
        // Add components to the main content panel
        contentPanel.add(Box.createVerticalStrut(UIConstants.LARGE_PADDING));
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(UIConstants.SMALL_PADDING));
        contentPanel.add(subtitleLabel);
        contentPanel.add(Box.createVerticalStrut(UIConstants.LARGE_PADDING));
        contentPanel.add(cardPanel);
        
        // Add the main panel to the frame
        add(contentPanel);
        
        // Set up action listeners
        setupActionListeners();
    }
    
    /**
     * Sets up action listeners for the form components
     */
    private void setupActionListeners() {
        // Login button action
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText().trim();
                String password = new String(passwordField.getPassword());
                
                // Basic validation
                if (email.isEmpty() || password.isEmpty()) {
                    errorLabel.setText("Please enter both email and password.");
                    return;
                }
                
                // Attempt login
                UserDAO userDAO = new UserDAO();
                User user = userDAO.validateLogin(email, password);
                
                if (user != null) {
                    // Login successful
                    JOptionPane.showMessageDialog(LoginForm.this, 
                            "Login successful! Welcome, " + user.getName() + "!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    
                    // Open dashboard
                    Dashboard dashboard = new Dashboard(user);
                    dashboard.setVisible(true);
                    
                    // Close login form
                    dispose();
                } else {
                    // Login failed
                    errorLabel.setText("Invalid email or password. Please try again.");
                    passwordField.setText("");
                }
            }
        });
        
        // Register link action
        registerLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                RegisterForm registerForm = new RegisterForm();
                registerForm.setVisible(true);
                dispose(); // Close the login form
            }
        });
    }
}
