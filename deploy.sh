#!/bin/bash
# NexusHR Phase 1 Deployment Script
# This script starts the core infrastructure and backend services required for the frontend.

echo "=========================================="
echo " Starting NexusHR Phase 1 Local Deployment"
echo "=========================================="

echo "[1/4] Starting Databases and Identity Provider (Postgres, Redis, Keycloak)..."
docker-compose up -d postgres redis keycloak

echo "[2/4] Waiting for PostgreSQL to be ready..."
# Simple wait loop for postgres (adjust timeout as needed)
sleep 10 

echo "[3/4] Building and starting API Gateway..."
# Assuming Java and Maven are installed locally for this phase
cd backend/api-gateway
mvn clean spring-boot:run &
cd ../../

echo "[4/4] Building and starting Core Services (Auth & Employee)..."
cd backend/auth-service
mvn clean spring-boot:run &
cd ../../

cd backend/employee-service
mvn clean spring-boot:run &
cd ../../

echo "=========================================="
echo " Backend Stack is starting up!"
echo " - API Gateway: http://localhost:8080"
echo " - Auth Service: http://localhost:8081"
echo " - Employee Service: http://localhost:8082"
echo " "
echo " Next step: Open a new terminal and run the frontend:"
echo "   cd frontend && npm install && npm run dev"
echo "=========================================="
