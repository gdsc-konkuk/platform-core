# Start with a base image containing Java runtime
FROM openjdk:17-jdk-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the executable jar file to the working directory
COPY build/libs/platform-core-*.jar app.jar

# Expose the port the application runs on
EXPOSE 8080

# Command to run the executable jar file
ENTRYPOINT ["java", "-jar", "app.jar"]
