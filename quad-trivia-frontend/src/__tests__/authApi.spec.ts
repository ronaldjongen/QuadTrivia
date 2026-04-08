import { describe, it, expect, vi, beforeEach } from 'vitest';
import { getLoginResult } from '../api/authenticationApi';
import { api } from '../api/client';

// Mock the api client
vi.mock('../api/client', () => ({
  api: {
    get: vi.fn(),
    post: vi.fn(),
  },
}));

describe('authApi', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('should return login result when the request is successful', async () => {
    const mockData = { username: 'testuser', isAuthenticated: true };
    (api.get as any).mockResolvedValue({ data: mockData });

    const result = await getLoginResult();

    expect(api.get).toHaveBeenCalledWith('/auth/me');
    expect(result).toEqual(mockData);
  });

  it('should throw an error when the request fails', async () => {
    (api.get as any).mockRejectedValue(new Error('Unauthorized'));

    await expect(getLoginResult()).rejects.toThrow('Unauthorized');
  });
});

import { login, logout } from '../api/authenticationApi';

describe('authenticationApi - login/logout', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('login should send post request', async () => {
    (api.post as any).mockResolvedValue({});
    const loginData = { username: 'test', password: 'password' };

    await login(loginData);

    expect(api.post).toHaveBeenCalledWith('/auth/login', loginData);
  });

  it('logout should send post request', async () => {
    (api.post as any).mockResolvedValue({});

    await logout();

    expect(api.post).toHaveBeenCalledWith('/auth/logout');
  });
});
