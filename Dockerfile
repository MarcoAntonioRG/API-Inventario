# Etapa 1: Construcción
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Ejecución
FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/inventory-service-1.0.0.jar ./inventory-service.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "inventory-service.jar"]