FROM openjdk:17-jdk-alpine
COPY target/toucher-1.0-SNAPSHOT.jar toucher-1.0-SNAPSHOT.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "/toucher-1.0-SNAPSHOT.jar"]
