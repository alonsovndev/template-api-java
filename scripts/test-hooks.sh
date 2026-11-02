#!/usr/bin/env bash
# Verifies the commit-msg and pre-commit hooks in a throwaway repository.
set -euo pipefail

root="$(cd "$(dirname "$0")/.." && pwd)"
work="$(mktemp -d)"
trap 'rm -rf "$work"' EXIT

git -C "$work" init -q
git -C "$work" config user.email test@example.com
git -C "$work" config user.name test
git -C "$work" config core.hooksPath "$root/.githooks"

failures=0
expect() { # expect <pass|fail> <description> <command...>
  local expectation="$1" description="$2"
  shift 2
  if "$@" >/dev/null 2>&1; then outcome=pass; else outcome=fail; fi
  if [ "$outcome" = "$expectation" ]; then echo "ok   - $description"; else echo "FAIL - $description"; failures=$((failures + 1)); fi
}

commit() { git -C "$work" commit -q -m "$1"; }

echo "readme" > "$work/README.md"
git -C "$work" add README.md
expect fail "rejects a non-conventional message" commit "update stuff"
expect pass "accepts a conventional message" commit "feat(health): add readiness"

echo 'DB_PASSWORD=hunter2' > "$work/.env"
git -C "$work" add -f .env
expect fail "blocks a staged .env file" commit "chore: add env"
git -C "$work" reset -q .env

echo 'String password = "hunter2";' > "$work/Config.java"
git -C "$work" add Config.java
expect fail "blocks a hardcoded password" commit "chore: add config"
git -C "$work" reset -q Config.java

mkdir "$work/scripts"
cp "$root"/scripts/*.sh "$work/scripts/"
git -C "$work" add scripts
expect pass "allows the repository scripts" commit "chore: add scripts"

echo 'POSTGRES_PASSWORD=' > "$work/.env.example"
git -C "$work" add .env.example
expect pass "allows .env.example" commit "docs: add env example"

exit "$failures"
