# ADR-003: JDBC with explicit mapping instead of JPA

- **Status**: Accepted
- **Date**: 2026-10-10

## Context

Domain entities must stay free of framework annotations, and the template should show repository ports with explicit persistence mapping.

## Decision

Adapters use Spring's `JdbcClient` and map rows to domain entities by hand. No JPA entities, no lazy loading, no generic base repository.

## Consequences

### Positive

- Entities and use cases are plain Java; adapters are small and testable against a contract.

### Negative

- More hand-written SQL than JPA. A project with heavy relational navigation may justify JPA behind the same ports.

## Alternatives Considered

1. **JPA/Hibernate with separate record classes** — flexible, but adds mapping layers and runtime behaviour this baseline does not need.
2. **Spring Data JDBC** — annotates aggregates, which couples them to the framework.
