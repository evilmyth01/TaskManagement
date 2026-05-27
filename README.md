# Task Management Platform — Event-Driven Microservices

A production-style distributed task management system built with Java Spring Boot, Apache Kafka, PostgreSQL, Redis, and Docker. Five independently deployable microservices communicating over HTTP and Kafka, behind a single API Gateway.

---

## Architecture

```
React Frontend (coming soon)
        │
        ▼
┌─────────────────────────────────────────────────────────┐
│           API Gateway  :8085                            │
│   JWT auth · rate limiting · circuit breaker · routing  │
└──────┬──────────┬──────────┬──────────────┬────────────┘
       │          │          │              │
       ▼          ▼          ▼              ▼
 ┌──────────┐ ┌────────┐ ┌──────────┐ ┌───────────┐
 │   Auth   │ │  Task  │ │  Notif.  │ │ Analytics │
 │  :8081   │ │  :8082 │ │  :8084   │ │  :8083    │
 └──────────┘ └───┬────┘ └────▲─────┘ └─────▲─────┘
                  │           │              │
                  ▼           │              │
          ┌───────────────────┴──────────────┘
          │         Apache Kafka
          │    topic: task-events
          │    2 consumer groups · DLQ
          └───────────────────────────────────
                  │                │
                  ▼                ▼
            PostgreSQL           Redis
          (4 schemas)     rate limit · cache
```

### Services

| Service | Port | Responsibility |
|---|---|---|
| API Gateway | 8085 | Single entry point — JWT validation, routing, rate limiting, circuit breaking |
| Auth Service | 8081 | User registration, login, JWT issuance, BCrypt, roles |
| Task Service | 8082 | Task CRUD, Kafka producer, assignee validation via Feign |
| Notification Service | 8084 | Kafka consumer, email via SMTP, DLQ retry, notification history |
| Analytics Service | 8083 | Kafka consumer, per-user productivity metrics, event audit log |

---

## Tech Stack

| Category | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.x |
| API Gateway | Spring Cloud Gateway (WebFlux) |
| Messaging | Apache Kafka |
| Auth / Security | Spring Security · jjwt · BCrypt |
| Service Communication | OpenFeign (sync) · Kafka (async) |
| Resilience | Resilience4j — circuit breaker · DLQ retry |
| Database | PostgreSQL 16 (per-service schemas) |
| Caching / Rate Limiting | Redis 7 |
| Infrastructure | Docker · Docker Compose |
| Build | Maven |

---

## Getting Started

### Prerequisites

- Docker and Docker Compose installed
- Java 17+
- Maven 3.8+

### 1. Start all infrastructure

Clone the repo and from the root directory run:

```bash
docker-compose up -d
```

This starts Kafka, Zookeeper, PostgreSQL (with all four schemas), Redis, Kafka UI, and Redis Commander.

| UI | URL |
|---|---|
| Kafka UI | http://localhost:8090 |

### 2. Configure environment variables

Each service needs a `JWT_SECRET` environment variable. Create a `.env` file in each service directory or export directly:

```bash
export JWT_SECRET=your_base64_encoded_256bit_secret_here
```

You can generate a secret with:

```bash
openssl rand -base64 32
```

For the Notification Service, you also need Gmail SMTP credentials:

```bash
export MAIL_USERNAME=your_gmail@gmail.com
export MAIL_PASSWORD=your_gmail_app_password
```

> Use a Gmail App Password, not your real password. Generate one at: Google Account → Security → 2-Step Verification → App Passwords.

### 3. Start services

Start each service individually from its directory:

```bash
# Auth Service
cd auth-service && mvn spring-boot:run

# Task Service
cd task-service && mvn spring-boot:run

# Notification Service
cd notification-service && mvn spring-boot:run

# Analytics Service
cd analytics-service && mvn spring-boot:run

# API Gateway
cd gateway && mvn spring-boot:run
```

Or run all from root if you have a parent pom configured.

---

## API Reference

All requests go through the gateway at `http://localhost:8085`. Authenticated endpoints require `Authorization: Bearer <token>` header.

### Auth Service — `/api/v1/auth`

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/auth/signup` | No | Register a new user |
| POST | `/auth/login` | No | Login and receive JWT |
| GET | `/auth/validate` | No | Validate a token (used by gateway) |
| GET | `/auth/{userId}/exists` | No | Check if user exists (internal) |
| GET | `/auth/{userId}/email` | No | Get user email by ID (internal) |

**Signup request:**
```json
{
  "name": "Vishal Sharma",
  "email": "vishal@example.com",
  "password": "yourpassword",
  "role": "USER"
}
```

**Login response:**
```json
{
  "token": "eyJhbGci...",
  "userId": 1,
  "name": "Vishal Sharma",
  "role": "USER"
}
```

---

### Task Service — `/api/v1/tasks`

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/tasks` | Yes | Create a new task |
| GET | `/tasks/{id}` | Yes | Get task by ID |
| PUT | `/tasks/{id}/status` | Yes | Update task status |
| PUT | `/tasks/{id}/assign` | Yes | Assign task to a user |
| GET | `/tasks?assignedToId=&status=` | Yes | Get tasks by assignee, optionally filtered by status |

**Create task request:**
```json
{
  "title": "Implement login page",
  "description": "Build the React login form with validation",
  "status": "CREATED"
}
```

**Task statuses:** `CREATED` · `IN_PROGRESS` · `DONE`

---

### Notification Service — `/api/v1/notifications`

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | `/notifications/user/{userId}` | Yes | Get all notifications for a user (paginated) |
| GET | `/notifications/user/{userId}/unread` | Yes | Get unread notification count |
| GET | `/notifications/{id}` | Yes | Get notification by ID |
| PUT | `/notifications/{id}/read` | Yes | Mark notification as read |

**Unread count response:**
```json
{
  "unreadCount": 3
}
```

---

### Analytics Service — `/api/v1/analytics`

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | `/analytics/user/{userId}` | Yes | Get productivity metrics for a user |
| GET | `/analytics/user/{userId}/activity?from=&to=` | Yes | Get event activity for a user in a date range |

**User analytics response:**
```json
{
  "userId": 1,
  "totalCreated": 12,
  "totalAssigned": 8,
  "totalCompleted": 5,
  "inProgress": 3,
  "avgCompletionMs": 86400000,
  "lastActivityAt": "2025-01-15T10:30:00Z"
}
```

---

## Kafka Events

Task Service publishes to the `task-events` topic. Events are partitioned by `taskId` to guarantee ordered delivery per task.

### Event payload

```json
{
  "eventType": "TASK_CREATED",
  "taskId": 1,
  "title": "Implement login page",
  "description": "Build the React login form",
  "status": "CREATED",
  "assignedToId": 2,
  "createdById": 1,
  "timestamp": "2025-01-15T10:00:00Z"
}
```

### Event types

| Event | Trigger | Consumers |
|---|---|---|
| `TASK_CREATED` | New task created | Notification, Analytics |
| `TASK_ASSIGNED` | Task assigned to a user | Notification, Analytics |
| `TASK_STATUS_UPDATED` | Task status changed | Notification, Analytics |

### Consumer groups

| Service | Group ID |
|---|---|
| Notification Service | `notification-service` |
| Analytics Service | `analytics-service` |

Both groups receive every event independently — neither competes with the other.

### Dead letter queue

Notification Service uses `FixedBackOff` — 3 retry attempts at 2 second intervals before publishing to `task-events-DLT` (dead letter topic). Monitor failed events via Kafka UI at `http://localhost:8090`.

---

## Gateway — How it works

All requests hit the gateway first. Here is what happens on every authenticated request:

```
1. Request arrives at :8085
2. JwtAuthFilter extracts Bearer token from Authorization header
3. Token validated locally using shared JWT_SECRET (no Auth Service call)
4. userId, email, role extracted from token claims
5. Injected as headers: X-User-Id, X-User-Email, X-User-Role
6. Rate limit checked against Redis (10 req/s per user, burst 20)
7. Circuit breaker checks service health
8. Request forwarded to downstream service
9. Downstream service reads X-User-Id header — no JWT parsing needed
```

**Whitelisted paths** (no JWT required): `/api/v1/auth/signup`, `/api/v1/auth/login`

### Circuit breakers

Each service route has a dedicated circuit breaker and fallback:

| Service | Fallback endpoint | Fallback response |
|---|---|---|
| Auth | `/fallback/auth` | 503 SERVICE_UNAVAILABLE |
| Task | `/fallback/task` | 503 SERVICE_UNAVAILABLE |
| Notification | `/fallback/notification` | 503 SERVICE_UNAVAILABLE |
| Analytics | `/fallback/analytics` | 503 SERVICE_UNAVAILABLE |

Circuit breaker config: sliding window size 10 · failure rate threshold 50% · wait in open state 10s

---

## Database Schema

One PostgreSQL instance, five tables.

**Indexes:**

| Table | Index | Reason |
|---|---|---|
| tasks | `idx_assigned_to` | Fast lookup of tasks by assignee |
| tasks | `idx_status` | Filter by status without full scan |
| notifications | `idx_user_id` | Fast unread count and list by user |
| task_event_log | `idx_user`, `idx_task`, `idx_occurred` | Analytics queries by user, task and date |

---

## Project Structure

```
task-platform/
│
├── auth-service/               Spring Boot — user auth and JWT
├── task-service/               Spring Boot — task CRUD and Kafka producer
├── notification-service/       Spring Boot — Kafka consumer and email
├── analytics-service/          Spring Boot — Kafka consumer and metrics
├── gateway/                    Spring Cloud Gateway
│
├── docker-compose.yml          All infrastructure in one file
├── init-db.sql                 Creates all four PostgreSQL databases
└── README.md
```

---

## Architecture Decisions

**Why Kafka instead of REST for inter-service events?**
Task state changes need to reach both Notification and Analytics independently. REST would require Task Service to know about both consumers and call them synchronously — tight coupling. Kafka decouples producers from consumers completely. Adding a new consumer (e.g. an audit service) requires zero changes to Task Service.

**Why validate JWT at the gateway instead of each service?**
Centralising validation eliminates duplicated security logic and removes a network call to Auth Service on every request. The shared secret is injected as an environment variable. Downstream services trust the headers injected by the gateway.

**Why two separate Kafka consumer groups?**
If Notification and Analytics shared one consumer group, Kafka would split partitions between them — each would miss events. Separate groups mean each service gets a full independent copy of every event.

**Why per-service database schemas?**
Microservices should own their data. Shared schemas create invisible coupling — a schema change in one service can break another. Isolated schemas enforce the boundary even on a single Postgres instance.

---

## What's next

- [ ] Dockerize all five Spring Boot services
- [ ] Kubernetes manifests (Deployments, Services, ConfigMaps, Secrets)
- [ ] Deploy to GKE
- [ ] Jenkins CI/CD pipeline — build → test → Docker image → push → rolling deploy
- [ ] React frontend
- [ ] Distributed tracing with Zipkin
- [ ] Prometheus + Grafana dashboards

---
