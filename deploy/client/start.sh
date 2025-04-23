#!/bin/bash

# Navigate to client directory
cd /path/to/HackSync/client

# Install dependencies
npm install

# Build the application
npm run build

# Start the client (using serve for production)
npx serve -s build -l 3000 