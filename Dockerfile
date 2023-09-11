# Use an official OpenJDK runtime as the base image
FROM gradle:jdk11

COPY . /app/

# Run the Gradle build inside the container
RUN ./gradlew build -x test
RUN ./gradlew run