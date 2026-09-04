# API Gateway

Single entry point for DevHire clients. The gateway discovers registered services through Eureka and routes requests with Spring Cloud LoadBalancer.

## Responsibilities

- Routes `/api/auth/**`, `/api/candidate-profiles/**` and `/api/recruiter-profiles/**` to `AUTH-SERVICE`.
- Uses `lb://AUTH-SERVICE` to resolve an available instance through Eureka.
- Exposes health and gateway actuator endpoints for local development.

## Run

Start `discovery-server` first, then run:

```powershell
.\mvnw.cmd spring-boot:run
```

The gateway listens on `http://localhost:8080`.

Copy `src/main/resources/application-example.properties` to `application.properties` for local development.
