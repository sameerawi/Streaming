# Use an official Maven image as a parent image
FROM maven:3.8.4-openjdk-11-slim

# Set the working directory to /app
WORKDIR /kafka-event-producer

# Copy the POM file first for efficient dependency caching
COPY pom.xml .

# Copy the rest of the application
COPY . .

# Build the application using Maven
RUN mvn clean install

# Expose any ports your application needs (if applicable)
EXPOSE 8080

# Specify the command to run on container start
CMD ["java", "-jar", "target/kafka-event-producer-1.0-SNAPSHOT-jar-with-dependencies.jar"]