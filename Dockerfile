FROM openjdk:16
EXPOSE 8080
ADD jar/holiday-planner-0.0.1-SNAPSHOT.jar holiday-planner-backend.jar
ENTRYPOINT ["java", "-jar", "/holiday-planner-backend.jar"]