# ETAP 1: Budowanie (Build Stage)
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# 1. Kopiujemy TYLKO plik konfiguracyjny
COPY backend/rating-system/pom.xml .

# 2. Pobieramy zależności (to się zcache'uje, jeśli nie zmienisz pom.xml)
RUN mvn dependency:go-offline

# 3. Dopiero teraz kopiujemy kod źródłowy
COPY backend/rating-system/src ./src

# 4. Budujemy aplikację (bez testów i checkstyle, bo to robi CI na GitHubie)
RUN mvn clean package -DskipTests -Dcheckstyle.skip

# ETAP 2: Uruchamianie (Run Stage)
# Używamy wersji Alpine (bardzo lekkiej) - Wymóg optymalizacji
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Kopiujemy tylko wynikowy plik .jar z poprzedniego etapu
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
