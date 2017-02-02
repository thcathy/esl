FROM openjdk:8-jre-alpine
MAINTAINER thcathy@gmail.com

ADD target/esl.jar /app.jar
RUN sh -c 'touch /app.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005 -Xmx2048m -XX:+UseConcMarkSweepGC","-jar","/app.jar"]
