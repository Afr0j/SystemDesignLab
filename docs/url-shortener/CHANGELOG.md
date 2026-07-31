# URL Shortener V1

## Goal

Build a basic URL shortening service.

## Architecture

Browser

↓

Spring Boot

↓

MySQL

## Features

- Create Short URL
- Redirect URL
- Delete URL
- URL Statistics

## Current Limitations

- Single Server
- Single Database
- No Redis
- No Load Balancer
- No Message Queue
- No Monitoring

## Future Improvements

- Redis Cache
- Load Balancer
- Multiple Servers
- Rate Limiter
- Distributed ID Generator
- Monitoring Dashboard



# Lessons Learned

## MySQL Collation Issue

During load testing with k6 we observed around 60% request failures caused by duplicate short_code values.

The Base62 encoder was correct, but MySQL was using the default case-insensitive collation (`utf8mb4_0900_ai_ci`).

This caused values like:

2BQ

and

2bq

to be treated as identical.

Solution:

Changed the column collation to

utf8mb4_bin

Result:

- Success rate improved from 40% to 100%.
- Base62 encoding now correctly distinguishes uppercase and lowercase.



# V4 - Event Driven Architecture (Kafka)
## Goal

Decouple analytics from the redirect flow.

Previously, every redirect request updated MySQL synchronously.

Problem:

Client
    ↓
Redirect
    ↓
UPDATE click_count
    ↓
Return URL

The user had to wait for the database update.

Solution:

Publish a RedirectEvent to Kafka and update analytics asynchronously.
Before
Client
   │
   ▼
UrlService
   │
   ▼
Redis
   │
   ▼
MySQL

After

Client
   │
   ▼
UrlService
   │
   ▼
Kafka Producer
   │
   ▼
Kafka Topic
   │
   ▼
Kafka Consumer
   │
   ▼
MySQL

### Concepts Learned

- Event Driven Architecture
- Kafka Producer
- Kafka Consumer
- Topic
- Partition
- Offset
- Consumer Group
- Asynchronous Processing
- Decoupling Services



Problems Faced

This is the most valuable section.

Problem 1
### MySQL Collation

Problem

Base62 is case-sensitive.

Example

ABC != abc

MySQL default collation was case-insensitive.

Result

Duplicate short codes.

Fix

Changed short_code column to binary collation.
Problem 2
### Snowflake IDs exceeded VARCHAR(8)

Problem

Snowflake Base62 values became longer than AUTO_INCREMENT ids.

Error

Data too long for column short_code

Fix

Increase VARCHAR(8) to VARCHAR(12).

(Use the actual size you settled on.)

Problem 3
### Spring Boot 4.x Kafka Auto Configuration

Problem

KafkaTemplate bean was not created automatically.

Symptoms

No qualifying bean of type KafkaTemplate

Investigation

- Verified dependency
- Verified properties
- Verified KafkaTemplate exists
- Verified @SpringBootApplication

Root Cause

Spring Boot 4.x behavior differed from the stable ecosystem used by our project.

Fix

Migrated project to Spring Boot 3.x.

Result

Kafka auto-configuration worked immediately.

This one is worth documenting because it shows your debugging process.

Problem 4
### Consumer failed with SerializationException

Error

No type information in headers

Cause

Consumer started reading old messages:

Hello Kafka
Afroj
Spring Boot

These were plain strings, not RedirectEvent JSON.

Fix

Created a new consumer group.

analytics-group-v2

Changed

auto-offset-reset=latest

Result

Consumer started from new events only.


### Lessons

Producer and Consumer are independent.

Producer never knows whether the Consumer is alive.

Kafka stores events on disk.

Consumer Groups maintain their own offsets.

Changing the Consumer Group creates a new logical reader.

latest

↓

Read only future events.

earliest

↓

Read the entire history.


### Lessons

Producer and Consumer are independent.

Producer never knows whether the Consumer is alive.

Kafka stores events on disk.

Consumer Groups maintain their own offsets.

Changing the Consumer Group creates a new logical reader.

latest

↓

Read only future events.

earliest

↓

Read the entire history.

                Browser
                    │
                    ▼
              UrlController
                    │
                    ▼
               UrlService
              /          \
             ▼            ▼
        Redis Cache   Kafka Producer
             │            │
             ▼            ▼
       Return URL   redirect-events
                          │
                          ▼
                 Kafka Consumer
                          │
                          ▼
                      MySQL

                      