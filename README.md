# MarkMan Business System

Modular monolith business-management platform for Philippine SMBs.
First commercial configuration: **MarkMan POS** — "Sell. Track. Manage."

## Status

Phase 1 (project scaffold) — no domain features yet. See `ARCHITECTURE.md`
(added once Phase 0 notes are written up) for the full V1 scope and module
boundaries.

## Stack

Java 25 · Spring Boot 4 · Spring Modulith · Spring Security · Spring Data
JPA/Hibernate · Thymeleaf + HTMX · PostgreSQL · Flyway · Maven · JUnit /
Testcontainers / ArchUnit · Docker.

## Running locally

Requirements: JDK 25, Docker Desktop (or equivalent) running.

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

Spring Boot's Docker Compose support starts `compose.yaml` (Postgres)
automatically when the `dev` profile is active — no manual `docker compose
up` needed.

## Running tests

```bash
./mvnw verify
```

This runs unit tests, the Spring Modulith module-boundary check
(`ModularityTests`), ArchUnit rules (`ArchitectureTests`), and a
Testcontainers-backed smoke test that boots the app against a real,
disposable PostgreSQL instance with Flyway migrations applied from scratch
(`SmokeTest`). Docker must be running for the last of these.

## Module layout

```
com.markman
├─ core        shared kernel (tenant, organization, user, role, security,
│               module config, audit) — OPEN, every module may depend on it
├─ catalog     categories, catalog items (PRODUCT / SERVICE)
├─ inventory   stock movement ledger + balances
├─ pos         shifts, cart, sale, payment, refund
├─ employee    staff management
└─ reporting   read-only cross-module reporting queries (documented exception
                to normal module boundaries — see reporting/package-info.java)
```

Boundaries are declared in each module's `package-info.java` and enforced by
`ModularityTests`.
