# Backend - Movie Rating System

Spring Boot REST API for movie rating system.

## Tech Stack

- Java 21 (lub 17)
- Spring Boot 3.x
- Spring Security + JWT
- Spring Data JPA
- PostgreSQL / SQLite
- Prometheus metrics
- JUnit 5 + Mockito

## Local Development

### Prerequisites
- Java 21 JDK
- Maven 3.9+

### Run with SQLite (development)
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=sqlite
```

### Run with PostgreSQL
```bash
# Start PostgreSQL first
docker run -d -p 5432:5432 -e POSTGRES_PASSWORD=postgres postgres:16

mvn spring-boot:run
```

## Testing
```bash
# Run all tests
mvn test

# Run with coverage
mvn test jacoco:report

# Coverage report: target/site/jacoco/index.html
```

## API Endpoints

See [API_DOCUMENTATION.md](../API_DOCUMENTATION.md)

## Database Migration

SQLite → PostgreSQL:
1. Export data from SQLite
2. Change profile to PostgreSQL
3. Import data

## Metrics

Exposed at `/actuator/prometheus`:
- `http_requests_total`
- `auth_success_total`
- `auth_failure_total`
- `reviews_created_total`

## Environment Variables

| Variable | Default                                        | Description |
|----------|------------------------------------------------|-------------|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5453/movierating` | Database URL |
| `SPRING_DATASOURCE_USERNAME` | `postgres`                                     | DB username |
| `SPRING_DATASOURCE_PASSWORD` | `postgres`                                     | DB password |
| `JWT_SECRET` | (see application.properties)                   | JWT signing key |
| `JWT_EXPIRATION` | `86400000`                                     | Token expiration (ms) |