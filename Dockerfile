# Этап 1: сборка jar
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# сначала копируем pom.xml — чтобы кешировались зависимости
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw mvnw

RUN ./mvnw -q -B -DskipTests dependency:go-offline

# теперь код
COPY src src

RUN ./mvnw -q -B clean package -DskipTests

# Этап 2: лёгкий образ только с JRE
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
