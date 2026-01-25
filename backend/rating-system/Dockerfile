# ETAP 1: Budowanie
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
# Dodajemy flagę -Dcheckstyle.skip, aby zignorować błędy stylu
RUN mvn clean package -DskipTests -Dcheckstyle.skip

# ETAP 2: Uruchamianie
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]