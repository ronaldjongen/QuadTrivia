import { beforeEach, describe, expect, it, vi } from 'vitest'
import { createPinia, setActivePinia } from 'pinia'
import router from '../router'
import { useAuthStore } from '../stores/authenticationStore'
import * as authApi from '../api/authenticationApi'
import { RoutePath } from '../router/routePath'

vi.mock('../api/authenticationApi', () => ({
  getLoginResult: vi.fn(),
  login: vi.fn(),
  logout: vi.fn(),
}))

describe('router auth guard', () => {
  beforeEach(async () => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
    await router.replace(RoutePath.Login)
  })

  it('redirects unauthenticated users away from protected quiz route', async () => {
    vi.mocked(authApi.getLoginResult).mockResolvedValue({
      username: null,
      isAuthenticated: false,
    })

    const store = useAuthStore()
    store.initialized = false

    await router.push(RoutePath.Quiz)

    expect(router.currentRoute.value.path).toBe(RoutePath.Login)
  })

  it('redirects authenticated users away from login route', async () => {
    vi.mocked(authApi.getLoginResult).mockResolvedValue({
      username: 'demo',
      isAuthenticated: true,
    })

    const store = useAuthStore()
    store.initialized = false

    await router.push(RoutePath.Quiz)
    await router.push(RoutePath.Login)

    expect(router.currentRoute.value.path).toBe(RoutePath.Quiz)
  })
})
