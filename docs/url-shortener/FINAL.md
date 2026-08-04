# Production-Grade URL Shortener — Architecture

## Complete Request Flow

```mermaid
flowchart TD
    Client([Client: Browser / Postman / App]) -->|HTTP Request| API[Spring Boot REST API]

    API --> Shorten[POST /shorten]
    API --> Redirect["GET /{shortCode}"]

    %% ---- Shorten flow ----
    Shorten --> Snowflake[Snowflake ID Generator]
    Snowflake --> Base62[Base62 Encoder]
    Base62 --> ShortCode[Short Code Created]
    ShortCode --> SaveMySQL[(Save into MySQL)]
    SaveMySQL --> AddBloom[Add to Bloom Filter]

    %% ---- Redirect flow ----
    Redirect --> RateLimiter[Redis Rate Limiter<br/>key = rate_limit:IP]
    RateLimiter -->|Too many requests| Return429[429 Too Many Requests]
    RateLimiter -->|OK| BloomCheck[Bloom Filter Check]
    BloomCheck -->|Definitely not present| Return404[404 Not Found]
    BloomCheck -->|Possibly present| Cache[(Redis Cache<br/>Cache-Aside Pattern)]

    Cache -->|Hit| ReturnLong[Return Long URL]
    Cache -->|Miss| QueryMySQL[(Query MySQL)]
    QueryMySQL --> StoreRedis[Store Result in Redis]
    StoreRedis --> ReturnLong

    ReturnLong --> Publish[Publish Redirect Event]
    Publish --> Topic[[Apache Kafka Topic]]

    Topic --> Consumer[Kafka Consumer: Analytics]
    Topic --> DLQ[Dead Letter Queue<br/>failed messages — future]

    Consumer --> Idempotency{Idempotency Check<br/>processed_events table}
    Idempotency -->|Duplicate| Ignore[Ignore Event]
    Idempotency -->|New| StoreAnalytics[(Store Analytics Event<br/>analytics_event)]
    Idempotency -->|New| IncrementClick[(Increment Click Count<br/>url_mapping)]
```

## Monitoring & Observability

```mermaid
flowchart TD
    App[Spring Boot Application] --> Micrometer[Micrometer Metrics]
    Micrometer --> Actuator["/actuator/prometheus"]
    Actuator --> Prometheus[Prometheus Server]
    Prometheus --> TSDB[(Time-Series Database)]
    TSDB --> Grafana[Grafana Dashboard]

    Grafana --> URLGen[URL Generated]
    Grafana --> CacheMetrics[Cache Hit / Miss]
    Grafana --> KafkaMetrics[Kafka Metrics]
    Grafana --> RateLimiterMetrics[Rate Limiter Metrics]
    Grafana --> CircuitBreakerMetrics[Circuit Breaker Metrics]
```

## Infrastructure (Docker)

```mermaid
flowchart TD
    Compose[Docker Compose] --> SpringBoot[Spring Boot Application]
    Compose --> MySQL[(MySQL Database)]
    Compose --> Redis[(Redis Cache)]
    Compose --> Kafka[Kafka Broker]
    Compose --> KafkaUI[Kafka UI]
    Compose --> Prometheus[Prometheus]
    Compose --> Grafana[Grafana]
```

## Failure Handling

**Redis down:**
`Redis Down` → `Circuit Breaker Opens` → `Skip Cache` → `Use MySQL Directly`

**Rate limiter's Redis down:**
`Rate Limiter Redis Down` → `Fallback` → `Allow Request`

**Kafka down:**
`Kafka Down` → `Redirect Still Works` → `Analytics Temporarily Lost`

> In a future version, the Outbox Pattern and a Dead Letter Queue would ensure those analytics aren't lost.

## Components Used

- **Client**
  - Spring Boot
    - REST Controller
    - Service Layer
    - Repository Layer
    - Global Exception Handler
    - Validation
    - Snowflake Generator
    - Base62 Encoder
    - Bloom Filter
    - Redis Cache
    - Redis Rate Limiter
    - Circuit Breaker (Resilience4j)
    - Kafka Producer
    - Kafka Consumer
    - Idempotent Consumer
    - Micrometer Metrics
  - MySQL
  - Redis
  - Kafka
  - Prometheus
  - Grafana
  - Docker

## What an Interviewer Can Infer From This Project

This single project demonstrates experience with:

### Backend

- Java
- Spring Boot
- REST APIs
- JPA / Hibernate
- Layered Architecture

### Databases

- MySQL
- Redis

### Distributed Systems

- Event-Driven Architecture
- Kafka
- Idempotent Consumer
- Cache-Aside Pattern
- Bloom Filter
- Snowflake IDs
- Base62 Encoding

### Resilience

- Circuit Breaker
- Graceful Fallback
- Rate Limiting

### Observability

- Micrometer
- Prometheus
- Grafana
- Custom Metrics

### Infrastructure

- Docker
- Docker Compose
