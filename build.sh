#!/usr/bin/bash

# This script builds and runs the OpenCourse development container using docker.

cd .devcontainer
docker-compose down
docker-compose up --build -d
