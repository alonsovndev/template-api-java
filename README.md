# template-api-java

Spring Boot 4 (Java 17, Maven) API template with feature-based Clean Architecture, DDD, explicit composition, constructor DI, and use cases. Java counterpart of `template-api-python`.

Included: typed per-profile configuration, `/health` and `/health/ready`, RFC 9457 errors, optional PostgreSQL with Flyway, ArchUnit layer guards, 80% coverage gate, non-root Docker image with a Lambda Web Adapter target, Compose, Git hooks, and agent context (`AGENTS.md`). No authentication or business endpoints; the DDD example is test-only.

## Local startup

```bash
make install   # enables Git hooks
make dev       # memory mode: http://localhost:8080/health
```

API docs: `http://localhost:8080/swagger-ui.html` (enabled only in `local` and `container`).

## Configuration

`APP_ENV` selects the profile (`local`, `dev`, `test`, `container`, `prod`); values come from process variables and, in `local`, `dev`, and `container`, an optional `.env`. See `.env.example` and [docs/development.md](docs/development.md) for every variable. Errors name fields, never values.

## Docker

```bash
cp .env.example .env        # set POSTGRES_PASSWORD
docker compose up --build   # postgres -> migrate (one-off) -> app on :8080
docker build --target lambda .   # AWS Lambda Web Adapter image
```

## Deployment targets

`lean` (Lambda + Supabase) and `enterprise` (ECS + RDS) are described in [docs/deployment.md](docs/deployment.md). Nothing is deployed or validated on a real account.

## Migrations

Flyway SQL in `src/main/resources/db/migration`, applied with `make migrate` or the Compose `migrate` service, never at startup.

## Architecture and adding a feature

```text
features/<feature>/{domain,application,infrastructure,presentation}
composition/   explicit @Bean wiring (the only place that chooses adapters)
```

`domain` and `application` are plain Java; `ArchitectureRulesTest` enforces the dependency rules. Steps for a new feature: [docs/architecture.md](docs/architecture.md).

## Verification

```bash
make ci             # Spotless, Checkstyle, tests, ArchUnit, 80% JaCoCo gate
make test-postgres  # PostgreSQL adapters and migrations (needs Docker)
./scripts/test-hooks.sh
docker build .
```

More: [docs/testing.md](docs/testing.md), [docs/adr](docs/adr), [CONTRIBUTING.md](CONTRIBUTING.md).
