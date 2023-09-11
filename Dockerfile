ARG VERSION=11
# Stage 1: Build
FROM openjdk:${VERSION}-jdk as BUILD

COPY . /src
WORKDIR /src
RUN ./gradlew --no-daemon shadowJar

# Stage 2: Run
FROM openjdk:${VERSION}-jre

# Create a group and a user within the container
RUN groupadd -r mygroup && useradd -r -u 1000 -g mygroup myuser

# Copy the JAR file from the previous stage
COPY --from=BUILD /src/build/libs/*.jar /bin/runner/run.jar
WORKDIR /bin/runner

# Change the ownership of the JAR file to the newly created user and group
RUN chown myuser:mygroup /bin/runner/run.jar

USER myuser

RUN java -jar /bin/runner/run.jar
