# Ad Performance Aggregator - Multi-stage build for the whole project
# Stage 1: Build
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

COPY gradlew gradlew.bat ./
COPY gradle/ gradle/
COPY settings.gradle.kts ./
COPY build.gradle.kts ./
COPY src/ src/

RUN ./gradlew shadowJar --no-daemon

# Stage 2: Run
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN adduser -D -u 1000 appuser
USER appuser

COPY --from=build /app/build/libs/aggregator-1.0.0.jar /app/aggregator.jar

# Mount points: /data for input CSV, /output for results (top10_*.csv, benchmark.log)
VOLUME /data
VOLUME /output

ENTRYPOINT ["java", "-jar", "/app/aggregator.jar"]
CMD ["--input", "/data/ad_data.csv", "--output", "/output"]
