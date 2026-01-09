# Step 1: Use OpenJDK 17 base image
FROM openjdk:17-jdk-slim as build

# Step 2: Set working directory
WORKDIR /app

# Step 3: Copy the Spring Boot jar file into the container
COPY target/myapp.jar /app/myapp.jar

# Step 4: Expose the port the app will run on
EXPOSE 8080

# Step 5: Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "/app/myapp.jar"]
