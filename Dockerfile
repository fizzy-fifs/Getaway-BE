FROM maven:3.8.7-openjdk-18-slim

#Create a directory called App
RUN mkdir app

#Add src content and pom.xml to app directory
ADD src app/src
ADD pom.xml app

#Generate executable jar in app directory
WORKDIR app
RUN mvn clean install

#Set entry point
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "target/Getaway-BE-0.0.1-SNAPSHOT.jar"]