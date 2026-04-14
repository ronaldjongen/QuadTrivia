<script setup lang="ts">
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/authenticationStore'
import { RoutePath } from '../router/routePath'
import { useI18n } from 'vue-i18n'
const {locale, availableLocales} = useI18n()

function changeLocale(newLocale: string) {
  locale.value = newLocale
  localStorage.setItem('locale', newLocale)
}
const { t } = useI18n()
const router = useRouter()
const authStore = useAuthStore()
const form = reactive({
  username: '',
  password: '',
})

async function onSubmit() {
  try {
    await authStore.login(form.username.trim(), form.password)
    await router.push(RoutePath.Quiz)
  } catch {}
}
</script>

<template>
  <header>
    <select :value="locale" @change="changeLocale(($event.target as HTMLSelectElement).value)">
      <option v-for="lang in availableLocales" :key="lang" :value="lang">
        {{ lang.toUpperCase() }}
      </option>
    </select>
  </header>
  <section class="login-page">
    <div class="login-shell">
      <aside class="brand-panel">
        <p class="eyebrow">{{ t('app.title') }}</p>
        <h1>{{t('app.description')}}</h1>
        <p class="brand-copy">
          {{t('auth.brand') }}
        </p>

        <div class="brand-points">
          <div class="point">
            <strong>{{ t('auth.brandJWT') }}</strong>
            <span>{{ t('auth.brandPointJWT') }}</span>
          </div>
          <div class="point">
            <strong>{{ t('auth.brandServerSide') }}</strong>
            <span>{{ t('auth.brandPointServerSide') }}</span>
          </div>
          <div class="point">
            <strong>Clean demo flow</strong>
            <span>{{ t('auth.brandPointCleanFlow') }}</span>
          </div>
        </div>
      </aside>

      <div class="card-panel">
        <form class="login-card" @submit.prevent="onSubmit">
          <div class="card-header">
            <p class="card-kicker">{{ t('auth.welcome') }}</p>
            <h2>Start de demo</h2>
            <p class="card-copy">
              {{t('auth.intro') }}
            </p>
          </div>

          <label class="field">
            <span>{{ t('auth.username') }}</span>
            <input
              data-testid="login-username"
              v-model="form.username"
              type="text"
              autocomplete="username"
              placeholder="demo"
            />
          </label>

          <label class="field">
            <span>{{ t('auth.password') }}</span>
            <input
              data-testid="login-password"
              v-model="form.password"
              type="password"
              autocomplete="current-password"
              placeholder="••••••••"
            />
          </label>
          <button data-testid="login-submit" class="submit-button" type="submit" :disabled="authStore.loading">
            {{ authStore.loading ? t('auth.busy') :     t('auth.submit')  }}
          </button>

          <p v-if="authStore.error" data-testid="login-error" class="error-message">
            {{ authStore.error }}
          </p>

          <p class="helper-text">
            {{ t('auth.helper') }}
          </p>
        </form>
      </div>
    </div>
  </section>
</template>

<style scoped>
:global(*) {
  box-sizing: border-box;
}

:global(body) {
  margin: 0;
  font-family:
    Inter, ui-sans-serif, system-ui, -apple-system, BlinkMacSystemFont,
    "Segoe UI", sans-serif;
  background: #f5f7f9;
  color: #111827;
}

.login-page {
  min-height: 100vh;
  display: grid;
  place-items: center;
  padding: 32px;
  background:
    radial-gradient(circle at top left, rgba(28, 100, 242, 0.08), transparent 32%),
    linear-gradient(180deg, #f8fafc 0%, #f3f4f6 100%);
}

.login-shell {
  width: min(1120px, 100%);
  display: grid;
  grid-template-columns: 1.1fr 0.9fr;
  gap: 28px;
  align-items: stretch;
}

.brand-panel {
  padding: 48px;
  border-radius: 28px;
  background:
    linear-gradient(135deg, #0f172a 0%, #111827 65%, #1f2937 100%);
  color: white;
  position: relative;
  overflow: hidden;
  box-shadow: 0 24px 60px rgba(15, 23, 42, 0.18);
}

.brand-panel::after {
  content: "";
  position: absolute;
  inset: auto -80px -80px auto;
  width: 220px;
  height: 220px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.06);
}

.eyebrow {
  margin: 0 0 16px;
  font-size: 13px;
  font-weight: 700;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: rgba(255, 255, 255, 0.72);
}

.brand-panel h1 {
  margin: 0;
  font-size: clamp(2rem, 4vw, 3.5rem);
  line-height: 1.05;
  letter-spacing: -0.03em;
  max-width: 10ch;
}

.brand-copy {
  margin: 20px 0 0;
  max-width: 44ch;
  font-size: 1rem;
  line-height: 1.7;
  color: rgba(255, 255, 255, 0.82);
}

.brand-points {
  margin-top: 36px;
  display: grid;
  gap: 14px;
}

.point {
  padding: 16px 18px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(4px);
}

.point strong {
  display: block;
  margin-bottom: 6px;
  font-size: 0.96rem;
}

.point span {
  color: rgba(255, 255, 255, 0.74);
  font-size: 0.92rem;
  line-height: 1.5;
}

.card-panel {
  display: grid;
  place-items: center;
}

.login-card {
  width: 100%;
  max-width: 460px;
  padding: 36px;
  border-radius: 24px;
  background: #ffffff;
  border: 1px solid #e5e7eb;
  box-shadow: 0 18px 45px rgba(15, 23, 42, 0.08);
}

.card-header {
  margin-bottom: 24px;
}

.card-kicker {
  margin: 0 0 8px;
  font-size: 0.85rem;
  font-weight: 700;
  color: #2563eb;
  text-transform: uppercase;
  letter-spacing: 0.08em;
}

.card-header h2 {
  margin: 0;
  font-size: 2rem;
  line-height: 1.1;
  letter-spacing: -0.03em;
}

.card-copy {
  margin: 10px 0 0;
  color: #6b7280;
  line-height: 1.6;
}

.field {
  display: grid;
  gap: 8px;
  margin-bottom: 18px;
}

.field span {
  font-size: 0.94rem;
  font-weight: 600;
  color: #111827;
}

.field input {
  width: 100%;
  height: 52px;
  padding: 0 16px;
  border-radius: 14px;
  border: 1px solid #d1d5db;
  background: #f9fafb;
  font: inherit;
  color: #111827;
  transition:
    border-color 0.18s ease,
    box-shadow 0.18s ease,
    background-color 0.18s ease;
}

.field input::placeholder {
  color: #9ca3af;
}

.field input:focus {
  outline: none;
  border-color: #2563eb;
  background: #ffffff;
  box-shadow: 0 0 0 4px rgba(37, 99, 235, 0.12);
}

.submit-button {
  width: 100%;
  height: 54px;
  margin-top: 8px;
  border: 0;
  border-radius: 14px;
  background: linear-gradient(135deg, #1d4ed8 0%, #2563eb 100%);
  color: white;
  font: inherit;
  font-weight: 700;
  cursor: pointer;
  transition:
    transform 0.14s ease,
    box-shadow 0.14s ease,
    opacity 0.14s ease;
  box-shadow: 0 14px 28px rgba(37, 99, 235, 0.22);
}

.submit-button:hover {
  transform: translateY(-1px);
}

.submit-button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
  transform: none;
}

.error-message {
  margin: 14px 0 0;
  padding: 12px 14px;
  border-radius: 12px;
  background: #fef2f2;
  color: #b91c1c;
  border: 1px solid #fecaca;
  font-size: 0.93rem;
}

.helper-text {
  margin: 16px 0 0;
  font-size: 0.88rem;
  line-height: 1.5;
  color: #6b7280;
}

@media (max-width: 920px) {
  .login-shell {
    grid-template-columns: 1fr;
  }

  .brand-panel,
  .login-card {
    padding: 28px;
  }

  .brand-panel h1 {
    max-width: 100%;
  }
}

@media (max-width: 560px) {
  .login-page {
    padding: 16px;
  }

  .brand-panel,
  .login-card {
    border-radius: 20px;
    padding: 22px;
  }

  .card-header h2 {
    font-size: 1.7rem;
  }
}
</style>
