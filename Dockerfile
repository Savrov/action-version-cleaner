# Stage 1: Build the application
FROM openjdk:11 AS builder
WORKDIR /app
COPY . /app/
RUN ./gradlew build -x test -x check

# Stage 2: Create a smaller image for running the application
FROM openjdk:11

WORKDIR /app
COPY --from=builder /app/build/libs/action-version-cleaner-SNAPSHOT.jar /app/action-version-cleaner-SNAPSHOT.jar

# Create a group
RUN groupadd -r mygroup && \
    # Create a user with the specified user ID and group ID (e.g., 1000)
    useradd -r -u 1000 -g mygroup myuser

# Change the ownership of the JAR file to the newly created user and group
RUN chown myuser:mygroup /app/action-version-cleaner-SNAPSHOT.jar

USER myuser

CMD ["java", "-jar", "/app/action-version-cleaner-SNAPSHOT.jar"]