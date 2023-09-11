# Use an official OpenJDK runtime as the base image
FROM openjdk:11

# Set the working directory in the container
WORKDIR /app

COPY . /app/

# Run the Gradle build inside the container
RUN ./gradlew build -x test -x check
RUN ./gradlew run
