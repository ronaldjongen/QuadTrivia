<script setup lang="ts">
import { useI18n } from 'vue-i18n'
const {locale, availableLocales} = useI18n()

function changeLocale(newLocale: string) {
  locale.value = newLocale
  localStorage.setItem('locale', newLocale)
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
  <main class="app-shell">
    <RouterView/>
  </main>
</template>

<style scoped>
.app-shell {
  min-height: 100vh;
}
</style>
