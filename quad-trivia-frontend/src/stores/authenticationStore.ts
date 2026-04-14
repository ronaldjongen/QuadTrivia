import { defineStore } from 'pinia'
import { getLoginResult, login, logout,ensureCsrf } from '../api/authenticationApi'

interface AuthState {
  username: string | null
  isAuthenticated: boolean
  initialized: boolean
  loading: boolean
  error: string | null
}

export const useAuthStore = defineStore('auth', {
  state: (): AuthState => ({
    username: null,
    isAuthenticated: false,
    initialized: false,
    loading: false,
    error: null,
  }),

  actions: {
    async bootstrap() {
      await ensureCsrf()
      await this.fetchMe()
    },
    async fetchMe() {
      this.loading = true
      this.error = null

      try {
        const me = await getLoginResult()
        this.username = me.isAuthenticated ? me.username : null
        this.isAuthenticated = me.isAuthenticated
      } catch {
        this.username = null
        this.isAuthenticated = false
      } finally {
        this.initialized = true
        this.loading = false
      }
    },

    async login(username: string, password: string) {
      this.loading = true
      this.error = null

      try {
        await ensureCsrf()
        await login({ username, password })
        await ensureCsrf()
        await this.fetchMe()
      } catch {
        this.error = 'Inloggen mislukt'
        this.isAuthenticated = false
        this.username = null
        throw new Error('Login failed')
      } finally {
        this.loading = false
      }
    },

    async logout() {
      this.loading = true
      this.error = null

      try {
        await logout()
        await ensureCsrf()
      } finally {
        this.username = null
        this.isAuthenticated = false
        this.loading = false
      }
    },
  },
})
