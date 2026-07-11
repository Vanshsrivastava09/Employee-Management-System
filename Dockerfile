# Employee Management System - Dockerfile for Production
# Multi-stage build for optimized image size

# Stage 1: Build
FROM gradle:8.5-jdk21 AS builder
WORKDIR /app
COPY backend/build.gradle backend/settings.gradle ./
COPY backend/gradlew gradlew.bat gradle/ ./
COPY backend/src ./src
RUN chmod +x gradlew && ./gradlew bootJar --no-daemon

# Stage 2: Run
FROM openjdk:21-slim
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
COPY uploads/ ./uploads/
EXPOSE 8080
ENV JAVA_OPTS="-Xmx512m -Xms256m"
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
