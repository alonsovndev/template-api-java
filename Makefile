.PHONY: install dev test lint format docker migrate test-postgres ci

install:
	git config core.hooksPath .githooks
	./mvnw -B -q dependency:resolve

dev:
	./mvnw spring-boot:run

# Tests plus the 80% line coverage gate (JaCoCo, bound to verify).
test:
	./mvnw -B verify

lint:
	./mvnw -B checkstyle:check spotless:check

format:
	./mvnw -B spotless:apply

docker:
	docker build -t template-api-java .

# Applies Flyway migrations to the database in the POSTGRES_* environment. Never runs at startup.
migrate:
	PERSISTENCE_DRIVER=postgresql ./mvnw -B spring-boot:run -Dspring-boot.run.arguments="--app.migrate=true --spring.main.web-application-type=none"

# Needs Docker: runs the tests tagged "postgres" against a disposable PostgreSQL.
test-postgres:
	./scripts/test-postgres.sh

# Runs exactly what the CI workflow runs.
ci: lint test
