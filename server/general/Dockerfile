# Build stage
FROM gradle:8.5-jdk21 AS build
WORKDIR /app

# Copy gradle files first for better caching
COPY gradle ./gradle
COPY gradlew .
COPY build.gradle.kts .
COPY settings.gradle.kts .

# Make gradlew executable
RUN chmod +x ./gradlew

# Add Kotlin plugin repository
RUN echo 'pluginManagement { repositories { gradlePluginPortal(); maven("https://plugins.gradle.org/m2/"); maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven") } }' >> settings.gradle.kts

# Download dependencies
RUN ./gradlew dependencies

# Copy source code
COPY src ./src

# Build the application
RUN ./gradlew build --no-daemon

# Run stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/build/libs/hacksync-backend-all.jar .
EXPOSE 8080
CMD ["java", "-jar", "hacksync-backend-all.jar"] 