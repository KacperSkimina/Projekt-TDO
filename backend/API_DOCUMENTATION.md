# API Documentation

## Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login user

### Movies
- `GET /api/movies` - Get all movies (public)
- `GET /api/movies?search={title}` - Search movies (public)
- `GET /api/movies/{id}` - Get movie by ID (public)
- `POST /api/movies` - Create movie (authenticated)
- `PUT /api/movies/{id}` - Update movie (authenticated)
- `DELETE /api/movies/{id}` - Delete movie (authenticated)

### Reviews
- `GET /api/reviews` - Get all reviews (public)
- `GET /api/reviews/{id}` - Get review by ID (public)
- `GET /api/reviews/movie/{movieId}` - Get reviews for movie (public)
- `POST /api/reviews` - Create review (authenticated)
- `PUT /api/reviews/{id}` - Update own review (authenticated)
- `DELETE /api/reviews/{id}` - Delete own review (authenticated)

### Monitoring
- `GET /actuator/health` - Health check
- `GET /actuator/prometheus` - Prometheus metrics
- `GET /actuator/metrics` - Available metrics

## Metrics Exposed

- `http_requests_total` - Total HTTP requests
- `auth_success_total` - Successful authentications
- `auth_failure_total` - Failed authentications
- `reviews_created_total` - Total reviews created
