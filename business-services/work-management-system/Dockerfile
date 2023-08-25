# Fetching latest version of Java
FROM openjdk:8-jdk-alpine
 
# Setting up work directory
WORKDIR /app

# Copy the jar file into our app
COPY ./target/work-management-system-0.0.1-SNAPSHOT.jar /app

# Exposing port 8484
EXPOSE 8484

# Starting the application
CMD ["java", "-jar", "work-management-system-0.0.1-SNAPSHOT.jar"]