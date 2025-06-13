#!/usr/bin/bash

# This script cleans up the artifacts from the frontend and backend.
# Author: !EEExp3rt

cd frontend
rm -rf dist node_modules
cd ../backend
mvn clean
cd ..
echo "🧹 Workspace cleaned up."