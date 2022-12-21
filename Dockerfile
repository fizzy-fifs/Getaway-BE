FROM openjdk:16
EXPOSE 8080
ADD target/holiday-planner-backend.jar holiday-planner-backend.jar
ENTRYPOINT ["java", "-jar", "/holiday-planner-backend.jar"]