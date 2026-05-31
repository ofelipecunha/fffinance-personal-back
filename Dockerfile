# Build
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

RUN apk add --no-cache maven

# Invalida cache do Docker no Render quando migrações ou código mudam.
ARG CACHEBUST=20260529-continhas-v8
RUN echo "Build cache bust: ${CACHEBUST}"

COPY pom.xml .
COPY src ./src

RUN mvn -DskipTests package -q

# Run
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring \
    && mkdir -p /tmp/fffinance-avatars /app/data/avatars \
    && chown -R spring:spring /app /tmp/fffinance-avatars

USER spring:spring

COPY --from=build --chown=spring:spring /app/target/*.jar app.jar

ENV SPRING_PROFILES_ACTIVE=prod
ENV APP_UPLOAD_AVATARS_DIR=/tmp/fffinance-avatars
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
