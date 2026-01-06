# Multi-stage build to keep final image slim
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

# Leverage Docker layer caching for dependencies
COPY pom.xml ./
RUN mvn -B -ntp dependency:go-offline

# Copy source and build application
COPY src ./src
RUN mvn -B -ntp package -DskipTests

FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app

# Copy built Spring Boot jar from the build stage
COPY --from=build /workspace/target/*.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
