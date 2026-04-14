#!/usr/bin/env bash
set -euo pipefail

APP_URL="${APP_URL:-http://localhost:8080}"
DEMO_USERNAME="${DEMO_USERNAME:-demo}"
DEMO_PASSWORD="${DEMO_PASSWORD:-demo123}"

COOKIE_JAR="$(mktemp)"
QUIZ_JSON="$(mktemp)"
PAYLOAD_JSON="$(mktemp)"
RESULT_JSON="$(mktemp)"

csrf_token() {
  awk '$6 == "XSRF-TOKEN" { print $7 }' "$COOKIE_JAR" | tail -n 1
}

require_csrf_token() {
  local token
  token="$(csrf_token)"
  if [[ -z "$token" ]]; then
    echo "Missing XSRF-TOKEN cookie in cookie jar" >&2
    exit 1
  fi
  printf '%s' "$token"
}

cleanup() {
  rm -f "$COOKIE_JAR" "$QUIZ_JSON" "$PAYLOAD_JSON" "$RESULT_JSON"
}
trap cleanup EXIT

echo "1/4 Login"
curl -sS -f \
  -c "$COOKIE_JAR" \
  -b "$COOKIE_JAR" \
  "$APP_URL/api/auth/csrf" >/dev/null

XSRF_TOKEN="$(require_csrf_token)"

curl -sS -f \
  -b "$COOKIE_JAR" \
  -c "$COOKIE_JAR" \
  -H 'Content-Type: application/json' \
  -H "X-XSRF-TOKEN: $XSRF_TOKEN" \
  -d "{\"username\":\"$DEMO_USERNAME\",\"password\":\"$DEMO_PASSWORD\"}" \
  "$APP_URL/api/auth/login" >/dev/null

echo "2/4 Fetch questions"
curl -sS -f \
  -b "$COOKIE_JAR" \
  "$APP_URL/api/questions" >"$QUIZ_JSON"

echo "3/4 Submit answers"
node -e '
const fs = require("fs");
const quiz = JSON.parse(fs.readFileSync(process.argv[1], "utf8"));
if (!quiz.quizId || !Array.isArray(quiz.questions) || quiz.questions.length === 0) {
  throw new Error("Invalid quiz payload");
}
const answers = quiz.questions.map((q) => {
  if (!q.id || !Array.isArray(q.options) || q.options.length === 0) {
    throw new Error("Invalid question payload");
  }
  return { questionId: q.id, optionId: q.options[0].id };
});
fs.writeFileSync(process.argv[2], JSON.stringify({ quizId: quiz.quizId, answers }));
' "$QUIZ_JSON" "$PAYLOAD_JSON"

curl -sS -f \
  -b "$COOKIE_JAR" \
  -H 'Content-Type: application/json' \
  -H "X-XSRF-TOKEN: $(require_csrf_token)" \
  --data @"$PAYLOAD_JSON" \
  "$APP_URL/api/checkanswers" >"$RESULT_JSON"

echo "4/4 Validate response contract"
node -e '
const fs = require("fs");
const result = JSON.parse(fs.readFileSync(process.argv[1], "utf8"));
if (!Number.isInteger(result.score) || !Number.isInteger(result.total) || !Array.isArray(result.results)) {
  throw new Error("Invalid checkanswers response");
}
if (result.results.some((item) => Object.prototype.hasOwnProperty.call(item, "correctOptionId"))) {
  throw new Error("Security regression: response contains correctOptionId");
}
console.log(`Smoke test passed: score=${result.score}, total=${result.total}`);
' "$RESULT_JSON"
