# Discovery Server

Service registry for the DevHire microservices architecture, powered by Netflix Eureka.

## Responsibilities

- Registers and exposes available service instances.
- Lets the API Gateway discover downstream services.
- Enables client-side load balancing through Spring Cloud LoadBalancer.

## Run

```powershell
.\mvnw.cmd spring-boot:run
```

The Eureka dashboard is available at `http://localhost:8761`.

Copy `src/main/resources/application-example.properties` to `application.properties` for local development.
