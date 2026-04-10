import { beforeEach, describe, expect, it, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import { reactive } from 'vue'
import LoginView from '../views/LoginView.vue'
import { RoutePath } from '../router/routePath'

const push = vi.fn()
const login = vi.fn()

const authStore = reactive({
  username: null as string | null,
  isAuthenticated: false,
  initialized: true,
  loading: false,
  error: null as string | null,
  login,
})

vi.mock('vue-router', () => ({
  useRouter: () => ({ push }),
}))

vi.mock('../stores/authenticationStore', () => ({
  useAuthStore: () => authStore,
}))

describe('LoginView', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    authStore.loading = false
    authStore.error = null
  })

  it('submits credentials and navigates to quiz route on successful login', async () => {
    login.mockResolvedValue(undefined)

    const wrapper = mount(LoginView)

    const usernameInput = wrapper.get('input[type="text"]')
    const passwordInput = wrapper.get('input[type="password"]')
    await usernameInput.setValue('demo-user')
    await passwordInput.setValue('demo-password')
    await wrapper.find('form').trigger('submit.prevent')

    expect(login).toHaveBeenCalledWith('demo-user', 'demo-password')
    expect(push).toHaveBeenCalledWith(RoutePath.Quiz)
  })

  it('does not navigate when login fails', async () => {
    login.mockRejectedValue(new Error('Invalid credentials'))

    const wrapper = mount(LoginView)

    const usernameInput = wrapper.get('input[type="text"]')
    const passwordInput = wrapper.get('input[type="password"]')
    await usernameInput.setValue('demo-user')
    await passwordInput.setValue('wrong-password')
    await wrapper.find('form').trigger('submit.prevent')

    expect(push).not.toHaveBeenCalled()
  })

  it('renders loading state and disables submit button while authenticating', async () => {
    authStore.loading = true

    const wrapper = mount(LoginView)
    const submit = wrapper.find('button[type="submit"]')

    expect(submit.attributes('disabled')).toBeDefined()
    expect(submit.text()).toContain('Bezig...')
  })

  it('shows backend error message when present in store', () => {
    authStore.error = 'Inloggen mislukt'

    const wrapper = mount(LoginView)

    expect(wrapper.text()).toContain('Inloggen mislukt')
  })
})
