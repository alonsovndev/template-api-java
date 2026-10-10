#!/usr/bin/env bash
# Runs the tests tagged "postgres" against a disposable PostgreSQL started with Docker Compose.
set -euo pipefail

cd "$(dirname "$0")/.."

if [ ! -S /var/run/docker.sock ] && [ -z "${DOCKER_HOST:-}" ] && ! docker info >/dev/null 2>&1; then
  echo "Docker is required for the PostgreSQL tests." >&2
  exit 1
fi

suffix="$(od -An -N6 -tx1 /dev/urandom | tr -d ' \n')"
export TEMPLATE_TEST_PROJECT="template-api-test-${suffix}"
TEMPLATE_TEST_PASSWORD="$(od -An -N16 -tx1 /dev/urandom | tr -d ' \n')"
export TEMPLATE_TEST_PASSWORD

compose=(docker compose -p "$TEMPLATE_TEST_PROJECT" -f compose.test.yml)
cleanup() { "${compose[@]}" down --volumes --remove-orphans >/dev/null 2>&1 || true; }
trap cleanup EXIT

"${compose[@]}" up --detach --wait postgres-test

port="$("${compose[@]}" port postgres-test 5432 | awk -F: '{print $NF}')"
published="$("${compose[@]}" port postgres-test 5432)"
case "$published" in
  127.0.0.1:*) ;;
  *) echo "PostgreSQL is not published on loopback: $published" >&2; exit 1 ;;
esac
export TEMPLATE_TEST_POSTGRES_PORT="$port"

./mvnw -B -Ppostgres-tests test
