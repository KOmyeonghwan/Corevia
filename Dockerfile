# Step 1: Use OpenJDK 17 base image
FROM eclipse-temurin:17-jdk as build

# Step 2: Set working directory
WORKDIR /app

# Step 3: Copy the Spring Boot jar file into the container
COPY build/libs/corenet-0.0.1-SNAPSHOT.jar app.jar

# Step 4: Expose the port the app will run on
EXPOSE 8080
# Step 5: Run the Spring Boot application
CMD ["java", "-jar", "app.jar"]

