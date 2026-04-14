# Quad Trivia

Full-stack quiz application for the Quad assignment.

## Stack

- Backend: Java 25, Spring Boot 4, Spring Security, Gradle
- Frontend: Vue 3, TypeScript, Pinia, Vue Router, Vite
- Runtime: Docker Compose with Nginx reverse proxy

## Project Structure

- `backend`: Spring Boot API
- `quad-trivia-frontend`: Vue SPA
- `nginx/default.conf`: reverse proxy (`/api` -> backend, `/` -> frontend)

## Requirements

- Java 25
- Node.js 22+
- Docker + Docker Compose (optional, for containerized run)

## Local Development

1. Backend

```bash
cd backend
APP_DEMO_PASSWORD_HASH='<bcrypt hash>' ./gradlew bootRun
```

2. Frontend

```bash
cd quad-trivia-frontend
npm install
npm run dev
```

Frontend uses `/api` as base URL and expects the backend on the same origin in Docker/Nginx mode.

## Authentication Flow

- The backend uses session-based authentication plus CSRF protection for state-changing requests.
- The frontend bootstraps `GET /api/auth/csrf` before checking `GET /api/auth/me`.
- `POST /api/auth/login`, `POST /api/auth/logout`, and `POST /api/checkanswers` require the `XSRF-TOKEN` cookie and a matching `X-XSRF-TOKEN` header.
- Axios is configured with `withCredentials: true`, `xsrfCookieName: XSRF-TOKEN`, and `xsrfHeaderName: X-XSRF-TOKEN`, so the SPA sends the token automatically after bootstrap.

## Docker Run

1. Create `.env` in project root:

```env
APP_DEMO_PASSWORD_HASH=$2b$12$replace_with_real_bcrypt_hash
```

2. Start:

```bash
docker compose up --build
```

3. Open: `http://localhost:8080`

## API Endpoints

- `GET /api/auth/csrf`
- `POST /api/auth/login`
- `POST /api/auth/logout`
- `GET /api/auth/me`
- `GET /api/questions`
- `POST /api/checkanswers`

Example bootstrap and login sequence for non-browser clients:

```text
1. GET /api/auth/csrf
2. Read the `XSRF-TOKEN` cookie from the response
3. POST /api/auth/login with:
   - Cookie: `XSRF-TOKEN=<value>`
   - Header: `X-XSRF-TOKEN: <same cookie value>`
4. Reuse the returned `JSESSIONID` for authenticated requests
```

## Quality Gates

### Backend

```bash
cd backend
./gradlew test
```

### Frontend

```bash
cd quad-trivia-frontend
npm run lint
npm run test:unit -- --run
npm run test:e2e
npm run type-check
npm run build
```

### End-to-End Smoke (HTTP session flow)

Run this against a running stack (for example after `docker compose up`):

```bash
./scripts/e2e-smoke.sh
```

## Testing Strategy

- Backend unit tests for service, client, DTO, controller, and auth endpoint behavior
- Backend integration test for authenticated quiz flow with CSRF bootstrap plus session handling
- Frontend unit/integration tests for stores, views, router guard, and CSRF bootstrap logic

## Security Notes

- Session-based authentication with server-managed security context
- BCrypt password hash from environment variable
- Protected quiz endpoints require authentication
- Correct option IDs are not exposed in score responses
