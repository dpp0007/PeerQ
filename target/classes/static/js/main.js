// Search functionality
document.addEventListener('DOMContentLoaded', function() {
    const searchInput = document.getElementById('global-search');
    const filtersBtn = document.querySelector('.search-filters-btn');
    const filtersPanel = document.querySelector('.search-filters-panel');
    const sortSelect = document.getElementById('sort-filter');
    const timeSelect = document.getElementById('time-filter');
    const statusCheckboxes = document.querySelectorAll('.status-filter');

    // Toggle filters panel
    filtersBtn.addEventListener('click', function(e) {
        e.stopPropagation();
        filtersPanel.classList.toggle('hidden');
    });

    // Close filters panel when clicking outside
    document.addEventListener('click', function(e) {
        if (!filtersPanel.contains(e.target) && e.target !== filtersBtn) {
            filtersPanel.classList.add('hidden');
        }
    });

    // Handle search input
    let searchTimeout;
    searchInput.addEventListener('input', function() {
        clearTimeout(searchTimeout);
        searchTimeout = setTimeout(() => {
            performSearch();
        }, 300); // Debounce search for 300ms
    });

    // Handle filter changes
    sortSelect.addEventListener('change', performSearch);
    timeSelect.addEventListener('change', performSearch);
    statusCheckboxes.forEach(checkbox => {
        checkbox.addEventListener('change', performSearch);
    });

    function performSearch() {
        const searchTerm = searchInput.value.trim();
        const sortBy = sortSelect.value;
        const timeFilter = timeSelect.value;
        const statusFilters = Array.from(statusCheckboxes)
            .filter(cb => cb.checked)
            .map(cb => cb.value);

        // Show loading state
        const questionList = document.querySelector('.question-list');
        questionList.classList.add('loading');

        // Make API call to search endpoint
        fetch(`/api/questions/search?q=${encodeURIComponent(searchTerm)}&sort=${sortBy}&time=${timeFilter}&status=${statusFilters.join(',')}`)
            .then(response => response.json())
            .then(data => {
                updateQuestionList(data);
            })
            .catch(error => {
                console.error('Search error:', error);
                // Show error state
                questionList.innerHTML = '<div class="error-message">Failed to load search results. Please try again.</div>';
            })
            .finally(() => {
                questionList.classList.remove('loading');
            });
    }

    function updateQuestionList(questions) {
        const questionList = document.querySelector('.question-list');
        
        if (questions.length === 0) {
            questionList.innerHTML = '<div class="no-results">No questions found matching your search criteria.</div>';
            return;
        }

        questionList.innerHTML = questions.map(question => `
            <div class="question-card">
                <div class="question-stats">
                    <div class="stat">
                        <span class="stat-value">${question.votes}</span>
                        <span class="stat-label">votes</span>
                    </div>
                    <div class="stat ${question.answerCount > 0 ? 'answered' : ''}">
                        <span class="stat-value">${question.answerCount}</span>
                        <span class="stat-label">answers</span>
                    </div>
                    <div class="stat">
                        <span class="stat-value">${question.views}</span>
                        <span class="stat-label">views</span>
                    </div>
                </div>
                <div class="question-content">
                    <h3 class="question-title">
                        <a href="/questions/${question.id}">${question.title}</a>
                    </h3>
                    <div class="question-tags">
                        ${question.tags.map(tag => `<span class="tag">${tag}</span>`).join('')}
                    </div>
                    <div class="question-meta">
                        <span class="time">${formatTimeAgo(question.createdAt)}</span>
                        <span class="author">by ${question.author}</span>
                    </div>
                </div>
            </div>
        `).join('');
    }

    function formatTimeAgo(dateString) {
        const date = new Date(dateString);
        const now = new Date();
        const diffInSeconds = Math.floor((now - date) / 1000);

        if (diffInSeconds < 60) return 'just now';
        if (diffInSeconds < 3600) return `${Math.floor(diffInSeconds / 60)}m ago`;
        if (diffInSeconds < 86400) return `${Math.floor(diffInSeconds / 3600)}h ago`;
        if (diffInSeconds < 2592000) return `${Math.floor(diffInSeconds / 86400)}d ago`;
        return date.toLocaleDateString();
    }
}); 