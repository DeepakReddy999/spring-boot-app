# Customer Management System

Customer Management System is a Spring Boot 3 backend built with Java 17, Maven, Spring Security, JPA, PostgreSQL, and OpenAPI. It currently focuses on two clear areas:
- JWT-based local authentication for demo and development use
- Customer management APIs for creating, importing, and viewing customer records

The project is structured like a small enterprise service, with separate controller, service, repository, DTO, entity, security, configuration, and exception layers.

## What The Project Does

- Authenticates seeded demo users with email and password
- Issues Bearer JWT tokens for local use
- Creates individual customers
- Imports customers from CSV files
- Retrieves customers by ID
- Lists customers with pagination
- Exposes Swagger UI and basic Actuator health/info endpoints

## Package Layout

```text
src/main/java/com/example/customer
|-- controller
|-- dto
|-- entity
|-- repository
`-- service
    `-- impl

src/main/java/com/example/employeetaskmanagement
|-- config
|-- controller
|-- dto
|-- entity
|-- exception
|-- repository
|-- security
`-- service
    `-- impl
```

The `com.example.customer` package contains the customer business module.
The `com.example.employeetaskmanagement` package currently holds shared application infrastructure such as authentication, security, exception handling, startup configuration, and seeded users.

## Simple Architecture Explanation

Think of the app as a small office system:
- Controllers are the front desk. They receive HTTP requests.
- Services are the operations team. They apply business rules.
- Repositories are the records team. They read and write database data.
- Security is the badge scanner. It controls who gets access.
- Exception handling is the support desk. It turns failures into clean API responses.

This separation makes the code easier to explain, test, and extend.

## Main APIs

### Authentication
- `POST /api/auth/login`
- `POST /api/auth/token`
- `GET /api/auth/me`

### Customers
- `POST /customers`
- `POST /customers/upload`
- `GET /customers/{id}`
- `GET /customers`

## Local Demo Credentials

These users are seeded automatically on startup:
- `admin@company.com / Admin@123`
- `manager@company.com / Manager@123`
- `employee@company.com / Employee@123`
- `anita.sharma@company.com / Employee@123`

## Running The App

### Prerequisites

- Java 17

### Runtime Database

The app starts with H2 by default for a zero-setup local run.

If you want PostgreSQL, use `src/main/resources/application-postgres.properties` with the `postgres` profile.

Default local config in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:h2:mem:customerdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.username=sa
spring.datasource.password=
```

PostgreSQL profile config in `src/main/resources/application-postgres.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/customerdb
spring.datasource.username=postgres
spring.datasource.password=secret
```

### Start Locally

```powershell
.\mvnw.cmd spring-boot:run
```

### Start With PostgreSQL

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=postgres"
```

### Run Tests

Tests use an in-memory H2 database through `src/test/resources/application.properties`, so builds do not require PostgreSQL:

```powershell
.\mvnw.cmd test
```

GitHub Actions runs the same Maven test phase on Ubuntu with Java 17, so this command is the simplest local pre-push validation.

## Swagger And Operations

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI docs: `http://localhost:8080/v3/api-docs`
- H2 console: `http://localhost:8080/h2-console`
- Health endpoint: `http://localhost:8080/actuator/health`
- Info endpoint: `http://localhost:8080/actuator/info`

For secured endpoints:
1. Call `POST /api/auth/login`
2. Copy the returned `accessToken`
3. Use `Authorization: Bearer <token>`
4. In Swagger UI, click `Authorize` and paste the token

## Notes On Authentication

The current JWT login is intentionally simple so the app is easy to run locally and demo in Swagger.
In a real enterprise deployment, authentication would usually move to an external identity provider such as Okta, Azure AD, or Keycloak using OAuth2 / OpenID Connect.

## Production Readiness Gaps

This codebase is in better shape for demos and debugging exercises, but a few things would still be added for full production readiness:
- database migrations with Flyway or Liquibase
- broader integration and security test coverage
- centralized logging and metrics export
- secret rotation and stronger environment-based config management
- external identity-provider integration
