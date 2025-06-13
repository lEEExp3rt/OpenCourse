#!/usr/bin/bash

# This script sets up the development container for the project.
# Author: !EEExp3rt

# Set up development container environment.
cd .devcontainer
docker-compose down
docker-compose up --build -d
