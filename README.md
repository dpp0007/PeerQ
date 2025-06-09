# PeerQ - College Community Discourse Platform

A modern JavaFX desktop application that provides a platform for college students to ask questions, share knowledge, and engage in academic discussions. Built with JavaFX and PostgreSQL, PeerQ offers a clean, intuitive interface for community-driven learning.

![PeerQ Logo](src/main/resources/static/images/logo.svg)

## 🚀 Features

### Core Functionality
- **Question & Answer System**: Post questions and receive answers from the community
- **User Authentication**: Secure login and registration with Galgotias University email validation
- **Search & Filter**: Find questions by keywords, categories, or users
- **Category Organization**: Questions organized into academic categories (Academics, Campus Life, Career, Technology, etc.)
- **Real-time Updates**: Questions and answers sync with the shared database

### User Experience
- **Dark Theme**: Modern dark UI with green accents for reduced eye strain
- **Responsive Design**: Adapts to different screen sizes and resolutions
- **Keyboard Shortcuts**: Quick navigation with F1-F4 keys and Escape
- **Character Limits**: Visual feedback for input length with color-coded warnings
- **Form Validation**: Comprehensive validation with helpful error messages
- **Password Visibility Toggle**: Eye icon to show/hide password while typing
- **Search Icon**: Visual indicator for search functionality

### Technical Features
- **Database Integration**: PostgreSQL backend with connection pooling
- **Cross-platform**: Runs on Windows, macOS, and Linux
- **Modern UI**: JavaFX-based interface with custom CSS styling
- **Error Handling**: Robust error handling and user feedback

## 📋 Prerequisites

Before running PeerQ, ensure you have the following installed:

- **Java 17 or higher** (OpenJDK or Oracle JDK)
- **Maven 3.6+** for building and dependency management
- **PostgreSQL 12+** database server
- **Git** for cloning the repository

## 🛠️ Installation & Setup

### 1. Clone the Repository
```bash
git clone https://github.com/your-username/peerq-community-platform.git
cd peerq-community-platform
```

### 2. Database Setup

#### Install PostgreSQL
- **Windows**: Download from [PostgreSQL Official Site](https://www.postgresql.org/download/windows/)
- **macOS**: `brew install postgresql`
- **Linux**: `sudo apt-get install postgresql postgresql-contrib`

#### Create Database
```sql
-- Connect to PostgreSQL as superuser
psql -U postgres

-- Create database and user
CREATE DATABASE peerq_db;
CREATE USER peerq_user WITH PASSWORD 'your_secure_password';
GRANT ALL PRIVILEGES ON DATABASE peerq_db TO peerq_user;
\q
```

#### Run Database Schema
```bash
# Navigate to the sql directory
cd sql

# Run the schema file
psql -U peerq_user -d peerq_db -f schema.sql
```

### 3. Configure Database Connection

Edit the database configuration in `src/main/java/com/peerq/util/DBConnection.java`:

```java
// Update these values to match your PostgreSQL setup
private static final String URL = "jdbc:postgresql://localhost:5432/peerq_db";
private static final String USER = "peerq_user";
private static final String PASSWORD = "your_secure_password";
```

### 4. Build the Application
```bash
# Clean and compile
mvn clean compile

# Run the application
mvn javafx:run
```

## 🎮 How to Use

### Getting Started
1. **Launch the Application**: Run `mvn javafx:run` from the project root
2. **Register an Account**: Use your Galgotias University email address
3. **Browse Questions**: View existing questions on the home screen
4. **Ask Questions**: Click "Ask Question" to post new questions
5. **Answer Questions**: Contribute by answering other students' questions

### Keyboard Shortcuts
- **F1**: Go to Home/Question List
- **F2**: Ask Question (if logged in)
- **F3**: Focus Search Bar
- **F4**: Login/Logout
- **Escape**: Go back to Question List

### Search & Filter
- Use the search bar to find questions by title, content, or author
- Filter questions by category using the dropdown menu
- Combine search terms with category filters for precise results

## 📱 Screenshots

### Dashboard
![Dashboard](docs/screenshots/dashboard.png)
*Main question list with search and filter functionality*

### Login Screen
![Login](docs/screenshots/login.png)
*Secure login with Galgotias University email validation*

### Ask Question
![Ask Question](docs/screenshots/ask-question.png)
*Form for posting new questions with character limits*

### Question Detail
![Question Detail](docs/screenshots/question-detail.png)
*Detailed view of questions with answers and reply functionality*

## 🏗️ Project Structure

```
peerq-community-platform/
├── src/main/java/com/peerq/
│   ├── desktopui/
│   │   └── PeerQMainApplication.java    # Main JavaFX application
│   ├── web/                             # Web application components
│   │   ├── AuthServlet.java
│   │   ├── QuestionServlet.java
│   │   └── StaticFileServlet.java
│   ├── model/                           # Data models
│   │   ├── User.java
│   │   ├── Question.java
│   │   ├── Answer.java
│   │   └── Vote.java
│   ├── dao/                             # Data Access Objects
│   │   ├── UserDAO.java
│   │   ├── QuestionDAO.java
│   │   ├── AnswerDAO.java
│   │   └── VoteDAO.java
│   └── util/
│       └── DBConnection.java            # Database connection management
├── src/main/resources/
│   ├── styles.css                       # JavaFX styling
│   └── static/                          # Web application resources
│       ├── index.html
│       ├── css/
│       ├── js/
│       └── images/
├── sql/
│   └── schema.sql                       # Database schema
├── pom.xml                              # Maven configuration
└── README.md
```

## 🔧 Configuration

### Application Settings
- **Window Size**: Default 1200x800, minimum 800x600
- **Database Pool**: Configurable connection pool size
- **Theme**: Dark theme with customizable accent colors

### Development Configuration
- **Java Version**: 17+
- **JavaFX Version**: 21
- **Maven Version**: 3.6+

## 🧪 Testing

### Manual Testing
1. **User Registration**: Test with valid/invalid email addresses
2. **Login Functionality**: Verify authentication and session management
3. **Question Posting**: Test form validation and database persistence
4. **Search & Filter**: Verify search accuracy and filter functionality
5. **Answer Submission**: Test answer posting and display

### Database Testing
```bash
# Test database connection
mvn test -Dtest=DBConnectionTest

# Run integration tests
mvn verify
```

## 🤝 Contributing

We welcome contributions to PeerQ! Here's how you can help:

### Development Setup
1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Make your changes and test thoroughly
4. Commit your changes: `git commit -m 'Add amazing feature'`
5. Push to the branch: `git push origin feature/amazing-feature`
6. Open a Pull Request

### Code Style Guidelines
- Follow Java naming conventions
- Use meaningful variable and method names
- Add comments for complex logic
- Ensure proper error handling
- Test your changes thoroughly

### Areas for Contribution
- **UI/UX Improvements**: Better accessibility, responsive design
- **Performance Optimization**: Database queries, UI rendering
- **New Features**: Advanced search, notifications, file uploads
- **Bug Fixes**: Report and fix issues
- **Documentation**: Improve code comments and user guides

## 🐛 Troubleshooting

### Common Issues

#### Database Connection Failed
```
Error: Connection refused
```
**Solution**: Ensure PostgreSQL is running and connection details are correct

#### JavaFX Runtime Error
```
Error: JavaFX runtime components are missing
```
**Solution**: Use `mvn javafx:run` instead of running the JAR directly

#### Maven Build Fails
```
Error: Compilation failed
```
**Solution**: Ensure Java 17+ is installed and JAVA_HOME is set correctly

#### Application Won't Start
```
Error: Failed to start PeerQ
```
**Solution**: Check database connection and ensure all dependencies are resolved

### Getting Help
- **Issues**: Create an issue on GitHub with detailed error information
- **Discussions**: Use GitHub Discussions for questions and ideas
- **Documentation**: Check this README and inline code comments

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **JavaFX Team**: For the excellent UI framework
- **PostgreSQL Community**: For the robust database system
- **Galgotias University**: For supporting student innovation
- **Contributors**: All those who have contributed to this project

## 📞 Contact

- **Project Link**: [https://github.com/your-username/peerq-community-platform](https://github.com/your-username/peerq-community-platform)
- **Issues**: [GitHub Issues](https://github.com/your-username/peerq-community-platform/issues)
- **Email**: peerq@galgotiasuniversity.ac.in

---

**Made with ❤️ by the PeerQ Team**
