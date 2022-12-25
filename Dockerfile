FROM maven:3.8.3-openjdk-16
#Make App directory
RUN mkdir app

#Add src content and pom.xml to app directory
ADD src app/src
ADD pom.xml app

#Generate executable jar in app directory
WORKDIR app
RUN mvn clean install

#Set entry point
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "target/holiday-planner-0.0.1-SNAPSHOT.jar"]