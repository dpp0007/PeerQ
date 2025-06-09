# PeerQ Desktop Application

A modern JavaFX desktop application for the PeerQ college community discourse platform. This desktop version provides a native application experience for students and faculty to engage in discussions, ask questions, and share knowledge.

## 🌟 Features

- **📱 Native Desktop Experience**: Built with JavaFX for smooth, responsive desktop performance
- **🔐 User Authentication**: Secure login and registration system
- **❓ Question & Answer Forum**: Post questions, provide answers, and engage in discussions
- **🎨 Modern UI**: Clean, intuitive interface with consistent styling
- **📱 Responsive Design**: Adapts to different window sizes
- **🔍 Search Functionality**: Search through questions, topics, and users
- **📂 Category Filtering**: Organize content by categories (Academics, Campus Life, Career, etc.)

## 🚀 Quick Start

### Prerequisites

- **Java 11 or higher** (OpenJDK or Oracle JDK)
- **Maven 3.6+** for building the project
- **Windows 10/11** (tested on Windows 10.0.26100)

### Installation & Running

#### Option 1: Using PowerShell (Recommended)
```powershell
# Navigate to the project directory
cd /d/PeerQ

# Run the PowerShell launcher
.\run-desktop.ps1
```

#### Option 2: Using Command Prompt
```cmd
# Navigate to the project directory
cd /d PeerQ

# Run the batch launcher
run-desktop.bat
```

#### Option 3: Manual Maven Commands
```bash
# Clean and compile
mvn clean compile

# Run the application
mvn javafx:run
```

#### Option 4: Build and Run JAR
```bash
# Build the project with dependencies
mvn clean package

# Run the JAR file
java -jar target/peerq-community-platform-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## 🏗️ Project Structure

```
src/main/java/com/peerq/desktopui/
├── PeerQMainApplication.java    # Main JavaFX application class
└── [Future UI components]

src/main/resources/
├── styles.css                   # CSS styling for the application
└── static/                      # Static assets

pom.xml                          # Maven configuration
run-desktop.ps1                  # PowerShell launcher
run-desktop.bat                  # Batch launcher
```

## 🎨 UI Components

### Main Application (`PeerQMainApplication.java`)
- **Header**: Navigation, search bar, and authentication buttons
- **Question List**: Browse and filter questions by category
- **Question Detail**: View full question with answers and reply functionality
- **Ask Question**: Form to post new questions
- **Authentication**: Login and registration forms

### Styling (`styles.css`)
- Consistent color scheme (Microsoft-inspired blue theme)
- Responsive button styles with hover effects
- Clean form styling
- Modern scrollbar design

## 🔧 Configuration

### Maven Configuration
The `pom.xml` file includes:
- JavaFX dependencies (version 21)
- Maven plugins for JavaFX and assembly
- Proper main class configuration

### Application Settings
- **Window Size**: 1200x800 pixels (minimum 800x600)
- **Theme**: Light theme with blue accent colors
- **Font**: Arial family for consistency

## 🐛 Troubleshooting

### Common Issues

#### 1. "Maven not found" Error
**Solution**: Install Maven and add it to your PATH
```powershell
# Check if Maven is installed
mvn --version
```

#### 2. "Java not found" Error
**Solution**: Install Java 11+ and add it to your PATH
```powershell
# Check Java version
java -version
```

#### 3. JavaFX Runtime Error
**Solution**: Ensure JavaFX dependencies are properly included
```bash
# Clean and rebuild
mvn clean compile
```

#### 4. Application Won't Start
**Solution**: Check the console for error messages and ensure all dependencies are resolved
```bash
# Full clean build
mvn clean install
```

### Debug Mode
To run with debug information:
```bash
mvn javafx:run -Djavafx.debug=true
```

## 🔄 Development

### Adding New Features
1. Create new UI components in `src/main/java/com/peerq/desktopui/`
2. Update the main application class to include new panels
3. Add corresponding CSS styles in `styles.css`
4. Test the application thoroughly

### Building for Distribution
```bash
# Create executable JAR with dependencies
mvn clean package

# The JAR will be in target/peerq-community-platform-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## 📋 System Requirements

- **Operating System**: Windows 10/11, macOS 10.14+, or Linux
- **Java**: OpenJDK 11+ or Oracle JDK 11+
- **Memory**: Minimum 512MB RAM (recommended 2GB+)
- **Storage**: 100MB free space
- **Display**: 800x600 minimum resolution

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## 📞 Support

If you encounter any issues:
1. Check the troubleshooting section above
2. Review the console output for error messages
3. Ensure all prerequisites are installed
4. Try running with debug mode enabled

## 📄 License

This project is part of the PeerQ community platform. See the main README.md for license information.

---

**Happy coding with PeerQ Desktop! 🎓💻** 