package com.peerq.desktopui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

// Import database-related classes
import com.peerq.dao.QuestionDAO;
import com.peerq.dao.UserDAO;
import com.peerq.dao.AnswerDAO;
import com.peerq.model.Question;
import com.peerq.model.User;
import com.peerq.model.Answer;
import com.peerq.util.DBConnection;

import java.sql.SQLException;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.ParseException;
import java.util.ArrayList;

public class PeerQMainApplication extends Application {
    private static final Color PRIMARY_COLOR = Color.rgb(0, 255, 127); // Green accent
    private static final Color BACKGROUND_COLOR = Color.rgb(18, 18, 18); // Dark background
    private static final Color CARD_BACKGROUND = Color.rgb(26, 26, 26); // Card background
    private static final Color BORDER_COLOR = Color.rgb(68, 68, 68); // Border color
    private static final Color TEXT_COLOR = Color.rgb(224, 224, 224); // Text color
    
    private StackPane mainContent;
    private VBox questionList;
    private VBox questionDetail;
    private VBox askQuestion;
    private VBox loginPanel;
    private VBox registerPanel;
    
    // Search and filter components
    private TextField searchField;
    private ComboBox<String> categoryFilter;
    private VBox questionsListContainer;
    
    // Header components for dynamic updates
    private HBox authButtons;
    private Button loginBtn;
    private Button registerBtn;
    private Label userLabel;
    private Button logoutBtn;
    
    // Database DAOs
    private QuestionDAO questionDAO;
    private UserDAO userDAO;
    private AnswerDAO answerDAO;
    
    // Current user
    private User currentUser;
    
    // Store all questions for filtering
    private List<Question> allQuestions = new ArrayList<>();
    
    @Override
    public void start(Stage primaryStage) {
        try {
            // Initialize database connections
            initializeDatabase();
            
            // Create main layout
            BorderPane root = new BorderPane();
            root.setStyle("-fx-background-color: #121212;");
            
            // Create header
            HBox header = createHeader();
            root.setTop(header);
            
            // Create main content area
            mainContent = new StackPane();
            mainContent.setStyle("-fx-background-color: #121212;");
            
            // Initialize all panels
            questionList = createQuestionList();
            questionDetail = createQuestionDetail();
            askQuestion = createAskQuestion();
            loginPanel = createLoginPanel();
            registerPanel = createRegisterPanel();
            
            // Add all panels to main content
            mainContent.getChildren().addAll(questionList, questionDetail, askQuestion, loginPanel, registerPanel);
            
            // Show question list by default
            showQuestionList();
            
            root.setCenter(mainContent);
            
            // Create scene
            Scene scene = new Scene(root, 1200, 800);
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            
            // Configure stage
            primaryStage.setTitle("PeerQ - College Community Discourse Platform");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);
            
            // Set up window close handler
            primaryStage.setOnCloseRequest(event -> {
                System.out.println("PeerQ Desktop Application closing...");
                System.exit(0);
            });
            
            primaryStage.show();
            
        } catch (Exception e) {
            System.err.println("Error starting PeerQ Desktop Application: " + e.getMessage());
            e.printStackTrace();
            
            // Show error dialog
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Application Error");
            alert.setHeaderText("Failed to start PeerQ");
            alert.setContentText("An error occurred while starting the application: " + e.getMessage());
            alert.showAndWait();
            
            System.exit(1);
        }
    }
    
    private void initializeDatabase() {
        try {
            questionDAO = new QuestionDAO();
            userDAO = new UserDAO();
            answerDAO = new AnswerDAO();
            System.out.println("Database connection initialized successfully");
        } catch (Exception e) {
            System.err.println("Error initializing database: " + e.getMessage());
            throw new RuntimeException("Failed to initialize database", e);
        }
    }
    
    private String formatDate(String dateString) {
        try {
            // Parse the date string (assuming it's in a standard format)
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = inputFormat.parse(dateString);
            
            // Format to desired output format
            SimpleDateFormat outputFormat = new SimpleDateFormat("d MMMM yyyy HH:mm");
            return outputFormat.format(date);
        } catch (ParseException e) {
            // If parsing fails, return the original string
            return dateString;
        }
    }
    
    private HBox createHeader() {
        HBox header = new HBox(20);
        header.setPadding(new Insets(15));
        header.getStyleClass().add("header");
        header.setPrefHeight(60);
        
        // Logo
        Label logo = new Label("PeerQ");
        logo.getStyleClass().add("logo");
        
        // Navigation
        HBox nav = new HBox(20);
        Button homeBtn = createNavButton("Home");
        Button askQuestionBtn = createNavButton("Ask Question");
        nav.getChildren().addAll(homeBtn, askQuestionBtn);
        
        // Search bar
        searchField = new TextField();
        searchField.setPromptText("Search questions, topics, or users...");
        searchField.setPrefWidth(300);
        searchField.getStyleClass().add("search-field");
        
        // Auth buttons
        authButtons = new HBox(10);
        loginBtn = createTextButton("Log In");
        registerBtn = createTextButton("Register");
        authButtons.getChildren().addAll(loginBtn, registerBtn);
        
        // User label
        userLabel = new Label();
        userLabel.getStyleClass().add("user-label");
        
        // Logout button
        logoutBtn = new Button("Logout");
        logoutBtn.getStyleClass().add("logout-button");
        
        // Add components to header
        header.getChildren().addAll(logo, nav, searchField, authButtons, userLabel, logoutBtn);
        HBox.setHgrow(nav, Priority.ALWAYS);
        
        // Initialize header state (logged out)
        updateHeaderForLoggedOutUser();
        
        // Add event handlers
        homeBtn.setOnAction(e -> {
            System.out.println("Home button clicked");
            showQuestionList();
        });
        askQuestionBtn.setOnAction(e -> {
            System.out.println("Ask Question button clicked");
            if (currentUser == null) {
                showAlert("Login Required", "Please log in to ask a question");
                showLogin();
            } else {
                showAskQuestion();
            }
        });
        loginBtn.setOnAction(e -> {
            System.out.println("Login button clicked in header");
            showLogin();
        });
        registerBtn.setOnAction(e -> {
            System.out.println("Register button clicked in header");
            showRegister();
        });
        logoutBtn.setOnAction(e -> {
            System.out.println("Logout button clicked in header");
            logout();
        });
        
        return header;
    }
    
    private Button createNavButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("nav-button");
        return button;
    }
    
    private Button createTextButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("text-button");
        return button;
    }
    
    private VBox createQuestionList() {
        VBox panel = new VBox(20);
        panel.setPadding(new Insets(20));
        panel.setStyle("-fx-background-color: #121212;");
        
        // Header with title and category filter on the right
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label title = new Label("Recent Questions");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: #00FF7F;");
        
        // Category filter moved to the right
        categoryFilter = new ComboBox<>();
        categoryFilter.getItems().addAll("All Categories", "Academics", "Campus Life", "Career", "Technology", "Miscellaneous");
        categoryFilter.setValue("All Categories");
        categoryFilter.setPrefWidth(150);
        
        // Add event handlers for search and filter
        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                filterQuestions();
            });
        }
        
        categoryFilter.setOnAction(e -> {
            filterQuestions();
        });
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        header.getChildren().addAll(title, spacer, categoryFilter);
        
        // Questions list
        questionsListContainer = new VBox(10);
        questionsListContainer.setStyle("-fx-background-color: #121212;");
        
        // Load real questions from database
        loadQuestionsFromDatabase(questionsListContainer);
        
        ScrollPane scrollPane = new ScrollPane(questionsListContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: #121212; -fx-border-color: transparent;");
        
        panel.getChildren().addAll(header, scrollPane);
        return panel;
    }
    
    private void loadQuestionsFromDatabase(VBox questionsList) {
        try {
            allQuestions = questionDAO.getAllQuestions();
            
            if (allQuestions.isEmpty()) {
                // Create sample questions if database is empty
                allQuestions = createSampleQuestions();
            }
            
            // Display questions
            displayQuestions(allQuestions, questionsList);
            
        } catch (Exception e) {
            System.err.println("Error loading questions: " + e.getMessage());
            // Fallback to sample questions
            allQuestions = createSampleQuestions();
            displayQuestions(allQuestions, questionsList);
        }
    }
    
    private List<Question> createSampleQuestions() {
        List<Question> sampleQuestions = new ArrayList<>();
        
        // Sample question 1
        Question q1 = new Question();
        q1.setId(1);
        q1.setTitle("How to prepare for final exams?");
        q1.setBody("I'm struggling with time management for my final exams. Any tips?");
        q1.setCategory("Academics");
        q1.setUserName("John Doe");
        q1.setCreatedAt("2025-06-09 10:42:00");
        q1.setAnswerCount(5);
        sampleQuestions.add(q1);
        
        // Sample question 2
        Question q2 = new Question();
        q2.setId(2);
        q2.setTitle("Best places to study on campus?");
        q2.setBody("Looking for quiet places to study during exam season.");
        q2.setCategory("Campus Life");
        q2.setUserName("Jane Smith");
        q2.setCreatedAt("2025-06-09 08:15:00");
        q2.setAnswerCount(3);
        sampleQuestions.add(q2);
        
        // Sample question 3
        Question q3 = new Question();
        q3.setId(3);
        q3.setTitle("Career advice for computer science students");
        q3.setBody("What are the best career paths for CS students after graduation?");
        q3.setCategory("Career");
        q3.setUserName("Mike Johnson");
        q3.setCreatedAt("2025-06-08 14:30:00");
        q3.setAnswerCount(7);
        sampleQuestions.add(q3);
        
        // Sample question 4
        Question q4 = new Question();
        q4.setId(4);
        q4.setTitle("Best programming languages to learn in 2025");
        q4.setBody("Which programming languages are most in demand for job market?");
        q4.setCategory("Technology");
        q4.setUserName("Sarah Wilson");
        q4.setCreatedAt("2025-06-08 16:45:00");
        q4.setAnswerCount(12);
        sampleQuestions.add(q4);
        
        return sampleQuestions;
    }
    
    private void filterQuestions() {
        if (questionsListContainer == null) return;
        
        String searchText = searchField.getText().toLowerCase().trim();
        String selectedCategory = categoryFilter.getValue();
        
        List<Question> filteredQuestions = new ArrayList<>();
        
        for (Question question : allQuestions) {
            boolean matchesSearch = searchText.isEmpty() || 
                question.getTitle().toLowerCase().contains(searchText) ||
                question.getBody().toLowerCase().contains(searchText) ||
                question.getUserName().toLowerCase().contains(searchText);
            
            boolean matchesCategory = "All Categories".equals(selectedCategory) || 
                selectedCategory.equals(question.getCategory());
            
            if (matchesSearch && matchesCategory) {
                filteredQuestions.add(question);
            }
        }
        
        // Clear and repopulate the questions list
        questionsListContainer.getChildren().clear();
        displayQuestions(filteredQuestions, questionsListContainer);
    }
    
    private void displayQuestions(List<Question> questions, VBox container) {
        if (questions.isEmpty()) {
            Label noResultsLabel = new Label("No questions found matching your criteria.");
            noResultsLabel.setStyle("-fx-text-fill: #E0E0E0; -fx-font-size: 16px;");
            noResultsLabel.setAlignment(Pos.CENTER);
            container.getChildren().add(noResultsLabel);
        } else {
            for (Question question : questions) {
                container.getChildren().add(createQuestionCardFromDB(question));
            }
        }
    }
    
    private VBox createQuestionCardFromDB(Question question) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.getStyleClass().add("question-card");
        
        Label titleLabel = new Label(question.getTitle());
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        titleLabel.setStyle("-fx-text-fill: #00FF7F;");
        
        Label previewLabel = new Label(question.getBody());
        previewLabel.setTextFill(Color.rgb(176, 176, 176));
        
        HBox meta = new HBox(10);
        Label categoryLabel = new Label(question.getCategory());
        categoryLabel.setTextFill(Color.rgb(106, 90, 205)); // Purple for category
        
        Label answersLabel = new Label(question.getAnswerCount() + " answers");
        answersLabel.setTextFill(Color.rgb(176, 176, 176));
        
        Label timeLabel = new Label(formatDate(question.getCreatedAt()));
        timeLabel.setTextFill(Color.rgb(176, 176, 176));
        
        meta.getChildren().addAll(categoryLabel, answersLabel, timeLabel);
        
        card.getChildren().addAll(titleLabel, previewLabel, meta);
        
        // Add click handler
        card.setOnMouseClicked(e -> showQuestionDetail(question));
        
        return card;
    }
    
    private VBox createQuestionCard(String title, String preview, String category, String answers, String time) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.getStyleClass().add("question-card");
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        titleLabel.setStyle("-fx-text-fill: #00FF7F;");
        
        Label previewLabel = new Label(preview);
        previewLabel.setTextFill(Color.rgb(176, 176, 176));
        
        HBox meta = new HBox(10);
        Label categoryLabel = new Label(category);
        categoryLabel.setTextFill(Color.rgb(106, 90, 205)); // Purple for category
        
        Label answersLabel = new Label(answers);
        answersLabel.setTextFill(Color.rgb(176, 176, 176));
        
        Label timeLabel = new Label(time);
        timeLabel.setTextFill(Color.rgb(176, 176, 176));
        
        meta.getChildren().addAll(categoryLabel, answersLabel, timeLabel);
        
        card.getChildren().addAll(titleLabel, previewLabel, meta);
        
        // Add click handler
        card.setOnMouseClicked(e -> showQuestionDetail());
        
        return card;
    }
    
    private VBox createQuestionDetail() {
        return createQuestionDetail(null);
    }
    
    private VBox createQuestionDetail(Question question) {
        VBox panel = new VBox(20);
        panel.setPadding(new Insets(20));
        panel.setStyle("-fx-background-color: #121212;");
        
        // Back button
        Button backButton = new Button("← Back to Questions");
        backButton.getStyleClass().add("back-button");
        backButton.setOnAction(e -> showQuestionList());
        
        // Question content
        VBox questionContent = new VBox(10);
        Label title = new Label(question != null ? question.getTitle() : "How to prepare for final exams?");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: #00FF7F;");
        
        HBox meta = new HBox(10);
        meta.getChildren().addAll(
            new Label("Posted by " + (question != null ? question.getUserName() : "John Doe")),
            new Label(formatDate(question != null ? question.getCreatedAt() : "2 hours ago")),
            new Label(question != null ? question.getCategory() : "Academics")
        );
        meta.getChildren().forEach(node -> {
            if (node instanceof Label) {
                ((Label) node).setTextFill(Color.rgb(176, 176, 176));
            }
        });
        
        TextArea content = new TextArea(
            question != null ? question.getBody() : 
            "I'm struggling with time management for my final exams. " +
            "I have 5 subjects to prepare for and only 2 weeks left. " +
            "Any tips on how to organize my study schedule effectively?"
        );
        content.setEditable(false);
        content.setWrapText(true);
        content.setStyle("-fx-background-color: #1A1A1A; -fx-border-color: #444444; -fx-text-fill: #E0E0E0; -fx-control-inner-background: #1A1A1A;");
        
        questionContent.getChildren().addAll(title, meta, content);
        
        // Answers section
        VBox answersSection = new VBox(20);
        Label answersTitle = new Label("Answers");
        answersTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        answersTitle.setStyle("-fx-text-fill: #00FF7F;");
        
        VBox answersList = new VBox(10);
        
        if (question != null) {
            // Load real answers from database
            try {
                List<Answer> answers = answerDAO.getAnswersByQuestionId(question.getId());
                for (Answer answer : answers) {
                    answersList.getChildren().add(createAnswerCard(answer));
                }
            } catch (Exception e) {
                System.err.println("Error loading answers: " + e.getMessage());
            }
        } else {
            // Sample answers
            answersList.getChildren().addAll(
                createAnswerCard("Jane Smith", "9 June 2025 10:45",
                    "Here's what worked for me:\n\n" +
                    "1. Create a study schedule with specific time slots for each subject\n" +
                    "2. Take regular breaks (25 minutes study, 5 minutes break)\n" +
                    "3. Use active recall techniques instead of passive reading\n" +
                    "4. Form study groups for difficult topics\n" +
                    "5. Get enough sleep and exercise"),
                createAnswerCard("Mike Johnson", "9 June 2025 11:30",
                    "I recommend using the Pomodoro Technique. Study for 25 minutes, " +
                    "then take a 5-minute break. After 4 pomodoros, take a longer break. " +
                    "This helps maintain focus and prevents burnout.")
            );
        }
        
        // Answer form
        VBox answerForm = new VBox(10);
        Label formTitle = new Label("Your Answer");
        formTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        formTitle.setStyle("-fx-text-fill: #00FF7F;");
        
        TextArea answerInput = new TextArea();
        answerInput.setPromptText("Write your answer here...");
        answerInput.setPrefRowCount(6);
        answerInput.setStyle("-fx-background-color: #1A1A1A; -fx-border-color: #444444; -fx-text-fill: #E0E0E0; -fx-control-inner-background: #1A1A1A;");
        
        Button submitButton = new Button("Submit Answer");
        submitButton.getStyleClass().add("button");
        
        // Add proper spacing and alignment for the submit button
        HBox submitButtonContainer = new HBox();
        submitButtonContainer.setAlignment(Pos.CENTER_RIGHT);
        submitButtonContainer.setPadding(new Insets(10, 0, 0, 0));
        submitButtonContainer.getChildren().add(submitButton);
        
        answerForm.getChildren().addAll(formTitle, answerInput, submitButtonContainer);
        
        // Add event handler for submit answer
        submitButton.setOnAction(e -> {
            String answerContent = answerInput.getText().trim();
            
            // Validation
            if (answerContent.isEmpty()) {
                showAlert("Error", "Please enter your answer");
                return;
            }
            
            if (currentUser == null) {
                showAlert("Error", "Please log in to submit an answer");
                showLogin();
                return;
            }
            
            if (question == null) {
                showAlert("Error", "Question not found");
                return;
            }
            
            try {
                // Create new answer
                Answer newAnswer = new Answer();
                newAnswer.setQuestionId(question.getId());
                newAnswer.setUserId(currentUser.getId());
                newAnswer.setContent(answerContent);
                newAnswer.setUserName(currentUser.getName());
                newAnswer.setCreatedAt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                newAnswer.setUpvotes(0);
                newAnswer.setAccepted(false);
                newAnswer.setAnonymous(false);
                
                // Save to database
                boolean success = answerDAO.createAnswer(newAnswer);
                
                if (success) {
                    showAlert("Success", "Answer submitted successfully!");
                    
                    // Clear form
                    answerInput.clear();
                    
                    // Refresh the question detail view to show the new answer
                    showQuestionDetail(question);
                } else {
                    showAlert("Error", "Failed to submit answer. Please try again.");
                }
                
            } catch (Exception ex) {
                System.err.println("Error submitting answer: " + ex.getMessage());
                showAlert("Error", "Failed to submit answer: " + ex.getMessage());
            }
        });
        
        panel.getChildren().addAll(backButton, questionContent, answersTitle, answersList, answerForm);
        return panel;
    }
    
    private VBox createAnswerCard(Answer answer) {
        return createAnswerCard(answer.getUserName(), answer.getCreatedAt(), answer.getContent());
    }
    
    private VBox createAnswerCard(String author, String time, String content) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.getStyleClass().add("answer-card");
        
        HBox meta = new HBox(10);
        meta.getChildren().addAll(
            new Label(author),
            new Label(formatDate(time))
        );
        meta.getChildren().forEach(node -> {
            if (node instanceof Label) {
                ((Label) node).setTextFill(Color.rgb(176, 176, 176));
            }
        });
        
        TextArea contentArea = new TextArea(content);
        contentArea.setEditable(false);
        contentArea.setWrapText(true);
        contentArea.setStyle("-fx-background-color: #1A1A1A; -fx-border-color: #444444; -fx-text-fill: #E0E0E0; -fx-control-inner-background: #1A1A1A;");
        
        card.getChildren().addAll(meta, contentArea);
        return card;
    }
    
    private VBox createAskQuestion() {
        VBox panel = new VBox(20);
        panel.setPadding(new Insets(20));
        panel.setStyle("-fx-background-color: #121212;");
        
        Label title = new Label("Ask a Question");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: #00FF7F;");
        
        VBox form = new VBox(15);
        form.getStyleClass().add("form-container");
        
        // Title field
        Label titleLabel = new Label("Title");
        TextField titleField = new TextField();
        titleField.setPromptText("Enter your question title");
        
        // Details field
        Label detailsLabel = new Label("Details");
        TextArea detailsArea = new TextArea();
        detailsArea.setPromptText("Provide more details about your question");
        detailsArea.setPrefRowCount(8);
        detailsArea.setStyle("-fx-background-color: #1A1A1A; -fx-border-color: #444444; -fx-text-fill: #E0E0E0; -fx-control-inner-background: #1A1A1A;");
        
        // Category dropdown
        Label categoryLabel = new Label("Category");
        ComboBox<String> categoryCombo = new ComboBox<>();
        categoryCombo.getItems().addAll(
            "Select a category", "Academics", "Campus Life", "Career", "Technology", "Miscellaneous"
        );
        categoryCombo.setValue("Select a category");
        
        // Tags field
        Label tagsLabel = new Label("Tags (optional, comma separated)");
        TextField tagsField = new TextField();
        tagsField.setPromptText("e.g., study, exams, time-management");
        
        // Buttons
        HBox buttons = new HBox(10);
        Button postButton = new Button("Post Question");
        postButton.getStyleClass().add("button");
        
        Button cancelButton = new Button("Cancel");
        cancelButton.getStyleClass().add("text-button");
        
        buttons.getChildren().addAll(postButton, cancelButton);
        
        form.getChildren().addAll(
            titleLabel, titleField,
            detailsLabel, detailsArea,
            categoryLabel, categoryCombo,
            tagsLabel, tagsField,
            buttons
        );
        
        panel.getChildren().addAll(title, form);
        
        // Add event handlers
        postButton.setOnAction(e -> {
            String questionTitle = titleField.getText().trim();
            String questionDetails = detailsArea.getText().trim();
            String selectedCategory = categoryCombo.getValue();
            String tags = tagsField.getText().trim();
            
            // Validation
            if (questionTitle.isEmpty()) {
                showAlert("Error", "Please enter a question title");
                return;
            }
            
            if (questionDetails.isEmpty()) {
                showAlert("Error", "Please enter question details");
                return;
            }
            
            if ("Select a category".equals(selectedCategory)) {
                showAlert("Error", "Please select a category");
                return;
            }
            
            if (currentUser == null) {
                showAlert("Error", "Please log in to post a question");
                showLogin();
                return;
            }
            
            try {
                // Create new question
                Question newQuestion = new Question();
                newQuestion.setTitle(questionTitle);
                newQuestion.setBody(questionDetails);
                newQuestion.setCategory(selectedCategory);
                newQuestion.setUserId(currentUser.getId());
                newQuestion.setUserName(currentUser.getName());
                newQuestion.setCreatedAt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                newQuestion.setAnswerCount(0);
                
                // Save to database
                boolean success = questionDAO.createQuestion(newQuestion);
                
                if (success) {
                    showAlert("Success", "Question posted successfully!");
                    
                    // Clear form
                    titleField.clear();
                    detailsArea.clear();
                    categoryCombo.setValue("Select a category");
                    tagsField.clear();
                    
                    // Refresh questions list
                    allQuestions = questionDAO.getAllQuestions();
                    if (questionsListContainer != null) {
                        filterQuestions();
                    }
                    
                    // Return to question list
                    showQuestionList();
                } else {
                    showAlert("Error", "Failed to post question. Please try again.");
                }
                
            } catch (Exception ex) {
                System.err.println("Error posting question: " + ex.getMessage());
                showAlert("Error", "Failed to post question: " + ex.getMessage());
            }
        });
        cancelButton.setOnAction(e -> showQuestionList());
        
        return panel;
    }
    
    private VBox createLoginPanel() {
        VBox panel = new VBox(20);
        panel.setPadding(new Insets(20));
        panel.setStyle("-fx-background-color: #121212;");
        panel.setAlignment(Pos.CENTER);
        
        VBox loginForm = new VBox(15);
        loginForm.getStyleClass().add("form-container");
        
        Label loginTitle = new Label("Log In");
        loginTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        loginTitle.setStyle("-fx-text-fill: #00FF7F;");
        
        Label emailLabel = new Label("Email");
        TextField emailField = new TextField();
        emailField.setPromptText("Enter your email");
        
        Label passwordLabel = new Label("Password");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        
        // Error label for displaying login errors
        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill: #FF6B6B; -fx-font-size: 12px;");
        errorLabel.setVisible(false);
        
        Button loginButton = new Button("Log In");
        loginButton.getStyleClass().add("button");
        
        HBox registerLink = new HBox();
        registerLink.getChildren().addAll(
            new Label("Don't have an account? "),
            new Hyperlink("Register")
        );
        registerLink.getChildren().forEach(node -> {
            if (node instanceof Label) {
                ((Label) node).setTextFill(Color.rgb(176, 176, 176));
            }
        });
        
        loginForm.getChildren().addAll(
            loginTitle, emailLabel, emailField,
            passwordLabel, passwordField, errorLabel, loginButton,
            registerLink
        );
        
        // Add event handlers
        loginButton.setOnAction(e -> {
            String email = emailField.getText().trim();
            String password = passwordField.getText();
            
            if (email.isEmpty() || password.isEmpty()) {
                errorLabel.setText("Please enter both email and password");
                errorLabel.setVisible(true);
                return;
            }
            
            try {
                // Attempt to authenticate user
                User user = userDAO.authenticateUser(email, password);
                if (user != null) {
                    currentUser = user;
                    errorLabel.setVisible(false);
                    System.out.println("User logged in: " + user.getName());
                    
                    // Update header to show user info
                    updateHeaderForLoggedInUser();
                    
                    // Return to question list
                    showQuestionList();
                } else {
                    errorLabel.setText("Invalid email or password");
                    errorLabel.setVisible(true);
                }
            } catch (Exception ex) {
                System.err.println("Login error: " + ex.getMessage());
                errorLabel.setText("Login failed. Please try again.");
                errorLabel.setVisible(true);
            }
        });
        
        // Handle Enter key press
        passwordField.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("ENTER")) {
                loginButton.fire();
            }
        });
        
        Hyperlink registerLinkNode = (Hyperlink) registerLink.getChildren().get(1);
        registerLinkNode.setOnAction(e -> showRegister());
        
        panel.getChildren().add(loginForm);
        return panel;
    }
    
    private VBox createRegisterPanel() {
        VBox panel = new VBox(20);
        panel.setPadding(new Insets(20));
        panel.setStyle("-fx-background-color: #121212;");
        panel.setAlignment(Pos.CENTER);
        
        VBox registerForm = new VBox(15);
        registerForm.getStyleClass().add("form-container");
        
        Label registerTitle = new Label("Register");
        registerTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        registerTitle.setStyle("-fx-text-fill: #00FF7F;");
        
        Label nameLabel = new Label("Full Name");
        TextField nameField = new TextField();
        nameField.setPromptText("Enter your full name");
        
        Label regEmailLabel = new Label("Galgotias University Email");
        TextField regEmailField = new TextField();
        regEmailField.setPromptText("username@galgotiasuniversity.ac.in");
        
        Label regPasswordLabel = new Label("Password");
        PasswordField regPasswordField = new PasswordField();
        regPasswordField.setPromptText("Enter your password");
        
        Label confirmPasswordLabel = new Label("Confirm Password");
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm your password");
        
        // Error label for displaying registration errors
        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill: #FF6B6B; -fx-font-size: 12px;");
        errorLabel.setVisible(false);
        
        // Success label for displaying registration success
        Label successLabel = new Label("");
        successLabel.setStyle("-fx-text-fill: #00FF7F; -fx-font-size: 12px;");
        successLabel.setVisible(false);
        
        Button registerButton = new Button("Register");
        registerButton.getStyleClass().add("button");
        
        HBox loginLink = new HBox();
        loginLink.getChildren().addAll(
            new Label("Already have an account? "),
            new Hyperlink("Log In")
        );
        loginLink.getChildren().forEach(node -> {
            if (node instanceof Label) {
                ((Label) node).setTextFill(Color.rgb(176, 176, 176));
            }
        });
        
        registerForm.getChildren().addAll(
            registerTitle, nameLabel, nameField,
            regEmailLabel, regEmailField,
            regPasswordLabel, regPasswordField,
            confirmPasswordLabel, confirmPasswordField,
            errorLabel, successLabel, registerButton, loginLink
        );
        
        // Add event handlers
        registerButton.setOnAction(e -> {
            String name = nameField.getText().trim();
            String email = regEmailField.getText().trim();
            String password = regPasswordField.getText();
            String confirmPassword = confirmPasswordField.getText();
            
            // Clear previous messages
            errorLabel.setVisible(false);
            successLabel.setVisible(false);
            
            // Validation
            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                errorLabel.setText("Please fill in all fields");
                errorLabel.setVisible(true);
                return;
            }
            
            if (!email.endsWith("@galgotiasuniversity.ac.in")) {
                errorLabel.setText("Please use your Galgotias University email address");
                errorLabel.setVisible(true);
                return;
            }
            
            if (password.length() < 6) {
                errorLabel.setText("Password must be at least 6 characters long");
                errorLabel.setVisible(true);
                return;
            }
            
            if (!password.equals(confirmPassword)) {
                errorLabel.setText("Passwords do not match");
                errorLabel.setVisible(true);
                return;
            }
            
            try {
                // Check if user already exists
                if (userDAO.getUserByEmail(email) != null) {
                    errorLabel.setText("User with this email already exists");
                    errorLabel.setVisible(true);
                    return;
                }
                
                // Create new user
                User newUser = new User();
                newUser.setName(name);
                newUser.setEmail(email);
                newUser.setPassword(password); // Note: In production, this should be hashed
                
                userDAO.createUser(newUser);
                
                successLabel.setText("Registration successful! You can now log in.");
                successLabel.setVisible(true);
                
                // Clear form
                nameField.clear();
                regEmailField.clear();
                regPasswordField.clear();
                confirmPasswordField.clear();
                
                // Auto-switch to login after 2 seconds
                new Thread(() -> {
                    try {
                        Thread.sleep(2000);
                        javafx.application.Platform.runLater(() -> showLogin());
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                }).start();
                
            } catch (Exception ex) {
                System.err.println("Registration error: " + ex.getMessage());
                errorLabel.setText("Registration failed. Please try again.");
                errorLabel.setVisible(true);
            }
        });
        
        // Handle Enter key press
        confirmPasswordField.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("ENTER")) {
                registerButton.fire();
            }
        });
        
        Hyperlink loginLinkNode = (Hyperlink) loginLink.getChildren().get(1);
        loginLinkNode.setOnAction(e -> showLogin());
        
        panel.getChildren().add(registerForm);
        return panel;
    }
    
    private void showQuestionList() {
        questionList.setVisible(true);
        questionDetail.setVisible(false);
        askQuestion.setVisible(false);
        loginPanel.setVisible(false);
        registerPanel.setVisible(false);
        
        // Refresh questions if needed
        if (questionsListContainer != null) {
            filterQuestions();
        }
    }
    
    private void showQuestionDetail() {
        showQuestionDetail(null);
    }
    
    private void showQuestionDetail(Question question) {
        questionList.setVisible(false);
        questionDetail.setVisible(false);
        askQuestion.setVisible(false);
        loginPanel.setVisible(false);
        registerPanel.setVisible(false);
        
        // Create a new question detail panel for this specific question
        VBox newQuestionDetail = createQuestionDetail(question);
        
        // Remove old question detail if it exists
        mainContent.getChildren().remove(questionDetail);
        
        // Add the new question detail and update the reference
        mainContent.getChildren().add(newQuestionDetail);
        questionDetail = newQuestionDetail;
        
        questionDetail.setVisible(true);
    }
    
    private void showAskQuestion() {
        questionList.setVisible(false);
        questionDetail.setVisible(false);
        askQuestion.setVisible(true);
        loginPanel.setVisible(false);
        registerPanel.setVisible(false);
    }
    
    private void showLogin() {
        System.out.println("showLogin() called - setting login panel visible");
        questionList.setVisible(false);
        questionDetail.setVisible(false);
        askQuestion.setVisible(false);
        loginPanel.setVisible(true);
        registerPanel.setVisible(false);
        System.out.println("Login panel visible: " + loginPanel.isVisible());
    }
    
    private void showRegister() {
        System.out.println("showRegister() called - setting register panel visible");
        questionList.setVisible(false);
        questionDetail.setVisible(false);
        askQuestion.setVisible(false);
        loginPanel.setVisible(false);
        registerPanel.setVisible(true);
        System.out.println("Register panel visible: " + registerPanel.isVisible());
    }
    
    private void logout() {
        System.out.println("Logout button clicked in header");
        currentUser = null;
        updateHeaderForLoggedOutUser();
        showLogin();
    }
    
    private void updateHeaderForLoggedInUser() {
        // Hide login/register buttons and show user info and logout button
        authButtons.setVisible(false);
        userLabel.setVisible(true);
        logoutBtn.setVisible(true);
        userLabel.setText("Welcome, " + currentUser.getName() + "!");
        System.out.println("Header updated for logged in user: " + currentUser.getName());
    }
    
    private void updateHeaderForLoggedOutUser() {
        // Show login/register buttons and hide user info and logout button
        authButtons.setVisible(true);
        userLabel.setVisible(false);
        logoutBtn.setVisible(false);
        System.out.println("Header updated for logged out user");
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
} 