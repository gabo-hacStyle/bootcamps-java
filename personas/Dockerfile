# Etapa 1: Build con Gradle (usar JDK 17 o 20)
FROM gradle:8.7-jdk21 AS build
WORKDIR /home/gradle/app
COPY . .
RUN gradle clean build --no-daemon

# Etapa 2: Imagen final con Temurin JDK 21
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY --from=build /home/gradle/app/build/libs/capacidades-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
