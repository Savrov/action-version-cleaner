# Use an official Gradle image with Kotlin DSL support as the base image
FROM gradle:7.2.0-jdk11 AS builder

# Set the working directory in the container
WORKDIR /app

# Copy only the necessary files for dependency resolution
COPY build.gradle.kts settings.gradle.kts /app/
COPY src /app/src/

# Resolve dependencies without running tests
RUN gradle resolveDependencies --no-daemon

# Copy the rest of the application files
COPY . /app/

# Build the application
RUN gradle build -x test --no-daemon

# Create the final runtime image
FROM openjdk:11-jre-slim

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file built in the previous stage
COPY --from=builder /app/build/libs/your-app.jar /app/your-app.jar

# Run the application
CMD ["java", "-jar", "your-app.jar"]