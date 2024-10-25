FROM mck-ontada-sre.jfrog.io/openjdk-17

EXPOSE 8080
ARG JAR_FILE=build/libs/*.jar

RUN useradd -ms /bin/bash nonrootuser
USER nonrootuser
WORKDIR /home/nonrootuser

#COPY ${JAR_FILE} app.jar
COPY target/*.jar app.jar
ENV JAVA_OPTS=""
CMD java $JAVA_OPTS -jar app.jar