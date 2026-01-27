# Movie Rating System

A full-stack web application for rating and reviewing movies, built with Spring Boot, React, and PostgreSQL.

## Team Members
- Kacper Skimina
- Mateusz Stojek

## Technology Stack

### Backend
- **Framework**: Spring Boot 4.0.1
- **Language**: Java 25
- **Security**: JWT Authentication
- **Database**: PostgreSQL / SQLite
- **Monitoring**: Prometheus + Actuator
- **Testing**: JUnit 5, Mockito

### Frontend
- **Framework**: React 19
- **HTTP Client**: Axios
- **Styling**: Custom CSS

### Infrastructure
- **Containerization**: Docker, Docker Compose
- **CI/CD**: GitHub Actions
- **Hosting**: Railway
- **Monitoring**: Prometheus, Grafana

## Features

### Core Functionality
- User authentication (register/login) with JWT
- Browse movies (public access)
- Add/edit/delete movies (authenticated users only)
- Add/edit/delete reviews (authenticated users only)
- Average rating calculation
- Search movies by title

### Security
- JWT token-based authentication
- Password encryption with BCrypt
- Role-based access control (USER/ADMIN)
- CORS configuration for frontend integration
- Users can only edit/delete their own reviews

### Monitoring
- Prometheus metrics exposure
- Custom metrics:
  - `http_requests_total` - Total HTTP requests
  - `auth_success_total` - Successful authentications
  - `auth_failure_total` - Failed authentications
  - `reviews_created_total` - Total reviews created
- Grafana dashboards for visualization
- Alert rules for high traffic and system downtime

## Project Structure
```
.
├── backend/
│   └── rating-system/          # Spring Boot application
│       ├── src/
│       │   ├── main/
│       │   │   ├── java/       # Application code
│       │   │   └── resources/  # Configuration files
│       │   └── test/           # Unit and integration tests
│       ├── Dockerfile          # Backend Docker image
│       └── pom.xml             # Maven dependencies
│
├── movie-front/                # React frontend
│   ├── src/
│   │   ├── App.js              # Main application component
│   │   └── App.css             # Styles
│   ├── Dockerfile              # Frontend Docker image
│   └── package.json            # NPM dependencies
│
├── monitoring/
│   ├── prometheus.yml          # Prometheus configuration
│   └── alert_rules.yml         # Alert definitions
│
├── .github/
│   └── workflows/
│       ├── ci.yml              # Continuous Integration
│       └── cd.yml              # Continuous Deployment
│
├── docker-compose.yml          # Multi-container orchestration
└── README.md                   # This file
```

## Team Contributions

### Mateusz Stojek - Backend & Database & Testing

#### Backend Development
- Implemented REST API with Spring Boot
  - Authentication endpoints (`/api/auth/register`, `/api/auth/login`)
  - Movie CRUD endpoints (`/api/movies`)
  - Review CRUD endpoints (`/api/reviews`)
- Developed business logic services
  - `AuthService` - User registration and login
  - `MovieService` - Movie management and search
  - `ReviewService` - Review management with ownership validation
  - `RatingService` - Average rating calculation
- Configured security
  - JWT token generation and validation (`JwtUtil`)
  - Spring Security configuration (`SecurityConfig`)
  - JWT authentication filter (`JwtAuthenticationFilter`)
  - Custom user details service

#### Database
- Designed database schema (Users, Movies, Reviews)
- Implemented JPA entities and repositories
- Configured SQLite profile for development
- Configured PostgreSQL profile for production
- Set up database migrations strategy
- Configured database volumes in Docker Compose

#### Testing
- Wrote comprehensive unit tests (70+ test cases)
  - Controller tests (AuthController, MovieController, ReviewController)
  - Service tests (AuthService, MovieService, ReviewService, RatingService)
  - Security tests (JwtUtil, CustomUserDetailsService)
  - Integration tests
- Configured JaCoCo for test coverage reporting
- Achieved high test coverage across all components

#### Monitoring Backend
- Implemented Prometheus metrics
  - HTTP request counter with interceptor
  - Authentication success/failure counters
  - Review creation counter
- Configured Spring Boot Actuator
- Set up custom metrics with Micrometer
- Created alert rules for Prometheus

### Kacper Skimina - Docker & CI/CD & Monitoring & Frontend

#### Docker & Containerization
- Created optimized Dockerfiles
  - Multi-stage build for backend (Maven + JRE Alpine)
  - Multi-stage build for frontend (Node + Nginx Alpine)
  - Implemented Docker layer caching
- Wrote docker-compose.yml orchestration
  - Backend service configuration
  - Frontend service configuration
  - PostgreSQL database service
  - Prometheus monitoring service
  - Grafana visualization service
  - Health checks and dependencies
  - Volume management
- Created .dockerignore files for optimization

#### CI/CD Pipeline
- Implemented GitHub Actions workflows
  - CI workflow (ci.yml)
    - Build verification on push and PR
    - Maven build with dependency caching
    - Checkstyle linting integration
    - Test execution (with skip flag for infrastructure focus)
  - CD workflow (cd.yml)
    - Automated Docker image building
    - Push to GitHub Container Registry
    - Git tag-based versioning (v1.0.0, v1.0.1, etc.)
    - Image tagging strategy

#### Monitoring & Observability
- Set up Prometheus monitoring
  - Configured scrape targets
  - Created prometheus.yml configuration
  - Set up alert rules (prometheus-alerts.yml)
    - High request rate alerts
    - Application downtime alerts
    - Authentication failure alerts
    - Low activity alerts
- Configured Grafana
  - Data source integration with Prometheus
  - Dashboard setup for metrics visualization

#### Frontend Development
- Built React application
  - User interface for movie browsing
  - Authentication forms (login/register)
  - Movie management interface
  - Review creation and display
- Implemented API integration
  - Axios HTTP client setup
  - JWT token management in localStorage
  - Error handling and user feedback
- Styled application with custom CSS
  - Responsive design
  - Movie card layouts
  - Form styling
  - Review display formatting

#### Deployment
- Deployed application to Railway
  - Backend service deployment
  - Frontend service deployment
  - Environment variable configuration
  - Database service setup
- Configured production environment
  - CORS settings for Railway domains
  - Port configuration for Railway
  - Health check endpoints
- Troubleshooting and debugging
  - Fixed CORS issues
  - Resolved port binding problems
  - Configured proper networking between services

## Getting Started

### Prerequisites
- Docker and Docker Compose
- Java 21 JDK (for local development)
- Node.js 18+ (for local development)
- Maven 3.9+ (for local development)

### Running with Docker Compose
```bash
# Clone the repository
git clone <repository-url>
cd Projekt-TDO

# Start all services
docker-compose up -d

# Services will be available at:
# - Backend API: http://localhost:8080
# - Frontend: http://localhost:3001
# - Prometheus: http://localhost:9090
# - Grafana: http://localhost:3000
```

### Running Backend Locally
```bash
cd backend/rating-system

# With SQLite (development)
mvn spring-boot:run -Dspring-boot.run.profiles=sqlite

# With PostgreSQL (production-like)
mvn spring-boot:run
```

### Running Frontend Locally
```bash
cd movie-front

# Install dependencies
npm install

# Start development server
npm start

# Frontend will be available at http://localhost:3000
```

## API Documentation

### Authentication Endpoints
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login and receive JWT token

### Movie Endpoints
- `GET /api/movies` - Get all movies (public)
- `GET /api/movies?search={title}` - Search movies (public)
- `GET /api/movies/{id}` - Get movie by ID (public)
- `POST /api/movies` - Create movie (authenticated)
- `PUT /api/movies/{id}` - Update movie (authenticated)
- `DELETE /api/movies/{id}` - Delete movie (authenticated)

### Review Endpoints
- `GET /api/reviews` - Get all reviews (public)
- `GET /api/reviews/{id}` - Get review by ID (public)
- `GET /api/reviews/movie/{movieId}` - Get reviews for movie (public)
- `POST /api/reviews` - Create review (authenticated)
- `PUT /api/reviews/{id}` - Update own review (authenticated)
- `DELETE /api/reviews/{id}` - Delete own review (authenticated)

### Monitoring Endpoints
- `GET /actuator/health` - Health check
- `GET /actuator/prometheus` - Prometheus metrics
- `GET /actuator/metrics` - Available metrics

## Testing

### Run Unit Tests
```bash
cd backend/rating-system

# Run all tests
mvn test

# Run tests with coverage report
mvn test jacoco:report

# View coverage report
open target/site/jacoco/index.html
```

### Test Coverage
The project includes comprehensive test coverage:
- Controller tests (AuthController, MovieController, ReviewController)
- Service tests (AuthService, MovieService, ReviewService, RatingService)
- Security tests (JwtUtil)
- Integration tests
- Metrics tests

## CI/CD Pipeline

### Continuous Integration (CI)
Triggered on push to `main` branch and pull requests:
- Build application with Maven
- Run Checkstyle linting
- Execute unit tests
- Validate code quality

### Continuous Deployment (CD)
Triggered on Git tags (e.g., `v1.0.0`):
- Build Docker image
- Push to GitHub Container Registry
- Tag with version number

### Creating a Release
```bash
# Create and push a tag
git tag v1.0.0
git push origin v1.0.0

# GitHub Actions will automatically build and push Docker image
# Image will be available at: ghcr.io/<username>/<repo>:v1.0.0
```

## Deployment

### Production Deployment (Railway)

The application is deployed on Railway:
- **Backend**: https://rating-system-api-production.up.railway.app
- **Frontend**: https://front-production-a80d.up.railway.app

### Environment Variables

Backend requires the following environment variables:
```bash
# Database (PostgreSQL)
SPRING_DATASOURCE_URL=jdbc:postgresql://host:port/database
SPRING_DATASOURCE_USERNAME=username
SPRING_DATASOURCE_PASSWORD=password
SPRING_PROFILES_ACTIVE=postgres

# Or use SQLite for development
SPRING_PROFILES_ACTIVE=sqlite

# JWT Configuration
JWT_SECRET=your-secret-key-minimum-256-bits
JWT_EXPIRATION=86400000

# Server Port (Railway sets this automatically)
PORT=8080
```

## Monitoring and Observability

### Prometheus Metrics

Access Prometheus at `http://localhost:9090` (when running locally)

Key queries:
```promql
# Request rate
rate(http_requests_total[5m])

# Authentication success rate
rate(auth_success_total[5m])

# Review creation rate
rate(reviews_created_total[5m])
```

### Grafana Dashboards

Access Grafana at `http://localhost:3000` (default credentials: admin/admin)

Pre-configured dashboards visualize:
- HTTP request rates
- Authentication metrics
- Review activity
- System health

### Alert Rules

Configured alerts:
- High request rate (>10 req/s for 1 minute)
- Very high request rate (>50 req/s for 30 seconds)
- Application down (>1 minute)
- High authentication failure rate
- Low review creation rate

## Database

### SQLite (Development)
Used for local development and testing. Database file stored in `./data/movierating.db`

### PostgreSQL (Production)
Used in production deployment. Configured via environment variables.

### Schema
- **users** - User accounts and authentication
- **movies** - Movie information
- **reviews** - User reviews and ratings

## Security Considerations

- Passwords are hashed using BCrypt
- JWT tokens expire after 24 hours
- CORS configured for specified origins
- SQL injection prevention via JPA/Hibernate
- Input validation on all endpoints
- Role-based access control

## Contributing

### Branch Strategy
- `main` - Production-ready code
- Feature branches - Named descriptively (e.g., `feature/add-movie-search`)
- Pull requests required for merging to main

### Code Quality
- Follow Java coding conventions
- Maintain test coverage above 70%
- Run Checkstyle before committing
- Write descriptive commit messages

### Commit Message Format
```
type: brief description

Longer description if needed

Examples:
- feat: Add movie search functionality
- fix: Fix authentication token expiration
- docs: Update API documentation
- test: Add tests for ReviewService
```

## Troubleshooting

### Backend won't start
- Check if port 8080 is available
- Verify database connection settings
- Check logs: `docker-compose logs app`

### Frontend won't connect to backend
- Verify API_URL in `movie-front/src/App.js`
- Check CORS configuration in `SecurityConfig.java`
- Check browser console for errors (F12)

### Metrics not appearing in Prometheus
- Verify `/actuator/prometheus` endpoint is accessible
- Check Prometheus targets: `http://localhost:9090/targets`
- Ensure MetricsInterceptor is registered in WebConfig

### Database connection failed
- For PostgreSQL: verify credentials and host
- For SQLite: check if `./data` directory exists
- Check Railway database service status

## License

This project is developed as part of a university assignment.

## Contact

For questions or issues, please contact the team members or create an issue in the repository.
