FROM eclipse-temurin:25-jdk-noble AS builder

WORKDIR /workspace

COPY gradlew gradlew
COPY gradle gradle
COPY settings.gradle.kts build.gradle.kts gradle.properties ./
COPY server server

RUN chmod +x gradlew \
    && ./gradlew --no-daemon --configure-on-demand :server:bootJar \
    && find server/build/libs -maxdepth 1 -type f -name '*.jar' ! -name '*-plain.jar' \
       -exec cp {} /workspace/app.jar \;

FROM eclipse-temurin:25-jre-noble

RUN groupadd --system app \
    && useradd --system --gid app --home-dir /app --create-home app

WORKDIR /app
COPY --from=builder --chown=app:app /workspace/app.jar app.jar

USER app
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
