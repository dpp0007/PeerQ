# PeerQ

Welcome to **PeerQ**, a dynamic web application designed to enhance community interactions within a university environment. PeerQ offers a platform for students and faculty to engage in meaningful discussions, share knowledge, and collaborate effectively.

---

## 🌟 Features

- **🔐 User Authentication**: Secure login, registration, and logout functionalities.
- **❓ Question & Answer Forum**: Post questions, provide answers, and engage in discussions with voting capabilities.
- **🖥️ User Interface**: Intuitive and responsive UI with forms, dashboards, and interactive components.

---

## 📂 Project Structure

### **Source Code**

- **`src/main/java/com/peerq`**: Core Java source code.
  - **`web`**: Servlets for handling web requests.
    - `AuthServlet.java`: Manages user authentication.
    - `StaticFileServlet.java`: Serves static files.
    - `QuestionServlet.java`: Handles question-related operations.
  - **`util`**: Utility classes.
    - `DBConnection.java`: Manages database connections.
  - **`ui`**: User interface components.
    - `RegisterForm.java`, `LoginForm.java`, `Dashboard.java`: UI forms and dashboards.
    - **`components`**: UI components like `QuestionCard.java` and `AnswerCard.java`.
  - **`model`**: Data models.
    - `User.java`, `Question.java`, `Answer.java`, `Vote.java`: Define application data structures.
  - **`dao`**: Data Access Objects.
    - `UserDAO.java`, `QuestionDAO.java`, `AnswerDAO.java`, `VoteDAO.java`: Handle database operations.

### **Other Directories**

- **`sql`**: SQL scripts for database setup.
- **`bin`**: Compiled binaries.
- **`attached_assets`**: Additional assets for the application.

### **Key Files**

- **`pom.xml`**: Maven configuration for dependencies and build.
- **`run.sh`**: Script to run the application.
- **`postgresql-42.6.0.jar`**: PostgreSQL JDBC driver.
- **`app.log`**: Application log file.
- **`.replit`**: Replit environment configuration.

---

## 🚀 Getting Started

1. **Database Setup**: Install and configure PostgreSQL. Use the `sql` scripts for database initialization.
2. **Build the Project**: Execute `mvn clean install` to build the project.
3. **Run the Application**: Use `run.sh` to start PeerQ.

---

## 📋 Dependencies

- **Java**: Version 8 or higher
- **Maven**: Version 3.6+
- **PostgreSQL**: Version 9.6+
- **Gson**: For JSON processing

---

## 🤝 Contributing

We welcome contributions! Please fork the repository and submit a pull request for review.

---

Thank you for choosing PeerQ! We hope it serves as a valuable tool for your university community.
