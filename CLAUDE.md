# Example Service (Spring Boot)

## Overview
Standalone microservice archetype for the SaaS POS system. Demonstrates the standard patterns for building new services: hexagonal architecture, DDD, multi-tenant per-database isolation, gateway-trusted JWT authentication, and observability.

## Tech Stack
- **Framework:** Spring Boot 4.0.3
- **Java:** 25
- **Database:** PostgreSQL (per-tenant database, per-service schema) + Liquibase migrations
- **Cache:** Redis
- **Messaging:** Kafka
- **Security:** Gateway-trusted JWT (Base64 decode only, no signature validation)
- **Observability:** OpenTelemetry, Sentry, Micrometer
- **Communication:** REST + OpenFeign (header forwarding: Authorization, Accept-Language)

## Architecture
**Hexagonal Architecture (Ports & Adapters) with DDD**

**Package:** `com.apus.salehub`
**Main Class:** `ExampleApplication`

```
com.apus.salehub/
├── adapter/
│   ├── in/
│   │   ├── rest/             # StockController, GlobalExceptionHandler, DTOs
│   │   └── messaging/        # StockReservationConsumer (Kafka consumer)
│   └── out/
│       ├── persistence/      # StockPersistenceAdapter, StockMovementPersistenceAdapter, etc.
│       └── messaging/        # KafkaEventPublisher
├── application/
│   ├── port/in/stock/        # AdjustStockUseCase, CheckStockUseCase, RecordMovementUseCase
│   ├── port/in/messaging/    # OrderPlacedEventHandler
│   ├── port/out/persistence/ # StockRepository, StockMovementRepository, ProductRepository, WarehouseRepository
│   ├── port/out/messaging/   # EventPublisher
│   └── service/              # AdjustStockService, CheckStockService, OrderPlacedEventService
├── domain/
│   ├── model/
│   │   ├── product/          # Product, ProductId
│   │   ├── stock/            # Stock, StockId, StockMovement, StockMovementId, StockMovementType
│   │   └── warehouse/        # Warehouse, WarehouseId
│   ├── valueobject/          # Quantity, Money, Address
│   ├── event/                # StockUpdatedEvent, LowStockAlertEvent, StockMovementRecordedEvent
│   ├── service/              # InventoryDomainService
│   └── exception/            # DomainException
└── config/
    ├── security/
    │   ├── SecurityConfiguration     # SecurityFilterChain + GatewayJwtAuthenticationFilter
    │   ├── JwtPayload                # Typed record for decoded JWT claims
    │   └── JwtUtils                  # Static JWT Base64 decode utility
    ├── tenant/
    │   ├── TenantContextHolder       # ThreadLocal tenant context
    │   ├── TenantContext             # Immutable tenant/system/platform context
    │   ├── TenantContextMissingException
    │   ├── CorrelationContext         # MDC-based correlation ID
    │   ├── GatewayAuthProperties     # app.auth.* config record
    │   ├── AuthMode                   # GATEWAY / DIRECT / AUTO enum
    │   ├── TenantDataSourceConfiguration  # Multi-tenant AbstractRoutingDataSource
    │   ├── TenantRoutingDataSource        # Per-tenant HikariCP pool with schema routing
    │   ├── TenantDataSourceProperties     # app.tenant.datasource.* config
    │   ├── kafka/                     # KafkaErrorHandler, KafkaMetrics, KafkaHeaderKeys
    │   │   ├── TenantConsumerInterceptor  # Extracts tenant from Kafka headers
    │   │   └── TenantProducerInterceptor  # Injects tenant into Kafka headers
    │   └── event/
    │       └── DomainEventEnvelope    # Generic event wrapper with metadata
    ├── BeansConfig                    # Domain service + JsonMapper beans
    ├── KafkaConfiguration             # Kafka producer/consumer with tenant interceptors
    ├── FeignConfiguration             # Header forwarding (Authorization, Accept-Language)
    └── LocaleConfiguration            # i18n + UTC timezone
```

## Security (Gateway-Trusted JWT)

The API gateway (APISIX) handles:
- JWT signature validation (Keycloak RS256)
- Authentication (token expiry, issuer, audience)
- Route-level RBAC

The service:
- Verifies `X-Gateway-Auth` header (proves request passed gateway)
- Base64-decodes JWT payload via `JwtUtils.decode()` → `JwtPayload` record
- Extracts: tenantId, userId (sub), roles (realm_access.roles), orgId, scope, etc.
- Sets `TenantContextHolder` with tenantId
- Populates `SecurityContext` with roles for `@PreAuthorize`
- Forwards `Authorization` and `Accept-Language` headers via OpenFeign

**No OAuth2 Resource Server dependency** — `spring-boot-starter-security-oauth2-resource-server` is not used.

## Multi-Tenancy (Per-Tenant Database, Per-Service Schema)

- **Database isolation:** Each tenant has its own PostgreSQL database (e.g., `tenant_123`)
- **Schema isolation:** Each service uses its own schema within the tenant database (e.g., schema `example`)
- **Provisioning:** Databases and schemas are pre-provisioned by infrastructure
- **Routing:** `TenantRoutingDataSource` extends `AbstractRoutingDataSource`, resolves tenant via `TenantContextHolder`
- **Connection:** `jdbc:postgresql://{host}:{port}/tenant_{tenantId}?currentSchema={service_schema}`
- **Pool:** HikariCP per-tenant, cached in `ConcurrentHashMap`, lazily created on first request
- **SQL:** No `tenant_id` column — isolation at database level. Tables use unqualified names (rely on `search_path`/`currentSchema`).

## ID Convention
All entity IDs use `Long` (PostgreSQL `bigint` with `autoIncrement`). This includes domain model IDs (`StockId`, `ProductId`, `WarehouseId`, `StockMovementId`) and tenant IDs. No UUIDs for database records.

## Domain Models
- **Stock** (aggregate root): StockId(Long), quantity, reservedQuantity, minimumLevel
- **StockMovement**: StockMovementId(Long), StockMovementType (IN, OUT, TRANSFER, ADJUSTMENT)
- **Product**: ProductId(Long), sku, name, unitPrice, category
- **Warehouse**: WarehouseId(Long), code, name, address
- **Value Objects**: Quantity, Money, Address

## Key Files
| Purpose | Location |
|---------|----------|
| REST Controller | `adapter/in/rest/controller/StockController.java` |
| Use Case | `application/port/in/stock/AdjustStockUseCase.java` |
| Service Impl | `application/service/AdjustStockService.java` |
| Domain Model | `domain/model/stock/Stock.java` |
| Repository Port | `application/port/out/persistence/StockRepository.java` |
| Persistence Adapter | `adapter/out/persistence/StockPersistenceAdapter.java` |
| Security Config | `config/security/SecurityConfiguration.java` |
| JWT Utility | `config/security/JwtUtils.java` |
| Tenant DataSource | `config/tenant/TenantDataSourceConfiguration.java` |
| Tenant Context | `config/tenant/TenantContextHolder.java` |
| Kafka Config | `config/KafkaConfiguration.java` |
| Feign Config | `config/FeignConfiguration.java` |
| Bean Config | `config/BeansConfig.java` |
| Liquibase | `resources/db/changelog/db.changelog-master.yaml` |

## API Endpoints
- `GET /api/v1/stocks/product/{productId}/warehouse/{warehouseId}` - Check stock (requires CASHIER role)
- `POST /api/v1/stocks/adjust` - Adjust stock level (requires MANAGER role, validated request body)

## Observability
- **Prometheus**: `/actuator/prometheus` exposed (Micrometer Registry Prometheus)
- **Health**: `/actuator/health` with `db`, `kafka`, `redis` indicators; readiness/liveness probes enabled
- **Tracing**: OpenTelemetry via `spring-boot-starter-opentelemetry`; sampling probability configurable via `TRACING_SAMPLING_PROBABILITY` (default 0.1); OTLP export via `OTEL_EXPORTER_OTLP_ENDPOINT`
- **Sentry**: DSN via `SENTRY_DSN` env var; environment via `SENTRY_ENVIRONMENT`
- **Logging**: Structured JSON (Logback `JsonEncoder`) for non-local profiles; console pattern includes `traceId`, `spanId`, `correlationId`, `tenantId`

## Configuration
- **App name:** example-service
- **Port:** 8088
- **Database:** per-tenant (tenant_{tenantId}), schema: example
- **Profiles:** `local` for development (disables Vault, Sentry, Kafka/Redis health checks)
- **i18n:** English (en), Vietnamese (vi)
- **Timezone:** UTC

## Commands
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
./mvnw compile
./mvnw test
```
---

---

## BMad Workflow

### Feature Documents
- Format: docs/features/{feature-name}/
- Each feature has: srs-{name}.md, prd-{name}.md
# Thêm feature vào đây khi copy vào:
# - {feature-name}: docs/features/{feature-name}/

### Output Paths
- Architecture → docs/architecture.md (APPEND only, never overwrite)
- Backlog → docs/product-backlog.md (APPEND only, never overwrite)
- Stories → stories/

### Architect Rule
- Only read docs for the current feature being worked on
- Always APPEND to docs/architecture.md with section header per feature
- Replace package com.apus.salehub → com.apus.warehouse
- Replace schema name example → warehouse

### Agent Rules
- Do NOT propose full rewrite
- Do NOT use UUID — dùng Long (bigint autoIncrement)
- Do NOT add OAuth2 Resource Server dependency
- Follow patterns in this file as the reference archetype
```

---