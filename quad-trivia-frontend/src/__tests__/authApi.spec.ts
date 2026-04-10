import { beforeEach, describe, expect, it, vi } from 'vitest'
import { getLoginResult, login, logout } from '../api/authenticationApi'
import { api } from '../api/client'

// Mock the api client
vi.mock('../api/client', () => ({
  api: {
    get: vi.fn(),
    post: vi.fn(),
  },
}))

describe('authApi', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('should return login result when the request is successful', async () => {
    const mockData = { username: 'testuser', isAuthenticated: true }
    vi.mocked(api.get).mockResolvedValue({ data: mockData })

    const result = await getLoginResult()

    expect(api.get).toHaveBeenCalledWith('/auth/me')
    expect(result).toEqual(mockData)
  })

  it('should throw an error when the request fails', async () => {
    vi.mocked(api.get).mockRejectedValue(new Error('Unauthorized'))

    await expect(getLoginResult()).rejects.toThrow('Unauthorized')
  })
})

describe('authenticationApi - login/logout', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('login should send post request', async () => {
    vi.mocked(api.post).mockResolvedValue({} as Awaited<ReturnType<typeof api.post>>)
    const loginData = { username: 'test', password: 'password' }

    await login(loginData)

    expect(api.post).toHaveBeenCalledWith('/auth/login', loginData)
  })

  it('logout should send post request', async () => {
    vi.mocked(api.post).mockResolvedValue({} as Awaited<ReturnType<typeof api.post>>)

    await logout()

    expect(api.post).toHaveBeenCalledWith('/auth/logout')
  })
})
