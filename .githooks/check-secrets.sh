#!/usr/bin/env bash
# Blocks commits that stage env/key files or add likely hardcoded secrets.

RED='\033[0;31m'
NC='\033[0m'

BLOCKED_FILES=(".env" ".env.*" "secrets.json" "credentials.json" "*.pem" "*.key" "*.p12" "*.pfx")
SENSITIVE_PATTERNS=(
  "password[[:space:]]*=[[:space:]]*['\"][^'\"\$][^'\"]*['\"]"
  "api[_-]?key[[:space:]]*=[[:space:]]*['\"][^'\"]+['\"]"
  "secret[_-]?key[[:space:]]*=[[:space:]]*['\"][^'\"]+['\"]"
  "token[[:space:]]*=[[:space:]]*['\"][^'\"]+['\"]"
  "bearer[[:space:]]+[a-zA-Z0-9_-]{20,}"
  "BEGIN (RSA )?PRIVATE KEY"
  "AWS_SECRET_ACCESS_KEY"
)

staged_files=()
while IFS= read -r -d '' path; do
  staged_files+=("$path")
done < <(git diff --cached --name-only --diff-filter=ACM -z)
failed=false

for file in "${staged_files[@]}"; do
  name=$(basename "$file")
  [ "$name" = ".env.example" ] && continue
  for blocked in "${BLOCKED_FILES[@]}"; do
    # shellcheck disable=SC2053
    if [[ "$name" == $blocked ]]; then
      echo -e "${RED}Blocked file staged: $file${NC} (add it to .gitignore)" >&2
      failed=true
    fi
  done
done

for file in "${staged_files[@]}"; do
  [ -f "$file" ] || continue
  # This script lists the patterns it searches for, so scanning it would always match.
  # The hook test script carries deliberate fake secrets as test data.
  [[ "$file" == mvnw.cmd || "$file" == .githooks/check-secrets.sh || "$file" == scripts/test-hooks.sh ]] && continue
  added_lines=$(git diff --cached -U0 -- "$file" | grep '^+' | grep -v '^+++')
  for pattern in "${SENSITIVE_PATTERNS[@]}"; do
    if printf '%s\n' "$added_lines" | grep -qiE "$pattern"; then
      echo -e "${RED}Possible secret in $file (pattern: $pattern)${NC}" >&2
      failed=true
    fi
  done
done

if [ "$failed" = true ]; then
  echo "Commit aborted. Use environment variables instead of hardcoded values." >&2
  exit 1
fi
