# Auth Service

Authentication and identity service for DevHire.

## Responsibilities

- Public registration for candidates and recruiters.
- BCrypt password hashing.
- JWT login and validation.
- Candidate and recruiter profile management.
- Registration in Eureka as `AUTH-SERVICE`.

## Routes

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/auth/me`
- `POST|GET|PUT /api/candidate-profiles`
- `POST|GET|PUT /api/recruiter-profiles`

Protected routes require `Authorization: Bearer <JWT>`.

## Run

Start `discovery-server` first. Configure `DB_PASSWORD` and `JWT_SECRET`, then run:

```powershell
.\mvnw.cmd spring-boot:run
```

The service listens on `http://localhost:8081`.

Copy `src/main/resources/application-example.properties` to `application.properties`. Never commit database credentials or the JWT secret.
