#ARG VERSION=11
## Stage 1: Build
#FROM openjdk:${VERSION}-jdk as BUILD
#
#COPY . /src
#WORKDIR /src
#RUN ./gradlew --no-daemon shadowJar
#
## Stage 2: Run
#FROM openjdk:${VERSION}-jre
#
## Create a group and a user within the container
#RUN groupadd -r mygroup && useradd -r -u 1000 -g mygroup myuser
#
## Copy the JAR file from the previous stage
#COPY --from=BUILD /src/build/libs/*.jar /bin/runner/run.jar
#WORKDIR /bin/runner
#
## Change the ownership of the JAR file to the newly created user and group
#RUN chown myuser:mygroup /bin/runner/run.jar
#
#USER myuser
#
#RUN echo 1=$0
#RUN echo 2=$1
#RUN echo 3=$2
#RUN echo 4=$3
#
#ENV GITHUB_REPOSITORY=${GITHUB_REPOSITORY}
#ENV PACKAGE_TYPE=${PACKAGE_TYPE}
#ENV VERSION_TAG=${VERSION_TAG}
#ENV GITHUB_TOKEN=${GITHUB_TOKEN}
#
#RUN echo GITHUB_REPOSITORY=${GITHUB_REPOSITORY}
#RUN echo PACKAGE_TYPE=${PACKAGE_TYPE}
#RUN echo VERSION_TAG=${VERSION_TAG}
#RUN echo GITHUB_TOKEN=${GITHUB_TOKEN}
#
#RUN java -jar /bin/runner/run.jar

FROM alpine:latest
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh
ENTRYPOINT ["/entrypoint.sh"]
