# Deployment

No profile is deployed or validated on a real account. The container is the artifact for every profile.

| Profile | Target | Status |
| --- | --- | --- |
| static | not applicable (API template) | n/a |
| lean | AWS Lambda via Lambda Web Adapter + Supabase PostgreSQL | Dockerfile target built; not run on Lambda |
| enterprise | ECS Fargate behind an ALB + RDS PostgreSQL | image builds; not deployed |

## Build notes

- Default image: `docker build .` (no adapter). Lambda image: `docker build --target lambda .`; the adapter only runs inside Lambda, so a local `docker run` exercises the app, not the adapter. JVM cold starts on Lambda are slow; measure before choosing `lean`.
- Lambda and poolers need `POSTGRES_POOL_MODE=null`. Credentials arrive as `POSTGRES_CREDENTIALS_JSON` (e.g. injected from Secrets Manager by ECS). Reading a secret by ARN inside the app is TBD.
- ALB/ECS target health uses `/health`; container and Lambda readiness use `/health/ready`, so a database outage does not recycle tasks.
- Set `APP_ENV=dev` or `prod` outside local Compose: API docs are exposed only in `local` and `container`.
- Run `--app.migrate=true --spring.main.web-application-type=none` as a one-off task before rolling out a new version, against a direct or session-mode database connection (not a transaction pooler).

## Rules for any target

No long-lived cloud keys (GitHub OIDC), no secrets in images or environment files, run as non-root, and review the Terraform plan before apply.
