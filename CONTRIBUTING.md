# Contributing

1. Branch from `dev`: `feature/<short-desc>`, `fix/<short-desc>`, or `chore/<short-desc>`.
2. Make the change with tests. Follow [AGENTS.md](AGENTS.md) and the [engineering handbook](https://github.com/alonsovndev/engineering-handbook).
3. Run `make install` once (enables the Git hooks), then `make ci`. It runs what CI runs.
4. Commit with [Conventional Commits](https://www.conventionalcommits.org/), for example `feat(health): add readiness probe`. The `commit-msg` hook rejects other formats.
5. Open a pull request into `dev`. The title follows the same format.

Do not bypass hooks, lower the coverage threshold, or add dependencies without stating why.
