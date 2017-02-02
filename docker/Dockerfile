FROM openjdk:8-jre-alpine
MAINTAINER thcathy@gmail.com

ADD target/esl.jar /app.jar
RUN sh -c 'touch /app.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar","-Xmx2048m -XX:+UseConcMarkSweepGC"]
