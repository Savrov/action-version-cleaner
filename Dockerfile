# Stage 1: Build the application
FROM openjdk:11 AS builder
WORKDIR /app
COPY . /app/
RUN ./gradlew build -x test -x check

# Stage 2: Create a smaller image for running the application
FROM openjdk:11
WORKDIR /app
COPY --from=builder /app/build/libs/action-version-cleaner-SNAPSHOT.jar /app/action-version-cleaner-SNAPSHOT.jar
CMD ["java", "-jar", "action-version-cleaner-SNAPSHOT.jar"]