# ADR-004: RFC 9457 problem details for errors

- **Status**: Accepted
- **Date**: 2026-10-10

## Context

The handbook lists the API error schema as TBD (candidate: RFC 9457).

## Decision

Every error response is `application/problem+json` through `ApiExceptionHandler` (`ResponseEntityExceptionHandler` plus a generic 500). Unexpected errors return a fixed detail and are logged server-side. Health endpoints keep their documented `status` bodies, including the 503 readiness body.

## Consequences

### Positive

- One error shape for clients, no stack traces or internals in responses.

### Negative

- Feature-specific error types and extension members are TBD until a feature needs them.

## Alternatives Considered

1. **Custom error envelope** — nothing gained over a standard Spring supports natively.
