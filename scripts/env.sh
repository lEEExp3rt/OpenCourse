#!/usr/bin/bash

# This script sets up the environment variables for the project.
# Author: !EEExp3rt

if [ ! -d ".envs" ]; then
    mkdir -p .envs
    echo "# Environment variables for app container." > .envs/app.env
    echo "# Environment variables for mysql container." > .envs/mysql.env
    echo "# Environment variables for redis container." > .envs/redis.env
    echo "# Environment variables for minio container." > .envs/minio.env
    echo "Please set the environment variables in .envs/ and run the script again to build the container."
fi
