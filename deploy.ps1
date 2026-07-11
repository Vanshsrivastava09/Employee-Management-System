# Employee Management System - Production Deployment Script (Windows)
# This script helps deploy the application to production

Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "EMS Production Deployment Script" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan

# Check if .env file exists
if (-not (Test-Path .env)) {
    Write-Host "Error: .env file not found!" -ForegroundColor Red
    Write-Host "Please copy .env.example to .env and configure your environment variables."
    exit 1
}

# Load environment variables from .env file
Get-Content .env | ForEach-Object {
    if ($_ -match '^([^=]+)=(.*)$') {
        $name = $matches[1].Trim()
        $value = $matches[2].Trim()
        [Environment]::SetEnvironmentVariable($name, $value, "Process")
    }
}

# Validate required environment variables
$requiredVars = @("MYSQL_URL", "MYSQL_USER", "MYSQL_PASSWORD", "ADMIN_USERNAME", "ADMIN_PASSWORD")
foreach ($var in $requiredVars) {
    $value = [Environment]::GetEnvironmentVariable($var, "Process")
    if ([string]::IsNullOrEmpty($value)) {
        Write-Host "Error: Required environment variable $var is not set in .env" -ForegroundColor Red
        exit 1
    }
}

Write-Host "✓ Environment variables loaded" -ForegroundColor Green

# Build the backend
Write-Host "Building backend..." -ForegroundColor Yellow
Set-Location backend
& .\gradlew.bat clean bootJar

if ($LASTEXITCODE -ne 0) {
    Write-Host "Error: Backend build failed" -ForegroundColor Red
    Set-Location ..
    exit 1
}

Write-Host "✓ Backend built successfully" -ForegroundColor Green

# Check if the JAR file was created
$jarFile = Get-ChildItem -Path build\libs -Filter *.jar | Select-Object -First 1
if (-not $jarFile) {
    Write-Host "Error: JAR file not found after build" -ForegroundColor Red
    Set-Location ..
    exit 1
}

Write-Host "✓ JAR file created: $($jarFile.Name)" -ForegroundColor Green

Set-Location ..

Write-Host ""
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "Build Summary" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "Backend JAR: backend\build\libs\$($jarFile.Name)"
Write-Host ""
Write-Host "Next steps:"
Write-Host "1. Upload backend\build\libs\$($jarFile.Name) to your production server"
Write-Host "2. Deploy the frontend\ folder to your static hosting service"
Write-Host "3. Update frontend\app.js API_BASE to your production backend URL"
Write-Host "4. Run the JAR with: java -jar backend\build\libs\$($jarFile.Name) --spring.profiles.active=prod"
Write-Host ""
Write-Host "==========================================" -ForegroundColor Cyan
