# Quad Trivia Frontend

Vue 3 + TypeScript single-page app for login, quiz flow, and results.

## Scripts

- `npm run dev`: start local dev server
- `npm run test:unit -- --run`: run unit/integration tests
- `npm run test:e2e`: run Playwright E2E tests
- `npm run type-check`: run Vue TypeScript checks
- `npm run lint`: run lint checks
- `npm run build`: production build

## Architecture

- `src/views`: route-level screens (`Login`, `Quiz`, `Result`)
- `src/stores`: Pinia stores for auth and quiz state
- `src/api`: typed API layer over Axios
- `src/router`: route definitions + auth guard
- `src/__tests__`: view/store/router tests

## Contract Notes

- API base URL: `/api`
- Credentials are included for session cookies (`withCredentials: true`)
- Result payload only exposes per-question correctness, not correct option IDs

## Playwright

Playwright expects a running app (default `http://localhost:8080`).

First-time local setup:

```bash
npm run test:e2e:install
```

Environment variables:

- `E2E_BASE_URL` (default: `http://localhost:8080`)
- `E2E_USERNAME` (default: `demo`)
- `E2E_PASSWORD` (default: `demo123`)

Example:

```bash
E2E_BASE_URL=http://localhost:8080 npm run test:e2e
```
