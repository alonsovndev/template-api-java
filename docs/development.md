# Development

Requirements: JDK 17+, Docker (only for the PostgreSQL tests and Compose). Maven is provided by `./mvnw`.

```bash
make install   # enables .githooks and resolves dependencies
make dev       # memory mode on http://localhost:8080 (APP_ENV=local, API docs at /swagger-ui.html)
make ci        # lint + tests + coverage gate
```

## Configuration

`APP_ENV` selects `application-<env>.yml`: `local`, `dev`, `test`, `container`, `prod`. Values come from process variables; `local`, `dev`, and `container` also read an optional `.env` in the working directory (process variables win). `prod` never reads `.env`.

| Variable | Default | Notes |
| --- | --- | --- |
| `APP_ENV` | `local` | invalid values fail startup |
| `PERSISTENCE_DRIVER` | `memory` (`postgresql` in dev, container, prod) | |
| `POSTGRES_HOST` / `PORT` / `USER` / `DB` / `PASSWORD` | `localhost` / `5432` / empty | host defaults to `postgres` in `container`, required in `prod` |
| `POSTGRES_CREDENTIALS_JSON` | empty | `{"username","password"}` (RDS secret format); overrides user and password |
| `POSTGRES_SSL_MODE` | `disable` | `require` encrypts without verifying the certificate (verification TBD) |
| `POSTGRES_POOL_MODE` | `queue` | `null` for transaction poolers (Supabase, PgBouncer) and Lambda: one connection shared by requests and the readiness probe, no server-side prepared statements |
| `CORS_ALLOWED_ORIGINS` | empty (CORS off) | comma-separated `scheme://host[:port]`; no wildcards or paths |
| `LOG_LEVEL` | `INFO` | |

Configuration errors list field names only, never values.

## Migrations

```bash
POSTGRES_PASSWORD=... POSTGRES_USER=app POSTGRES_DB=app make migrate
```

Migrations run as a separate process (`--app.migrate=true`, which requires `PERSISTENCE_DRIVER=postgresql` and fails otherwise), never when the server starts. The step opens its own two connections and ignores `POSTGRES_POOL_MODE`, but it still needs a direct or session-mode connection, not a transaction pooler. Compose runs them as a one-off `migrate` service before `app`.

## Docker

```bash
cp .env.example .env   # set POSTGRES_PASSWORD
docker compose up --build
```

The image runs as uid 999 and exposes 8080; the HEALTHCHECK uses `/health/ready`.
