@echo off
echo Starting PeerQ Desktop Application...
echo.

REM Check if Maven is installed
mvn --version >nul 2>&1
if errorlevel 1 (
    echo Error: Maven is not installed or not in PATH
    echo Please install Maven and try again
    pause
    exit /b 1
)

REM Clean and compile the project
echo Building the project...
mvn clean compile

REM Run the JavaFX application
echo Starting the application...
mvn javafx:run

pause 