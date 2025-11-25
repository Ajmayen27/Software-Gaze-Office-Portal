# ============================
#   STEP 1 — Build the JAR
# ============================
FROM gradle:8.5-jdk17 AS builder

WORKDIR /app

# Copy Gradle files for caching
COPY build.gradle settings.gradle ./
COPY gradle ./gradle
COPY gradlew ./
COPY gradlew.bat ./

# Pre-download dependencies
RUN ./gradlew dependencies --no-daemon || true

# Copy source code
COPY src ./src

# Build the Spring Boot JAR
RUN ./gradlew bootJar --no-daemon

# ============================
#   STEP 2 — Run the JAR
# ============================
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copy jar from builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose port 8081
EXPOSE 8081

# Run Spring Boot on port 8081
ENTRYPOINT ["java", "-jar", "app.jar", "--server.port=8081"]
