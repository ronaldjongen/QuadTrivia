# Architecture Notes

## Goals

- Keep the solution compact and easy to review.
- Ensure the backend owns all answer validation.
- Keep frontend and backend under one origin for session auth.

## Key Decisions

1. Session-based authentication
- Spring Security with server-side session context.
- Frontend sends credentials with cookies (`withCredentials: true`).

2. Answer validation on backend
- Frontend receives question/option IDs.
- Backend stores correct mappings in quiz sessions and computes the score.
- Result payload does not expose correct option IDs.

3. Runtime topology
- `frontend` and `backend` containers behind an `nginx` reverse proxy.
- Single public origin simplifies browser cookie behavior and API routing.

4. Caching and resilience
- Open Trivia API client supports fallback to local cache.
- Quiz sessions have TTL cleanup and are removed after submission.

## Testing Strategy

1. Backend
- Unit tests for service/client/controller behavior.
- Integration test for authenticated flow (`login -> questions -> checkanswers`).

2. Frontend
- Unit/integration tests for stores, views, and router guard.
- Tests focus on behavioral requirements, not implementation details.

3. System smoke
- `scripts/e2e-smoke.sh` verifies login, session continuity, quiz retrieval, answer submit, and response contract at HTTP level.
