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

- `POST /api/auth/login`
- `POST /api/auth/logout`
- `GET /api/auth/me`
- `GET /api/questions`
- `POST /api/checkanswers`

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

- Backend unit tests for service, client, DTO, and controller behavior
- Backend integration test for authenticated quiz flow with session handling
- Frontend unit/integration tests for stores, views, and router guard

## Security Notes

- Session-based authentication with server-managed security context
- BCrypt password hash from environment variable
- Protected quiz endpoints require authentication
- Correct option IDs are not exposed in score responses
