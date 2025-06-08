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

public class PeerQMainApplication extends Application {
    private static final Color PRIMARY_COLOR = Color.rgb(0, 120, 212);
    private static final Color BACKGROUND_COLOR = Color.WHITE;
    private static final Color BORDER_COLOR = Color.rgb(230, 230, 230);
    private static final Color TEXT_COLOR = Color.rgb(100, 100, 100);
    
    private StackPane mainContent;
    private VBox questionList;
    private VBox questionDetail;
    private VBox askQuestion;
    private VBox authPanel;
    
    @Override
    public void start(Stage primaryStage) {
        // Create main layout
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: white;");
        
        // Create header
        HBox header = createHeader();
        root.setTop(header);
        
        // Create main content area
        mainContent = new StackPane();
        mainContent.setStyle("-fx-background-color: white;");
        
        // Initialize all panels
        questionList = createQuestionList();
        questionDetail = createQuestionDetail();
        askQuestion = createAskQuestion();
        authPanel = createAuthPanel();
        
        // Add all panels to main content
        mainContent.getChildren().addAll(questionList, questionDetail, askQuestion, authPanel);
        
        // Show question list by default
        showQuestionList();
        
        root.setCenter(mainContent);
        
        // Create scene
        Scene scene = new Scene(root, 1200, 800);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        
        // Configure stage
        primaryStage.setTitle("PeerQ - College Community Discourse Platform");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private HBox createHeader() {
        HBox header = new HBox(20);
        header.setPadding(new Insets(15));
        header.setStyle("-fx-background-color: white; -fx-border-color: #e6e6e6; -fx-border-width: 0 0 1 0;");
        header.setPrefHeight(60);
        
        // Logo
        Label logo = new Label("PeerQ");
        logo.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        logo.setStyle("-fx-text-fill: #0078d4;");
        
        // Navigation
        HBox nav = new HBox(20);
        Button homeBtn = createNavButton("Home");
        Button askQuestionBtn = createNavButton("Ask Question");
        nav.getChildren().addAll(homeBtn, askQuestionBtn);
        
        // Search bar
        TextField searchField = new TextField();
        searchField.setPromptText("Search questions, topics, or users...");
        searchField.setPrefWidth(300);
        searchField.setStyle("-fx-background-color: white; -fx-border-color: #e6e6e6; -fx-border-radius: 4;");
        
        // Auth buttons
        HBox authButtons = new HBox(10);
        Button loginBtn = createTextButton("Log In");
        Button registerBtn = createTextButton("Register");
        authButtons.getChildren().addAll(loginBtn, registerBtn);
        
        // Add components to header
        header.getChildren().addAll(logo, nav, searchField, authButtons);
        HBox.setHgrow(nav, Priority.ALWAYS);
        
        // Add event handlers
        homeBtn.setOnAction(e -> showQuestionList());
        askQuestionBtn.setOnAction(e -> showAskQuestion());
        loginBtn.setOnAction(e -> showAuth());
        registerBtn.setOnAction(e -> showAuth());
        
        return header;
    }
    
    private Button createNavButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: transparent; -fx-text-fill: #0078d4; -fx-font-size: 14; -fx-font-weight: bold;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: transparent; -fx-text-fill: #005a9e; -fx-font-size: 14; -fx-font-weight: bold;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: transparent; -fx-text-fill: #0078d4; -fx-font-size: 14; -fx-font-weight: bold;"));
        return button;
    }
    
    private Button createTextButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: transparent; -fx-text-fill: #0078d4; -fx-font-size: 14;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: transparent; -fx-text-fill: #005a9e; -fx-font-size: 14;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: transparent; -fx-text-fill: #0078d4; -fx-font-size: 14;"));
        return button;
    }
    
    private VBox createQuestionList() {
        VBox panel = new VBox(20);
        panel.setPadding(new Insets(20));
        panel.setStyle("-fx-background-color: white;");
        
        // Header
        HBox header = new HBox();
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Label title = new Label("Recent Questions");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: #0078d4;");
        
        ComboBox<String> categoryFilter = new ComboBox<>();
        categoryFilter.getItems().addAll("All Categories", "Academics", "Campus Life", "Career", "Technology", "Miscellaneous");
        categoryFilter.setValue("All Categories");
        categoryFilter.setPrefWidth(150);
        categoryFilter.setStyle("-fx-background-color: white; -fx-border-color: #e6e6e6; -fx-border-radius: 4;");
        
        header.getChildren().addAll(title, categoryFilter);
        HBox.setHgrow(title, Priority.ALWAYS);
        HBox.setMargin(categoryFilter, new Insets(0, 0, 0, 20));
        
        // Questions list
        VBox questionsList = new VBox(10);
        questionsList.setStyle("-fx-background-color: white;");
        
        // Add sample questions
        questionsList.getChildren().addAll(
            createQuestionCard("How to prepare for final exams?", 
                "I'm struggling with time management for my final exams. Any tips?",
                "Academics", "5 answers", "2 hours ago"),
            createQuestionCard("Best places to study on campus?",
                "Looking for quiet places to study during exam season.",
                "Campus Life", "3 answers", "5 hours ago")
        );
        
        ScrollPane scrollPane = new ScrollPane(questionsList);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: white; -fx-border-color: transparent;");
        
        panel.getChildren().addAll(header, scrollPane);
        return panel;
    }
    
    private VBox createQuestionCard(String title, String preview, String category, String answers, String time) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-border-color: #e6e6e6; -fx-border-width: 0 0 1 0;");
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        titleLabel.setStyle("-fx-text-fill: #0078d4;");
        
        Label previewLabel = new Label(preview);
        previewLabel.setTextFill(Color.rgb(100, 100, 100));
        
        HBox meta = new HBox(10);
        Label categoryLabel = new Label(category);
        categoryLabel.setTextFill(Color.rgb(0, 120, 212));
        
        Label answersLabel = new Label(answers);
        answersLabel.setTextFill(Color.rgb(100, 100, 100));
        
        Label timeLabel = new Label(time);
        timeLabel.setTextFill(Color.rgb(100, 100, 100));
        
        meta.getChildren().addAll(categoryLabel, answersLabel, timeLabel);
        
        card.getChildren().addAll(titleLabel, previewLabel, meta);
        
        // Add click handler
        card.setOnMouseClicked(e -> showQuestionDetail());
        
        return card;
    }
    
    private VBox createQuestionDetail() {
        VBox panel = new VBox(20);
        panel.setPadding(new Insets(20));
        panel.setStyle("-fx-background-color: white;");
        
        // Back button
        Button backButton = new Button("← Back to Questions");
        backButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #0078d4;");
        backButton.setOnAction(e -> showQuestionList());
        
        // Question content
        VBox questionContent = new VBox(10);
        Label title = new Label("How to prepare for final exams?");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        HBox meta = new HBox(10);
        meta.getChildren().addAll(
            new Label("Posted by John Doe"),
            new Label("2 hours ago"),
            new Label("Academics")
        );
        
        TextArea content = new TextArea(
            "I'm struggling with time management for my final exams. " +
            "I have 5 subjects to prepare for and only 2 weeks left. " +
            "Any tips on how to organize my study schedule effectively?"
        );
        content.setEditable(false);
        content.setWrapText(true);
        content.setStyle("-fx-background-color: white; -fx-border-color: transparent;");
        
        questionContent.getChildren().addAll(title, meta, content);
        
        // Answers section
        VBox answersSection = new VBox(20);
        Label answersTitle = new Label("Answers");
        answersTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        VBox answersList = new VBox(10);
        answersList.getChildren().addAll(
            createAnswerCard("Jane Smith", "2 hours ago",
                "Here's what worked for me:\n\n" +
                "1. Create a study schedule with specific time slots for each subject\n" +
                "2. Take regular breaks (25 minutes study, 5 minutes break)\n" +
                "3. Use active recall techniques instead of passive reading\n" +
                "4. Form study groups for difficult topics\n" +
                "5. Get enough sleep and exercise"),
            createAnswerCard("Mike Johnson", "1 hour ago",
                "I recommend using the Pomodoro Technique. Study for 25 minutes, " +
                "then take a 5-minute break. After 4 pomodoros, take a longer break. " +
                "This helps maintain focus and prevents burnout.")
        );
        
        // Answer form
        VBox answerForm = new VBox(10);
        Label formTitle = new Label("Your Answer");
        formTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        TextArea answerInput = new TextArea();
        answerInput.setPromptText("Write your answer here...");
        answerInput.setPrefRowCount(6);
        
        Button submitButton = new Button("Submit Answer");
        submitButton.setStyle("-fx-background-color: #0078d4; -fx-text-fill: white;");
        
        answerForm.getChildren().addAll(formTitle, answerInput, submitButton);
        
        panel.getChildren().addAll(backButton, questionContent, answersTitle, answersList, answerForm);
        return panel;
    }
    
    private VBox createAnswerCard(String author, String time, String content) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-border-color: #e6e6e6; -fx-border-width: 0 0 1 0;");
        
        HBox meta = new HBox(10);
        meta.getChildren().addAll(
            new Label(author),
            new Label(time)
        );
        
        TextArea contentArea = new TextArea(content);
        contentArea.setEditable(false);
        contentArea.setWrapText(true);
        contentArea.setStyle("-fx-background-color: white; -fx-border-color: transparent;");
        
        card.getChildren().addAll(meta, contentArea);
        return card;
    }
    
    private VBox createAskQuestion() {
        VBox panel = new VBox(20);
        panel.setPadding(new Insets(20));
        panel.setStyle("-fx-background-color: white;");
        
        Label title = new Label("Ask a Question");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        VBox form = new VBox(15);
        
        // Title field
        Label titleLabel = new Label("Title");
        TextField titleField = new TextField();
        titleField.setPromptText("Enter your question title");
        
        // Details field
        Label detailsLabel = new Label("Details");
        TextArea detailsArea = new TextArea();
        detailsArea.setPromptText("Provide more details about your question");
        detailsArea.setPrefRowCount(8);
        
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
        postButton.setStyle("-fx-background-color: #0078d4; -fx-text-fill: white;");
        
        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #0078d4;");
        
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
        postButton.setOnAction(e -> showQuestionList());
        cancelButton.setOnAction(e -> showQuestionList());
        
        return panel;
    }
    
    private VBox createAuthPanel() {
        VBox panel = new VBox(20);
        panel.setPadding(new Insets(20));
        panel.setStyle("-fx-background-color: white;");
        panel.setAlignment(javafx.geometry.Pos.CENTER);
        
        // Login form
        VBox loginForm = new VBox(15);
        Label loginTitle = new Label("Log In");
        loginTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        Label emailLabel = new Label("Email");
        TextField emailField = new TextField();
        emailField.setPromptText("Enter your email");
        
        Label passwordLabel = new Label("Password");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        
        Button loginButton = new Button("Log In");
        loginButton.setStyle("-fx-background-color: #0078d4; -fx-text-fill: white;");
        
        HBox registerLink = new HBox();
        registerLink.getChildren().addAll(
            new Label("Don't have an account? "),
            new Hyperlink("Register")
        );
        
        loginForm.getChildren().addAll(
            loginTitle, emailLabel, emailField,
            passwordLabel, passwordField, loginButton,
            registerLink
        );
        
        // Register form
        VBox registerForm = new VBox(15);
        Label registerTitle = new Label("Register");
        registerTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
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
        
        Button registerButton = new Button("Register");
        registerButton.setStyle("-fx-background-color: #0078d4; -fx-text-fill: white;");
        
        HBox loginLink = new HBox();
        loginLink.getChildren().addAll(
            new Label("Already have an account? "),
            new Hyperlink("Log In")
        );
        
        registerForm.getChildren().addAll(
            registerTitle, nameLabel, nameField,
            regEmailLabel, regEmailField,
            regPasswordLabel, regPasswordField,
            confirmPasswordLabel, confirmPasswordField,
            registerButton, loginLink
        );
        
        // Add forms to panel
        panel.getChildren().addAll(loginForm, registerForm);
        
        // Add event handlers
        loginButton.setOnAction(e -> showQuestionList());
        registerButton.setOnAction(e -> showQuestionList());
        
        return panel;
    }
    
    private void showQuestionList() {
        questionList.setVisible(true);
        questionDetail.setVisible(false);
        askQuestion.setVisible(false);
        authPanel.setVisible(false);
    }
    
    private void showQuestionDetail() {
        questionList.setVisible(false);
        questionDetail.setVisible(true);
        askQuestion.setVisible(false);
        authPanel.setVisible(false);
    }
    
    private void showAskQuestion() {
        questionList.setVisible(false);
        questionDetail.setVisible(false);
        askQuestion.setVisible(true);
        authPanel.setVisible(false);
    }
    
    private void showAuth() {
        questionList.setVisible(false);
        questionDetail.setVisible(false);
        askQuestion.setVisible(false);
        authPanel.setVisible(true);
    }
    
    public static void main(String[] args) {
        launch(args);
    }
} 