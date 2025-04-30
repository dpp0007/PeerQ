package com.peerq.ui;

import com.peerq.dao.QuestionDAO;
import com.peerq.model.Question;
import com.peerq.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Form for asking a new question.
 */
public class AskQuestionForm extends JDialog {
    private final User currentUser;
    private final QuestionDAO questionDAO;
    
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JTextField tagsField;
    private JComboBox<String> categoryComboBox;
    private JCheckBox anonymousCheckbox;
    private JButton submitButton;
    private JButton cancelButton;
    
    /**
     * Constructor for the Ask Question form
     * 
     * @param user The currently logged in user
     */
    public AskQuestionForm(User user) {
        this.currentUser = user;
        this.questionDAO = new QuestionDAO();
        
        initializeUI();
    }
    
    /**
     * Sets up the UI components
     */
    private void initializeUI() {
        // Basic window setup
        setTitle("Ask a Question");
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(700, 600);
        setLocationRelativeTo(null); // Center on screen
        setModal(true);
        setResizable(true);
        
        // Main content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(UIConstants.BG_COLOR);
        contentPanel.setBorder(UIConstants.PANEL_PADDING);
        
        // Title
        JLabel titleLabel = new JLabel("Ask a Question");
        titleLabel.setFont(UIConstants.TITLE_FONT);
        titleLabel.setForeground(UIConstants.PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Form card
        JPanel formCard = new JPanel();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBackground(UIConstants.CARD_BG_COLOR);
        formCard.setBorder(UIConstants.CARD_BORDER);
        formCard.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Question title field
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(UIConstants.CARD_BG_COLOR);
        JLabel titleFieldLabel = new JLabel("Question Title");
        titleFieldLabel.setFont(UIConstants.REGULAR_FONT);
        titleFieldLabel.setForeground(UIConstants.TEXT_COLOR);
        titleField = new JTextField();
        titleField.setFont(UIConstants.REGULAR_FONT);
        titleField.setBorder(UIConstants.FIELD_BORDER);
        titleField.setPreferredSize(new Dimension(titleField.getPreferredSize().width, UIConstants.FORM_FIELD_HEIGHT));
        titlePanel.add(titleFieldLabel, BorderLayout.NORTH);
        titlePanel.add(titleField, BorderLayout.CENTER);
        titlePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, titlePanel.getPreferredSize().height));
        
        // Description area
        JPanel descriptionPanel = new JPanel(new BorderLayout());
        descriptionPanel.setBackground(UIConstants.CARD_BG_COLOR);
        JLabel descriptionLabel = new JLabel("Description");
        descriptionLabel.setFont(UIConstants.REGULAR_FONT);
        descriptionLabel.setForeground(UIConstants.TEXT_COLOR);
        descriptionArea = new JTextArea();
        descriptionArea.setFont(UIConstants.REGULAR_FONT);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBorder(UIConstants.FIELD_BORDER);
        
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea);
        descriptionScrollPane.setPreferredSize(new Dimension(descriptionScrollPane.getPreferredSize().width, 200));
        
        descriptionPanel.add(descriptionLabel, BorderLayout.NORTH);
        descriptionPanel.add(descriptionScrollPane, BorderLayout.CENTER);
        descriptionPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));
        
        // Tags field
        JPanel tagsPanel = new JPanel(new BorderLayout());
        tagsPanel.setBackground(UIConstants.CARD_BG_COLOR);
        JLabel tagsLabel = new JLabel("Tags (comma separated)");
        tagsLabel.setFont(UIConstants.REGULAR_FONT);
        tagsLabel.setForeground(UIConstants.TEXT_COLOR);
        tagsField = new JTextField();
        tagsField.setFont(UIConstants.REGULAR_FONT);
        tagsField.setBorder(UIConstants.FIELD_BORDER);
        tagsField.setPreferredSize(new Dimension(tagsField.getPreferredSize().width, UIConstants.FORM_FIELD_HEIGHT));
        tagsPanel.add(tagsLabel, BorderLayout.NORTH);
        tagsPanel.add(tagsField, BorderLayout.CENTER);
        tagsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, tagsPanel.getPreferredSize().height));
        
        // Category selection
        JPanel categoryPanel = new JPanel(new BorderLayout());
        categoryPanel.setBackground(UIConstants.CARD_BG_COLOR);
        JLabel categoryLabel = new JLabel("Category");
        categoryLabel.setFont(UIConstants.REGULAR_FONT);
        categoryLabel.setForeground(UIConstants.TEXT_COLOR);
        categoryComboBox = new JComboBox<>(UIConstants.QUESTION_CATEGORIES);
        categoryComboBox.setFont(UIConstants.REGULAR_FONT);
        categoryComboBox.setBorder(UIConstants.FIELD_BORDER);
        categoryComboBox.setPreferredSize(new Dimension(categoryComboBox.getPreferredSize().width, UIConstants.FORM_FIELD_HEIGHT));
        categoryPanel.add(categoryLabel, BorderLayout.NORTH);
        categoryPanel.add(categoryComboBox, BorderLayout.CENTER);
        categoryPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, categoryPanel.getPreferredSize().height));
        
        // Anonymous checkbox
        anonymousCheckbox = new JCheckBox("Post Anonymously");
        anonymousCheckbox.setFont(UIConstants.REGULAR_FONT);
        anonymousCheckbox.setForeground(UIConstants.TEXT_COLOR);
        anonymousCheckbox.setBackground(UIConstants.CARD_BG_COLOR);
        anonymousCheckbox.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonsPanel.setBackground(UIConstants.CARD_BG_COLOR);
        
        submitButton = new JButton("Submit Question");
        submitButton.setFont(UIConstants.BUTTON_FONT);
        submitButton.setForeground(Color.WHITE);
        submitButton.setBackground(UIConstants.PRIMARY_COLOR);
        submitButton.setBorder(UIConstants.BUTTON_BORDER);
        submitButton.setFocusPainted(false);
        submitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Button hover effect
        submitButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                submitButton.setBackground(UIConstants.PRIMARY_COLOR.darker());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                submitButton.setBackground(UIConstants.PRIMARY_COLOR);
            }
        });
        
        cancelButton = new JButton("Cancel");
        cancelButton.setFont(UIConstants.BUTTON_FONT);
        cancelButton.setForeground(UIConstants.TEXT_COLOR);
        cancelButton.setBackground(UIConstants.BG_COLOR);
        cancelButton.setBorder(UIConstants.BUTTON_BORDER);
        cancelButton.setFocusPainted(false);
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        buttonsPanel.add(submitButton);
        buttonsPanel.add(cancelButton);
        
        // Add components to the form card
        formCard.add(titlePanel);
        formCard.add(Box.createVerticalStrut(UIConstants.STANDARD_PADDING));
        formCard.add(descriptionPanel);
        formCard.add(Box.createVerticalStrut(UIConstants.STANDARD_PADDING));
        formCard.add(tagsPanel);
        formCard.add(Box.createVerticalStrut(UIConstants.STANDARD_PADDING));
        formCard.add(categoryPanel);
        formCard.add(Box.createVerticalStrut(UIConstants.STANDARD_PADDING));
        formCard.add(anonymousCheckbox);
        formCard.add(Box.createVerticalStrut(UIConstants.STANDARD_PADDING));
        formCard.add(buttonsPanel);
        
        // Add components to the content panel
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(UIConstants.STANDARD_PADDING));
        contentPanel.add(formCard);
        
        // Add content panel to the dialog
        add(contentPanel);
        
        // Set up action listeners
        setupActionListeners();
    }
    
    /**
     * Sets up action listeners for the form components
     */
    private void setupActionListeners() {
        // Submit button action
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get form values
                String title = titleField.getText().trim();
                String description = descriptionArea.getText().trim();
                String tags = tagsField.getText().trim();
                String category = (String) categoryComboBox.getSelectedItem();
                boolean isAnonymous = anonymousCheckbox.isSelected();
                
                // Validation
                if (title.isEmpty()) {
                    JOptionPane.showMessageDialog(AskQuestionForm.this,
                            "Please enter a question title.",
                            "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (description.isEmpty()) {
                    JOptionPane.showMessageDialog(AskQuestionForm.this,
                            "Please enter a question description.",
                            "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Create question object
                Question question = new Question(title, description, tags, currentUser.getId(), category, isAnonymous);
                
                // Submit question
                int questionId = questionDAO.addQuestion(question);
                
                if (questionId > 0) {
                    // Question submitted successfully
                    JOptionPane.showMessageDialog(AskQuestionForm.this,
                            "Your question has been posted successfully!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    
                    // Close the form
                    dispose();
                } else {
                    // Failed to submit question
                    JOptionPane.showMessageDialog(AskQuestionForm.this,
                            "Failed to post your question. Please try again.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        // Cancel button action
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Check if form has content
                if (!titleField.getText().trim().isEmpty() || 
                    !descriptionArea.getText().trim().isEmpty() || 
                    !tagsField.getText().trim().isEmpty()) {
                    
                    int response = JOptionPane.showConfirmDialog(AskQuestionForm.this,
                            "Are you sure you want to cancel? Your question draft will be lost.",
                            "Confirm Cancel",
                            JOptionPane.YES_NO_OPTION);
                    
                    if (response == JOptionPane.YES_OPTION) {
                        dispose();
                    }
                } else {
                    dispose();
                }
            }
        });
    }
}
