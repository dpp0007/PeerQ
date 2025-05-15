package com.peerq.ui;

import com.peerq.dao.UserDAO;
import com.peerq.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.regex.Pattern;

/**
 * Registration form for the PeerQ application.
 */
public class RegisterForm extends JFrame {
    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton registerButton;
    private JLabel loginLink;
    private JLabel errorLabel;
    
    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = 
            Pattern.compile("^[A-Za-z0-9._%+-]+@galgotiasuniversity\\.ac\\.in$");
    
    /**
     * Constructor that initializes the registration form
     */
    public RegisterForm() {
        initializeUI();
    }
    
    /**
     * Sets up the UI components
     */
    private void initializeUI() {
        // Basic window setup
        setTitle(UIConstants.APP_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 550);
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
        
        // Card panel for the registration form
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBackground(UIConstants.CARD_BG_COLOR);
        cardPanel.setBorder(UIConstants.CARD_BORDER);
        cardPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Register title
        JLabel registerTitle = new JLabel("Create Account");
        registerTitle.setFont(UIConstants.SUBTITLE_FONT);
        registerTitle.setForeground(UIConstants.TEXT_COLOR);
        registerTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Error label (initially hidden)
        errorLabel = new JLabel(" ");
        errorLabel.setFont(UIConstants.SMALL_FONT);
        errorLabel.setForeground(UIConstants.ERROR_COLOR);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Name field
        JPanel namePanel = new JPanel(new BorderLayout());
        namePanel.setBackground(UIConstants.CARD_BG_COLOR);
        JLabel nameLabel = new JLabel("Full Name");
        nameLabel.setFont(UIConstants.REGULAR_FONT);
        nameLabel.setForeground(UIConstants.TEXT_COLOR);
        nameField = new JTextField(20);
        nameField.setFont(UIConstants.REGULAR_FONT);
        nameField.setBorder(UIConstants.FIELD_BORDER);
        nameField.setPreferredSize(new Dimension(nameField.getPreferredSize().width, UIConstants.FORM_FIELD_HEIGHT));
        namePanel.add(nameLabel, BorderLayout.NORTH);
        namePanel.add(nameField, BorderLayout.CENTER);
        namePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, namePanel.getPreferredSize().height));
        
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
        
        // Confirm Password field
        JPanel confirmPasswordPanel = new JPanel(new BorderLayout());
        confirmPasswordPanel.setBackground(UIConstants.CARD_BG_COLOR);
        JLabel confirmPasswordLabel = new JLabel("Confirm Password");
        confirmPasswordLabel.setFont(UIConstants.REGULAR_FONT);
        confirmPasswordLabel.setForeground(UIConstants.TEXT_COLOR);
        confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setFont(UIConstants.REGULAR_FONT);
        confirmPasswordField.setBorder(UIConstants.FIELD_BORDER);
        confirmPasswordField.setPreferredSize(new Dimension(confirmPasswordField.getPreferredSize().width, UIConstants.FORM_FIELD_HEIGHT));
        confirmPasswordPanel.add(confirmPasswordLabel, BorderLayout.NORTH);
        confirmPasswordPanel.add(confirmPasswordField, BorderLayout.CENTER);
        confirmPasswordPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, confirmPasswordPanel.getPreferredSize().height));
        
        // Register button
        registerButton = new JButton("Register");
        registerButton.setFont(UIConstants.BUTTON_FONT);
        registerButton.setForeground(Color.WHITE);
        registerButton.setBackground(UIConstants.PRIMARY_COLOR);
        registerButton.setBorder(UIConstants.BUTTON_BORDER);
        registerButton.setFocusPainted(false);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Button hover effect
        registerButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                registerButton.setBackground(UIConstants.PRIMARY_COLOR.darker());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                registerButton.setBackground(UIConstants.PRIMARY_COLOR);
            }
        });
        
        // Login link
        JPanel loginPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        loginPanel.setBackground(UIConstants.CARD_BG_COLOR);
        loginPanel.add(new JLabel("Already have an account?"));
        loginLink = new JLabel("Log In");
        loginLink.setFont(UIConstants.REGULAR_FONT);
        loginLink.setForeground(UIConstants.PRIMARY_COLOR);
        loginLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginPanel.add(loginLink);
        
        // Add components to the card
        cardPanel.add(registerTitle);
        cardPanel.add(Box.createVerticalStrut(UIConstants.STANDARD_PADDING));
        cardPanel.add(errorLabel);
        cardPanel.add(Box.createVerticalStrut(UIConstants.SMALL_PADDING));
        cardPanel.add(namePanel);
        cardPanel.add(Box.createVerticalStrut(UIConstants.STANDARD_PADDING));
        cardPanel.add(emailPanel);
        cardPanel.add(Box.createVerticalStrut(UIConstants.STANDARD_PADDING));
        cardPanel.add(passwordPanel);
        cardPanel.add(Box.createVerticalStrut(UIConstants.STANDARD_PADDING));
        cardPanel.add(confirmPasswordPanel);
        cardPanel.add(Box.createVerticalStrut(UIConstants.LARGE_PADDING));
        cardPanel.add(registerButton);
        cardPanel.add(Box.createVerticalStrut(UIConstants.STANDARD_PADDING));
        cardPanel.add(loginPanel);
        
        // Add components to the main content panel
        contentPanel.add(Box.createVerticalStrut(UIConstants.STANDARD_PADDING));
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(UIConstants.SMALL_PADDING));
        contentPanel.add(subtitleLabel);
        contentPanel.add(Box.createVerticalStrut(UIConstants.STANDARD_PADDING));
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
        // Register button action
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get form values
                String name = nameField.getText().trim();
                String email = emailField.getText().trim();
                String password = new String(passwordField.getPassword());
                String confirmPassword = new String(confirmPasswordField.getPassword());
                
                // Validation
                if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    errorLabel.setText("All fields are required.");
                    return;
                }
                
                if (!EMAIL_PATTERN.matcher(email).matches()) {
                    errorLabel.setText("Please use a valid Galgotias University email address (@galgotiasuniversity.ac.in).");
                    return;
                }
                
                if (password.length() < 6) {
                    errorLabel.setText("Password must be at least 6 characters long.");
                    return;
                }
                
                if (!password.equals(confirmPassword)) {
                    errorLabel.setText("Passwords do not match.");
                    return;
                }
                
                // Create user object
                User user = new User(name, email, password);
                
                // Register the user
                UserDAO userDAO = new UserDAO();
                int userId = userDAO.registerUser(user);
                
                if (userId > 0) {
                    // Registration successful
                    JOptionPane.showMessageDialog(RegisterForm.this, 
                            "Registration successful! You can now log in.",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    
                    // Open login form
                    LoginForm loginForm = new LoginForm();
                    loginForm.setVisible(true);
                    
                    // Close registration form
                    dispose();
                } else if (userId == -1) {
                    // Email already exists
                    errorLabel.setText("Email is already registered.");
                } else {
                    // Other error
                    errorLabel.setText("Registration failed. Please try again.");
                }
            }
        });
        
        // Login link action
        loginLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                LoginForm loginForm = new LoginForm();
                loginForm.setVisible(true);
                dispose(); // Close the registration form
            }
        });
    }
}
