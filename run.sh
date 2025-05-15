#!/bin/bash

# Clean and package the project with Maven, including all dependencies
echo "Building project with Maven..."
mvn clean package assembly:single -DskipTests

# Check if Maven build was successful
if [ $? -eq 0 ]; then
  echo "Build successful! Starting application..."
  
  # Run the application with the assembled JAR that includes all dependencies
  java -jar target/peerq-community-platform-1.0-SNAPSHOT-jar-with-dependencies.jar
else
  echo "Build failed. Please check the Maven output for errors."
fi