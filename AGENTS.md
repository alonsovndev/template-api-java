# AGENTS.md

<!-- ai-repo-setup:generated:start -->

## Overview

Spring Boot 4 (Java 17, Maven) API template using feature-based Clean Architecture, DDD, explicit composition, constructor DI, and use cases. Follows the [engineering handbook](https://github.com/alonsovndev/engineering-handbook) (backend, testing, git). Deployment profile: chosen per project (see `docs/deployment.md`).

## Commands

| Task                        | Command                                   |
| --------------------------- | ----------------------------------------- |
| Install (enables Git hooks) | `make install`                            |
| Run locally (memory mode)   | `make dev`                                |
| Tests + 80% coverage gate   | `make test` (`./mvnw verify`)             |
| Lint (Checkstyle, Spotless) | `make lint`                               |
| Format                      | `make format`                             |
| Everything CI runs          | `make ci`                                 |
| Single test class           | `./mvnw test -Dtest=HealthEndpointTest`   |
| PostgreSQL tests (Docker)   | `make test-postgres`                      |
| Apply migrations            | `make migrate` (needs `PERSISTENCE_DRIVER=postgresql` + `POSTGRES_*`) |
| Docker image                | `make docker`                             |

Run `make ci` before declaring work done.

## Structure

```text
src/main/java/com/alonsovndev/template/
  composition/        composition root: the only place that picks adapters for ports
  config/             typed, validated settings (AppProperties), CORS
  features/<f>/       domain/ application/ infrastructure/ presentation/ (create only the layers needed)
  shared/             infrastructure (PostgreSQL, Flyway) and presentation (problem+json errors)
src/main/resources/   application.yml + application-{local,dev,test,container,prod}.yml, db/migration/
src/test/.../support/items/   test-only DDD example (entity, port, use cases, memory + JDBC adapters)
docs/                 architecture, development, testing, deployment, adr/
```

## Architecture

- `domain` and `application` are plain Java: no Spring, Jakarta, Jackson, JDBC. No `@Component`/`@Service` on them.
- Use cases take a command (when they need input) and return a result record. `presentation` uses application results only, never domain types.
- Every use case and adapter is wired by an explicit `@Bean` in `composition/`. Features never import `composition`.
- Repositories are ports with explicit methods; adapters map rows to entities explicitly. No generic base repository.
- Rules are enforced by `ArchitectureRulesTest` (ArchUnit). Do not weaken a rule to pass.
- Migrations are Flyway SQL in `db/migration`, applied as an explicit step, never at startup.

Details: `docs/architecture.md`.

## Testing

- Unit tests for domain and use cases need no Spring context. Controllers and wiring use `@SpringBootTest` + MockMvc.
- Adapter behaviour is checked by one contract class run against every implementation (`ItemRepositoryContract`).
- PostgreSQL tests are tagged `postgres` and run only through `make test-postgres` against a disposable database.
- 80% line coverage is enforced by `make test` (JaCoCo). Do not lower it or add excludes to pass. Changing it needs an ADR.

Details: `docs/testing.md`.

## Git

- Conventional Commits, enforced by `.githooks/commit-msg` and the PR-title check. No AI or tool co-author trailers.
- Branch from `dev`: `feature/*`, `fix/*`, `chore/*`. PRs target `dev`. Never commit or push on `main` or `dev`.
- Do not commit, push, or bypass hooks (`--no-verify`) unless asked.

## Security

- Never commit `.env` files or secrets. `.githooks/check-secrets.sh` blocks the obvious cases.
- Configuration errors name fields, never values. Do not log credentials or JDBC URLs with passwords.
- `prod` reads process variables only (no `.env`). API docs are exposed only in `local` and `container`.
- Do not add dependencies, call external services, or trigger pipelines without approval.

## Definition of done

`make ci` passes, tests cover the changed behaviour, docs are updated, no new warnings. Do not delete, skip, or weaken tests, and do not disable lint, hooks, ArchUnit rules, or the coverage gate.

## Deployment

TBD. `docs/deployment.md` lists the options; CI (`.github/workflows/ci.yml`) only verifies.

## Known gaps

- Secrets Manager credential loading (`POSTGRES_CREDENTIALS_SECRET_ARN`), structured logging with request IDs, Sentry and OpenTelemetry, and deployment automation are not implemented.
- CI runs `make ci`, which excludes the `postgres`-tagged tests; they run only through `make test-postgres`.
- The workflow callers are pinned to a `github-workflows` commit SHA; move to `v1` once that tag exists.

<!-- ai-repo-setup:generated:end -->

<!-- ai-repo-setup:custom:start -->
<!-- Add project-specific rules below this line. Preserved across re-runs. -->
<!-- ai-repo-setup:custom:end -->
