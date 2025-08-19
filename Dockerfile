FROM maven:3.9.11-eclipse-temurin-21 AS build

# Set the working directory
WORKDIR /app

# Copy the pom.xml and source code into the container
COPY pom.xml /app/
RUN mvn dependency:go-offline -B

# Copy the source code into the container
COPY src /app/src

# Package the application
RUN mvn clean package -DskipTests

FROM openjdk:21-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the JAR file into the container
COPY --from=build /app/target/*.jar /app/application.jar

# Expose the port the application runs on
EXPOSE 8080

# Set the entry point for the container
ENTRYPOINT ["java", "-jar", "application.jar"]

