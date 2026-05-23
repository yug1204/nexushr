# NexusHR – AI-Enabled Enterprise HR & Workforce Intelligence Platform

> **Project Code:** AMX-JFS-2026-04 | **Domain:** Java Full-Stack | **Version:** 1.0

## 🚀 Overview

NexusHR is a production-grade Java full-stack HR management platform covering the complete employee lifecycle — from onboarding to offboarding — with real-time attendance, automated payroll, 360-degree performance reviews, and AI-driven workforce intelligence.

**Designed for:** Mid-to-large enterprises (5,000–50,000 employees)

### Key Metrics
| Metric | Target |
|--------|--------|
| Uptime SLA | 99.95% |
| API Latency (P95) | < 300ms |
| HR Workload Reduction | 40–60% |
| Payroll Accuracy | < 0.1% error |
| AI Attrition Model AUC | ≥ 0.84 |

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    React 19 SPA (Vite)                   │
│  Dashboard│Employees│Attendance│Payroll│Perf│AI Insights │
└──────────────────────────┬──────────────────────────────┘
                           │ HTTPS
                    ┌──────┴──────┐
                    │ API Gateway │ (Spring Cloud Gateway)
                    │  JWT Relay  │
                    └──────┬──────┘
          ┌────────────────┼────────────────┐
    ┌─────┴─────┐   ┌─────┴──────┐  ┌──────┴───────┐
    │Auth Service│   │  Employee  │  │  Attendance  │
    │JWT + MFA   │   │  Service   │  │  + Leave     │
    └────────────┘   └────────────┘  └──────────────┘
    ┌────────────┐   ┌────────────┐  ┌──────────────┐
    │  Payroll   │   │Performance │  │ Notification │
    │  Engine    │   │  Module    │  │   Service    │
    └────────────┘   └────────────┘  └──────────────┘
    ┌────────────────────────────────────────────────┐
    │          AI Workforce Intelligence             │
    │  Attrition Model │ Skill Gap │ Engagement     │
    └──────┬─────────────────────────────────────────┘
           │
    ┌──────┴──────────────────────────────────┐
    │  PostgreSQL 17 │ Redis 7 │ Kafka 3.7   │
    │  Elasticsearch │ Keycloak 25           │
    └─────────────────────────────────────────┘
```

---

## 📂 Monorepo Structure

```
nexushr/
├── backend/
│   ├── pom.xml                    # Parent POM (Spring Boot 3.3, Java 21)
│   ├── common-lib/                # Shared DTOs, exceptions, audit base entity
│   ├── auth-service/              # JWT + MFA + Keycloak integration
│   ├── employee-service/          # Employee CRUD, org chart, lifecycle
│   ├── attendance-service/        # Clock-in/out, leave management, WebSocket
│   ├── payroll-service/           # Gross-to-net, TDS, PF/ESI, PDF payslips
│   ├── performance-service/       # OKR goals, 360° reviews, calibration
│   ├── ai-service/                # Attrition prediction, skill gap analysis
│   ├── notification-service/      # Email, SMS, WebSocket, push
│   └── api-gateway/               # Spring Cloud Gateway, rate limiting
├── frontend/
│   ├── src/
│   │   ├── components/            # Layout, Sidebar, Header
│   │   ├── pages/                 # Dashboard, Employees, Attendance,
│   │   │                          # Payroll, Performance, AI Insights
│   │   ├── store/                 # Zustand state management
│   │   ├── api/                   # Axios client with JWT interceptors
│   │   └── index.css              # Design system
│   └── package.json
├── docs/
│   └── adr/                       # Architecture Decision Records
├── .github/workflows/ci.yml       # CI/CD pipeline
├── docker-compose.yml             # Local dev stack
├── NexusHR.postman_collection.json # API collection (30+ endpoints)
├── init-databases.sql             # Database initialization
└── README.md
```

---

## 🛠️ Tech Stack

### Backend
| Layer | Technology | Version |
|-------|-----------|---------|
| Runtime | Java (LTS) | 21 |
| Framework | Spring Boot | 3.3 |
| Security | Spring Security 6 + JWT (RS256) | 6.x |
| ORM | Spring Data JPA + Hibernate 6 | 3.x |
| Database | PostgreSQL | 17 |
| Migrations | Flyway | 10.x |
| Caching | Redis (Redisson) | 7+ |
| Messaging | Apache Kafka | 3.7 |
| Search | Elasticsearch | 8.15 |
| API Docs | SpringDoc OpenAPI | 2.5 |
| AI/ML | Custom RF Engine + SHAP explainability | — |

### Frontend
| Layer | Technology | Version |
|-------|-----------|---------|
| Framework | React + TypeScript | 19 |
| Build | Vite | 6+ |
| State | Zustand + TanStack Query v5 | — |
| Charts | Recharts | 3.x |
| Routing | React Router | 7 |
| Icons | Lucide React | — |

### Infrastructure
| Layer | Technology |
|-------|-----------|
| Containers | Docker 27 (Multi-stage, non-root) |
| CI/CD | GitHub Actions |
| Orchestration | Kubernetes 1.31 + Helm (planned) |
| Monitoring | Prometheus + Grafana + Loki |

---

## 🚀 Quick Start

### Prerequisites
- Java 21+ (JDK)
- Node.js 20+
- Docker & Docker Compose
- Maven 3.9+

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `POSTGRES_USER` | nexushr | Database username |
| `POSTGRES_PASSWORD` | nexushr_secret | Database password |
| `POSTGRES_DB` | nexushr_main | Default database |
| `KEYCLOAK_ADMIN` | admin | Keycloak admin username |
| `KEYCLOAK_ADMIN_PASSWORD` | admin | Keycloak admin password |

### 1. Start Infrastructure

```bash
docker-compose up -d
```

This starts: PostgreSQL 17, Redis 7, Kafka, Elasticsearch, Keycloak

### 2. Build & Run Backend

```bash
cd backend
mvn clean install -DskipTests

# Start individual services
cd auth-service && mvn spring-boot:run
cd employee-service && mvn spring-boot:run
cd payroll-service && mvn spring-boot:run
cd performance-service && mvn spring-boot:run
cd ai-service && mvn spring-boot:run
```

### 3. Start Frontend

```bash
cd frontend
npm install
npm run dev
```

Open: **http://localhost:5173**

Demo credentials: `admin@nexushr.com` / `demo1234`

---

## 📋 Module Completion Status

### ✅ Week 1 – Core Backend, Auth & Employee Module
- [x] Maven multi-module monorepo architecture (9 services)
- [x] Spring Boot 3.3 parent POM with Java 21
- [x] Docker Compose local dev stack (PostgreSQL, Redis, Kafka, ES, Keycloak)
- [x] Authentication service (JWT RS256 + Argon2id + MFA + account locking)
- [x] Auth endpoints: register, login, refresh, logout
- [x] Employee entity with full lifecycle states
- [x] Flyway V1 migration: employees, departments, attendance, leave, audit
- [x] Employee CRUD REST + org chart (recursive CTE)
- [x] Spring Security 6 configuration
- [x] Attendance domain model + clock-in/out + WebSocket
- [x] Leave management with approval workflow
- [x] OpenAPI/Swagger documentation

### ✅ Week 2 – Payroll, Performance & React Frontend
- [x] Payroll calculation engine (gross-to-net)
- [x] Indian tax engine (Old/New regime TDS slabs, Section 87A rebate)
- [x] PF (12%+12%), ESI (0.75%+3.25%), Professional Tax
- [x] Payroll run orchestration (virtual threads, parallel processing)
- [x] Payslip model with full earnings/deductions breakdown
- [x] Performance management (OKR framework)
- [x] Review cycle state machine (DRAFT→SUBMITTED→CALIBRATION→PUBLISHED)
- [x] 360° feedback + calibration endpoints
- [x] React 19 + TypeScript + Vite frontend
- [x] Professional UI design system
- [x] Zustand + TanStack Query state management
- [x] 8 pages: Dashboard, Employees, Attendance, Payroll, Performance, AI Insights, Settings, Login

### ✅ Week 3 – AI Intelligence & Platform Features
- [x] AI Attrition Prediction Engine (Random Forest-inspired, 6 features)
- [x] SHAP explainability (per-prediction feature importance)
- [x] Risk classification (LOW/MEDIUM/HIGH/CRITICAL)
- [x] AI-generated retention recommendations in plain English
- [x] Skill gap analysis (NLP-categorized, priority-ranked)
- [x] Engagement scoring composite model
- [x] Batch prediction with virtual thread parallelism
- [x] AI Insights dashboard (risk distribution, dept analysis, skill gaps)
- [x] Notification service scaffolding
- [x] API Gateway with Spring Cloud Gateway routes
- [x] Global exception handling (common-lib)
- [x] Immutable audit log schema (SHA-256 hash chaining)

### ✅ Week 4 – DevOps, Testing & Documentation
- [x] Multi-stage Dockerfiles (backend: Temurin JDK→JRE, frontend: Node→Nginx)
- [x] Non-root container user, JVM container tuning
- [x] Health checks on all containers
- [x] GitHub Actions CI/CD pipeline (lint→test→build→Docker→security scan)
- [x] Unit tests: AuthService, TaxCalculationEngine, AttritionPredictionEngine
- [x] Architecture Decision Records (5 ADRs)
- [x] Postman collection (30+ endpoints with auto-token extraction)
- [x] Comprehensive README with architecture diagrams

---

## 🤖 AI Workforce Intelligence

### Attrition Prediction Model
- **Algorithm:** Random Forest-inspired weighted scoring
- **Features:** Tenure, performance rating, salary change %, absence days, promotion lag, engagement score
- **Explainability:** SHAP values per prediction — each factor's contribution
- **Output:** Risk score (0-1), risk level, top 3 factors, plain-English recommendation
- **Model Version:** rf-v1.2.0-2026 | **Target AUC:** ≥ 0.82

### Skill Gap Analysis
- NLP-based skill categorization (Technical, Leadership, Communication, Domain)
- Gap scoring with priority ranking (LOW/MEDIUM/HIGH/CRITICAL)
- AI-generated learning recommendations per skill

---

## 🔐 Security

- **Authentication:** Keycloak OIDC + JWT RS256 + Argon2id password hashing
- **Authorization:** RBAC + ABAC with @PreAuthorize
- **API Security:** CORS, CSRF, rate limiting, Bean Validation
- **Encryption:** TLS 1.3 + AES-256 at rest
- **Account Protection:** Auto-lock after 5 failed attempts (30-min lockout)
- **Audit:** Immutable append-only log with SHA-256 hash chaining

---

## 🧪 Testing

```bash
# Run all backend tests
cd backend && mvn test

# Run specific service tests
cd backend/payroll-service && mvn test
cd backend/auth-service && mvn test
cd backend/ai-service && mvn test
```

---

## 📄 License

Internal – Amdox Technologies Engineering Division

---

*Crafted with precision and modern engineering principles*
*Amdox Technologies · Java Full-Stack Domain · April 2026*
