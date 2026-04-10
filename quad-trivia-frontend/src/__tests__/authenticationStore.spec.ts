import { beforeEach, describe, expect, it, vi } from 'vitest'
import { createPinia, setActivePinia } from 'pinia'
import { useAuthStore } from '../stores/authenticationStore'
import * as authApi from '../api/authenticationApi'

vi.mock('../api/authenticationApi', () => ({
  getLoginResult: vi.fn(),
  login: vi.fn(),
  logout: vi.fn(),
}))

describe('authenticationStore', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  it('fetchMe sets authenticated state when backend returns authenticated user', async () => {
    vi.mocked(authApi.getLoginResult).mockResolvedValue({
      username: 'alice',
      isAuthenticated: true,
    })

    const store = useAuthStore()

    await store.fetchMe()

    expect(store.username).toBe('alice')
    expect(store.isAuthenticated).toBe(true)
    expect(store.initialized).toBe(true)
    expect(store.loading).toBe(false)
    expect(store.error).toBeNull()
  })

  it('fetchMe clears user state when backend call fails', async () => {
    vi.mocked(authApi.getLoginResult).mockRejectedValue(new Error('Unauthorized'))

    const store = useAuthStore()
    store.username = 'stale-user'
    store.isAuthenticated = true

    await store.fetchMe()

    expect(store.username).toBeNull()
    expect(store.isAuthenticated).toBe(false)
    expect(store.initialized).toBe(true)
    expect(store.loading).toBe(false)
  })

  it('login authenticates user by posting credentials and then refreshing profile', async () => {
    vi.mocked(authApi.login).mockResolvedValue(undefined)
    vi.mocked(authApi.getLoginResult).mockResolvedValue({
      username: 'bob',
      isAuthenticated: true,
    })

    const store = useAuthStore()

    await store.login('bob', 'secret')

    expect(authApi.login).toHaveBeenCalledWith({ username: 'bob', password: 'secret' })
    expect(authApi.getLoginResult).toHaveBeenCalledTimes(1)
    expect(store.username).toBe('bob')
    expect(store.isAuthenticated).toBe(true)
    expect(store.error).toBeNull()
  })

  it('login exposes user-friendly error and throws when login fails', async () => {
    vi.mocked(authApi.login).mockRejectedValue(new Error('Bad credentials'))

    const store = useAuthStore()
    store.username = 'old-user'
    store.isAuthenticated = true

    await expect(store.login('bob', 'wrong')).rejects.toThrow('Login failed')

    expect(store.error).toBe('Inloggen mislukt')
    expect(store.username).toBeNull()
    expect(store.isAuthenticated).toBe(false)
    expect(store.loading).toBe(false)
  })

  it('logout always clears auth state even when backend logout fails', async () => {
    vi.mocked(authApi.logout).mockRejectedValue(new Error('Network issue'))

    const store = useAuthStore()
    store.username = 'alice'
    store.isAuthenticated = true

    await expect(store.logout()).rejects.toThrow('Network issue')

    expect(store.username).toBeNull()
    expect(store.isAuthenticated).toBe(false)
    expect(store.loading).toBe(false)
    expect(store.error).toBeNull()
  })
})
