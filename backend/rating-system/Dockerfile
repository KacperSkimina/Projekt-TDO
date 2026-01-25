# ETAP 1: Budowanie (używamy obrazu z Javą 25)
FROM maven:3.9.9-eclipse-temurin-25-alpine AS build
COPY . .
RUN mvn clean package -DskipTests

# ETAP 2: Uruchamianie (lekki obraz JRE)
FROM eclipse-temurin:25-jre-alpine
COPY --from=build /target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]