# Trainer Workload Service

## Overview

**Trainer Workload Service** is a Spring Boot–based microservice responsible for
tracking and aggregating **trainers’ monthly workload**.

The service is designed as a **supporting microservice** and is not intended to be
accessed directly by end users.  
It receives events from the main **Gym CRM System** and maintains workload
statistics for trainers.

Key responsibilities:

- Receive training workload events (add / remove)
- Maintain monthly workload summaries per trainer
- Provide aggregated workload data on demand
- Secure inter-service communication using JWT
- Register with Eureka for service discovery

---

## Responsibilities

1. Consume training workload events from the main CRM service
2. Update trainer workload statistics (year / month / total hours)
3. Handle both **add** and **remove** training operations
4. Provide workload data for a specific trainer and month
5. Validate all incoming requests using service-level JWT authentication
6. Log requests using a shared `transactionId` for cross-service tracing

---

## Architecture

- **Spring Boot**
- **Hexagonal (Ports & Adapters) architecture**
- **Repository abstraction (persistence can be replaced later)**
- **Service-to-service communication only**
- **No direct user authentication**

---

## Launch

1. **Configure Eureka (`application.yml`)**:

```properties
eureka:
    client:
        service-url:
            defaultZone: http://localhost:8761/eureka/
```

2. **Run the application**:

```bash
mvn spring-boot:run
```

## Inter-Service Communication

This microservice is called by the **Gym CRM System** using REST.

- Communication is performed via **Feign**
- The service is discovered via **Eureka**
- Requests are authenticated using **JWT service tokens**
- Each request propagates a `transactionId` for distributed logging

---

## Security

### Service-to-Service Security

- All endpoints are protected
- Requests must include a valid **JWT service token**
- Tokens are validated by a dedicated JWT filter
- No user authentication or authorization logic is implemented

This service does **not** expose public endpoints.

---

## REST API

1. All endpoints are documented using **Swagger/OpenAPI**.
2. Access the Swagger UI at: `http://localhost:8081/swagger-ui/index.html`
3. Input validation and error handling are implemented for all endpoints.

---

### API Capabilities

- Receive trainer workload events
- Retrieve monthly workload summary for a trainer

---

## Logging

The service implements **transaction-based logging**:

- A `transactionId` is extracted from incoming requests
- The same `transactionId` is used across logs
- Enables tracing of requests across multiple microservices

---

## Project Structure

```
com.trainerworkload
 ├─ application
 │   ├─ request     #helpers for handling incoming requests
 │   └─ service (port / impl) #Business logic
 ├─ domain
 │   ├─ model       #Entities: TrainerWorkload
 │   ├─ exception   #Custom exceptions
 │   └─ port        #Repository interfaces
 ├─ infrastructure
 │   ├─ config      # Security
 │   ├─ security    # JWT, filter
 │   ├─ logging     # Filter for transaction logging
 │   ├─ repository  # Repositories
 │   ├─ mapper      # Entity ↔ DAO mappers
 │   └─ dao         # DAO classes
 └─ presentation
     ├─ controller  # REST controllers
     ├─ dto         # REST request/response DTOs
     ├─ mapper      # DTO ↔ Entity mappers
     └─ advice      # Global exception handling

```

---

## Testing

1. Unit tests with **JUnit 5** covering services, repositories, and controllers.
2. Run tests via:

```bash
mvn test
```

---

## Dependencies

1. Java 8+
2. Spring Boot / Spring Core
3. Spring Security + JWT
4. JUnit 5
5. SLF4J / Logback
6. Swagger/OpenAPI
7. Spring Web / Spring MVC 
8. Spring Cloud Netflix Eureka Client 
9. Jakarta Validation 
10. Lombok

---

## Notes

1. This microservice is not user-facing.
2. It relies on the main CRM service for event production.
3. Persistence is abstracted and can be replaced with a database implementation.

