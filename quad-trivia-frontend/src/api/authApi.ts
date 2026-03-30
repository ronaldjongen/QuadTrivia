import {api} from './client';
import type {LoginRequest, LoginResult} from '@/data/auth';

export async function login(data: LoginRequest): Promise<void> {
  return api.post('/auth/login', data);
}

export async function logout(): Promise<void> {
  return api.post('/auth/logout');
}

export async function getLoginResult(): Promise<LoginResult> {
  const response= await api.get('/auth/login-result');
  return response.data;
}
