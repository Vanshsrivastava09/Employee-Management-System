#!/bin/bash

# Employee Management System - Production Deployment Script
# This script helps deploy the application to production

set -e

echo "=========================================="
echo "EMS Production Deployment Script"
echo "=========================================="

# Check if .env file exists
if [ ! -f .env ]; then
    echo "Error: .env file not found!"
    echo "Please copy .env.example to .env and configure your environment variables."
    exit 1
fi

# Load environment variables
export $(cat .env | grep -v '^#' | xargs)

# Validate required environment variables
required_vars=("MYSQL_URL" "MYSQL_USER" "MYSQL_PASSWORD" "ADMIN_USERNAME" "ADMIN_PASSWORD")
for var in "${required_vars[@]}"; do
    if [ -z "${!var}" ]; then
        echo "Error: Required environment variable $var is not set in .env"
        exit 1
    fi
done

echo "✓ Environment variables loaded"

# Build the backend
echo "Building backend..."
cd backend
./gradlew clean bootJar

if [ $? -ne 0 ]; then
    echo "Error: Backend build failed"
    exit 1
fi

echo "✓ Backend built successfully"

# Check if the JAR file was created
JAR_FILE=$(find build/libs -name "*.jar" | head -n 1)
if [ -z "$JAR_FILE" ]; then
    echo "Error: JAR file not found after build"
    exit 1
fi

echo "✓ JAR file created: $JAR_FILE"

cd ..

echo ""
echo "=========================================="
echo "Build Summary"
echo "=========================================="
echo "Backend JAR: backend/$JAR_FILE"
echo ""
echo "Next steps:"
echo "1. Upload backend/$JAR_FILE to your production server"
echo "2. Deploy the frontend/ folder to your static hosting service"
echo "3. Update frontend/app.js API_BASE to your production backend URL"
echo "4. Run the JAR with: java -jar backend/$JAR_FILE --spring.profiles.active=prod"
echo ""
echo "=========================================="
