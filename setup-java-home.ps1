# Script to set up JAVA_HOME environment variable

# Set Java home directory to the specified JDK location
$javaHome = "C:\Program Files\Java\jdk-24"

# Check if the directory exists
if (-not (Test-Path $javaHome)) {
    Write-Host "JDK directory not found at $javaHome" -ForegroundColor Red
    exit 1
}

# Set JAVA_HOME for the current session
$env:JAVA_HOME = $javaHome
Write-Host "JAVA_HOME set to $javaHome for current session" -ForegroundColor Green

# Set JAVA_HOME permanently for the current user
[System.Environment]::SetEnvironmentVariable('JAVA_HOME', $javaHome, 'User')
Write-Host "JAVA_HOME set permanently for current user" -ForegroundColor Green

# Add the Java bin directory to the PATH if it's not already there
$javaBinPath = "$javaHome\bin"
$currentPath = [Environment]::GetEnvironmentVariable("Path", "User")

if ($currentPath -notlike "*$javaBinPath*") {
    $newPath = "$currentPath;$javaBinPath"
    [Environment]::SetEnvironmentVariable("Path", $newPath, "User")
    Write-Host "Added Java bin directory to PATH" -ForegroundColor Green
}

# Also set for the System to ensure Maven can see it
[System.Environment]::SetEnvironmentVariable('JAVA_HOME', $javaHome, 'Machine')
Write-Host "JAVA_HOME set permanently at system level" -ForegroundColor Green

# Create a batch file to run Maven with correct environment
$mvnBatchContent = @"
@echo off
set JAVA_HOME=$javaHome
set PATH=%JAVA_HOME%\bin;%PATH%
mvn %*
"@

$mvnBatchPath = "D:\PeerQ\run-maven.bat"
$mvnBatchContent | Out-File -FilePath $mvnBatchPath -Encoding ASCII
Write-Host "Created Maven wrapper batch file at $mvnBatchPath" -ForegroundColor Green

# Verify setup
Write-Host "Verifying setup:" -ForegroundColor Cyan
Write-Host "JAVA_HOME = $env:JAVA_HOME"
Write-Host "You should now be able to run Maven commands."
Write-Host "Try: mvn clean package assembly:single -DskipTests"
Write-Host "Note: You may need to restart your PowerShell window for changes to take effect."
Write-Host "`nYou have two options to run Maven:" -ForegroundColor Yellow
Write-Host "1. Restart your PowerShell/Command Prompt and run Maven normally" -ForegroundColor Yellow
Write-Host "2. Use the wrapper batch file we created:" -ForegroundColor Yellow
Write-Host "   .\run-maven.bat clean package assembly:single -DskipTests" -ForegroundColor Cyan
Write-Host "`nThe wrapper batch file ensures JAVA_HOME is set correctly for that specific command." -ForegroundColor Yellow

# After the existing code, add these lines to run the desktop app
Write-Host "`nAttempting to run the desktop application..." -ForegroundColor Yellow

# Set the proper environment variables
$env:JAVA_HOME = $javaHome
$env:Path = "$javaHome\bin;" + $env:Path

# Find and run the JAR file
$jarFiles = Get-ChildItem -Path "D:\PeerQ\target" -Filter "*-jar-with-dependencies.jar" -ErrorAction SilentlyContinue
if ($jarFiles -and $jarFiles.Count -gt 0) {
    $jarPath = $jarFiles[0].FullName
    Write-Host "Found JAR file: $jarPath" -ForegroundColor Green
    Write-Host "Running application..." -ForegroundColor Green
    java -jar $jarPath
}
else {
    Write-Host "JAR file not found. Attempting to build the project first..." -ForegroundColor Yellow
    
    # Run Maven to build the project - Simplified command
    $mvnCommand = "mvn"
    $mvnArgs = "clean package assembly:single -DskipTests"
    
    Write-Host "Running: $mvnCommand $mvnArgs" -ForegroundColor Cyan
    & $mvnCommand $mvnArgs
    
    # Check if build was successful and try to run the app again
    if ($LASTEXITCODE -eq 0) {
        $jarFiles = Get-ChildItem -Path "D:\PeerQ\target" -Filter "*-jar-with-dependencies.jar" -ErrorAction SilentlyContinue
        if ($jarFiles -and $jarFiles.Count -gt 0) {
            $jarPath = $jarFiles[0].FullName
            Write-Host "Found JAR file: $jarPath" -ForegroundColor Green
            Write-Host "Running application..." -ForegroundColor Green
            
            try {
                # First try running as executable JAR
                java -jar $jarPath
            }
            catch {
                Write-Host "Standard JAR execution failed. Trying with main class..." -ForegroundColor Yellow
                # Try with the explicit main class
                java -cp $jarPath com.peerq.DesktopMain
            }
        }
        else {
            Write-Host "Could not find the JAR file after building. Check your project configuration." -ForegroundColor Red
        }
    }
    else {
        Write-Host "Build failed. Please check your Maven configuration and try again." -ForegroundColor Red
    }
}
