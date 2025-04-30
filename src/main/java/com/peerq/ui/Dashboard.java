package com.peerq.ui;

import com.peerq.dao.QuestionDAO;
import com.peerq.model.Question;
import com.peerq.model.User;
import com.peerq.ui.components.QuestionCard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Dashboard that displays the main question feed and navigation sidebar.
 */
public class Dashboard extends JFrame {
    private final User currentUser;
    private final QuestionDAO questionDAO;
    
    private JPanel questionsPanel;
    private JScrollPane scrollPane;
    private JLabel filterLabel;
    private JButton askQuestionButton;
    
    private JButton allQuestionsButton;
    private JButton myQuestionsButton;
    private JButton unansweredButton;
    private JButton academicsButton;
    private JButton clubsButton;
    private JButton dormLifeButton;
    private JButton eventsButton;
    
    private JTextField searchField;
    private JButton searchButton;
    
    /**
     * Constructor for the Dashboard
     * 
     * @param user The currently logged in user
     */
    public Dashboard(User user) {
        this.currentUser = user;
        this.questionDAO = new QuestionDAO();
        
        initializeUI();
        loadQuestions();
    }
    
    /**
     * Sets up the UI components
     */
    private void initializeUI() {
        // Basic window setup
        setTitle(UIConstants.APP_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null); // Center on screen
        setMinimumSize(new Dimension(800, 600));
        
        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(UIConstants.BG_COLOR);
        
        // Header panel
        JPanel headerPanel = createHeaderPanel();
        contentPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Main layout split between sidebar and content
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(200);
        splitPane.setDividerSize(1);
        splitPane.setBorder(null);
        
        // Sidebar panel
        JPanel sidebarPanel = createSidebarPanel();
        splitPane.setLeftComponent(sidebarPanel);
        
        // Questions content panel
        JPanel contentAreaPanel = new JPanel(new BorderLayout());
        contentAreaPanel.setBackground(UIConstants.BG_COLOR);
        contentAreaPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Questions title panel
        JPanel questionsTitlePanel = new JPanel(new BorderLayout());
        questionsTitlePanel.setBackground(UIConstants.BG_COLOR);
        
        filterLabel = new JLabel("All Questions");
        filterLabel.setFont(UIConstants.SUBTITLE_FONT);
        filterLabel.setForeground(UIConstants.TEXT_COLOR);
        questionsTitlePanel.add(filterLabel, BorderLayout.WEST);
        
        askQuestionButton = new JButton("Ask Question");
        askQuestionButton.setFont(UIConstants.BUTTON_FONT);
        askQuestionButton.setForeground(Color.WHITE);
        askQuestionButton.setBackground(UIConstants.PRIMARY_COLOR);
        askQuestionButton.setBorder(UIConstants.BUTTON_BORDER);
        askQuestionButton.setFocusPainted(false);
        askQuestionButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        askQuestionButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                askQuestionButton.setBackground(UIConstants.PRIMARY_COLOR.darker());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                askQuestionButton.setBackground(UIConstants.PRIMARY_COLOR);
            }
        });
        askQuestionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AskQuestionForm askQuestionForm = new AskQuestionForm(currentUser);
                askQuestionForm.setVisible(true);
                askQuestionForm.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                        // Refresh questions when the ask question form is closed
                        loadQuestions();
                    }
                });
            }
        });
        questionsTitlePanel.add(askQuestionButton, BorderLayout.EAST);
        
        contentAreaPanel.add(questionsTitlePanel, BorderLayout.NORTH);
        
        // Questions panel for the cards
        questionsPanel = new JPanel();
        questionsPanel.setBackground(UIConstants.BG_COLOR);
        questionsPanel.setLayout(new BoxLayout(questionsPanel, BoxLayout.Y_AXIS));
        
        // Scroll pane for questions
        scrollPane = new JScrollPane(questionsPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        
        contentAreaPanel.add(scrollPane, BorderLayout.CENTER);
        
        splitPane.setRightComponent(contentAreaPanel);
        
        contentPanel.add(splitPane, BorderLayout.CENTER);
        
        // Set the content pane
        setContentPane(contentPanel);
        
        // Setup action listeners
        setupActionListeners();
    }
    
    /**
     * Creates the header panel with logo, search and user info
     * 
     * @return The header JPanel
     */
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIConstants.PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        // Logo
        JLabel logoLabel = new JLabel(UIConstants.APP_TITLE);
        logoLabel.setFont(UIConstants.TITLE_FONT);
        logoLabel.setForeground(Color.WHITE);
        headerPanel.add(logoLabel, BorderLayout.WEST);
        
        // Search panel
        JPanel searchPanel = new JPanel();
        searchPanel.setOpaque(false);
        searchField = new JTextField(20);
        searchField.setFont(UIConstants.REGULAR_FONT);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 1, true),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        searchPanel.add(searchField);
        
        searchButton = new JButton("Search");
        searchButton.setFont(UIConstants.REGULAR_FONT);
        searchButton.setForeground(UIConstants.PRIMARY_COLOR);
        searchButton.setBackground(Color.WHITE);
        searchButton.setBorder(UIConstants.BUTTON_BORDER);
        searchButton.setFocusPainted(false);
        searchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchPanel.add(searchButton);
        
        headerPanel.add(searchPanel, BorderLayout.CENTER);
        
        // User info
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setOpaque(false);
        
        JLabel userLabel = new JLabel("Welcome, " + currentUser.getName());
        userLabel.setFont(UIConstants.REGULAR_FONT);
        userLabel.setForeground(Color.WHITE);
        userPanel.add(userLabel);
        
        JLabel reputationLabel = new JLabel("Reputation: " + currentUser.getReputation());
        reputationLabel.setFont(UIConstants.SMALL_FONT);
        reputationLabel.setForeground(Color.WHITE);
        userPanel.add(reputationLabel);
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(UIConstants.REGULAR_FONT);
        logoutButton.setForeground(UIConstants.PRIMARY_COLOR);
        logoutButton.setBackground(Color.WHITE);
        logoutButton.setBorder(UIConstants.BUTTON_BORDER);
        logoutButton.setFocusPainted(false);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(
                    Dashboard.this,
                    "Are you sure you want to logout?",
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION
            );
            
            if (result == JOptionPane.YES_OPTION) {
                LoginForm loginForm = new LoginForm();
                loginForm.setVisible(true);
                dispose();
            }
        });
        userPanel.add(logoutButton);
        
        headerPanel.add(userPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    /**
     * Creates the sidebar panel with filter options
     * 
     * @return The sidebar JPanel
     */
    private JPanel createSidebarPanel() {
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(new Color(240, 240, 240));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Sidebar title
        JLabel sidebarTitle = new JLabel("Filters");
        sidebarTitle.setFont(UIConstants.SUBTITLE_FONT);
        sidebarTitle.setForeground(UIConstants.TEXT_COLOR);
        sidebarTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebarPanel.add(sidebarTitle);
        sidebarPanel.add(Box.createVerticalStrut(15));
        
        // Create filter buttons with consistent styling
        allQuestionsButton = createSidebarButton("All Questions", true);
        myQuestionsButton = createSidebarButton("My Questions", false);
        unansweredButton = createSidebarButton("Unanswered", false);
        
        sidebarPanel.add(allQuestionsButton);
        sidebarPanel.add(Box.createVerticalStrut(5));
        sidebarPanel.add(myQuestionsButton);
        sidebarPanel.add(Box.createVerticalStrut(5));
        sidebarPanel.add(unansweredButton);
        sidebarPanel.add(Box.createVerticalStrut(20));
        
        // Categories title
        JLabel categoriesTitle = new JLabel("Categories");
        categoriesTitle.setFont(UIConstants.SUBTITLE_FONT);
        categoriesTitle.setForeground(UIConstants.TEXT_COLOR);
        categoriesTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebarPanel.add(categoriesTitle);
        sidebarPanel.add(Box.createVerticalStrut(15));
        
        // Category buttons
        academicsButton = createSidebarButton("Academics", false);
        clubsButton = createSidebarButton("Clubs", false);
        dormLifeButton = createSidebarButton("Dorm Life", false);
        eventsButton = createSidebarButton("Events", false);
        
        sidebarPanel.add(academicsButton);
        sidebarPanel.add(Box.createVerticalStrut(5));
        sidebarPanel.add(clubsButton);
        sidebarPanel.add(Box.createVerticalStrut(5));
        sidebarPanel.add(dormLifeButton);
        sidebarPanel.add(Box.createVerticalStrut(5));
        sidebarPanel.add(eventsButton);
        
        // Add filler to push content to the top
        sidebarPanel.add(Box.createVerticalGlue());
        
        return sidebarPanel;
    }
    
    /**
     * Creates a styled button for the sidebar
     * 
     * @param text The button text
     * @param isSelected Whether the button is initially selected
     * @return The created JButton
     */
    private JButton createSidebarButton(String text, boolean isSelected) {
        JButton button = new JButton(text);
        button.setFont(UIConstants.REGULAR_FONT);
        button.setForeground(isSelected ? UIConstants.PRIMARY_COLOR : UIConstants.TEXT_COLOR);
        button.setBackground(isSelected ? new Color(235, 242, 250) : null);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        
        return button;
    }
    
    /**
     * Sets up action listeners for the sidebar buttons and search
     */
    private void setupActionListeners() {
        // All Questions button
        allQuestionsButton.addActionListener(e -> {
            resetButtonStyles();
            allQuestionsButton.setForeground(UIConstants.PRIMARY_COLOR);
            allQuestionsButton.setBackground(new Color(235, 242, 250));
            filterLabel.setText("All Questions");
            loadQuestions();
        });
        
        // My Questions button
        myQuestionsButton.addActionListener(e -> {
            resetButtonStyles();
            myQuestionsButton.setForeground(UIConstants.PRIMARY_COLOR);
            myQuestionsButton.setBackground(new Color(235, 242, 250));
            filterLabel.setText("My Questions");
            loadMyQuestions();
        });
        
        // Unanswered button
        unansweredButton.addActionListener(e -> {
            resetButtonStyles();
            unansweredButton.setForeground(UIConstants.PRIMARY_COLOR);
            unansweredButton.setBackground(new Color(235, 242, 250));
            filterLabel.setText("Unanswered Questions");
            loadUnansweredQuestions();
        });
        
        // Academics button
        academicsButton.addActionListener(e -> {
            resetButtonStyles();
            academicsButton.setForeground(UIConstants.PRIMARY_COLOR);
            academicsButton.setBackground(new Color(235, 242, 250));
            filterLabel.setText("Academics");
            loadQuestionsByCategory("Academics");
        });
        
        // Clubs button
        clubsButton.addActionListener(e -> {
            resetButtonStyles();
            clubsButton.setForeground(UIConstants.PRIMARY_COLOR);
            clubsButton.setBackground(new Color(235, 242, 250));
            filterLabel.setText("Clubs");
            loadQuestionsByCategory("Clubs");
        });
        
        // Dorm Life button
        dormLifeButton.addActionListener(e -> {
            resetButtonStyles();
            dormLifeButton.setForeground(UIConstants.PRIMARY_COLOR);
            dormLifeButton.setBackground(new Color(235, 242, 250));
            filterLabel.setText("Dorm Life");
            loadQuestionsByCategory("Dorm Life");
        });
        
        // Events button
        eventsButton.addActionListener(e -> {
            resetButtonStyles();
            eventsButton.setForeground(UIConstants.PRIMARY_COLOR);
            eventsButton.setBackground(new Color(235, 242, 250));
            filterLabel.setText("Events");
            loadQuestionsByCategory("Events");
        });
        
        // Search button
        searchButton.addActionListener(e -> {
            String keyword = searchField.getText().trim();
            if (!keyword.isEmpty()) {
                resetButtonStyles();
                filterLabel.setText("Search Results: \"" + keyword + "\"");
                searchQuestions(keyword);
            }
        });
        
        // Enter key in search field
        searchField.addActionListener(e -> searchButton.doClick());
    }
    
    /**
     * Resets the styling of all sidebar buttons
     */
    private void resetButtonStyles() {
        for (JButton button : new JButton[] {
                allQuestionsButton, myQuestionsButton, unansweredButton,
                academicsButton, clubsButton, dormLifeButton, eventsButton
        }) {
            button.setForeground(UIConstants.TEXT_COLOR);
            button.setBackground(null);
        }
    }
    
    /**
     * Loads all questions into the questions panel
     */
    private void loadQuestions() {
        List<Question> questions = questionDAO.getAllQuestions();
        displayQuestions(questions);
    }
    
    /**
     * Loads questions posted by the current user
     */
    private void loadMyQuestions() {
        List<Question> questions = questionDAO.getQuestionsByUserId(currentUser.getId());
        displayQuestions(questions);
    }
    
    /**
     * Loads unanswered questions
     */
    private void loadUnansweredQuestions() {
        List<Question> questions = questionDAO.getUnansweredQuestions();
        displayQuestions(questions);
    }
    
    /**
     * Loads questions by category
     * 
     * @param category The category to filter by
     */
    private void loadQuestionsByCategory(String category) {
        List<Question> questions = questionDAO.getQuestionsByCategory(category);
        displayQuestions(questions);
    }
    
    /**
     * Searches questions by keyword
     * 
     * @param keyword The search term
     */
    private void searchQuestions(String keyword) {
        List<Question> questions = questionDAO.searchQuestions(keyword);
        displayQuestions(questions);
    }
    
    /**
     * Displays a list of questions in the questions panel
     * 
     * @param questions The list of questions to display
     */
    private void displayQuestions(List<Question> questions) {
        // Clear existing questions
        questionsPanel.removeAll();
        
        if (questions.isEmpty()) {
            // No questions found
            JLabel noQuestionsLabel = new JLabel("No questions found");
            noQuestionsLabel.setFont(UIConstants.REGULAR_FONT);
            noQuestionsLabel.setForeground(UIConstants.LIGHT_TEXT_COLOR);
            noQuestionsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            questionsPanel.add(Box.createVerticalStrut(50));
            questionsPanel.add(noQuestionsLabel);
        } else {
            // Add question cards
            for (Question question : questions) {
                QuestionCard questionCard = new QuestionCard(question, currentUser.getId());
                questionCard.setAlignmentX(Component.LEFT_ALIGNMENT);
                questionsPanel.add(questionCard);
                questionsPanel.add(Box.createVerticalStrut(10));
            }
        }
        
        // Update UI
        questionsPanel.revalidate();
        questionsPanel.repaint();
        
        // Scroll to top
        SwingUtilities.invokeLater(() -> {
            scrollPane.getVerticalScrollBar().setValue(0);
        });
    }
}
