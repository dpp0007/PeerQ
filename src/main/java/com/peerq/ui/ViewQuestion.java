package com.peerq.ui;

import com.peerq.dao.AnswerDAO;
import com.peerq.dao.QuestionDAO;
import com.peerq.dao.UserDAO;
import com.peerq.model.Answer;
import com.peerq.model.Question;
import com.peerq.model.User;
import com.peerq.ui.components.AnswerCard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Screen for viewing a question with its answers and submitting new answers.
 */
public class ViewQuestion extends JFrame {
    private final int questionId;
    private final int currentUserId;
    private Question question;
    private User currentUser;
    
    private final QuestionDAO questionDAO;
    private final AnswerDAO answerDAO;
    private final UserDAO userDAO;
    
    private JPanel answersPanel;
    private JTextArea answerTextArea;
    private JButton submitAnswerButton;
    private JCheckBox anonymousCheckbox;
    
    /**
     * Constructor for the View Question screen
     * 
     * @param questionId The ID of the question to view
     * @param currentUserId The ID of the currently logged in user
     */
    public ViewQuestion(int questionId, int currentUserId) {
        this.questionId = questionId;
        this.currentUserId = currentUserId;
        this.questionDAO = new QuestionDAO();
        this.answerDAO = new AnswerDAO();
        this.userDAO = new UserDAO();
        
        // Load the question data
        loadQuestion();
        
        // Load the current user
        currentUser = userDAO.getUserById(currentUserId);
        
        // Initialize the UI
        initializeUI();
        
        // Load the answers
        loadAnswers();
    }
    
    /**
     * Loads the question data from the database
     */
    private void loadQuestion() {
        question = questionDAO.getQuestionById(questionId);
        if (question == null) {
            JOptionPane.showMessageDialog(this,
                    "Question not found.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            dispose();
        }
    }
    
    /**
     * Sets up the UI components
     */
    private void initializeUI() {
        // Basic window setup
        setTitle("Question: " + question.getTitle());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null); // Center on screen
        setMinimumSize(new Dimension(700, 500));
        
        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(UIConstants.BG_COLOR);
        
        // Question panel
        JPanel questionPanel = createQuestionPanel();
        contentPanel.add(questionPanel, BorderLayout.NORTH);
        
        // Answers panel
        JPanel answersContainer = new JPanel(new BorderLayout());
        answersContainer.setBackground(UIConstants.BG_COLOR);
        answersContainer.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));
        
        // Answers title
        JLabel answersTitle = new JLabel("Answers");
        answersTitle.setFont(UIConstants.SUBTITLE_FONT);
        answersTitle.setForeground(UIConstants.TEXT_COLOR);
        answersContainer.add(answersTitle, BorderLayout.NORTH);
        
        // Answers list
        answersPanel = new JPanel();
        answersPanel.setLayout(new BoxLayout(answersPanel, BoxLayout.Y_AXIS));
        answersPanel.setBackground(UIConstants.BG_COLOR);
        
        JScrollPane answersScrollPane = new JScrollPane(answersPanel);
        answersScrollPane.setBorder(null);
        answersScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        answersContainer.add(answersScrollPane, BorderLayout.CENTER);
        
        contentPanel.add(answersContainer, BorderLayout.CENTER);
        
        // Answer form panel
        JPanel answerFormPanel = createAnswerFormPanel();
        contentPanel.add(answerFormPanel, BorderLayout.SOUTH);
        
        // Add content panel to the frame
        add(contentPanel);
        
        // Set up action listeners
        setupActionListeners();
    }
    
    /**
     * Creates the panel displaying the question details
     * 
     * @return The question panel
     */
    private JPanel createQuestionPanel() {
        JPanel questionPanel = new JPanel(new BorderLayout(0, 10));
        questionPanel.setBackground(UIConstants.BG_COLOR);
        questionPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Question card
        JPanel questionCard = new JPanel(new BorderLayout(10, 10));
        questionCard.setBackground(UIConstants.CARD_BG_COLOR);
        questionCard.setBorder(UIConstants.CARD_BORDER);
        
        // Title and solved status
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel(question.getTitle());
        titleLabel.setFont(UIConstants.TITLE_FONT);
        titleLabel.setForeground(UIConstants.TEXT_COLOR);
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        if (question.isSolved()) {
            JLabel solvedLabel = new JLabel("[Solved]");
            solvedLabel.setFont(UIConstants.REGULAR_FONT);
            solvedLabel.setForeground(UIConstants.SUCCESS_COLOR);
            titlePanel.add(solvedLabel, BorderLayout.EAST);
        }
        
        questionCard.add(titlePanel, BorderLayout.NORTH);
        
        // Question body
        JTextArea bodyTextArea = new JTextArea(question.getBody());
        bodyTextArea.setFont(UIConstants.REGULAR_FONT);
        bodyTextArea.setForeground(UIConstants.TEXT_COLOR);
        bodyTextArea.setLineWrap(true);
        bodyTextArea.setWrapStyleWord(true);
        bodyTextArea.setEditable(false);
        bodyTextArea.setBackground(UIConstants.CARD_BG_COLOR);
        bodyTextArea.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        questionCard.add(bodyTextArea, BorderLayout.CENTER);
        
        // Question footer (tags, category, user, timestamp)
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setOpaque(false);
        
        // Tags on the left
        JPanel tagsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        tagsPanel.setOpaque(false);
        
        if (question.getTags() != null && !question.getTags().isEmpty()) {
            String[] tags = question.getTags().split(",");
            for (String tag : tags) {
                JLabel tagLabel = new JLabel(tag.trim());
                tagLabel.setFont(UIConstants.SMALL_FONT);
                tagLabel.setForeground(UIConstants.PRIMARY_COLOR);
                tagLabel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(UIConstants.PRIMARY_COLOR, 1, true),
                    BorderFactory.createEmptyBorder(2, 5, 2, 5)
                ));
                tagsPanel.add(tagLabel);
            }
        }
        
        footerPanel.add(tagsPanel, BorderLayout.WEST);
        
        // Info panel on the right (category, Posted by, Timestamp)
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        infoPanel.setOpaque(false);
        
        // Category
        if (question.getCategory() != null && !question.getCategory().isEmpty()) {
            JLabel categoryLabel = new JLabel(question.getCategory());
            categoryLabel.setFont(UIConstants.SMALL_FONT);
            categoryLabel.setForeground(UIConstants.SECONDARY_COLOR);
            infoPanel.add(categoryLabel);
        }
        
        // Posted by
        String postedBy = question.isAnonymous() ? "Anonymous" : question.getUserName();
        JLabel userLabel = new JLabel("Posted by " + postedBy);
        userLabel.setFont(UIConstants.SMALL_FONT);
        userLabel.setForeground(UIConstants.LIGHT_TEXT_COLOR);
        infoPanel.add(userLabel);
        
        // Timestamp
        JLabel timestampLabel = new JLabel(question.getCreatedAt());
        timestampLabel.setFont(UIConstants.SMALL_FONT);
        timestampLabel.setForeground(UIConstants.LIGHT_TEXT_COLOR);
        infoPanel.add(timestampLabel);
        
        footerPanel.add(infoPanel, BorderLayout.EAST);
        
        questionCard.add(footerPanel, BorderLayout.SOUTH);
        
        // Add back button above the question card
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        
        JButton backButton = new JButton("← Back to Questions");
        backButton.setFont(UIConstants.REGULAR_FONT);
        backButton.setForeground(UIConstants.PRIMARY_COLOR);
        backButton.setBackground(null);
        backButton.setBorderPainted(false);
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        topPanel.add(backButton, BorderLayout.WEST);
        
        // Add components to the main panel
        questionPanel.add(topPanel, BorderLayout.NORTH);
        questionPanel.add(questionCard, BorderLayout.CENTER);
        
        return questionPanel;
    }
    
    /**
     * Creates the panel for submitting new answers
     * 
     * @return The answer form panel
     */
    private JPanel createAnswerFormPanel() {
        JPanel answerFormPanel = new JPanel(new BorderLayout());
        answerFormPanel.setBackground(UIConstants.CARD_BG_COLOR);
        answerFormPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, UIConstants.PRIMARY_COLOR.brighter()),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Answer title
        JLabel answerTitle = new JLabel("Your Answer");
        answerTitle.setFont(UIConstants.SUBTITLE_FONT);
        answerTitle.setForeground(UIConstants.TEXT_COLOR);
        answerFormPanel.add(answerTitle, BorderLayout.NORTH);
        
        // Center panel with textarea and checkbox
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        
        // Answer text area
        answerTextArea = new JTextArea();
        answerTextArea.setFont(UIConstants.REGULAR_FONT);
        answerTextArea.setLineWrap(true);
        answerTextArea.setWrapStyleWord(true);
        answerTextArea.setBorder(UIConstants.FIELD_BORDER);
        
        JScrollPane answerScrollPane = new JScrollPane(answerTextArea);
        answerScrollPane.setPreferredSize(new Dimension(answerScrollPane.getPreferredSize().width, 100));
        centerPanel.add(answerScrollPane, BorderLayout.CENTER);
        
        // Anonymous checkbox
        anonymousCheckbox = new JCheckBox("Post Anonymously");
        anonymousCheckbox.setFont(UIConstants.REGULAR_FONT);
        anonymousCheckbox.setForeground(UIConstants.TEXT_COLOR);
        anonymousCheckbox.setBackground(UIConstants.CARD_BG_COLOR);
        centerPanel.add(anonymousCheckbox, BorderLayout.SOUTH);
        
        answerFormPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Submit button
        submitAnswerButton = new JButton("Submit Answer");
        submitAnswerButton.setFont(UIConstants.BUTTON_FONT);
        submitAnswerButton.setForeground(Color.WHITE);
        submitAnswerButton.setBackground(UIConstants.PRIMARY_COLOR);
        submitAnswerButton.setBorder(UIConstants.BUTTON_BORDER);
        submitAnswerButton.setFocusPainted(false);
        submitAnswerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Button hover effect
        submitAnswerButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                submitAnswerButton.setBackground(UIConstants.PRIMARY_COLOR.darker());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                submitAnswerButton.setBackground(UIConstants.PRIMARY_COLOR);
            }
        });
        
        // East panel for button alignment
        JPanel eastPanel = new JPanel(new BorderLayout());
        eastPanel.setOpaque(false);
        eastPanel.add(submitAnswerButton, BorderLayout.NORTH);
        
        answerFormPanel.add(eastPanel, BorderLayout.EAST);
        
        return answerFormPanel;
    }
    
    /**
     * Sets up action listeners for form components
     */
    private void setupActionListeners() {
        // Submit answer button
        submitAnswerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String answerContent = answerTextArea.getText().trim();
                boolean isAnonymous = anonymousCheckbox.isSelected();
                
                // Validation
                if (answerContent.isEmpty()) {
                    JOptionPane.showMessageDialog(ViewQuestion.this,
                            "Please enter your answer.",
                            "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Create answer object
                Answer answer = new Answer(questionId, currentUserId, answerContent, isAnonymous);
                
                // Submit answer
                int answerId = answerDAO.submitAnswer(answer);
                
                if (answerId > 0) {
                    // Answer submitted successfully
                    JOptionPane.showMessageDialog(ViewQuestion.this,
                            "Your answer has been posted successfully!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    
                    // Clear the form
                    answerTextArea.setText("");
                    anonymousCheckbox.setSelected(false);
                    
                    // Reload answers
                    loadAnswers();
                } else {
                    // Failed to submit answer
                    JOptionPane.showMessageDialog(ViewQuestion.this,
                            "Failed to post your answer. Please try again.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
    
    /**
     * Loads answers for the current question
     */
    private void loadAnswers() {
        // Clear existing answers
        answersPanel.removeAll();
        
        // Get answers for this question
        List<Answer> answers = answerDAO.getAnswersByQuestionId(questionId);
        
        if (answers.isEmpty()) {
            // No answers yet
            JLabel noAnswersLabel = new JLabel("No answers yet. Be the first to answer!");
            noAnswersLabel.setFont(UIConstants.REGULAR_FONT);
            noAnswersLabel.setForeground(UIConstants.LIGHT_TEXT_COLOR);
            noAnswersLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            answersPanel.add(Box.createVerticalStrut(20));
            answersPanel.add(noAnswersLabel);
        } else {
            // Add answer cards
            for (Answer answer : answers) {
                // Check if current user is the question author
                boolean isQuestionAuthor = currentUserId == question.getUserId();
                
                AnswerCard answerCard = new AnswerCard(answer, currentUserId, isQuestionAuthor);
                answerCard.setAlignmentX(Component.LEFT_ALIGNMENT);
                answersPanel.add(answerCard);
                answersPanel.add(Box.createVerticalStrut(10));
            }
        }
        
        // Update UI
        answersPanel.revalidate();
        answersPanel.repaint();
    }
}
