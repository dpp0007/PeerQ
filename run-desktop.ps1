# PeerQ Desktop Application Launcher
Write-Host "Starting PeerQ Desktop Application..." -ForegroundColor Green
Write-Host ""

# Check if Maven is installed
try {
    $mavenVersion = mvn --version 2>$null
    if ($LASTEXITCODE -ne 0) {
        throw "Maven not found"
    }
    Write-Host "Maven found: $($mavenVersion[0])" -ForegroundColor Yellow
} catch {
    Write-Host "Error: Maven is not installed or not in PATH" -ForegroundColor Red
    Write-Host "Please install Maven and try again" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

# Check if Java is installed
try {
    $javaVersion = java -version 2>&1
    if ($LASTEXITCODE -ne 0) {
        throw "Java not found"
    }
    Write-Host "Java found: $($javaVersion[0])" -ForegroundColor Yellow
} catch {
    Write-Host "Error: Java is not installed or not in PATH" -ForegroundColor Red
    Write-Host "Please install Java 11 or higher and try again" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

# Clean and compile the project
Write-Host "Building the project..." -ForegroundColor Cyan
mvn clean compile
if ($LASTEXITCODE -ne 0) {
    Write-Host "Error: Failed to build the project" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

# Run the JavaFX application
Write-Host "Starting the application..." -ForegroundColor Cyan
mvn javafx:run

Write-Host "Application closed." -ForegroundColor Green
Read-Host "Press Enter to exit" 