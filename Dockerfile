ARG VERSION=11
# Stage 1: Build
FROM openjdk:${VERSION}-jdk as BUILD

COPY . /src
WORKDIR /src
RUN ./gradlew --no-daemon shadowJar

# Stage 2: Run
FROM openjdk:${VERSION}-jre

# Copy the JAR file from the previous stage
COPY --from=BUILD /src/build/libs/*.jar /bin/runner/run.jar
WORKDIR /bin/runner

# Create a group and a user
RUN groupadd -r mygroup && useradd -r -u 1000 -g mygroup myuser

# Switch to the newly created user
USER myuser

# Change the ownership of the JAR file to the newly created user and group
RUN chown myuser:mygroup /bin/runner/run.jar

CMD ["java","-jar","run.jar"]