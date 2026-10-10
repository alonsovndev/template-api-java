# Architecture

Feature-based Clean Architecture. Each feature owns the layers it needs under `features/<feature>/`; Health has no `domain` because it has no business invariants.

```text
presentation ──> application ──> domain
                      ^
infrastructure ───────┘          (adapters implement application/domain ports)
composition ──> everything       (wires ports to adapters; features never import it)
```

| Layer            | Contains                                             | May use                          | Must not use                                   |
| ---------------- | ---------------------------------------------------- | -------------------------------- | ---------------------------------------------- |
| `domain`         | entities, value objects, invariants, repository ports | plain Java                       | Spring, Jakarta, Jackson, JDBC, other layers   |
| `application`    | use cases, commands, result records, ports           | `domain`                         | Spring, Jakarta, Jackson, JDBC, `infrastructure`, `presentation`, `config` |
| `infrastructure` | adapters (JDBC, probes), mapping                     | `application`, `domain`, Spring  | `presentation`                                 |
| `presentation`   | controllers, HTTP schemas                            | `application` results, Spring    | `domain`                                       |

`ArchitectureRulesTest` enforces this with ArchUnit, plus meta-tests that run the same rules against deliberately bad fixture classes so a guard that stops biting fails the build.

## Composition and DI

Spring is a detail of the composition root, not of the business code. Use cases and adapters are plain classes with constructor injection; `composition/ApplicationConfiguration` declares one `@Bean` per use case and selects adapters by `app.persistence.driver` (`memory` or `postgresql`). Only controllers, the exception handler, and configuration classes are component-scanned.

## Adding a feature

1. `domain`: entities with invariants in the constructor, and a repository port.
2. `application`: one class per use case (`CreateX.execute(command) -> result`).
3. `infrastructure`: adapters with explicit mapping, e.g. `JdbcXRepository` (see the test-only `support/items` example).
4. `presentation`: thin controller plus request/response records.
5. `composition`: add the `@Bean`s; write a Flyway migration in `db/migration`; add tests, including the repository contract for each adapter.

## Persistence

JDBC (`JdbcClient`) rather than JPA, so entities stay framework-free and mapping is explicit (ADR-003). Flyway migrations are applied as a separate step (ADR-002). In `memory` mode no DataSource exists; the application starts and is ready without a business datastore.

## Errors

All errors are RFC 9457 `application/problem+json` (ADR-004). Unexpected failures return a generic detail and log the cause server-side.

## Health

- `GET /health`: liveness, no dependency checks.
- `GET /health/ready`: 200 `{"status":"ready"}` or 503 `{"status":"not_ready"}` from a `SELECT 1` (2 s timeout) in PostgreSQL mode. A database outage never fails liveness.
