FROM java:8-alpine
MAINTAINER thcathy@gmail.com

ADD target/esl.jar /app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar","-Xmx2048m -XX:+UseConcMarkSweepGC"]
