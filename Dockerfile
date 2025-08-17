# Build application.
FROM eclipse-temurin:24-jdk-alpine AS build
WORKDIR /src

# Copy Maven wrapper and descriptors first for better caching.
COPY mvnw pom.xml ./
COPY .mvn .mvn

# Download dependencies.
RUN ./mvnw -q -B -e -DskipTests dependency:go-offline

# Copy sources and build.
COPY src src
RUN ./mvnw -q -B -DskipTests package

# Runtime:
FROM eclipse-temurin:24-jre-alpine
RUN addgroup -S app && adduser -S app -G app
USER app
WORKDIR /app

# Copy the built jar.
COPY --from=build /src/target/*.jar /app/app.jar
COPY .env /app

# Expose the application port.
EXPOSE ${PORT}

# JVM finetuning.
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75 -XX:+UseG1GC -Dfile.encoding=UTF-8"

# Start the application.
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
