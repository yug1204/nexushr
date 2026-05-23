# Multi-stage Dockerfile for NexusHR microservices
# Usage: docker build --build-arg SERVICE_NAME=auth-service -t nexushr/auth-service .

FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /build
ARG SERVICE_NAME

# Copy Maven wrapper and POM files
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
COPY common-lib/pom.xml common-lib/pom.xml
COPY ${SERVICE_NAME}/pom.xml ${SERVICE_NAME}/pom.xml

# Download dependencies (cached layer)
RUN ./mvnw dependency:go-offline -pl common-lib,${SERVICE_NAME} -am -B

# Copy source code
COPY common-lib/src/ common-lib/src/
COPY ${SERVICE_NAME}/src/ ${SERVICE_NAME}/src/

# Build the service
RUN ./mvnw package -pl common-lib,${SERVICE_NAME} -am -DskipTests -B

# ---- Runtime Stage ----
FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app

ARG SERVICE_NAME
ENV SERVICE_NAME=${SERVICE_NAME}

# Security: run as non-root
RUN addgroup -g 1001 -S nexushr && \
    adduser -u 1001 -S nexushr -G nexushr

# Copy artifact
COPY --from=builder /build/${SERVICE_NAME}/target/*.jar app.jar

# JVM tuning for containers
ENV JAVA_OPTS="-XX:+UseContainerSupport \
    -XX:MaxRAMPercentage=75.0 \
    -XX:InitialRAMPercentage=50.0 \
    -Djava.security.egd=file:/dev/./urandom \
    -Dspring.profiles.active=docker"

USER nexushr
EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
    CMD wget -qO- http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
