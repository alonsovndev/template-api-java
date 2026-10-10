# ADR-002: Flyway, applied as an explicit step

- **Status**: Accepted
- **Date**: 2026-10-10

## Context

Handbook ADR-003 chose Alembic for Python and left Java migrations TBD.

## Decision

Flyway with plain SQL under `db/migration`, with an empty `V1` baseline (no production business tables). Migrations run as `--app.migrate=true --spring.main.web-application-type=none`, never when the server starts. Test-only migrations live in `src/test/resources/db/fixture` and are added to Flyway's locations only by tests.

## Consequences

### Positive

- Matches the Alembic model: explicit, ordered, reviewable SQL, and rollouts that do not race on schema changes.

### Negative

- Operators must run the migrate step (Compose does); forgetting it leaves the schema behind.

## Alternatives Considered

1. **Liquibase** — more abstraction than a SQL-first team needs.
2. **Flyway on startup** — simpler, but concurrent task starts race and a failed migration blocks every instance.
