# Use an official OpenJDK runtime as the base image
FROM openjdk:21-jdk

# Set the working directory in the container
WORKDIR /app

# Copy the built JAR from the Gradle build folder into the container
COPY app/build/libs/app.jar app.jar

# Expose port 8080 for the Spring Boot application
EXPOSE 8080

# Run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]
