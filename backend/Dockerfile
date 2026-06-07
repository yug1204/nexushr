# Stage 1: Build Java microservices
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /app
# Copy parent pom and common-lib first for caching
COPY pom.xml .
COPY common-lib/pom.xml common-lib/
COPY auth-service/pom.xml auth-service/
COPY employee-service/pom.xml employee-service/
COPY attendance-service/pom.xml attendance-service/
COPY payroll-service/pom.xml payroll-service/
COPY performance-service/pom.xml performance-service/
COPY ai-service/pom.xml ai-service/
COPY notification-service/pom.xml notification-service/
COPY recruitment-service/pom.xml recruitment-service/
COPY api-gateway/pom.xml api-gateway/

# Download dependencies (cache layer)
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Distroless Runtime for a specific service (configurable via build arg)
FROM gcr.io/distroless/java21-debian12:nonroot
ARG SERVICE_NAME=api-gateway
WORKDIR /app

# Run as non-root user for security
USER nonroot

COPY --from=builder /app/${SERVICE_NAME}/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
