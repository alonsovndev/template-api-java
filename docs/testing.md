# Testing

| Layer | Tool | Where |
| --- | --- | --- |
| Domain, use cases, config | JUnit 5 + AssertJ + Mockito | `src/test/.../unit` |
| HTTP and wiring | `@SpringBootTest` + MockMvc | `src/test/.../integration` |
| Architecture | ArchUnit | `src/test/.../architecture` |
| PostgreSQL adapters and migrations | JUnit tag `postgres`, disposable Docker database | `integration/Postgres*Test` |

## Rules

- Domain and application tests do not start Spring.
- Each repository adapter runs the same `ItemRepositoryContract`; add new adapters to it instead of copying assertions.
- The `items` example lives under `src/test/.../support/items` and its migration under `src/test/resources/db/fixture`; production code and Flyway's production location never see them (`ArchitectureRules.productionDoesNotUseTestSupport`).
- Never point PostgreSQL tests at a real database: `PostgresTestDatabase` drops the `public` schema and refuses anything but the throwaway identity created by `scripts/test-postgres.sh`.

## Coverage gate

`make test` runs `./mvnw verify`; JaCoCo fails the build below **80% lines** (`jacoco.line.minimum` in `pom.xml`). Only `TemplateApplication` is excluded. Do not lower the threshold or widen the excludes to get a green run; it is governed by the handbook's coverage-gate ADR.

## Running

```bash
make test
./mvnw test -Dtest=ArchitectureRulesTest
make test-postgres        # needs Docker
./scripts/test-hooks.sh   # verifies the commit-msg and secrets hooks
```
