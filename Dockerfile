FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app

# Copy project files needed by Maven build plugins
COPY pom.xml .
COPY sonar-project.properties .
COPY src ./src

# Build Spring Boot jar, skip tests/frontend and non-essential quality plugins in container build
RUN mvn -DskipTests -Dskip.webapp -Dcheckstyle.skip=true -Dspotless.skip=true -Djacoco.skip=true package

FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy built jar from builder image
COPY --from=builder /app/target/*.jar app.jar

# Railway injects PORT dynamically; default to 8080 for local docker run
ENV SPRING_PROFILES_ACTIVE=prod \
    PORT=8080

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java -Xms256m -Xmx512m -Dserver.port=${PORT} -jar /app/app.jar"]

