#!/usr/bin/bash

# This script cleans up the artifacts from the frontend and backend.
# Author: !EEExp3rt

mvn clean
rm -rf dist node_modules
echo "🧹 Workspace cleaned up."