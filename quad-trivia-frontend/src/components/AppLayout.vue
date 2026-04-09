<script setup lang="ts">
import { useAuthStore } from '../stores/authenticationStore'
import { RoutePath } from '../router/routePath'
const authStore = useAuthStore()

async function logout() {
  await authStore.logout()
  window.location.href = RoutePath.Loginroute
}
</script>

<template>
  <div class="app-layout">
    <header class="topbar">
      <div class="logo">Quad Trivia</div>

      <div class="actions">
        <span class="user">{{ authStore.username }}</span>
        <button class="logout" @click="logout">
          Logout
        </button>
      </div>
    </header>

    <main class="content">
      <slot />
    </main>
  </div>
</template>

<style scoped>
.app-layout {
  min-height: 100vh;
  background: #f5f7f9;
}

.topbar {
  height: 72px;
  padding: 0 32px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #ffffff;
  border-bottom: 1px solid #e5e7eb;
}

.logo {
  font-weight: 700;
  font-size: 1.1rem;
}

.actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.logout {
  height: 40px;
  padding: 0 16px;
  border-radius: 10px;
  border: 1px solid #e5e7eb;
  background: #f9fafb;
  cursor: pointer;
}
</style>
