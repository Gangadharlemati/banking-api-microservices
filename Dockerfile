# --- Build Stage ---
# Use a Maven image to build the application's .jar file.
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -f pom.xml clean package -DskipTests

# --- Run Stage ---
# Now, use a lightweight Java image to actually run the application.
FROM openjdk:17-slim
WORKDIR /app

# Copy ONLY the built .jar file from the 'build' stage.
COPY --from=build /app/target/*.jar app.jar

# Expose the port the application will run on
EXPOSE 8081

# The command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]