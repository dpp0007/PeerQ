// Global state
const state = {
    user: null,
    currentQuestion: null,
    questions: []
};

// DOM elements
const elements = {
    // Navigation
    mainNav: document.getElementById('main-nav'),
    authSection: document.getElementById('auth-section'),
    askQuestionBtn: document.getElementById('ask-question-btn'),
    
    // Sections
    authForms: document.getElementById('auth-forms'),
    loginForm: document.getElementById('login-form'),
    registerForm: document.getElementById('register-form'),
    questionList: document.getElementById('question-list'),
    questionDetail: document.getElementById('question-detail'),
    askQuestion: document.getElementById('ask-question'),
    
    // Forms
    loginFormElement: document.getElementById('login-form-element'),
    registerFormElement: document.getElementById('register-form-element'),
    questionForm: document.getElementById('question-form'),
    answerForm: document.getElementById('answer-form'),
    
    // Links
    showRegister: document.getElementById('show-register'),
    showLogin: document.getElementById('show-login'),
    backToQuestions: document.getElementById('back-to-questions'),
    cancelQuestion: document.getElementById('cancel-question'),
    
    // Containers
    questionsContainer: document.getElementById('questions-container'),
    questionDetailContainer: document.getElementById('question-detail-container'),
    answersContainer: document.getElementById('answers-container'),
    answerFormContainer: document.getElementById('answer-form-container'),
    
    // Notification
    notification: document.getElementById('notification')
};

// Initialize App
function initApp() {
    // Check if user is already logged in (from session)
    checkAuthStatus();
    
    // Load initial questions
    loadQuestions();
    
    // Setup event listeners
    setupEventListeners();
}

// API Functions
async function fetchAPI(endpoint, method = 'GET', body = null) {
    const options = {
        method,
        headers: {
            'Content-Type': 'application/json'
        },
        credentials: 'include' // Include cookies for session
    };
    
    if (body) {
        options.body = JSON.stringify(body);
    }
    
    try {
        const response = await fetch(endpoint, options);
        const data = await response.json();
        
        if (!response.ok) {
            throw new Error(data.message || 'Something went wrong');
        }
        
        return data;
    } catch (error) {
        console.error('API Error:', error);
        throw error;
    }
}

// Auth Functions
async function checkAuthStatus() {
    try {
        // We'll simulate this check for now
        // In a real app, you'd make an API call to check session status
        
        // For now, just check if we have user info in localStorage
        const savedUser = localStorage.getItem('peerqUser');
        
        if (savedUser) {
            state.user = JSON.parse(savedUser);
            updateAuthUI();
        } else {
            state.user = null;
            updateAuthUI();
        }
    } catch (error) {
        console.error('Auth check error:', error);
        state.user = null;
        updateAuthUI();
    }
}

async function login(email, password) {
    try {
        const data = await fetchAPI('/api/auth/login', 'POST', { email, password });
        
        if (data.success) {
            state.user = {
                id: data.userId,
                name: data.userName,
                email: email,
                isAdmin: data.isAdmin
            };
            
            // Save user to localStorage
            localStorage.setItem('peerqUser', JSON.stringify(state.user));
            
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

async function register(name, email, password) {
    try {
        const data = await fetchAPI('/api/auth/register', 'POST', { name, email, password });
        
        if (data.success) {
            state.user = {
                id: data.userId,
                name: data.userName,
                email: email,
                isAdmin: false
            };
            
            // Save user to localStorage
            localStorage.setItem('peerqUser', JSON.stringify(state.user));
            
            updateAuthUI();
            hideAuthForms();
            showNotification('Registered successfully!', 'success');
        }
        
        return data;
    } catch (error) {
        showNotification(error.message || 'Registration failed. Please try again.', 'error');
        throw error;
    }
}

async function logout() {
    try {
        await fetchAPI('/api/auth/logout', 'POST');
        
        // Clear user state and localStorage
        state.user = null;
        localStorage.removeItem('peerqUser');
        
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

// Question Functions
async function loadQuestions(category = '') {
    try {
        elements.questionsContainer.innerHTML = '<div class="loading">Loading questions...</div>';
        
        // Build API endpoint with optional category filter
        let endpoint = '/api/questions';
        if (category) {
            endpoint += `?category=${category}`;
        }
        
        // Fetch questions from API
        const questions = await fetchAPI(endpoint);
        state.questions = questions;
        
        renderQuestions(questions);
    } catch (error) {
        console.error('Error loading questions:', error);
        elements.questionsContainer.innerHTML = `
            <div class="error">
                <p>Failed to load questions. Please try again later.</p>
                <button class="btn btn-primary" onclick="loadQuestions()">Retry</button>
            </div>
        `;
    }
}

async function loadQuestionDetail(questionId) {
    try {
        // Show loading state
        elements.questionDetailContainer.innerHTML = '<div class="loading">Loading question...</div>';
        elements.answersContainer.innerHTML = '';
        
        const data = await fetchAPI(`/api/questions/${questionId}`);
        state.currentQuestion = data.question;
        
        renderQuestionDetail(data.question, data.answers);
        
        // Show the question detail section
        showSection(elements.questionDetail);
    } catch (error) {
        console.error('Error loading question detail:', error);
        elements.questionDetailContainer.innerHTML = `
            <div class="error">
                <p>Failed to load question details. Please try again later.</p>
                <button class="btn btn-primary" onclick="loadQuestionDetail(${questionId})">Retry</button>
            </div>
        `;
    }
}

async function submitQuestion(questionData) {
    try {
        const data = await fetchAPI('/api/questions', 'POST', questionData);
        
        if (data.success) {
            // Reset form
            elements.questionForm.reset();
            
            // Show questions list and reload questions
            showSection(elements.questionList);
            loadQuestions();
            
            showNotification('Question posted successfully!', 'success');
        }
        
        return data;
    } catch (error) {
        showNotification(error.message || 'Failed to post question. Please try again.', 'error');
        throw error;
    }
}

async function submitAnswer(questionId, answerData) {
    try {
        const data = await fetchAPI(`/api/questions/${questionId}/answers`, 'POST', answerData);
        
        if (data.success) {
            // Reset form
            elements.answerForm.reset();
            
            // Reload question to get updated answers
            loadQuestionDetail(questionId);
            
            showNotification('Answer submitted successfully!', 'success');
        }
        
        return data;
    } catch (error) {
        showNotification(error.message || 'Failed to submit answer. Please try again.', 'error');
        throw error;
    }
}

// Rendering Functions
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
        
        // Add logout event listener
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
        
        // Add auth buttons event listeners
        document.getElementById('login-btn').addEventListener('click', showLoginForm);
        document.getElementById('register-btn').addEventListener('click', showRegisterForm);
        
        // Hide ask question button
        elements.askQuestionBtn.classList.add('hidden');
        
        // Hide answer form if on question detail page
        elements.answerFormContainer.classList.add('hidden');
    }
}

function renderQuestions(questions) {
    if (questions.length === 0) {
        elements.questionsContainer.innerHTML = `
            <div class="no-questions">
                <p>No questions found. Be the first to ask a question!</p>
            </div>
        `;
        return;
    }
    
    const questionsHTML = questions.map(question => `
        <div class="question-card" data-question-id="${question.id}">
            <h3 class="question-title">${question.title}</h3>
            <div class="question-meta">
                <span>${question.anonymous ? 'Anonymous' : question.userName}</span>
                <span>${formatDate(question.createdAt)}</span>
            </div>
            <span class="question-category">${formatCategory(question.category)}</span>
            <div class="question-stats">
                <span>${question.answerCount || 0} answers</span>
                <span>${question.voteCount || 0} votes</span>
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
    
    elements.questionsContainer.innerHTML = questionsHTML;
    
    // Add click event listeners to question cards
    document.querySelectorAll('.question-card').forEach(card => {
        card.addEventListener('click', () => {
            const questionId = card.dataset.questionId;
            loadQuestionDetail(questionId);
        });
    });
}

function renderQuestionDetail(question, answers) {
    // Render question
    elements.questionDetailContainer.innerHTML = `
        <div class="question-detail">
            <h2 class="question-detail-title">${question.title}</h2>
            <div class="question-detail-content">${question.body}</div>
            <div class="question-detail-footer">
                <span class="question-author">${question.anonymous ? 'Anonymous' : question.userName}</span>
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
                        <span class="answer-author">${answer.anonymous ? 'Anonymous' : answer.userName}</span>
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

// UI Helpers
function showSection(section) {
    // Hide all sections
    elements.authForms.classList.add('hidden');
    elements.questionList.classList.add('hidden');
    elements.questionDetail.classList.add('hidden');
    elements.askQuestion.classList.add('hidden');
    
    // Show the requested section
    section.classList.remove('hidden');
}

function showLoginForm() {
    elements.registerForm.classList.add('hidden');
    elements.loginForm.classList.remove('hidden');
    showSection(elements.authForms);
}

function showRegisterForm() {
    elements.loginForm.classList.add('hidden');
    elements.registerForm.classList.remove('hidden');
    showSection(elements.authForms);
}

function hideAuthForms() {
    showSection(elements.questionList);
}

function showNotification(message, type = 'info') {
    elements.notification.textContent = message;
    elements.notification.className = `notification ${type} visible`;
    
    // Hide notification after 3 seconds
    setTimeout(() => {
        elements.notification.classList.remove('visible');
    }, 3000);
}

// Utility Functions
function formatDate(dateString) {
    if (!dateString) return 'Unknown date';
    
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
    });
}

function formatCategory(category) {
    if (!category) return 'Uncategorized';
    
    // Convert snake_case to Title Case
    return category
        .split('_')
        .map(word => word.charAt(0).toUpperCase() + word.slice(1))
        .join(' ');
}

// Event Listeners
function setupEventListeners() {
    // Auth forms
    elements.loginFormElement.addEventListener('submit', function(e) {
        e.preventDefault();
        
        const email = document.getElementById('login-email').value;
        const password = document.getElementById('login-password').value;
        
        login(email, password).catch(error => console.error('Login error:', error));
    });
    
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
        if (!email.endsWith('.edu')) {
            showNotification('Registration requires a .edu email address', 'error');
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
    
    // Question form
    elements.questionForm.addEventListener('submit', function(e) {
        e.preventDefault();
        
        if (!state.user) {
            showNotification('You must be logged in to ask a question', 'error');
            showLoginForm();
            return;
        }
        
        const questionData = {
            title: document.getElementById('question-title').value,
            body: document.getElementById('question-body').value,
            category: document.getElementById('question-category').value,
            tags: document.getElementById('question-tags').value,
            isAnonymous: document.getElementById('question-anonymous').checked
        };
        
        submitQuestion(questionData).catch(error => console.error('Submit question error:', error));
    });
    
    // Answer form
    elements.answerForm.addEventListener('submit', function(e) {
        e.preventDefault();
        
        if (!state.user) {
            showNotification('You must be logged in to submit an answer', 'error');
            showLoginForm();
            return;
        }
        
        if (!state.currentQuestion) {
            showNotification('Question not found', 'error');
            return;
        }
        
        const answerData = {
            content: document.getElementById('answer-content').value,
            isAnonymous: document.getElementById('answer-anonymous').checked
        };
        
        submitAnswer(state.currentQuestion.id, answerData).catch(error => console.error('Submit answer error:', error));
    });
    
    // Navigation
    elements.askQuestionBtn.addEventListener('click', function(e) {
        e.preventDefault();
        
        if (!state.user) {
            showNotification('You must be logged in to ask a question', 'error');
            showLoginForm();
            return;
        }
        
        showSection(elements.askQuestion);
    });
    
    elements.backToQuestions.addEventListener('click', function() {
        state.currentQuestion = null;
        showSection(elements.questionList);
    });
    
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