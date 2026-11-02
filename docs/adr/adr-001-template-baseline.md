# ADR-001: Template baseline

- **Status**: Accepted
- **Date**: 2026-10-10

## Context

The handbook leaves Java and Spring Boot standards TBD. This template mirrors `template-api-python` (profiles, health contract, test strategy, container) for JVM-based enterprise projects.

## Decision

Spring Boot 4.1 on Java 17 with Maven (wrapper committed). Build checks: Spotless (google-java-format), Checkstyle, JUnit 5, ArchUnit, and a JaCoCo 80% line gate. `make ci` mirrors CI. Package root `com.alonsovndev.template`. Spring Boot's DataSource and Flyway auto-configuration are not used; the composition root builds them only when `app.persistence.driver=postgresql`.

## Consequences

### Positive

- Same operational contract as the Python template: profiles, `/health`, `/health/ready`, non-root container, Lambda target.
- Memory mode needs no database.

### Negative

- Boot 4 is new; some libraries and test annotations differ from Boot 3 guides.
- Java 17 is the floor; Java 21 features are unavailable until the baseline moves.

## Alternatives Considered

1. **Gradle** — more flexible, but heavier and not installed on the reference machine.
2. **Spring Boot 3.5** — more documentation, but its open-source support window has ended.
