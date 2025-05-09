# Build stage
FROM gradle:8.5-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle build --no-daemon

# Run stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/build/libs/hacksync-backend-all.jar .
EXPOSE 8080
CMD ["java", "-jar", "hacksync-backend-all.jar"] 