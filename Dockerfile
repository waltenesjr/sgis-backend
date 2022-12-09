FROM adoptopenjdk:11-jre-hotspot
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} sgis-server.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/sgis-server.jar"]