<script setup lang="ts">
import {reactive} from 'vue'
import {useRouter} from 'vue-router'
import {useAuthStore} from '../stores/authenticationStore'
import { RoutePath } from '../router/routePath'
const router = useRouter()
const authStore = useAuthStore()
const form = reactive({
  username: '',
  password: '',
})

async function onSumbit() {
  try {
    await authStore.login(form.username, form.password)
    await router.push(RoutePath.Quizroute)
  } catch (error) {
    console.log(error)
  }
}
</script>

<template>
  <section class="login-page">
    <h1>Login</h1>
    <form class="login-form" @submit.prevent="onSumbit">
      <label for="username">Gebruikersnaam
        <input v-model="form.username" type="text"/>
      </label>
      <label for="username">Wachtwoord
        <input v-model="form.password" type="text"/>
      </label>
      <button type="submit" :disabled="authStore.loading">
        Inloggen
      </button>
      <p v-if="authStore.error">{{ authStore.error }}</p>
    </form>
  </section>
</template>

<style scoped>

</style>
