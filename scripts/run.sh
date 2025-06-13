#!/usr/bin/bash

# This script helps you start and run the OpenCourse application.
# Author: !EEExp3rt

set -e

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}🚀 Starting OpenCourse application...${NC}"

# Check dependencies before running the application.
check_dependencies() {
    echo -e "${YELLOW}🔍 Checking dependencies...${NC}"

    # Check if required commands are available.
    if ! command -v node &> /dev/null; then
        echo -e "${RED}❌ Node.js is not installed!${NC}"
        exit 1
    fi

    if ! command -v npm &> /dev/null; then
        echo -e "${RED}❌ npm is not installed!${NC}"
        exit 1
    fi

    if ! command -v mvn &> /dev/null; then
        echo -e "${RED}❌ Maven is not installed!${NC}"
        exit 1
    fi

    if ! command -v java &> /dev/null; then
        echo -e "${RED}❌ Java is not installed!${NC}"
        exit 1
    fi

    # Check if the frontend dependencies are installed.
    cd frontend

    if [ ! -d "node_modules" ]; then
        echo -e "${YELLOW}📦 Using npm to install packages...${NC}"
        npm install
    fi

    if ! npm list concurrently &> /dev/null; then
        echo -e "${YELLOW}📦 Installing concurrently...${NC}"
        npm install --save-dev concurrently
    fi

    cd ..

    echo -e "${GREEN}✅ Dependencies checked.${NC}"
}

# Check if the required ports are available.
check_ports() {
    echo -e "${YELLOW}🔍 Checking ports...${NC}"

    if lsof -ti:8080 &> /dev/null; then
        local pid=$(lsof -ti:8080)
        local process_name=$(ps -p $pid -o comm= 2>/dev/null || echo "unknown")
        echo -e "${RED}❌ Port 8080 is already in use!${NC}"
        echo -e "   Process ID: $pid"
        echo -e "   Process name: $process_name"
        echo -e "${YELLOW}💡 Please stop the process using port 8080 and try again.${NC}"
        echo -e "${YELLOW}   You can use: kill $pid${NC}"
        exit 1
    fi

    if lsof -ti:5173 &> /dev/null; then
        local pid=$(lsof -ti:5173)
        local process_name=$(ps -p $pid -o comm= 2>/dev/null || echo "unknown")
        echo -e "${RED}❌ Port 5173 is already in use!${NC}"
        echo -e "   Process ID: $pid"
        echo -e "   Process name: $process_name"
        echo -e "${YELLOW}💡 Please stop the process using port 5173 and try again.${NC}"
        echo -e "${YELLOW}   You can use: kill $pid${NC}"
        exit 1
    fi

    sleep 2
    echo -e "${GREEN}✅ All ports are available.${NC}"
}

# Start the application.
start_application() {
    echo -e "${GREEN}🎯 Starting OpenCourse application...${NC}"
    echo -e "${BLUE}Backend: http://localhost:8080${NC}"
    echo -e "${BLUE}Frontend: http://localhost:5173${NC}"
    echo -e "${YELLOW}Press Ctrl+C to stop the application${NC}"
    echo ""

    cd frontend
    npx concurrently \
        --names "backend,frontend" \
        --prefix-colors "blue,green" \
        --prefix "[{name}]" \
        --kill-others-on-fail \
        "cd ../backend && mvn spring-boot:run" \
        "npm run dev"
}

main() {
    trap cleanup_on_exit INT TERM

    check_dependencies
    check_ports
    start_application
}

# Exit cleanup function.
cleanup_on_exit() {
    echo -e "\n${YELLOW}🛑 Stoping the application...${NC}"

    # Kill all background jobs.
    jobs -p | xargs -r kill 2>/dev/null || true

    echo -e "${GREEN}✅ OpenCourse application stoped.${NC}"
    exit 0
}

main "$@"