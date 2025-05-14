#!/usr/bin/bash
# This script helps you set up the development environment for the project.
# Author: !EEExp3rt

# Set source code directory.
mkdir -p src/main/java/opencourse/{configs,controllers,models,repositories,services,utils}
mkdir -p src/main/resources
mkdir -p src/test/java/opencourse/{configs,controllers,models,repositories,services,utils}
mkdir -p src/test/resources

# Set environment variables and build the container.
if [ ! -d ".envs" ]; then
    mkdir -p .envs
    echo "# Environment variables for app container." > .envs/app.env
    echo "# Environment variables for mysql container." > .envs/mysql.env
    echo "# Environment variables for redis container." > .envs/redis.env
    echo "# Environment variables for minio container." > .envs/minio.env
    echo "Please set the environment variables in .envs/ and run the script again to build the container."
else
    echo "Start building the development container..."
    cd .devcontainer
    docker-compose down
    docker-compose up --build -d
    cd ..
    echo "Development container is built up and running."
fi
