# Use OpenJDK as base image
FROM openjdk:17-jdk-slim

# Set working dir
WORKDIR /app

# Copy the jar file (build it first)
COPY target/demo-0.0.1-SNAPSHOT.jar app.jar

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
