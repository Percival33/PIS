FROM gradle:jdk17-focal
COPY build/libs/service-discovery-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]
