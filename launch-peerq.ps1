# PeerQ Desktop Application Launcher
# Enhanced version with better user experience

param(
    [switch]$Debug,
    [switch]$Help
)

if ($Help) {
    Write-Host "PeerQ Desktop Application Launcher" -ForegroundColor Green
    Write-Host ""
    Write-Host "Usage:" -ForegroundColor Yellow
    Write-Host "  .\launch-peerq.ps1              # Run normally"
    Write-Host "  .\launch-peerq.ps1 -Debug       # Run with debug output"
    Write-Host "  .\launch-peerq.ps1 -Help        # Show this help"
    Write-Host ""
    Write-Host "Prerequisites:" -ForegroundColor Yellow
    Write-Host "  - Java 11 or higher"
    Write-Host "  - Maven 3.6+"
    Write-Host "  - Windows 10/11"
    exit 0
}

# Set console title
$Host.UI.RawUI.WindowTitle = "PeerQ Desktop Launcher"

# Clear screen and show banner
Clear-Host
Write-Host "╔══════════════════════════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║                    PeerQ Desktop Application                 ║" -ForegroundColor Cyan
Write-Host "║                College Community Discourse Platform          ║" -ForegroundColor Cyan
Write-Host "╚══════════════════════════════════════════════════════════════╝" -ForegroundColor Cyan
Write-Host ""

# Function to check if command exists
function Test-Command($cmdname) {
    return [bool](Get-Command -Name $cmdname -ErrorAction SilentlyContinue)
}

# Check prerequisites
Write-Host "🔍 Checking prerequisites..." -ForegroundColor Yellow

# Check Java
if (Test-Command "java") {
    try {
        $javaVersion = java -version 2>&1 | Select-String "version"
        Write-Host "✅ Java found: $javaVersion" -ForegroundColor Green
    } catch {
        Write-Host "❌ Java version check failed" -ForegroundColor Red
        exit 1
    }
} else {
    Write-Host "❌ Java not found. Please install Java 11 or higher." -ForegroundColor Red
    Write-Host "   Download from: https://adoptium.net/" -ForegroundColor Gray
    Read-Host "Press Enter to exit"
    exit 1
}

# Check Maven
if (Test-Command "mvn") {
    try {
        $mavenVersion = mvn --version | Select-String "Apache Maven"
        Write-Host "✅ Maven found: $mavenVersion" -ForegroundColor Green
    } catch {
        Write-Host "❌ Maven version check failed" -ForegroundColor Red
        exit 1
    }
} else {
    Write-Host "❌ Maven not found. Please install Maven 3.6+." -ForegroundColor Red
    Write-Host "   Download from: https://maven.apache.org/download.cgi" -ForegroundColor Gray
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host ""

# Check if we're in the right directory
if (-not (Test-Path "pom.xml")) {
    Write-Host "❌ pom.xml not found. Please run this script from the PeerQ project root." -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

# Build the project
Write-Host "🔨 Building PeerQ Desktop Application..." -ForegroundColor Yellow
Write-Host "   This may take a few moments..." -ForegroundColor Gray

$buildArgs = @("clean", "compile")
if ($Debug) {
    $buildArgs += "-X"
}

try {
    $buildResult = mvn $buildArgs 2>&1
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Build successful!" -ForegroundColor Green
    } else {
        Write-Host "❌ Build failed!" -ForegroundColor Red
        if ($Debug) {
            Write-Host $buildResult -ForegroundColor Red
        }
        Read-Host "Press Enter to exit"
        exit 1
    }
} catch {
    Write-Host "❌ Build error: $($_.Exception.Message)" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host ""

# Launch the application
Write-Host "🚀 Launching PeerQ Desktop Application..." -ForegroundColor Green
Write-Host "   The application window should appear shortly..." -ForegroundColor Gray
Write-Host ""

$runArgs = @("javafx:run")
if ($Debug) {
    $runArgs += "-Djavafx.debug=true"
}

try {
    # Run the application
    mvn $runArgs
    
    Write-Host ""
    Write-Host "👋 PeerQ Desktop Application has been closed." -ForegroundColor Green
} catch {
    Write-Host ""
    Write-Host "❌ Application error: $($_.Exception.Message)" -ForegroundColor Red
} finally {
    Write-Host ""
    Write-Host "Thank you for using PeerQ! 🎓" -ForegroundColor Cyan
    Read-Host "Press Enter to exit"
} 