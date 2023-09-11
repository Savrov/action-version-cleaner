# Stage 1: Build the application
FROM openjdk:11 AS builder
WORKDIR /app
COPY . /app/
RUN ./gradlew clean build -x test -x check
RUN ./gradlew run