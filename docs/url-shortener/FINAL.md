What We Learned
1. Core Backend
Spring Boot
REST APIs
Layered Architecture
DTOs
Exception Handling
Validation
Global Exception Handler
2. Database

We learned

MySQL
Spring Data JPA
Hibernate
Entity Mapping
Repository Pattern
Transactions
Database Design

Tables

url_mapping
analytics_event
processed_events
3. URL Generation

Instead of random strings,

we implemented

Snowflake ID
        ↓
Base62 Encoding
        ↓
Short URL

Learned

Distributed ID Generation
Why UUID isn't always the best choice
Why Snowflake is used at Twitter and many large companies
Base62 Encoding
4. Caching

Implemented

Redis

Concepts

Cache Aside Pattern
Cache Hit
Cache Miss
TTL
Cache Eviction

Flow

Request

↓

Redis

↓

Hit → Return

↓

Miss

↓

MySQL

↓

Update Cache
5. Bloom Filter

Implemented

Bloom Filter

Purpose

Avoid unnecessary database queries for invalid short URLs.

Flow

Unknown URL

↓

Bloom Filter

↓

Definitely Not Present

↓

Return 404

(No DB Query)

Learned

Bit Arrays
Hash Functions
False Positives
No False Negatives
6. Kafka

Implemented

Producer

↓

Topic

↓

Consumer

Producer publishes

RedirectEvent

Consumer

Saves analytics
Updates click count

Why?

To make redirect fast while analytics runs asynchronously.

7. Idempotency

Implemented

processed_events

Purpose

Prevent duplicate Kafka events.

Flow

Receive Event

↓

Already Processed?

↓

Yes

↓

Ignore
8. Rate Limiter

Implemented

Redis-based rate limiting.

Key

rate_limit:<IP>

Learned

Fixed Window
TTL
Per-IP limiting

Why per IP?

Protects the whole service instead of individual URLs.

9. Circuit Breaker

Implemented

Resilience4j

Scenarios

Redis Down

↓

Skip Cache

↓

Use MySQL

Rate Limiter Redis Down

↓

Allow Request

↓

Keep Service Alive

Learned

Closed
Open
Half Open
Fallback Methods
10. Failure Handling

We intentionally tested

✅ Redis Down

✅ Kafka Down

✅ Cache Failure

✅ Rate Limiter Failure

Instead of crashing,

the system degraded gracefully.

11. Metrics

Implemented

Micrometer Counters

Examples

url.generated

url.redirect

cache.hit

cache.miss

cache.redis.failure

rate_limit.allowed

rate_limit.blocked

kafka.events.published

kafka.events.processed

bloom.rejected
12. Prometheus

Collected metrics from

/actuator/prometheus

Learned

Metric scraping
Counter naming
Prometheus format
13. Grafana

Built dashboard showing

URL Generated
Redirects
Cache Hits
Cache Misses
Redis Failures
Kafka Published
Kafka Processed
Bloom Rejected
Rate Limited
14. Docker

Containerized

MySQL
Redis
Kafka
Kafka UI
Prometheus
Grafana

Everything runs using

docker compose up -d
🧠 Important System Design Concepts Learned
Layered Architecture
Cache Aside Pattern
Event Driven Architecture
Asynchronous Processing
Circuit Breaker
Rate Limiting
Bloom Filter
Idempotency
Observability
Metrics
Monitoring
Docker Networking
Graceful Degradation
Fail Fast Principle
🔄 Final Architecture
                Client
                   │
                   ▼
             Spring Boot API
                   │
      ┌────────────┼────────────┐
      ▼            ▼            ▼
Rate Limiter   Bloom Filter   Metrics
      │
      ▼
 Redis Cache
   │      │
Hit      Miss
 │         │
 ▼         ▼
Return    MySQL
             │
             ▼
      Kafka Producer
             │
             ▼
      Kafka Topic
             │
             ▼
      Kafka Consumer
      │             │
      ▼             ▼
Analytics      Click Count
             │
             ▼
 Prometheus
      │
      ▼
 Grafana
📈 What Makes This Project Different

This isn't just another CRUD application.

It demonstrates:

High Performance
Scalability Concepts
Fault Tolerance
Distributed Systems Basics
Observability
Event-Driven Processing
Production-like Infrastructure
🚀 Technologies Used
Backend
Java 21
Spring Boot
Spring MVC
Spring Data JPA
Hibernate
Maven
Database
MySQL
Cache
Redis
Messaging
Apache Kafka
Resilience
Resilience4j
Monitoring
Micrometer
Prometheus
Grafana
Infrastructure
Docker
Docker Compose
Utilities
Snowflake ID Generator
Base62 Encoding
Bloom Filter