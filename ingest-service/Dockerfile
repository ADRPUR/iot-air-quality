# ------------------------------------------------------------
# Multistage Dockerfile for ingest-service (Spring Boot 3 + JDK 21)
# ------------------------------------------------------------

###############################
# 1. Build stage (Maven)
###############################
FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /app

# Copy wrapper & pom first to leverage Docker layer cache
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Download dependencies – offline cache
RUN ./mvnw -q dependency:go-offline

# Copy sources & build fat‑jar (spring‑boot:repackage)
COPY src ./src
RUN ./mvnw -q package -DskipTests spring-boot:repackage

###############################
# 2. Runtime stage (JRE only)
###############################
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy runnable Spring Boot jar from previous stage
COPY --from=build /app/target/ingest-service-*-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar"]
