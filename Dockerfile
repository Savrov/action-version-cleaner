# Use an official OpenJDK runtime as the base image
FROM openjdk:11

# Set the working directory in the container
WORKDIR /app

COPY gradlew /app/
COPY build.gradle.kts settings.gradle.kts /app/
COPY src /app/src

# Run the Gradle build inside the container
RUN ./gradlew build

# Specify the default command to run when the container starts
CMD ["./gradlew", "run"]