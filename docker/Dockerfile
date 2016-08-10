FROM tomcat:8.5-alpine
MAINTAINER thcathy@gmail.com

RUN rm -rf $CATALINA_HOME/webapps/* && \
  mkdir $CATALINA_HOME/webapps/ROOT

ADD target/ESL.war $CATALINA_HOME/webapps/
ADD docker/index.html $CATALINA_HOME/webapps/ROOT

CMD ["catalina.sh", "run"]