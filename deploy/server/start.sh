#!/bin/bash

# Set environment variables
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk
export PATH=$JAVA_HOME/bin:$PATH

# Navigate to server directory
cd /../..server/general

# Build the application
./gradlew build

# Start the server
java -jar build/libs/hacksync-backend-0.0.1.jar