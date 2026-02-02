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

## MongoDB Setup (Docker Compose)

Example `docker-compose.yml`:

```yaml
services:
  mongo:
    image: mongo:7.0
    container_name: gym-crm-mongo
    restart: always
    ports:
      - "27017:27017"
    environment:
      MONGO_INITDB_ROOT_USERNAME: gymuser
      MONGO_INITDB_ROOT_PASSWORD: pass
      MONGO_INITDB_DATABASE: gymdb
    volumes:
      - mongo_data:/data/db

volumes:
  mongo_data:

```

## Launch

1. **Start MongoDB via Docker Compose**:

```bash
docker-compose up -d
```

2. **Configure RabbitMQ**:

```yaml
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: gymuser
    password: pass
    listener:
      simple:
        concurrency: 1
        max-concurrency: 3
```

3. **Configure Eureka**:

```yaml
eureka:
  client:
    register-with-eureka: false
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

4. **Configure MongoDB**:

```yaml
spring:
  mongodb:
    uri: mongodb://gymuser:pass@localhost:27017/gymdb?authSource=admin
```

5. **Run the application**:

```bash
mvn spring-boot:run
```

## Inter-Service Communication

This microservice **consumes trainer workload events asynchronously** from the Gym CRM System via **RabbitMQ**.

- Messages are sent in **JSON format** and represent training add/remove events.
- Dead Letter Queue (DLQ) is used for messages with missing or invalid information.
- Each message carries a `transactionId` for distributed tracing across microservices.
- The service is discovered via **Eureka**
- Optional GET endpoints can be called by authorized services using a **JWT service token** to retrieve aggregated trainer workload data.

---

## Security

### Service-to-Service Security

- All REST endpoints (e.g., GET for aggregated data) are protected via JWT service tokens
- Event consumption via RabbitMQ is validated for required fields and JWT headers if present
- This service does **not** perform any user-facing authentication or authorization

This service does **not** expose public endpoints.

---

## REST API

1. All endpoints are documented using **Swagger/OpenAPI**.
2. Access the Swagger UI at: `http://localhost:8081/swagger-ui/index.html`
3. Input validation and error handling are implemented for all endpoints.

---

### API Capabilities

- **GET endpoints** to retrieve monthly workload summary for a trainer (only accessible to authorized services via JWT)
- **Event consumption** (ADD/DELETE) is handled **asynchronously via RabbitMQ**.

---

## Logging

The service implements **transaction-based logging**:

- A `transactionId` is extracted from incoming requests
- The same `transactionId` is used across logs
- Enables tracing of requests across multiple microservices

---

## Environments

The project supports multiple Spring profiles (`local`, `dev`, `stg`, `prod`) for different environments.
For this project, only the `local` profile is actively used; other profiles are present for demonstration purposes.

---

## RabbitMQ Management

The Trainer Workload Service uses RabbitMQ for asynchronous message processing.  
You can monitor queues, messages, and dead letter queues using the **RabbitMQ Management UI**:

- URL: [http://localhost:15672](http://localhost:15672)
- Default credentials (for local development):
    - Username: `gymuser`
    - Password: `pass`

Through this interface you can:
- Inspect messages in the `trainer-workload` queue
- View messages moved to the Dead Letter Queue (DLQ)
- Monitor consumer concurrency and message throughput
- Manually publish or requeue messages if necessary

---

## Project Structure

```
com.trainerworkload
 ├─ application
 │   ├─ event       # Message events (TrainerWorkloadEvent)
 │   └─ service (impl) # Orchestrators of business use cases
 ├─ domain
 │   ├─ model       # Entities: TrainerWorkload
 │   ├─ exception   # Custom exceptions
 │   └─ port        # Repository interfaces
 ├─ infrastructure
 │   ├─ config      # RabbitMessageConverter, Mongo, Security
 │   ├─ security    # JWT, filter
 │   ├─ messaging   # Consumers, exception handlers
 │   ├─ logging     # Filter for transaction logging
 │   └─ persistance
 │      ├─ adapter     # Adapters
 │      ├─ document    # Document classes
 │      ├─ mongorepo   # MongoRepositories
 │      └─ mapper      # Entity ↔ Document mappers
 └─ presentation
     ├─ controller  # REST controllers
     ├─ dto         # REST response DTOs
     └─ advice      # Global exception handling

```

---

## Testing

1. **Unit tests (JUnit 5)** covering core services, repositories, and controllers.
2. **Integration tests** verifying interaction with external infrastructure components such as **RabbitMQ**.
3. Run tests via:

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
8. Jakarta Validation 
9. Lombok 
10. Spring AMQP / RabbitMQ
11. Spring Cloud Netflix Eureka Client 
12. Spring Data MongoDB
13. Testcontainers
14. Mockito

---

## Notes

1. This microservice is not user-facing.
2. It relies on the main CRM service for event production.
3. Persistence is backed by MongoDB, providing durable storage for trainer workload statistics.

