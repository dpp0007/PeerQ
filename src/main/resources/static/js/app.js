/**
 * PeerQ - College Community Platform
 * Main JavaScript application file
 */

// Global state object - stores application data
const state = {
    user: null,              // Current logged-in user information
    currentQuestion: null,   // Currently viewed question details
    questions: []            // List of questions for the main view
};

// DOM element references - cached for performance
const elements = {
    // Navigation elements
    mainNav: document.getElementById('main-nav'),
    authSection: document.getElementById('auth-section'),
    askQuestionBtn: document.getElementById('ask-question-btn'),
    
    // Section containers
    authForms: document.getElementById('auth-forms'),
    loginForm: document.getElementById('login-form'),
    registerForm: document.getElementById('register-form'),
    questionList: document.getElementById('question-list'),
    questionDetail: document.getElementById('question-detail'),
    askQuestion: document.getElementById('ask-question'),
    
    // Form elements
    loginFormElement: document.getElementById('login-form-element'),
    registerFormElement: document.getElementById('register-form-element'),
    questionForm: document.getElementById('question-form'),
    answerForm: document.getElementById('answer-form'),
    
    // Navigation links
    showRegister: document.getElementById('show-register'),
    showLogin: document.getElementById('show-login'),
    backToQuestions: document.getElementById('back-to-questions'),
    cancelQuestion: document.getElementById('cancel-question'),
    
    // Content containers
    questionsContainer: document.getElementById('questions-container'),
    questionDetailContainer: document.getElementById('question-detail-container'),
    answersContainer: document.getElementById('answers-container'),
    answerFormContainer: document.getElementById('answer-form-container'),
    
    // UI elements
    notification: document.getElementById('notification')
};

/**
 * Initialize the application
 * - Check authentication status
 * - Load initial data
 * - Set up event handlers
 */
function initApp() {
    // Check if user is already logged in
    checkAuthStatus();
    
    // Load initial questions
    loadQuestions();
    
    // Setup all event listeners
    setupEventListeners();
}

/**
 * API communication helper function
 * @param {string} endpoint - API endpoint to call
 * @param {string} method - HTTP method (GET, POST, etc.)
 * @param {Object} body - Request body data (for POST, PUT, etc.)
 * @returns {Promise<Object>} - Response data
 */
async function fetchAPI(endpoint, method = 'GET', body = null) {
    const options = {
        method,
        headers: {
            'Content-Type': 'application/json'
        },
        credentials: 'include' // Include cookies for session management
    };
    
    // Add request body if provided
    if (body) {
        options.body = JSON.stringify(body);
    }
    
    try {
        const response = await fetch(endpoint, options);
        const data = await response.json();
        
        // Handle non-2xx responses
        if (!response.ok) {
            throw new Error(data.message || `API error: ${response.status}`);
        }
        
        return data;
    } catch (error) {
        console.error(`API Error (${endpoint}):`, error);
        throw error; // Re-throw for handling by the caller
    }
}

/**
 * AUTHENTICATION FUNCTIONS
 */

/**
 * Check if the user is already logged in
 * Uses localStorage for session persistence (would use cookies in production)
 */
async function checkAuthStatus() {
    try {
        // For demo purposes, we're using localStorage
        // In production, this should be a server API call to verify session
        const savedUser = localStorage.getItem('peerqUser');
        
        if (savedUser) {
            state.user = JSON.parse(savedUser);
        } else {
            state.user = null;
        }
        
        // Update UI based on authentication status
        updateAuthUI();
    } catch (error) {
        console.error('Authentication check failed:', error);
        // Reset to logged-out state on error
        state.user = null;
        updateAuthUI();
    }
}

/**
 * Log in a user
 * @param {string} email - User email
 * @param {string} password - User password
 * @returns {Promise<Object>} - Login result
 */
async function login(email, password) {
    try {
        const data = await fetchAPI('/api/auth/login', 'POST', { email, password });
        
        if (data.success) {
            // Store user data
            state.user = {
                id: data.userId,
                name: data.userName,
                email: email,
                isAdmin: data.isAdmin
            };
            
            // Save to localStorage for session persistence
            localStorage.setItem('peerqUser', JSON.stringify(state.user));
            
            // Update UI
            updateAuthUI();
            hideAuthForms();
            showNotification('Logged in successfully!', 'success');
        }
        
        return data;
    } catch (error) {
        showNotification(error.message || 'Login failed. Please try again.', 'error');
        throw error;
    }
}

/**
 * Register a new user
 * @param {string} name - User's full name
 * @param {string} email - User's email (must be @galgotiasuniversity.ac.in)
 * @param {string} password - User's password
 * @returns {Promise<Object>} - Registration result
 */
async function register(name, email, password) {
    try {
        const data = await fetchAPI('/api/auth/register', 'POST', { name, email, password });
        
        if (data.success) {
            // Don't auto-login after registration
            state.user = null;
            localStorage.removeItem('peerqUser');
            
            // Show login form for user to authenticate
            showLoginForm();
            showNotification('Registered successfully! Please log in with your credentials.', 'success');
        }
        
        return data;
    } catch (error) {
        showNotification(error.message || 'Registration failed. Please try again.', 'error');
        throw error;
    }
}

/**
 * Log out the current user
 */
async function logout() {
    try {
        // Call logout API endpoint
        await fetchAPI('/api/auth/logout', 'POST');
        
        // Clear user session data
        state.user = null;
        localStorage.removeItem('peerqUser');
        
        // Update UI
        updateAuthUI();
        showNotification('Logged out successfully!', 'success');
    } catch (error) {
        console.error('Logout error:', error);
        // Force logout on frontend anyway
        state.user = null;
        localStorage.removeItem('peerqUser');
        updateAuthUI();
    }
}

/**
 * QUESTION FUNCTIONS
 */

/**
 * Load questions, optionally filtered by category
 * @param {string} category - Optional category filter
 */
async function loadQuestions(category = '') {
    try {
        // Show loading indicator
        elements.questionsContainer.innerHTML = '<div class="loading">Loading questions...</div>';
        
        // Build API endpoint with optional category filter
        let endpoint = '/api/questions';
        if (category) {
            endpoint += `?category=${category}`;
        }
        
        // Fetch questions from API
        const questions = await fetchAPI(endpoint);
        state.questions = questions;
        
        // Render the questions to DOM
        renderQuestions(questions);
    } catch (error) {
        console.error('Error loading questions:', error);
        // Show error with retry button
        elements.questionsContainer.innerHTML = `
            <div class="error">
                <p>Failed to load questions. Please try again later.</p>
                <button class="btn btn-primary" onclick="loadQuestions()">Retry</button>
            </div>
        `;
    }
}

/**
 * Load detailed view of a specific question with its answers
 * @param {string|number} questionId - ID of the question to load
 */
async function loadQuestionDetail(questionId) {
    try {
        // Show loading indicators
        elements.questionDetailContainer.innerHTML = '<div class="loading">Loading question...</div>';
        elements.answersContainer.innerHTML = '';
        
        // Fetch question data with answers
        const data = await fetchAPI(`/api/questions/${questionId}`);
        state.currentQuestion = data.question;
        
        // Render question and answers
        renderQuestionDetail(data.question, data.answers);
        
        // Switch to question detail view
        showSection(elements.questionDetail);
    } catch (error) {
        console.error('Error loading question detail:', error);
        // Show error with retry button
        elements.questionDetailContainer.innerHTML = `
            <div class="error">
                <p>Failed to load question details. Please try again later.</p>
                <button class="btn btn-primary" onclick="loadQuestionDetail('${questionId}')">Retry</button>
            </div>
        `;
    }
}

/**
 * Submit a new question
 * @param {Object} questionData - Question data object
 * @returns {Promise<Object>} - Submission result
 */
async function submitQuestion(questionData) {
    try {
        const data = await fetchAPI('/api/questions', 'POST', questionData);
        
        if (data.success) {
            // Reset the form
            elements.questionForm.reset();
            
            // Return to question list and reload questions
            showSection(elements.questionList);
            loadQuestions();
            
            // Show success notification
            showNotification('Question posted successfully!', 'success');
        }
        
        return data;
    } catch (error) {
        showNotification(error.message || 'Failed to post question. Please try again.', 'error');
        throw error;
    }
}

/**
 * Submit an answer to a question
 * @param {string|number} questionId - ID of the question being answered
 * @param {Object} answerData - Answer data object
 * @returns {Promise<Object>} - Submission result
 */
async function submitAnswer(questionId, answerData) {
    try {
        const data = await fetchAPI(`/api/questions/${questionId}/answers`, 'POST', answerData);
        
        if (data.success) {
            // Reset the answer form
            elements.answerForm.reset();
            
            // Reload question to show the new answer
            loadQuestionDetail(questionId);
            
            // Show success notification
            showNotification('Answer submitted successfully!', 'success');
        }
        
        return data;
    } catch (error) {
        showNotification(error.message || 'Failed to submit answer. Please try again.', 'error');
        throw error;
    }
}

/**
 * UI FUNCTIONS
 */

/**
 * Update authentication UI elements based on login status
 */
function updateAuthUI() {
    if (state.user) {
        // User is logged in
        elements.authSection.innerHTML = `
            <div class="user-menu">
                <div class="user-info">
                    <span>${state.user.name}</span>
                    <button id="logout-btn" class="btn btn-text">Logout</button>
                </div>
            </div>
        `;
        
        // Update mobile auth section
        const mobileAuthSection = document.getElementById('mobile-auth-section');
        if (mobileAuthSection) {
            mobileAuthSection.innerHTML = `
                <div class="user-info">
                    <span>Logged in as: ${state.user.name}</span>
                </div>
                <button id="mobile-logout-btn" class="btn btn-primary">Logout</button>
            `;
            
            // Add mobile logout event listener
            document.getElementById('mobile-logout-btn').addEventListener('click', logout);
        }
        
        // Add desktop logout event listener
        document.getElementById('logout-btn').addEventListener('click', logout);
        
        // Show ask question button
        elements.askQuestionBtn.classList.remove('hidden');
        
        // Show answer form if on question detail page
        if (state.currentQuestion) {
            elements.answerFormContainer.classList.remove('hidden');
        }
    } else {
        // User is not logged in
        elements.authSection.innerHTML = `
            <button id="login-btn" class="btn btn-text">Log In</button>
            <button id="register-btn" class="btn btn-primary">Register</button>
        `;
        
        // Update mobile auth section
        const mobileAuthSection = document.getElementById('mobile-auth-section');
        if (mobileAuthSection) {
            mobileAuthSection.innerHTML = `
                <button id="mobile-login-btn" class="btn btn-text">Log In</button>
                <button id="mobile-register-btn" class="btn btn-primary">Register</button>
            `;
            
            // Add mobile auth buttons event listeners
            document.getElementById('mobile-login-btn').addEventListener('click', showLoginForm);
            document.getElementById('mobile-register-btn').addEventListener('click', showRegisterForm);
        }
        
        // Add desktop auth buttons event listeners
        document.getElementById('login-btn').addEventListener('click', showLoginForm);
        document.getElementById('register-btn').addEventListener('click', showRegisterForm);
        
        // Hide ask question button
        elements.askQuestionBtn.classList.add('hidden');
        
        // Hide answer form if on question detail page
        elements.answerFormContainer.classList.add('hidden');
    }
}

/**
 * Render the list of questions to the DOM
 * @param {Array} questions - Array of question objects
 */
function renderQuestions(questions) {
    // Handle empty state
    if (questions.length === 0) {
        elements.questionsContainer.innerHTML = `
            <div class="no-questions">
                <p>No questions found. Be the first to ask a question!</p>
            </div>
        `;
        return;
    }
    
    // Generate HTML for each question
    const questionsHTML = questions.map(question => `
        <div class="question-card" data-question-id="${question.id}">
            <h3 class="question-title">${question.title}</h3>
            <div class="question-meta">
                <span>${question.userName || 'User'}</span>
                <span>${formatDate(question.createdAt)}</span>
            </div>
            <span class="question-category">${formatCategory(question.category)}</span>
            <div class="question-stats">
                <span>${question.answerCount || 0} answers</span>
            </div>
            ${question.tags ? `
                <div class="question-tags">
                    ${question.tags.split(',').map(tag => `
                        <span class="question-tag">${tag.trim()}</span>
                    `).join('')}
                </div>
            ` : ''}
        </div>
    `).join('');
    
    // Insert HTML into container
    elements.questionsContainer.innerHTML = questionsHTML;
    
    // Add click event listeners to question cards
    document.querySelectorAll('.question-card').forEach(card => {
        card.addEventListener('click', () => {
            const questionId = card.dataset.questionId;
            loadQuestionDetail(questionId);
        });
    });
}

/**
 * Render a detailed question view with its answers
 * @param {Object} question - Question object
 * @param {Array} answers - Array of answer objects
 */
function renderQuestionDetail(question, answers) {
    // Render question
    elements.questionDetailContainer.innerHTML = `
        <div class="question-detail">
            <h2 class="question-detail-title">${question.title}</h2>
            <div class="question-detail-content">${question.body}</div>
            <div class="question-detail-footer">
                <span class="question-author">${question.userName || 'User'}</span>
                <span class="question-date">${formatDate(question.createdAt)}</span>
            </div>
        </div>
    `;
    
    // Render answers
    if (answers && answers.length > 0) {
        const answersHTML = `
            <h3 class="answers-header">${answers.length} ${answers.length === 1 ? 'Answer' : 'Answers'}</h3>
            ${answers.map(answer => `
                <div class="answer-card">
                    <div class="answer-content">${answer.content}</div>
                    <div class="answer-footer">
                        <span class="answer-author">${answer.userName || 'User'}</span>
                        <span class="answer-date">${formatDate(answer.createdAt)}</span>
                    </div>
                </div>
            `).join('')}
        `;
        
        elements.answersContainer.innerHTML = answersHTML;
    } else {
        elements.answersContainer.innerHTML = `
            <h3 class="answers-header">No Answers Yet</h3>
            <p class="no-answers">Be the first to answer this question!</p>
        `;
    }
    
    // Show answer form if user is logged in
    if (state.user) {
        elements.answerFormContainer.classList.remove('hidden');
    } else {
        elements.answerFormContainer.classList.add('hidden');
    }
}

/**
 * Switch the active section (page view)
 * @param {HTMLElement} section - The section to show
 */
function showSection(section) {
    // Hide all sections
    elements.authForms.classList.add('hidden');
    elements.questionList.classList.add('hidden');
    elements.questionDetail.classList.add('hidden');
    elements.askQuestion.classList.add('hidden');
    
    // Show the selected section
    section.classList.remove('hidden');
    section.classList.add('active');
    
    // Update navigation highlighting for both desktop and mobile
    const navLinks = document.querySelectorAll('#main-nav a, .mobile-nav a');
    navLinks.forEach(link => {
        link.classList.remove('active');
        if (link.dataset.section === section.id) {
            link.classList.add('active');
        }
    });
}

/**
 * Show the login form
 */
function showLoginForm() {
    elements.registerForm.classList.add('hidden');
    elements.loginForm.classList.remove('hidden');
    showSection(elements.authForms);
}

/**
 * Show the registration form
 */
function showRegisterForm() {
    elements.loginForm.classList.add('hidden');
    elements.registerForm.classList.remove('hidden');
    showSection(elements.authForms);
}

/**
 * Hide auth forms and return to question list
 */
function hideAuthForms() {
    showSection(elements.questionList);
}

/**
 * Show a notification message to the user
 * @param {string} message - Message to display
 * @param {string} type - Notification type (info, success, error)
 */
function showNotification(message, type = 'info') {
    elements.notification.textContent = message;
    elements.notification.className = `notification ${type} visible`;
    
    // Auto-hide notification after 3 seconds
    setTimeout(() => {
        elements.notification.classList.remove('visible');
    }, 3000);
}

/**
 * Format a date string for display
 * @param {string} dateString - ISO date string
 * @returns {string} - Formatted date
 */
function formatDate(dateString) {
    if (!dateString) return 'Unknown date';
    
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
    });
}

/**
 * Format a category string for display
 * @param {string} category - Category in snake_case
 * @returns {string} - Formatted category in Title Case
 */
function formatCategory(category) {
    if (!category) return 'Uncategorized';
    
    // Convert snake_case to Title Case
    return category
        .split('_')
        .map(word => word.charAt(0).toUpperCase() + word.slice(1))
        .join(' ');
}

/**
 * EVENT LISTENERS
 * Set up all event listeners for the application
 */
function setupEventListeners() {
    // Mobile menu toggle
    const mobileMenuButton = document.getElementById('mobile-menu-toggle');
    const mobileMenu = document.getElementById('mobile-menu');
    
    if (mobileMenuButton && mobileMenu) {
        // Toggle mobile menu when button is clicked
        mobileMenuButton.addEventListener('click', function(e) {
            e.preventDefault();
            this.classList.toggle('active');
            mobileMenu.classList.toggle('show');
        });
        
        // Close menu when clicking outside
        document.addEventListener('click', function(event) {
            if (!event.target.closest('#mobile-menu-toggle') && 
                !event.target.closest('#mobile-menu') && 
                mobileMenu.classList.contains('show')) {
                mobileMenu.classList.remove('show');
                mobileMenuButton.classList.remove('active');
            }
        });
    }
    
    // Mobile ask question button
    const mobileAskQuestionBtn = document.getElementById('mobile-ask-question-btn');
    if (mobileAskQuestionBtn) {
        mobileAskQuestionBtn.addEventListener('click', function(e) {
            e.preventDefault();
            
            // Check if user is logged in
            if (!state.user) {
                showNotification('You must be logged in to ask a question', 'error');
                showLoginForm();
                return;
            }
            
            // Show ask question form
            showSection(elements.askQuestion);
            
            // Close mobile menu
            if (mobileMenu) {
                mobileMenu.classList.remove('show');
                if (mobileMenuButton) mobileMenuButton.classList.remove('active');
            }
        });
    }
    
    // Mobile navigation links
    const mobilNavLinks = document.querySelectorAll('.mobile-nav a');
    mobilNavLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            const sectionId = this.dataset.section;
            if (sectionId) {
                const section = document.getElementById(sectionId);
                if (section) {
                    // Show selected section
                    showSection(section);
                    
                    // Update active state on nav links
                    document.querySelectorAll('#main-nav a, .mobile-nav a').forEach(navLink => {
                        if (navLink.dataset.section === sectionId) {
                            navLink.classList.add('active');
                        } else {
                            navLink.classList.remove('active');
                        }
                    });
                    
                    // Reload questions when navigating to home
                    if (sectionId === 'question-list') {
                        loadQuestions();
                    }
                }
            }
            
            // Close mobile menu
            mobileMenu.classList.remove('show');
            mobileMenuButton.classList.remove('active');
        });
    });
    
    // Mobile auth buttons event listeners
    document.addEventListener('click', function(event) {
        // Mobile login button
        if (event.target.id === 'mobile-login-btn') {
            event.preventDefault();
            showLoginForm();
            // Close mobile menu
            if (mobileMenu) {
                mobileMenu.classList.remove('show');
                if (mobileMenuButton) mobileMenuButton.classList.remove('active');
            }
        }
        
        // Mobile register button
        if (event.target.id === 'mobile-register-btn') {
            event.preventDefault();
            showRegisterForm();
            // Close mobile menu
            if (mobileMenu) {
                mobileMenu.classList.remove('show');
                if (mobileMenuButton) mobileMenuButton.classList.remove('active');
            }
        }
        
        // Mobile logout button
        if (event.target.id === 'mobile-logout-btn') {
            event.preventDefault();
            logout();
            // Close mobile menu
            if (mobileMenu) {
                mobileMenu.classList.remove('show');
                if (mobileMenuButton) mobileMenuButton.classList.remove('active');
            }
        }
    });

    // Desktop navigation links
    document.querySelectorAll('#main-nav a').forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            const sectionId = this.dataset.section;
            if (sectionId) {
                const section = document.getElementById(sectionId);
                if (section) {
                    showSection(section);
                    if (sectionId === 'question-list') {
                        loadQuestions(); // Reload questions when navigating to home
                    }
                }
            }
        });
    });

    // Login form submission
    elements.loginFormElement.addEventListener('submit', function(e) {
        e.preventDefault();
        
        const email = document.getElementById('login-email').value;
        const password = document.getElementById('login-password').value;
        
        login(email, password).catch(error => console.error('Login error:', error));
    });
    
    // Registration form submission
    elements.registerFormElement.addEventListener('submit', function(e) {
        e.preventDefault();
        
        const name = document.getElementById('register-name').value;
        const email = document.getElementById('register-email').value;
        const password = document.getElementById('register-password').value;
        const confirmPassword = document.getElementById('register-confirm-password').value;
        
        // Validate password match
        if (password !== confirmPassword) {
            showNotification('Passwords do not match!', 'error');
            return;
        }
        
        // Validate email domain
        if (!email.endsWith('@galgotiasuniversity.ac.in')) {
            showNotification('Please use a valid Galgotias University email address (@galgotiasuniversity.ac.in)', 'error');
            return;
        }
        
        register(name, email, password).catch(error => console.error('Registration error:', error));
    });
    
    // Auth form navigation
    elements.showRegister.addEventListener('click', function(e) {
        e.preventDefault();
        showRegisterForm();
    });
    
    elements.showLogin.addEventListener('click', function(e) {
        e.preventDefault();
        showLoginForm();
    });
    
    // Question form submission
    elements.questionForm.addEventListener('submit', function(e) {
        e.preventDefault();
        
        // Check if user is logged in
        if (!state.user) {
            showNotification('You must be logged in to ask a question', 'error');
            showLoginForm();
            return;
        }
        
        // Gather form data
        const questionData = {
            title: document.getElementById('question-title').value,
            body: document.getElementById('question-body').value,
            category: document.getElementById('question-category').value,
            tags: document.getElementById('question-tags').value,
            isAnonymous: false
        };
        
        submitQuestion(questionData).catch(error => console.error('Submit question error:', error));
    });
    
    // Answer form submission
    elements.answerForm.addEventListener('submit', function(e) {
        e.preventDefault();
        
        // Check if user is logged in
        if (!state.user) {
            showNotification('You must be logged in to submit an answer', 'error');
            showLoginForm();
            return;
        }
        
        // Check if we have a current question
        if (!state.currentQuestion) {
            showNotification('Question not found', 'error');
            return;
        }
        
        // Gather form data
        const answerData = {
            content: document.getElementById('answer-content').value,
            isAnonymous: false
        };
        
        submitAnswer(state.currentQuestion.id, answerData).catch(error => console.error('Submit answer error:', error));
    });
    
    // Ask question button
    elements.askQuestionBtn.addEventListener('click', function(e) {
        e.preventDefault();
        
        // Check if user is logged in
        if (!state.user) {
            showNotification('You must be logged in to ask a question', 'error');
            showLoginForm();
            return;
        }
        
        showSection(elements.askQuestion);
    });
    
    // Back to questions button
    elements.backToQuestions.addEventListener('click', function() {
        state.currentQuestion = null;
        showSection(elements.questionList);
    });
    
    // Cancel question button
    elements.cancelQuestion.addEventListener('click', function() {
        elements.questionForm.reset();
        showSection(elements.questionList);
    });
    
    // Category filter
    document.getElementById('category-filter').addEventListener('change', function() {
        const category = this.value;
        loadQuestions(category);
    });
}

// Initialize app when DOM is loaded
document.addEventListener('DOMContentLoaded', initApp);