#!/bin/bash

# Set environment variables
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH

# Navigate to server directory
cd /home/dev/git/HackSync/server/general

# Build the application
./gradlew build

# Start the server
java -jar build/libs/hacksync-backend-all.jar